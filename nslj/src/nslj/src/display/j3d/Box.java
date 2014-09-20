package nslj.src.display.j3d;

import javax.media.j3d.Appearance;

import org.odejava.*;
import org.odejava.Body;
import org.openmali.vecmath2.Vector3f;

import com.sun.j3d.utils.geometry.Primitive;

/**
 * Represents a generic box
 */
public class Box extends NslObject3d
{
    /**
     * Box constructor
     * @param name - name of the cylinder
     * @param app - appearance of the cylinder surface
     * @param dim - size (radius, length)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Box(String name, Appearance app, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, dim, translate, eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor
     * @param name - name of the cylinder
     * @param app - appearance of the cylinder surface
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size (radius, length)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Box(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate,
                    float[] eulerAngles)
    {
        super(name, app, textureFile, dim, translate, eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor with joint
     * @param name - name of object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public Box(String name, int jointType, Vector3f[] jointAxes, Appearance app, Vector3f dim,
                       Vector3f translation, float[] eulerAngles)
    {
        super(name, jointType, jointAxes, app, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor with joint and texture
     * @param name - name of object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public Box(String name, int jointType, Vector3f[] jointAxes, Appearance app, String textureFile,
                       Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, jointType, jointAxes, app, textureFile, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor with parent object
     * @param name - name of the object
     * @param parent - parent object
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public Box(String name, NslObject3d parent, Appearance app, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        super(name,parent,app,dim,translation,eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor with texture and parent object
     * @param name - name of the object
     * @param parent - parent object
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public Box(String name, NslObject3d parent, Appearance app, String textureFile, Vector3f dim,
                       Vector3f translation, float[] eulerAngles)
    {
        super(name, parent, app, textureFile, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor with parent and joint
     * @param name - name of the object
     * @param parent - parent object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public Box(String name, NslObject3d parent, int jointType, Vector3f[] jointAxes, Appearance app,
                       Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, parent, jointType, jointAxes, app, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Box constructor with parent, joint, and texture
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
    public Box(String name, NslObject3d parent, int jointType, Vector3f[] jointAxes, Appearance app,
                       String textureFile, Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, parent, jointType, jointAxes, app, textureFile, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Initialize the visual component
     */
    protected void initVisual()
    {
        // If there is no texture file - create the box using the given dimension and appearance
        // Need to half dimensions here since J3D box dimensions are actually distance to center, not edge length
        if(textureFile==null || textureFile.length()==0)
            primitive=new com.sun.j3d.utils.geometry.Box(dim.getX()/2, dim.getY()/2, dim.getZ()/2, appearance);
        // If there is a texture file - create the box with generated coordinates to attach the texture
        else
            primitive=new com.sun.j3d.utils.geometry.Box(dim.getX()/2, dim.getY()/2, dim.getZ()/2,
                                                         Primitive.GENERATE_TEXTURE_COORDS, appearance);
        // Add the box shape to the visual scene
        transformGroup.addChild(primitive);
    }

    /**
     * Initialize the physical component
     * @param world - the world to add the box to
     * @param collSpace - the collision space to use to detect collisions with other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
        // Create the body geometry
        geom=new GeomBox(name, dim.getX(), dim.getY(), dim.getZ());
        if(!Float.isInfinite(density))
        {
            // Create the body in the given world with the geometry
            body = new Body(name, world, geom);
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
            collSpace.addGeom(geom);
    }

    /**
     * Get the number of planes in the box
     * @return - 6=number of planes in a box
     */
    public int getNumPlanes()
    {
        return 6;
    }

    /**
     * Get the object shape
     * @return NslObject3d.SHAPE_RECT
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_RECT;
    }

    /**
     * Get the area of each plane
     * @return - array of the area of each plane
     */
    public float[] getPlaneAreas()
    {
        return new float[]{dim.getX()*dim.getY(),dim.getX()*dim.getY(),dim.getY()*dim.getZ(),dim.getY()*dim.getZ(),
                dim.getX()*dim.getZ(),dim.getX()*dim.getZ()};
    }
}
