package nslj.src.display.j3d;

import javax.media.j3d.*;
import javax.vecmath.Matrix3f;
import java.util.Vector;
import java.awt.*;

import org.openmali.vecmath2.Vector3f;
import org.odejava.*;
import org.odejava.Body;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.image.TextureLoader;
import nslj.src.lang.NslHierarchy;

/**
 * NslObject3d - A simulated 3D object that includes visualization (Java3d) and physical simulation (ODE) components
 */
public abstract class NslObject3d
{
    // Shape types
    public static int SHAPE_SPHERE=0;
    public static int SHAPE_CYLINDER=1;
    public static int SHAPE_RECT=2;
    public static int SHAPE_CONE=3;
    public static int SHAPE_CAPSULE=4;
    public static int SHAPE_COMPLEX=5;

    // Joint types
    public final static int JOINT_TYPE_NONE =-1;
    public final static int JOINT_TYPE_BALL =0;
    public final static int JOINT_TYPE_UNIVERSAL =1;
    public final static int JOINT_TYPE_HINGE =2;
    public final static int JOINT_TYPE_HINGE2 =3;
    public final static int JOINT_TYPE_FIXED =4;
    public final static int JOINT_TYPE_CONTACT =5;

    // Name of this object
    protected String name;
    // Name of objects that this object is allowed to collide with
    protected Vector<String> allowedCollisions;
    // This object's branch group
    protected BranchGroup branchGroup;
    // Transform group defining object location and orientation
    protected TransformGroup transformGroup;
    // Transform from parent
    protected Transform3D localTransform;
    // World transform
    protected Transform3D rootTransform;
    // World transform
    protected Transform3D worldTransform;
    // Object visual appearance
    protected Appearance appearance;
    // Size
    protected Vector3f dim;
    // Visual shape
    protected com.sun.j3d.utils.geometry.Primitive primitive;
    // Physical shape
    protected PlaceableGeom geom;
    // Physical body
    protected Body body;
    // Density
    protected float density;
    // The parent of this object
    protected NslObject3d parent;
    // Children of this object
    protected Vector<NslObject3d> children=new Vector<NslObject3d>();
    // Joint connecting this segment to its parent
    protected Joint joint;
    // Axes of the joint
    protected Vector3f[] jointAxes;
    // Type of joint
    protected int jointType;
    // Texture image file
    protected String textureFile=null;
    protected Vector3f linearVel;
    protected Vector3f angularVel;
    protected Vector3f lastLinearVel;
    protected Vector3f lastAngularVel;

    // Contact points
    protected Vector<Vector3f> contactPoints;
    // Contact normal vector
    protected Vector<Vector3f> contactNormals;


    /**
     * Default constructor
     */
    public NslObject3d()
    {
        this.allowedCollisions=new Vector<String>();
        this.contactPoints=new Vector<Vector3f>();
        this.contactNormals=new Vector<Vector3f>();
    }

    /**
     * NslObject3d constructor
     * @param name - name of object
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public NslObject3d(String name, Appearance app, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        this();
        this.name=name;
        this.appearance=app;
        this.dim=dim;
        this.jointType=JOINT_TYPE_NONE;

        // Init branch group
        branchGroup = new BranchGroup();
        branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        branchGroup.setCapability(BranchGroup.ALLOW_DETACH);

        // Init transform group
        transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        branchGroup.addChild(transformGroup);

        // Create local transform
        localTransform=new Transform3D(NslVecmathUtils.createRotationMatrix(eulerAngles),
                                       NslVecmathUtils.convertVector3f(translation), 1.0f);
        transformGroup.setTransform(localTransform);
        // Set world transform
        rootTransform=new Transform3D(localTransform);
        worldTransform=new Transform3D(localTransform);
    }

    /**
     * NslObject3d constructor with texture
     * @param name - name of object
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public NslObject3d(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        this(name, app, dim, translation, eulerAngles);
        this.textureFile=textureFile;
        appearance.setTexture((new TextureLoader(ClassLoader.getSystemResource("resources/"+textureFile),
                                                 new Container())).getTexture());
    }

    /**
     * NslObject3d constructor with joint
     * @param name - name of object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public NslObject3d(String name, int jointType, Vector3f[] jointAxes, Appearance app, Vector3f dim,
                       Vector3f translation, float[] eulerAngles)
    {
        this(name, app, dim, translation, eulerAngles);
        this.jointType=jointType;
        this.jointAxes=jointAxes;
    }

    /**
     * NslObject3d constructor with joint and texture
     * @param name - name of object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public NslObject3d(String name, int jointType, Vector3f[] jointAxes, Appearance app, String textureFile,
                       Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        this(name, jointType, jointAxes, app, dim, translation, eulerAngles);
        this.textureFile=textureFile;
        appearance.setTexture((new TextureLoader(ClassLoader.getSystemResource("resources/"+textureFile),
                                                 new Container())).getTexture());
    }

    /**
     * NslObject3d constructor with parent object
     * @param name - name of the object
     * @param parent - parent object
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public NslObject3d(String name, NslObject3d parent, Appearance app, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        this(name,app,dim,translation,eulerAngles);
        // Set world transform to parent's world transform multiplied by local transform
        rootTransform.mul(parent.rootTransform, localTransform);
        worldTransform.mul(parent.worldTransform, localTransform);
        transformGroup.setTransform(worldTransform);
        this.parent=parent;
        parent.addChild(this);
    }

    /**
     * NslObject3d constructor with texture and parent object
     * @param name - name of the object
     * @param parent - parent object
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public NslObject3d(String name, NslObject3d parent, Appearance app, String textureFile, Vector3f dim,
                       Vector3f translation, float[] eulerAngles)
    {
        this(name, parent, app, dim, translation, eulerAngles);
        this.textureFile=textureFile;
        appearance.setTexture((new TextureLoader(textureFile, new Container())).getTexture());
    }

    /**
     * NslObject3d constructor with parent and joint
     * @param name - name of the object
     * @param parent - parent object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public NslObject3d(String name, NslObject3d parent, int jointType, Vector3f[] jointAxes, Appearance app,
                       Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        this(name, parent, app, dim, translation, eulerAngles);
        this.jointType=jointType;
        this.jointAxes=jointAxes;
    }

    /**
     * NslObject3d constructor with parent, joint, and texture
     * @param name - name of the object
     * @param parent - parent object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public NslObject3d(String name, NslObject3d parent, int jointType, Vector3f[] jointAxes, Appearance app,
                       String textureFile, Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        this(name, parent, jointType, jointAxes, app, dim, translation, eulerAngles);
        this.textureFile=textureFile;
        appearance.setTexture((new TextureLoader(textureFile, new Container())).getTexture());
    }

    /**
     * Initialize the object - visual and physical initialization. If world is null, only visual initialization is
     * performed. Recursively initializes children as well.
     * @param world - world to add object to
     * @param collSpace - collision space to add object to
     */
    public void init(World world, HashSpace collSpace)
    {
        // Initialize visual component
        initVisual();

        // Initialize physical component
        if(world!=null)
        {
            initPhysical(world, collSpace);
            updatePhysical();
            initJoint(world);
        }

        // Call init on children
        for (NslObject3d aChildren : children)
            aChildren.init(world, collSpace);
    }

    /**
     * Visual initialization - must be implemented by subclasses
     */
    protected abstract void initVisual();

    /**
     * Update visual information based on physical simulation - recursively calls updateVisual on children
     */
    public void updateVisual()
    {
        // If there is a physical component
        if(body!=null)
        {
            // Get position and orientation
            worldTransform.set(NslVecmathUtils.convertMatrix3f(body.getRotation()),
                               NslVecmathUtils.convertTuple3f(body.getPosition()), 1.0f);
            // Update visual component position and orientation
            transformGroup.setTransform(worldTransform);
        }
        // Call updateVisual on child objects
        for (NslObject3d aChildren : children)
            aChildren.updateVisual();
    }

    /**
     * Physical initialization - must be implemented by subclasses
     * @param world - world to add object to
     * @param collSpace - collision space to use
     */
    protected abstract void initPhysical(World world, HashSpace collSpace);

    /**
     * Update physical information based on visual
     */
    public void updatePhysical()
    {
        // If there is a physical component
        if(body!=null)
        {
            Matrix3f rotation=new Matrix3f();
            javax.vecmath.Vector3f position=new javax.vecmath.Vector3f();
            worldTransform.get(rotation,position);

            // Update physical component rotation and position
            body.setRotation(NslVecmathUtils.convertMatrix3f(rotation));
            body.setPosition(NslVecmathUtils.convertVector3f(position));
            // Set velocity to 0
            body.setAngularVel(0,0,0);
            body.setLinearVel(0,0,0);
        }
    }

    /**
     * Initialize joint
     * @param world - world to add joint to
     */
    protected void initJoint(World world)
    {
        // Get position and orientation of object in world reference frame
        javax.vecmath.Vector3f pos=new javax.vecmath.Vector3f();
        javax.vecmath.Matrix3f rot=new javax.vecmath.Matrix3f();
        worldTransform.get(pos);
        worldTransform.get(rot);

        switch(jointType)
        {
            // Initialize ball joint
            case JOINT_TYPE_BALL:
                // Create joint
                joint=new JointBall(world);
                // Use joint to attach object to parent object
                if(parent!=null)
                    joint.attach(parent.body,body);
                // Use joint to attach body to space
                else
                    joint.attach(body, null);
                // Set anchor to position of object
                ((JointBall)joint).setAnchor(pos.x,pos.y,pos.z);
                // Set joint axes
                javax.vecmath.Vector3f a1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(a1);
                joint.setAxis1(a1.x,a1.y,a1.z);
                javax.vecmath.Vector3f a3= NslVecmathUtils.convertVector3f(jointAxes[2]);
                rot.transform(a3);
                joint.setAxis2(a3.x,a3.y,a3.z);
                break;
            // Initialize universal joint
            case JOINT_TYPE_UNIVERSAL:
                // Create joint
                joint=new JointUniversal(world);
                // Use joint to attach object to parent object
                if(parent!=null)
                    joint.attach(parent.body,body);
                // Use joint to attach body to space
                else
                    joint.attach(body, null);
                // Set anchor to position of object
                ((JointUniversal)joint).setAnchor(pos.x,pos.y,pos.z);
                // Set joint axes
                javax.vecmath.Vector3f b1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(b1);
                joint.setAxis1(b1.x,b1.y,b1.z);
                javax.vecmath.Vector3f b2= NslVecmathUtils.convertVector3f(jointAxes[1]);
                rot.transform(b2);
                joint.setAxis2(b2.x,b2.y,b2.z);
                break;
            //Initialize hinge joint
            case JOINT_TYPE_HINGE:
                // Create joint
                joint=new JointHinge(world);
                // Use joint to attach object to parent object
                if(parent!=null)
                    joint.attach(parent.body,body);
                // Use joint to attach body to space
                else
                    joint.attach(body, null);
                // Set anchor to position of object
                ((JointHinge)joint).setAnchor(pos.x, pos.y, pos.z);
                // Set joint axis
                javax.vecmath.Vector3f c1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(c1);
                joint.setAxis1(c1.x,c1.y,c1.z);
                break;
            // Intialized fixed joint
            case JOINT_TYPE_FIXED:
                // Create joint
                joint=new JointFixed(world, new JointGroup());
                // Use joint to attach object to parent object
                if(parent!=null)
                    joint.attach(parent.body,body);
                // Use joint to attach body to space
                else
                    joint.attach(body, null);
                break;
        }
    }

    /**
     * Move the object to a new position and orientation
     * @param newPosition - new position
     * @param newAngles - new orientation
     */
    public void move(Vector3f newPosition, float[] newAngles)
    {
        // Create rotation matrix
        javax.vecmath.Matrix3f rotation= NslVecmathUtils.createRotationMatrix(newAngles);
        // Update local transform based on new rotation and position
        localTransform=new Transform3D(rotation, NslVecmathUtils.convertVector3f(newPosition), 1.0f);
        // Update visual component
        transformGroup.setTransform(localTransform);
        worldTransform=new Transform3D(localTransform);
        // Update physical component from visual component
        if(body!=null)
            updatePhysical();
    }

    /**
     * Move relative to parent
     * @param newPosition - new position
     * @param newAngles - new orientation
     */
    public void moveRelative(Vector3f newPosition, float[] newAngles)
    {
        // Create rotation matrix
        javax.vecmath.Matrix3f rotation= NslVecmathUtils.createRotationMatrix(newAngles);
        // Update local transform based on new rotation and position
        localTransform=new Transform3D(rotation, NslVecmathUtils.convertVector3f(newPosition), 1.0f);
        // Update visual component
        transformGroup.setTransform(localTransform);
        worldTransform=new Transform3D(localTransform);
        // Set world transform to parent's world transform multiplied by local transform
        worldTransform.mul(parent.worldTransform, localTransform);
        // Update physical component from visual component
        if(body!=null)
            updatePhysical();
    }

    /**
     * Translate to a new position
     * @param newPosition - new position
     */
    public void translate(float[] newPosition)
    {
        // Get current orientation
        javax.vecmath.Matrix3f rot=new javax.vecmath.Matrix3f();
        worldTransform.get(rot);
        // Update local transform based on current rotation and new position
        localTransform=new Transform3D(rot, NslVecmathUtils.convertVector3f(new Vector3f(newPosition[0],
            newPosition[1],newPosition[2])), 1.0f);
        // Update visual component
        transformGroup.setTransform(localTransform);
        worldTransform=new Transform3D(localTransform);
        // Update physical component from visual component
        if(body!=null)
            updatePhysical();
    }

    /**
     * Rotate to a new orientation
     * @param newAngles - new orientation
     */
    public void rotate(float[] newAngles)
    {
        // Get current position
        javax.vecmath.Vector3f pos=new javax.vecmath.Vector3f();
        worldTransform.get(pos);
        // Update local transform based on new rotation and current position
        localTransform=new Transform3D(NslVecmathUtils.createRotationMatrix(newAngles), pos, 1.0f);
        // Update visual component
        transformGroup.setTransform(localTransform);
        worldTransform=new Transform3D(localTransform);
        // Update physical component from visual component
        if(body!=null)
            updatePhysical();
    }

    /**
     * Set the texture from an image file
     * @param textureImageFile - texture image filename
     */
    public void setTexture(String textureImageFile)
    {
        // Load texture
        TextureLoader tex = new TextureLoader(textureImageFile, new Container());
        // Generate texture coordinates
        TexCoordGeneration tcg=new TexCoordGeneration();
        tcg.setGenMode(TexCoordGeneration.OBJECT_LINEAR);
        appearance.setTexCoordGeneration(tcg);
        // Get texture image
        ImageComponent2D image = tex.getImage();
        // Set texture
        if(image == null)
            System.out.println("load failed for texture: "+textureImageFile);
        else
        {
            appearance.setTexture(tex.getTexture());
        }
   }

    /**
     * Get joint axes
     * @return - array of joint axes
     */
    public Vector3f[] getJointAxes()
    {
        return jointAxes;
    }

    /**
     * Get joint
     * @return Joint
     */
    public Joint getJoint()
    {
        return joint;
    }

    /**
     * Allow collision with another object (do not apply contact forces)
     * @param name - name of the object to allow collisions with
     */
    public void allowCollision(String name)
    {
        if(!allowedCollisions.contains(name))
            allowedCollisions.add(name);
    }

    /**
     * Disallow collision with another object (apply contacat forces)
     * @param name - name of object to disallow collisions with
     */
    public void disallowCollision(String name)
    {
        if(allowedCollisions.contains(name))
            allowedCollisions.remove(name);
    }

    /**
     * Check if this object is allowed to collide with the given object
     * @param obj2 - the object to check for collision permissions with the current object
     * @return - whether or not collisions are allowed between the two objects
     */
    public boolean collisionAllowed(NslObject3d obj2)
    {
        return obj2 == null || allowedCollisions.contains(obj2.name) || obj2.allowedCollisions.contains(name);
    }

    /**
     * Get the object branch group
     * @return BranchGroup
     */
    public BranchGroup getBranchGroup()
    {
        return branchGroup;
    }

    /**
     * Get the object transform group
     * @return TransformGroup
     */
    public TransformGroup getTransformGroup()
    {
        return transformGroup;
    }

    /**
     * Get the object visual appearance
     * @return Appearance
     */
    public Appearance getAppearance()
    {
        return appearance;
    }

    /**
     * Get the object local transformation
     * @return Transform3D
     */
    public Transform3D getLocalTransform()
    {
        return localTransform;
    }

    /**
     * Get the object world transformation
     * @return Transform3D
     */
    public Transform3D getWorldTransform()
    {
        return worldTransform;
    }
    
    /**
     * Get the object size
     * @return Vector3f
     */
    public Vector3f getDim()
    {
        return dim;
    }

    /**
     * Get the object name
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the object physical geometry
     * @return Geom
     */
    public Geom getGeom()
    {
        return geom;
    }

    /**
     * Get the object physical body
     * @return Body
     */
    public Body getBody()
    {
        return body;
    }

    /**
     * Add a child object
     * @param child - child object
     */
    public void addChild(NslObject3d child)
    {
        children.add(child);
    }

    /**
     * Get a child object
     * @param name - name of the child
     * @return - the child object with the given name if it exists
     */
    public NslObject3d getChild(String name)
    {
        for (NslObject3d aChildren : children)
        {
            if (aChildren.getName().equals(name))
                return aChildren;
            else
            {
                NslObject3d obj = aChildren.getChild(name);
                if (obj != null)
                {
                    return obj;
                }
            }
        }
        return null;
    }

    /**
     * Get the parent object
     * @return NslObject3d
     */
    public NslObject3d getParent()
    {
        return parent;
    }

    /**
     * Get the children objects
     * @return - Vector<NslObject3d>
     */
    public Vector<NslObject3d> getChildren()
    {
        return children;
    }

    /**
     * Get the number of planes in this object
     * @return int
     */
    public int getNumPlanes()
    {
        return 0;
    }

    /**
     * Get the object shape - must be implemented by subclasses
     * @return int
     */
    public abstract int getShape();

    /**
     * Get object shape surface normal vectors
     * @return Vector3f[]
     */
    public Vector3f[] getNormals()
    {
        NormalGenerator normalG=new NormalGenerator();
        GeometryInfo info=new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        Transform3D trans=new Transform3D();
        primitive.getLocalToVworld(trans);
        Vector3f[] normals=new Vector3f[getNumPlanes()];
        for(int i=0; i<normals.length; i++)
        {
            info.reset((GeometryArray)primitive.getShape(i).getGeometry());
            normalG.generateNormals(info);            
            trans.transform(info.getNormals()[0]);
            normals[i]= NslVecmathUtils.convertVector3f(info.getNormals()[0]);
        }
        return normals;
    }

    /**
     * Return the center of each plane of the object surface
     * @return Vector3f[]
     */
    public Vector3f[] getPlaneCenters()
    {
        GeometryInfo info=new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        Transform3D trans=new Transform3D();
        primitive.getLocalToVworld(trans);
        Vector3f[] centers=new Vector3f[getNumPlanes()];
        for(int i=0; i<centers.length; i++)
        {
            info.reset((GeometryArray)primitive.getShape(i).getGeometry());
            centers[i]=new Vector3f();
            for(int j=0; j<info.getCoordinates().length; j++)
            {
                centers[i].setX(centers[i].getX()+info.getCoordinates()[j].x);
                centers[i].setY(centers[i].getY()+info.getCoordinates()[j].y);
                centers[i].setZ(centers[i].getZ()+info.getCoordinates()[j].z);
            }
            centers[i].setX(centers[i].getX()/info.getCoordinates().length);
            centers[i].setY(centers[i].getY()/info.getCoordinates().length);
            centers[i].setZ(centers[i].getZ()/info.getCoordinates().length);
        }
        return centers;
    }

    /**
     * Return the coordinates of the vertex of each plane
     * @return Vector3f[][]
     */
    public Vector3f[][] getPlaneCoordinates()
    {
        GeometryInfo info=new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        Transform3D trans=new Transform3D();
        primitive.getLocalToVworld(trans);
        Vector3f[][] coords=new Vector3f[getNumPlanes()][4];
        for(int i=0; i<coords.length; i++)
        {
            info.reset((GeometryArray)primitive.getShape(i).getGeometry());
            for(int j=0; j<4; j++)
                coords[i][j]=new Vector3f(info.getCoordinates()[j].x,info.getCoordinates()[j].y,
                                          info.getCoordinates()[j].z);
        }
        return coords;
    }

    /**
     * Get the area of each plane
     * @return float[]
     */
    public float[] getPlaneAreas()
    {
        return null;
    }

    /**
     * Recursively add child objects to the scene
     * @param g - BranchGroup to add children to
     */
    public void addChildrenToScene(BranchGroup g)
    {
        for (NslObject3d aChildren : children)
        {
            g.addChild(aChildren.getBranchGroup());
            aChildren.addChildrenToScene(g);
        }
    }

    public void process()
    {
        if(body!=null)
        {
            lastLinearVel=linearVel;
            linearVel=body.getLinearVel();
            lastAngularVel=angularVel;
            angularVel=body.getAngularVel();
        }

        updateVisual();
    }
    /**
     * Get density
     * @return - density
     */
    public float getDensity()
    {
        return density;
    }

    /**
     * Set density
     * @param density - new density
     */
    public void setDensity(float density)
    {
        this.density = density;
    }

    public NslObject3d getRoot()
    {
        if(parent!=null)
            return parent.getRoot();
        else
            return this;
    }

    public float getMass()
    {
        if(body!=null)
            return body.getMass();
        else
            return 0;
    }

    public float getForce()
    {
        if(NslHierarchy.system.getCurrentCycle()>1)
        {
            float dt=(float)NslHierarchy.system.getDelta();
            Vector3f linearDiff=new Vector3f((linearVel.getX()-lastLinearVel.getX())/dt,
                    (linearVel.getY()-lastLinearVel.getY())/dt, (linearVel.getZ()-lastLinearVel.getZ())/dt);
            return (float)Math.sqrt(linearDiff.getX()*linearDiff.getX()+linearDiff.getY()*linearDiff.getY()+linearDiff.getZ()*linearDiff.getZ());
        }
        else
            return 0;
    }

    public boolean isContact()
    {
        return contactPoints.size()>0;
    }

    public boolean isContactHierarchical()
    {
        for (NslObject3d aChildren : children)
        {
            if (aChildren.isContactHierarchical())
                return true;
        }
        return isContact();
    }

    public void contact(Vector3f point, Vector3f normal)
    {
        this.contactPoints.add(point);
        this.contactNormals.add(normal);
    }

    public void resetContact()
    {
        this.contactPoints.clear();
        this.contactNormals.clear();
    }

    public void resetContactHierarchical()
    {
        resetContact();
        for (NslObject3d aChildren : children)
            aChildren.resetContactHierarchical();
    }

    public float[] getNormal()
    {
        float[] normal=new float[3];
        for (Vector3f contactNormal : contactNormals)
        {
            normal[0] += contactNormal.getX();
            normal[1] += contactNormal.getY();
            normal[2] += contactNormal.getZ();
        }
        return normal;
    }

    public void setTransparency(float t)
    {
        TransparencyAttributes attr=getAppearance().getTransparencyAttributes();
        if(attr==null)
        {
            attr=new TransparencyAttributes(TransparencyAttributes.BLENDED, t);
            getAppearance().setTransparencyAttributes(attr);
        }
        else
            attr.setTransparency(t);
        
        for(NslObject3d child : children)
            child.setTransparency(t);
    }

    public Vector<NslObject3d> getSelfAndDescendants()
    {
        Vector<NslObject3d> all=new Vector<NslObject3d>();
        all.add(this);
        for(NslObject3d child : children)
        {
            Vector<NslObject3d> grandChildren=child.getSelfAndDescendants();
            for(NslObject3d obj : grandChildren)
                all.add(obj);
        }
        return all;
    }
}
