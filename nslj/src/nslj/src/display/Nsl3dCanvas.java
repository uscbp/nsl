package nslj.src.display;

import com.sun.j3d.utils.universe.ViewingPlatform;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.audioengines.javasound.JavaSoundMixer;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

import nslj.src.display.j3d.*;
import nslj.src.lang.NslHierarchy;
import org.odejava.World;
import org.odejava.HashSpace;
import org.odejava.Odejava;
import org.odejava.ode.Ode;
import org.odejava.collision.JavaCollision;
import org.odejava.collision.Contact;
import org.openmali.vecmath2.Vector3f;

/**
 * Canvas for physical simulation using ODE and 3D visualization using Java3d
 */
public class Nsl3dCanvas extends NslCanvas
{
    protected static float DEFAULT_GRAVITY_MAGNITUDE=9.8f;
    public static int colorWall=new java.awt.Color(0, 0, 0).getRGB();
    public static int colorNode=new java.awt.Color(0, 255, 0).getRGB();
    public static int colorWater=new java.awt.Color(0, 0, 255).getRGB();
    public static int colorFood =new java.awt.Color(255, 255, 0).getRGB();

    // The visual universe
    protected VirtualUniverse u;
    // The 3D canvas to render the universe in
    protected Canvas3D canvas;
    // The offscreen 3d canvas for screen capturing
    protected OffScreenCanvas3D offScreenCanvas;
    // The root BranchGroup of the scene graph
    protected BranchGroup sceneRoot;
    // The root object BranchNode of the scene graph
    protected BranchGroup objTrans;
    // The view of the universe
    protected View view;
    // View bounds
    protected BoundingSphere bounds;
    // The physical world
    protected World world;
    // The physical world collision space
    protected HashSpace worldCollSpace;
    // Collision calculation
    protected JavaCollision worldCollCalcs;
    // Contact information
    protected Contact worldContactInfo;
    // The viewing platform
    protected ViewingPlatform vp;
    // Locale of the view
    protected Locale locale;
    // List of objects in the world
    public static Vector<NslObject3d> objects=new Vector<NslObject3d>();
    // List of lights
    protected Vector<Light> lights=new Vector<Light>();
    // Dimensions of the maze (if loaded)
    protected Vector3f mazeDim= new  org.openmali.vecmath2.Vector3f(32,5,32);
    // Size of each map square
    protected javax.vecmath.Vector3d mapSquareSize;
    // Map image
    protected java.awt.image.BufferedImage mapImage;
    // Magnitude of gravitational force
    private float gravityMagnitude=DEFAULT_GRAVITY_MAGNITUDE;

    /**
     * Create a 3d canvas that is a view from the given locale in an existing universe
     * @param nslDisplayFrame - The frame the canvas will be created in
     * @param fullName - name of this canvas
     * @param locale - locale of the view in the existing universe
     */
    public Nsl3dCanvas(NslFrame nslDisplayFrame, String fullName, Locale locale)
    {
        super(nslDisplayFrame, fullName, null);
        this.locale=locale;
        bounds=new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10000.0);
        canvas= createOnscreenCanvas3D();
        offScreenCanvas = createOffscreenCanvas3D(canvas);
        vp = new ViewingPlatform(1);
        Viewer viewer = new Viewer(canvas);
        view=viewer.getView();
        viewer.getView().setSceneAntialiasingEnable(true);
        view.addCanvas3D(offScreenCanvas);
        view.setBackClipDistance(100.0);
        viewer.setViewingPlatform(vp);

        initGUI();
    }

    /**
     * Create a new 3d canvas with a new simulated world
     * @param nslDisplayFrame - the frame to create the canvas in
     * @param fullName - name of variable (unused - needed for compatibility with other canvases)
     */
    public Nsl3dCanvas(NslFrame nslDisplayFrame, String fullName)
    {
        super(nslDisplayFrame, fullName, null);
        init();
    }

    /**
     * Create a new 3d canvas with a new simulated world
     * @param nslDisplayFrame - the frame to create the canvas in
     * @param fullName - name of variable (unused - needed for compatibility with other canvases)
     * @param g = gravity magnitude
     */
    public Nsl3dCanvas(NslFrame nslDisplayFrame, String fullName, float g)
    {
        super(nslDisplayFrame, fullName, null);
        this.gravityMagnitude=g;
        init();
    }

    /**
     * Create a new 3d canvas with a new simulated world
     * @param nslDisplayFrame - the frame to create the canvas in
     * @param min - min value (unused - needed for compatibility with other canvases)
     * @param max - max value (unused - needed for compatibility with other canvases)
     */
    public Nsl3dCanvas(NslFrame nslDisplayFrame, double min, double max)
    {
        super(nslDisplayFrame, min, max);
        init();
    }

    /**
     * Create a new 3d canvas with a new simulated world
     * @param frame - the frame to create the canvas in
     * @param fullName - name of variable (unused - needed for compatibility with other canvases)
     * @param varInfo - variable info (unused - needed for compatibility with other canvases)
     */
    public Nsl3dCanvas(NslFrame frame, String fullName, NslVariableInfo varInfo)
    {
        super(frame, fullName, varInfo);
        init();
    }

    /**
     * Initialize the physical, visual, and GUI components of the canvas
     */
    public void init()
    {
        initPhysWorld(gravityMagnitude);

        initVisualWorld();

        initGUI();
    }

    /**
     * Initialize the physical components
     * @param gravityMagnitude - magnitude of gravitational force
     */
    private void initPhysWorld(float gravityMagnitude)
    {
        // inialize the world and collision objects
        Odejava.getLibraryVersion();

        world = new World();
        world.setGravity(0, -gravityMagnitude, 0);

        // set step size (smaller is more accurate, but slower)
        world.setStepSize((float) NslHierarchy.system.getDelta());

        // create a collision space for the world's box and spheres
        worldCollSpace = new HashSpace();
        // for collision calculations
        worldCollCalcs = new JavaCollision(world);
        worldContactInfo = new Contact( worldCollCalcs.getContactIntBuffer(), worldCollCalcs.getContactFloatBuffer());
    }

    /**
     * Initialize the visual components
     */
    private void initVisualWorld()
    {
        // Create a scene
        createSceneGraph();

        // Create 3d canvas
        canvas= createOnscreenCanvas3D();

        // Create new universe and locale
        u = new VirtualUniverse();
        locale = new Locale(u);

        // Initialize the main view
        initView();

        // create an off Screen Buffer
        offScreenCanvas = createOffscreenCanvas3D(canvas);
        view.addCanvas3D(offScreenCanvas);
    }

    /**
     * Cleans up objects
     */
    public void onClose()
    {
        u.removeAllLocales();
        canvas=null;
        offScreenCanvas=null;
        worldCollSpace.delete();
        worldCollCalcs.delete();
        world.delete();
        Ode.dCloseODE();
    }

    /**
     * Utility method to create a Canvas3D GUI component. The Canvas3D is used by Java 3D to output rendered frames.
     * @return Canvas3D
     */
    protected Canvas3D createOnscreenCanvas3D()
    {
        /*
        * First we query Java 3D for the available device information.
        * We set up a GraphicsConfigTemplate3D and specify that we would
        * prefer a device configuration that supports antialiased output.
        */
        GraphicsConfigTemplate3D gc3D = new GraphicsConfigTemplate3D();
        gc3D.setSceneAntialiasing( GraphicsConfigTemplate.REQUIRED );

        //We then get a list of all the screen devices for the
        //local graphics environment
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(gc3D);

        //We select the best configuration supported by the first screen
        //device, and specify whether we are creating an onscreen or
        //an offscreen Canvas3D.
        Canvas3D c3d = new Canvas3D(gc, false );
        /*
        * Here we have hard-coded the initial size of the Canvas3D.
        * However, because we have used a BorderLayout layout algorithm,
        * this will be automatically resized to fit—-as the parent JFrame
        * is resized.
        */
        c3d.setSize( 500, 500 );
        return c3d;
    }

    /**
     * Create offscreen canvas
     * @param onScreenCanvas = on screen 3d canvas
     * @return OffScreenCanvas3D
     */
    protected OffScreenCanvas3D createOffscreenCanvas3D(Canvas3D onScreenCanvas)
    {
        Screen3D sOn = onScreenCanvas.getScreen3D();

        // Create offscreen canvas
        BufferedImage bImage = new BufferedImage((int)sOn.getSize().getWidth(), (int)sOn.getSize().getHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);
        ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
        buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
        Raster drawRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f), Raster.RASTER_COLOR, 0, 0,
                                       (int)sOn.getSize().getWidth(), (int)sOn.getSize().getHeight(), buffer, null);
        drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);

        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        template.setSceneAntialiasing(GraphicsConfigTemplate.REQUIRED);
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
        offScreenCanvas = new OffScreenCanvas3D(gc, drawRaster);

        // set the offscreen to match the onscreen
        Screen3D sOff = offScreenCanvas.getScreen3D();
        sOff.setSize(sOn.getSize());
        sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
        sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());

        return offScreenCanvas;
    }

    /**
     * Adds a mouse listener to the onscreen 3D canvas
     * @param mouseListener - mouse listener object
     */
    public synchronized void addMouseListener(java.awt.event.MouseListener mouseListener)
    {
        canvas.addMouseListener(mouseListener);
    }

    /**
     * Initialize the GUI components
     */
    protected void initGUI()
    {
        setLayout(new BorderLayout());
        add("Center", canvas);
    }

    /**
     * Create the scene graph
     */
    protected void createSceneGraph()
    {
        // Create the root of the branch graph
        sceneRoot = new BranchGroup();
        sceneRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        sceneRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        sceneRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bounds = new BoundingSphere(new Point3d(), 100000.0);

        setLighting();

        // Create a TransformGroup and initialize it to the
        // identity. Enable the TRANSFORM_WRITE capability so that
        // the mouse behaviors code can modify it at runtime. Add it to the
        // root of the subgraph.
        objTrans = new BranchGroup();
        objTrans.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objTrans.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objTrans.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        sceneRoot.addChild(objTrans);
    }

    public boolean isStarted()
    {
        return (sceneRoot==null || (sceneRoot.isCompiled() && sceneRoot.isLive())) && vp.isCompiled() && vp.isLive();
    }

    public void start()
    {
        // Attach scene to the virtual universe
        if(sceneRoot!=null)
        {
            sceneRoot.compile();
            locale.addBranchGraph(sceneRoot);
        }
        vp.compile();
        locale.addBranchGraph(vp);
    }

    /**
     * Set the user's initial viewpoint using lookAt()
     */
    private void initView()
    {
        // Create a viewer with the onscreen canvas
        vp = new ViewingPlatform(1);
        vp.getViewPlatformTransform().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE );
        vp.getViewPlatformTransform().setCapability(TransformGroup.ALLOW_TRANSFORM_READ );

        Viewer viewer = new Viewer(canvas);

        OrbitBehavior orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL);
        orbit.setSchedulingBounds(bounds);
        vp.setViewPlatformBehavior(orbit);

        // attach the offscreen canvas to the view
        view=viewer.getView();
        view.setSceneAntialiasingEnable(true);
        viewer.setViewingPlatform(vp);
        view.setBackClipDistance(100.0);

        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);

        // args are: viewer posn, where looking, up direction
        //t3d.lookAt( new Point3d(0,3.5,11), new Point3d(0,0,0), new Vector3d(0,1,0));
        t3d.lookAt( new Point3d(0,2,-7), new Point3d(0,1.5,0), new Vector3d(0,1,0));
        t3d.invert();

        steerTG.setTransform(t3d);

        PhysicalBody myBody = new PhysicalBody();
        PhysicalEnvironment myEnvironment = new PhysicalEnvironment();
        view.setPhysicalBody(myBody);
        view.setPhysicalEnvironment(myEnvironment);
        AudioDevice myMixer = new JavaSoundMixer(myEnvironment);
        myMixer.initialize();
    }

    public void paintCanvas(Graphics2D bufferGraphics)
    {}

    public void paintData(Graphics2D bufferGraphics)
    {}

    /**
     * Adds background with a texture
     * @param backgroundTexture - texture to load for the background
     */
    public void addBackground(String backgroundTexture)
    {        
        Background back = new Background();
        back.setApplicationBounds(bounds);

        if(backgroundTexture!=null && backgroundTexture.length()>0)
        {
            TextureLoader tex = new TextureLoader(ClassLoader.getSystemResource("resources/"+backgroundTexture),
                                                  new Container());
            back.setImage(tex.getImage());
        }
        sceneRoot.addChild(back);
    }

    /**
     * Adds background with a color
     * @param color - color fo the background
     */
    public void addBackground(Color3f color)
    {
        Background back = new Background(color);
        back.setApplicationBounds(bounds);
        sceneRoot.addChild(back);
    }

    /**
     * Setup lighting
     */
    private void setLighting()
    {
        // Set up the ambient light
        addAmbientLight(new Color3f(0.3f, 0.3f, 0.3f));

        // Set up the directional lights
        addDirectionalLight(new javax.vecmath.Color3f(1.0f, 1.0f, 1.0f), new javax.vecmath.Vector3f(0.0f, -1f, 0.0f));

        addSpotLight(new Color3f(0.3f,0.3f,0.3f), new Point3f(0,5,0), new Point3f(0.05f,0.01f,0.005f),
                     new javax.vecmath.Vector3f(0,-1,0), (float)(Math.PI/4), 0f);

        addSpotLight(new Color3f(0.3f,0.3f,0.3f), new Point3f(0,5,-2), new Point3f(0.05f,0.01f,0.005f),
                     new javax.vecmath.Vector3f(0,-1,0), (float)(Math.PI/4), 0f);
    }

    /**
     * Adds an ambient light to the scene
     * @param color - color of the light
     */
    public void addAmbientLight(Color3f color)
    {
        addLight(new AmbientLight(color));
    }

    /**
     * Adds a directional light to the scene
     * @param color - color of the light
     * @param direction - direction of the light
     */
    public void addDirectionalLight(Color3f color, javax.vecmath.Vector3f direction)
    {
        addLight(new DirectionalLight(color, direction));
    }

    /**
     * Adds a point light to the scene
     * @param color - color of the light
     * @param position - position of the light
     * @param attenuation - attenuation parameters
     */
    public void addPointLight(Color3f color, Point3f position, Point3f attenuation)
    {
        addLight(new PointLight(color, position, attenuation));
    }

    /**
     * Adds a spotlight to the scene
     * @param color - color of the light
     * @param position - position of the light
     * @param attenuation - attenuation parameters
     * @param direction - direction of the light
     * @param spreadAngle - spread angle
     * @param concentration - concentration
     */
    public void addSpotLight(Color3f color, Point3f position, Point3f attenuation, javax.vecmath.Vector3f direction,
                             float spreadAngle, float concentration)
    {
        addLight(new SpotLight(color, position, attenuation, direction, spreadAngle, concentration));
    }

    /**
     * Add a light to the scene
     * @param light - light to add
     */
    public void addLight(Light light)
    {
        light.setInfluencingBounds(bounds);
        sceneRoot.addChild(light);
        lights.add(light);
    }

    /**
     * Add an object to the scene and the physical world (replacing existing objects with the same name
     * @param object - object to add
     */
    public void addObject(NslObject3d object)
    {
        addObject(object, true);
    }

    /**
     * Add an object to the scene and the physical world
     * @param object - object to add
     * @param replace - whether or not to replace existing objects with the same name
     */
    public void addObject(NslObject3d object, boolean replace)
    {
        if(replace && objects!=null && objects.size()>0)
        {
            for (NslObject3d object1 : objects)
            {
                if (object1.getName().equals(object.getName()))
                {
                    removeObject(object1);
                }
            }
        }
        object.init(world, worldCollSpace);
        objTrans.addChild(object.getBranchGroup());
        objects.add(object);
        object.addChildrenToScene(objTrans);
    }

    /**
     * Remove an object from the scene and the physical world
     * @param obj - object to remove
     */
    public void removeObject(NslObject3d obj)
    {
        for(int i=0; i<obj.getChildren().size(); i++)
            removeObject(obj.getChildren().get(i));
        objTrans.removeChild(obj.getBranchGroup());
        if(obj.getGeom()!=null && worldCollSpace.containsGeom(obj.getGeom().getName()))
            worldCollSpace.remove(obj.getGeom());
        if(obj.getBody()!=null && world.containsBody(obj.getName()))
            world.deleteBody(obj.getBody());
        objects.remove(obj);
    }

    /**
     * Remove an Object from the scene and physical world
     * @param objName - name of the object to remove
     */
    public void removeObject(String objName)
    {
        NslObject3d obj=getObject(objName);
        if(obj!=null)
            removeObject(obj);
    }

    /**
     * Get an existing object
     * @param name - name of the object
     * @return NslObject3d
     */
    public static NslObject3d getObject(String name)
    {
        for (NslObject3d object : objects)
        {
            if (object.getName().equals(name))
                return object;
            else
            {
                NslObject3d obj = object.getChild(name);
                if (obj != null)
                    return obj;
            }
        }
        return null;
    }

    /**
     * Get an existing object
     * @param idx - index of the object in the list of objects
     * @return NslObject3d
     */
    public static NslObject3d getObject(int idx)
    {
        if(objects!=null && objects.size()>idx)
            return objects.get(idx);
        return null;
    }

    /**
     * Get the index of an existing object
     * @param name - name of the object
     * @return int
     */
    public static int getObjectIndex(String name)
    {
        for(int i=0; i<objects.size(); i++)
        {
            if(objects.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    /**
     * Performs one simulation step on the physical world, processes collisions, and updates visual representations
     */
    public void collectTrial()
    {
        if(!isStarted())
            start();

        if(world!=null && worldCollCalcs!=null)
        {
            for (NslObject3d object : objects)
                object.resetContactHierarchical();

            // find collisions
            worldCollCalcs.collide(worldCollSpace);
            // process contact points
            processWorldContacts();
            // add contacts to contactInfo jointGroup
            worldCollCalcs.applyContacts();
            // advance the simulation
            world.stepFast();
        }

        // update visual representations
        for (NslObject3d object : objects)
            object.process();
        recordTrial();

    }

    protected int getVideoFrameWidth()
    {
        return (int)canvas.getScreen3D().getSize().getWidth();
    }

    protected int getVideoFrameHeight()
    {
        return (int)canvas.getScreen3D().getSize().getHeight();
    }

    /**
     * Adds a background sound, loading the wav file with the given filename
     * @param soundFile - wav file to load
     * @return - new BackgroundSound
     */
    public BackgroundSound addBackgroundSound(String soundFile)
    {
        try
        {
            MediaContainer media = new MediaContainer(ClassLoader.getSystemResource("resources/"+soundFile));
            BackgroundSound sound= new  BackgroundSound(media, 10.0f);
            sound.setCapability( Sound.ALLOW_ENABLE_WRITE );
            sound.setCapability( Sound.ALLOW_ENABLE_READ );
            sound.setSchedulingBounds( bounds );
            sound.setLoop(BackgroundSound.INFINITE_LOOPS);
            getSceneRoot().addChild(sound);
            return sound;
        }
        catch( Exception e )
        {
            System.err.println( "Error could not load sound file: "+e  );
            return null;
        }
    }

    /**
     * Adds a point sound, loading the wav file with the given filename
     * @param soundFile - wav file to load
     * @param position - position where the sound will be located
     * @return - new PointSound
     */
    public PointSound addPointSound(String soundFile, Point3f position)
    {
        try
        {
            MediaContainer media = new MediaContainer(ClassLoader.getSystemResource("resources/"+soundFile));
            PointSound sound= new  PointSound(media, 10.0f, position.x, position.y, position.z);
            sound.setCapability( Sound.ALLOW_ENABLE_WRITE );
            sound.setCapability( Sound.ALLOW_ENABLE_READ );
            sound.setSchedulingBounds( bounds );
            sound.setLoop(BackgroundSound.INFINITE_LOOPS);
            getSceneRoot().addChild(sound);
            return sound;
        }
        catch( Exception e )
        {
            System.err.println( "Error could not load sound file: "+e  );
            return null;
        }
    }

    /**
     * Adds a cone sound, loading the wav file with the given filename
     * @param soundFile - wav file to load
     * @param position - position where the sound will be located
     * @param direction - direction the sound is emanating in
     * @return - new ConeSound
     */
    public ConeSound addConeSound(String soundFile, Point3f position, Vector3f direction)
    {
        try
        {
            MediaContainer media = new MediaContainer(ClassLoader.getSystemResource("resources/"+soundFile));
            ConeSound sound= new  ConeSound(media, 10.0f, position.x, position.y, position.z, direction.getX(),
                                            direction.getY(), direction.getZ());
            sound.setCapability( Sound.ALLOW_ENABLE_WRITE );
            sound.setCapability( Sound.ALLOW_ENABLE_READ );
            sound.setSchedulingBounds( bounds );
            sound.setLoop(BackgroundSound.INFINITE_LOOPS);
            getSceneRoot().addChild(sound);
            return sound;
        }
        catch( Exception e )
        {
            System.err.println( "Error could not load sound file: "+e  );
            return null;
        }
    }
    /**
     * Process world contact points
     */
    private void processWorldContacts()
    {
        if(worldCollCalcs !=null)
        {
            for (int i = 0; i < worldCollCalcs.getContactCount(); i++)
            {
                if(worldContactInfo!=null)
                {
                    // look at the ith contact point
                    worldContactInfo.setIndex(i);

                    if(worldContactInfo.getGeom1()!=null && worldContactInfo.getGeom2()!=null)
                    {
                        NslObject3d obj1 = getObject(worldContactInfo.getGeom1().getName());
                        NslObject3d obj2 = getObject(worldContactInfo.getGeom2().getName());

                        if(obj1!=null && obj2!=null)
                        {
                            if(obj1.collisionAllowed(obj2) || obj2.collisionAllowed(obj1))
                                worldContactInfo.ignoreContact();
                            else
                            {
                                worldContactInfo.setMode(Ode.dContactBounce);
                                worldContactInfo.setBounce(0.1f);
                                worldContactInfo.setBounceVel(1f);
                                worldContactInfo.setMu(1000.0f);
                                if((obj1 instanceof BodySegment) && !(obj2 instanceof BodySegment))
                                {
                                    obj1.contact(new Vector3f(worldContactInfo.getPosition()), new Vector3f(worldContactInfo.getNormal()));
                                    Vector3f reverseNormal=new Vector3f(worldContactInfo.getNormal());
                                    reverseNormal.setX(-1*reverseNormal.getX());
                                    reverseNormal.setY(-1*reverseNormal.getY());
                                    reverseNormal.setZ(-1*reverseNormal.getZ());
                                    obj2.contact(new Vector3f(worldContactInfo.getPosition()), reverseNormal);
                                }
                                else if(!(obj1 instanceof BodySegment) && (obj2 instanceof BodySegment))
                                {
                                    obj1.contact(new Vector3f(worldContactInfo.getPosition()), new Vector3f(worldContactInfo.getNormal()));
                                    Vector3f reverseNormal=new Vector3f(worldContactInfo.getNormal());
                                    reverseNormal.setX(-1*reverseNormal.getX());
                                    reverseNormal.setY(-1*reverseNormal.getY());
                                    reverseNormal.setZ(-1*reverseNormal.getZ());
                                    obj2.contact(new Vector3f(worldContactInfo.getPosition()), reverseNormal);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get 3D canvas
     * @return - canvas
     */
    public Canvas3D getCanvas()
    {
        return canvas;
    }

    /**
     * Get the collision space of the world
     * @return HashSpace
     */
    public HashSpace getWorldCollSpace()
    {
        return worldCollSpace;
    }

    /**
     * Get world collision calculations
     * @return JavaCollision
     */
    public JavaCollision getWorldCollCalcs()
    {
        return worldCollCalcs;
    }

    /**
     * Get world contact info
     * @return Contact
     */
    public Contact getWorldContactInfo()
    {
        return worldContactInfo;
    }

    /**
     * Get the simulated world
     * @return World
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * Get object transform BranchGroup
     * @return BrancGroup
     */
    public BranchGroup getObjTrans()
    {
        return objTrans;
    }

    /**
     * Get viewing platform
     * @return ViewingPlatform
     */
    public ViewingPlatform getVp()
    {
        return vp;
    }

    /**
     * Get scene root
     * @return BranchGroup
     */
    public BranchGroup getSceneRoot()
    {
        return sceneRoot;
    }

    public Locale get3dLocale()
    {
        return locale;
    }

    /**
     * Draw a marker in the world
     * @param point - location of the point
     * @param color - color of the point
     * @return - marker object
     */
    public nslj.src.display.j3d.Box drawMarker(javax.vecmath.Vector3f point, Color color)
    {
        Appearance boxAppear =  new  Appearance();
        // diffuse color=black
        Color3f dColor=new Color3f(0f, 0f, 0f);
        // specular color=white
        Color3f sColor=new Color3f(1f,1f,1f);
        Color3f nColor=new Color3f(color);
        boxAppear.setMaterial( new  Material(nColor, dColor, nColor, sColor, 64));
        NslObject3d obj=new nslj.src.display.j3d.Box("marker",
                                                     boxAppear,
                                                     new Vector3f(.02f,.02f,.02f),
                                                     new Vector3f(point.x, point.y, point.z),
                                                     new float[]{0,0,0});
        obj.init(null,null);
        objTrans.addChild(obj.getBranchGroup());
        objects.add(obj);
        return (nslj.src.display.j3d.Box)obj;
    }

    /**
     * Save image of canvas view
     */
    public void save()
    {
        ImageSaveDialog sd=new ImageSaveDialog(this);
        sd.setDialogTitle("Select Canvas Capture File");
        sd.save(getImage(), false);
    }

    /**
     * Get screenshot of canvas
     * @return - image
     */
    protected BufferedImage getImage()
    {
        offScreenCanvas.print();
        while(offScreenCanvas.isPrinting())
        {
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {

            }
        }

        return offScreenCanvas.getDrawRaster().getImage().getImage();
    }

    /**
     * Get canvas bounds
     * @return - bounds
     */
    public Bounds getCanvasBounds()
    {
        return bounds;
    }

    public void createMap(String mapFile)
    {
        javax.media.j3d.Texture tex= new com.sun.j3d.utils.image.TextureLoader(ClassLoader.getSystemResource("resources/"+mapFile),
                                                                               this).getTexture();
        mapImage=((javax.media.j3d.ImageComponent2D) tex.getImage(0)).getImage();

        float imageWidth = mapImage.getWidth();
        float imageHeight = mapImage.getHeight();

        mazeDim.setX(imageWidth);
        mazeDim.setZ(imageHeight);

        for (int nPixelX = 1; nPixelX<imageWidth-1 ; nPixelX++)
        {
            for (int nPixelY = 1; nPixelY<imageHeight-1 ; nPixelY++)
                createMapItem(mapImage.getRGB(nPixelX, nPixelY), nPixelX, nPixelY);
        }

        createFloor(mazeDim);

        createExternalWall(mazeDim);
    }

    protected void createMapItem(int color, int nPixelX, int nPixelY)
    {
        if(color==colorWall)
            createWall(nPixelX, nPixelY);
        else if(color==colorWater)
            createWater(nPixelX, nPixelY);
        else if(color== colorFood)
            createFood(nPixelX, nPixelY);
    }

    protected void createFloor(org.openmali.vecmath2.Vector3f mazeDim)
    {
        Plane floor= new Plane("floor",  new  Appearance(), "textures/floor.gif",
                                new  org.openmali.vecmath2.Vector3f(mazeDim.getX(),0,mazeDim.getZ()),
                                new  org.openmali.vecmath2.Vector3f(0,0,0),  new  float[]{0,0,0});
        addObject(floor);
    }

    protected void createWater(int nPixelX, int nPixelY)
    {
        javax.vecmath.Vector3d point = convertToWorldCoordinate(nPixelX, nPixelY);

        javax.media.j3d.Appearance waterAppearance =  new  javax.media.j3d.Appearance();
        waterAppearance.setPolygonAttributes( new  javax.media.j3d.PolygonAttributes(javax.media.j3d.PolygonAttributes.POLYGON_FILL,
                                                                                     javax.media.j3d.PolygonAttributes.CULL_NONE,
                                                                                     0, false));
        waterAppearance.setTransparencyAttributes( new  javax.media.j3d.TransparencyAttributes(javax.media.j3d.TransparencyAttributes.BLENDED,
                                                                                               1.0f));
        waterAppearance.setTextureAttributes( new  javax.media.j3d.TextureAttributes(javax.media.j3d.TextureAttributes.REPLACE,
                                                                                     new  javax.media.j3d.Transform3D(),
                                                                                     new  javax.vecmath.Color4f(0, 0, 0, 1), javax.media.j3d.TextureAttributes.FASTEST));

        nslj.src.display.j3d.Box water =  new  nslj.src.display.j3d.Box("water_"+nPixelX+"_"+nPixelY,
                                                                        waterAppearance, "textures/water.gif",
                                                                        new org.openmali.vecmath2.Vector3f((float)getMapSquareSize().x*2-.1f, .001f,
                                                                                                           (float)getMapSquareSize().x*2-.1f),
                                                                        new org.openmali.vecmath2.Vector3f((float)point.x,
                                                                                                           0.1f,
                                                                                                           (float)point.z),
                                                                        new float[]{0,0,0});
        water.setDensity(Float.POSITIVE_INFINITY);
        //water.allowCollision("wall");
        addObject(water,false);
    }

    protected void createFood(int nPixelX, int nPixelY)
    {
        javax.vecmath.Vector3d point = convertToWorldCoordinate(nPixelX, nPixelY);

        final double cheeseheight = mazeDim.getY()*.25;

        nslj.src.display.j3d.Cone cheese =  new  nslj.src.display.j3d.Cone("food"+nPixelX+"_"+nPixelY,
                                                                          new javax.media.j3d.Appearance(), "textures/cheese.jpg",
                                                                          new org.openmali.vecmath2.Vector3f((float)getMapSquareSize().x-.1f,
                                                                                                             (float)(cheeseheight*getMapSquareSize().x),
                                                                                                             (float)getMapSquareSize().z-.1f),
                                                                          new org.openmali.vecmath2.Vector3f((float)point.x,
                                                                                                             (float)(cheeseheight*getMapSquareSize().x)/2+.1f,
                                                                                                             (float)point.z),
                                                                          new float[]{(float)(-Math.PI/2),0,0});
        //cheese.allowCollision("floor");
        cheese.setDensity(Float.POSITIVE_INFINITY);
        addObject(cheese,false);
    }

    protected void createWall(int nPixelX, int nPixelY)
    {
        javax.vecmath.Vector3d point = convertToWorldCoordinate(nPixelX, nPixelY);

        javax.vecmath.Vector3d squareSize = getMapSquareSize();

        nslj.src.display.j3d.Box wall =  new  nslj.src.display.j3d.Box("wall", new javax.media.j3d.Appearance(),
                                                                       "textures/wall.jpg",
                                                                       new org.openmali.vecmath2.Vector3f((float)squareSize.x,
                                                                                                          (float)(mazeDim.getY()*squareSize.x),
                                                                                                          (float)squareSize.z),
                                                                       new org.openmali.vecmath2.Vector3f((float)point.x,
                                                                                                          mazeDim.getY(),
                                                                                                          (float)point.z),
                                                                       new float[]{0,0,0});
        //wall.allowCollision("wall");
        wall.setDensity(Float.POSITIVE_INFINITY);
        addObject(wall,false);
    }

    protected void createExternalWall(org.openmali.vecmath2.Vector3f mazeDim)
    {
        javax.vecmath.Vector3d squareSize = getMapSquareSize();
        nslj.src.display.j3d.Box left= new  nslj.src.display.j3d.Box("wall", new javax.media.j3d.Appearance(),
                                                                     "textures/wall.jpg",
                                                                     new org.openmali.vecmath2.Vector3f((float)squareSize.x,
                                                                                                        (float)(mazeDim.getY()*squareSize.x),
                                                                                                        (float)(mazeDim.getZ()*squareSize.z)),
                                                                     new org.openmali.vecmath2.Vector3f(-mazeDim.getX()+(float)(squareSize.x/2),
                                                                                                        mazeDim.getY(),0),
                                                                     new float[]{0,0,0});
        //left.allowCollision("wall");
        left.setDensity(Float.POSITIVE_INFINITY);
        addObject(left,false);

        nslj.src.display.j3d.Box right= new nslj.src.display.j3d.Box("wall", new javax.media.j3d.Appearance(),
                                                                     "textures/wall.jpg",
                                                                     new org.openmali.vecmath2.Vector3f((float)squareSize.x,
                                                                                                        (float)(mazeDim.getY()*squareSize.x),
                                                                                                        (float)(mazeDim.getZ()*squareSize.z)),
                                                                     new org.openmali.vecmath2.Vector3f(mazeDim.getX()-(float)(squareSize.x/2),
                                                                                                        mazeDim.getY(),0),
                                                                     new float[]{0,0,0});
        //right.allowCollision("wall");
        right.setDensity(Float.POSITIVE_INFINITY);
        addObject(right,false);

        nslj.src.display.j3d.Box front= new nslj.src.display.j3d.Box("wall", new javax.media.j3d.Appearance(),
                                                                     "textures/wall.jpg",
                                                                     new org.openmali.vecmath2.Vector3f((float)(mazeDim.getX()*squareSize.x),
                                                                                                        (float)(mazeDim.getY()*squareSize.x),
                                                                                                        (float)squareSize.x),
                                                                     new org.openmali.vecmath2.Vector3f(0,mazeDim.getY(),
                                                                                                        mazeDim.getZ()-(float)(squareSize.x/2)),
                                                                     new float[]{0,0,0});
        //front.allowCollision("wall");
        front.setDensity(Float.POSITIVE_INFINITY);
        addObject(front,false);

        nslj.src.display.j3d.Box back= new nslj.src.display.j3d.Box("wall", new javax.media.j3d.Appearance(),
                                                                    "textures/wall.jpg",
                                                                    new org.openmali.vecmath2.Vector3f((float)(mazeDim.getX()*squareSize.x),
                                                                                                       (float)(mazeDim.getY()*squareSize.x),
                                                                                                       (float)squareSize.x),
                                                                    new org.openmali.vecmath2.Vector3f(0,mazeDim.getY(),
                                                                                                       -mazeDim.getZ()+(float)(squareSize.x/2)),
                                                                    new float[]{0,0,0});
        //back.allowCollision("wall");
        back.setDensity(Float.POSITIVE_INFINITY);
        addObject(back,false);
    }

    protected javax.vecmath.Vector3d convertToWorldCoordinate(int nPixelX, int nPixelY)
    {
        return convertToWorldCoordinate(new Point2d(nPixelX, nPixelY));
    }

    public javax.vecmath.Vector3d getMapSquareSize()
    {
        if (mapSquareSize==null )
        {
            mapSquareSize =  new  javax.vecmath.Vector3d(2 , 0, 2);
        }
        return mapSquareSize;
    }


    public boolean isCollision(Transform3D t3d)
    {
        // get the translation
        javax.vecmath.Vector3d m_Translation=new javax.vecmath.Vector3d();
        javax.vecmath.Matrix3d angle=new javax.vecmath.Matrix3d();
        t3d.get(angle);
        t3d.get(m_Translation);

        Point3d noseDisplacement=new Point3d(0,0,-3.1);
        angle.transform(noseDisplacement);

        m_Translation.add(noseDisplacement);

        Vector3d mapSquareSize = getMapSquareSize();

        // first check that we are still inside the "world"
        return m_Translation.x < -mazeDim.getX() + mapSquareSize.x || m_Translation.x > mazeDim.getX() - mapSquareSize.x
                || m_Translation.z < -mazeDim.getZ() + mapSquareSize.z || m_Translation.z > mazeDim.getZ() - mapSquareSize.z || isCollision(m_Translation);

    }

    // returns true if the given x,z location in the world corresponds to a wall
    // section
    public boolean isCollision(Vector3d worldCoord)
    {
        Point2d point = convertToMapCoordinate(worldCoord);

        int color = mapImage.getRGB((int) point.x, (int) point.y);

        // we can't walk through walls
        return color == colorWall;
    }

    public int getMapCoordColor(double x, double y)
    {
        return mapImage.getRGB((int)x, (int)y);
    }
    
    public Point2d convertToMapCoordinate(Vector3d worldCoord)
    {
        Point2d point2d = new Point2d();

        Vector3d squareSize = getMapSquareSize();

        point2d.x = (worldCoord.x + mazeDim.getX()*squareSize.x*.5-.5*squareSize.x) / squareSize.x;
        point2d.y = (worldCoord.z + mazeDim.getZ()*squareSize.z*.5-.5*squareSize.z) / squareSize.z;

        return point2d;
    }

    public Vector3d convertToWorldCoordinate(Point2d mapCoord)
    {
        Vector3d worldCoord=new Vector3d();
        Vector3d squareSize = getMapSquareSize();
        worldCoord.x=(mapCoord.x-mazeDim.getX()*.5)*squareSize.x+.5*squareSize.x;
        worldCoord.z=(mapCoord.y-mazeDim.getZ()*.5)*squareSize.z+.5*squareSize.z;
        return worldCoord;
    }

    public Vector3f getMazeDim()
    {
        return mazeDim;
    }

    public float getGravityMagnitude()
    {
        return gravityMagnitude;
    }

    public void setGravityMagnitude(float gravityMagnitude)
    {
        this.gravityMagnitude = gravityMagnitude;
        world.setGravity(0, -gravityMagnitude, 0);
    }
}
