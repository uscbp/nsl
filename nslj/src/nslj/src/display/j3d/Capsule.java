package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.*;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3f;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import java.awt.*;

/**
 * Represents a generic capsule
 */
public class Capsule extends NslObject3d
{
    // Sphere capping the main visual cylinder
    protected com.sun.j3d.utils.geometry.Sphere sphere1;
    protected com.sun.j3d.utils.geometry.Sphere sphere2;
    // Transform to place sphere at end of limb
    protected Transform3D sphere1Transform;
    // Transform group for sphere
    protected TransformGroup sphere1TransformGroup;
    // Transform to place sphere at end of limb
    protected Transform3D sphere2Transform;
    // Transform group for sphere
    protected TransformGroup sphere2TransformGroup;

    /**
     * Capsule constructor
     * @param name - name of the capsule
     * @param app - appearance of the capsule surface
     * @param dim - size (radius, length)
     * @param translation - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Capsule(String name, Appearance app, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        super();
        this.name=name;
        this.appearance=app;
        this.dim=dim;
        this.jointType=JOINT_TYPE_NONE;
        this.density=100;

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
     * Capsule constructor
     * @param name - name of the capsule
     * @param app - appearance of the capsule surface
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size (radius, length)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Capsule(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate,
                    float[] eulerAngles)
    {
        this(name, app, dim, translate, eulerAngles);
        this.density=100;        
        this.textureFile=textureFile;
        appearance.setTexture((new TextureLoader(ClassLoader.getSystemResource("resources/"+textureFile),
                                                 new Container())).getTexture());
    }

    /**
     * Initialize the visual component
     */
    protected void initVisual()
    {
        primitive=new Cylinder(dim.getX(),dim.getY(),appearance);
        // Translate to the end of the cylinder to connect with children
        Transform3D t=new Transform3D();
        // Need to rotate by pi/2 around x-axis because visual cylinder is oriented upright in y-axis, while GeomCapsule
        // is oriented in z-direction
        t.setEuler(new Vector3d(-Math.PI/2,0,0));
        TransformGroup tg=new TransformGroup(t);
        tg.setTransform(t);
        tg.addChild(primitive);
        transformGroup.addChild(tg);

        // Put a sphere on the end of the cylinder
        sphere1Transform=new Transform3D();
        // Translate to the end of the cylinder
        sphere1Transform.set(new javax.vecmath.Vector3d(0.0, -.5*dim.getY(), 0.0));
        sphere1TransformGroup=new TransformGroup(sphere1Transform);
        sphere1=new Sphere(dim.getX(), appearance);
        sphere1TransformGroup.addChild(sphere1);
        tg.addChild(sphere1TransformGroup);

        // Put a sphere on the end of the cylinder
        sphere2Transform=new Transform3D();
        // Translate to the end of the cylinder
        sphere2Transform.set(new javax.vecmath.Vector3d(0.0, .5*dim.getY(), 0.0));
        sphere2TransformGroup=new TransformGroup(sphere2Transform);
        sphere2=new Sphere(dim.getX(), appearance);
        sphere2TransformGroup.addChild(sphere2);
        tg.addChild(sphere2TransformGroup);
    }

    /**
     * Initialize the physical component
     * @param world - world to add the cylinder to
     * @param collSpace - collision space to use to detect collisions with other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
        // Create the body geometry - collision detection is not good for cylinders, so we use a box of
        // approximately the same size as the cylinder
        geom=new GeomCapsule(name, dim.getX(), dim.getY());
        if(!Float.isInfinite(density))
        {
            // Create the body in the given world with the geometry
            body = new org.odejava.Body(name, world, geom);
            // Adjust the base based on the density and volume
            Mass m=new Mass();
            m.setMass(geom, density);
            body.adjustMass(m.getMass());
            body.setLinearVel(0,0,0);
            body.setAngularVel(0, 0, 0);
            // Add the body to the collision space
            if(collSpace!=null)
                collSpace.addBodyGeoms( body );
        }
        else if(collSpace!=null)
            collSpace.add(geom);
    }

    /**
     * Get the number of planes
     * @return - 0 = number of planes in a capsule
     */
    public int getNumPlanes()
    {
        return 0;
    }

    /**
     * Get the shape of the object
     * @return NslObject3d.SHAPE_CAPSULE
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_CAPSULE;
    }

    /**
     * Return the coordinates of the vertex of each plane
     * @return Vector3f[][]
     */
    public Vector3f[][] getPlaneCoordinates()
    {
        return new Vector3f[0][0];
    }

    /**
     * Get the area of each plane
     * @return - array of plane areas
     */
    public float[] getPlaneAreas()
    {
        return new float[0];
    }

    /**
     * Get object shape surface normal vectors
     * @return Vector3f[]
     */
    public Vector3f[] getNormals()
    {
        return new Vector3f[0];
    }

    /**
     * Return the center of each plane of the object surface
     * @return Vector3f[]
     */
    public Vector3f[] getPlaneCenters()
    {
        return new Vector3f[0];
    }

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
}
