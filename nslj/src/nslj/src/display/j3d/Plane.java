package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.*;

import javax.media.j3d.Appearance;

import com.sun.j3d.utils.geometry.Primitive;

/**
 * Represents a generic plane
 */
public class Plane extends NslObject3d
{
    /**
     * Plane constructor
     * @param name - name of the plane
     * @param app - appearance of the plane surface
     * @param dim - size
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions - this is not actually used, plane will have (0,1,0) normal
     * vector
     * TODO - create normal vector for plane based on given euler angles
     */
    public Plane(String name, Appearance app, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, dim, translate, eulerAngles);
        this.density=5;
    }

    /**
     * Plane constructor
     * @param name - name of the plane
     * @param app - appearance of the plane surface
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions - this is not actually used, plane will have (0,1,0) normal
     * vector
     * TODO - create normal vector for plane based on given euler angles
     */
    public Plane(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, textureFile, dim, translate, eulerAngles);
        this.density=5;
    }

    /**
     * Initialize the visual component
     */
    protected void initVisual()
    {
        // If there is no texture file - create the plane using the given dimension and appearance
        if(textureFile==null || textureFile.length()==0)
            primitive=new com.sun.j3d.utils.geometry.Box(dim.getX(), 0, dim.getZ(), appearance);
        // If there is a texture file - create the cone with generated coordinates to attach the texture
        else
            primitive=new com.sun.j3d.utils.geometry.Box(dim.getX(), 0, dim.getZ(), Primitive.GENERATE_TEXTURE_COORDS,
                                                         appearance);
        // Add the plane to the visual scene
        transformGroup.addChild(primitive);
    }

    /**
     * Initialize the physical component
     * @param world - world to add the plane to
     * @param collSpace - collision space to use to detect collisions with other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
        // Add a new plane geometry to the collision space
        if(collSpace!=null)
            collSpace.addGeom(new GeomPlane(name,0,1,0,0));
    }

    /**
     * Get the number of planes
     * @return - 1=number of planes in a plane
     */
    public int getNumPlanes()
    {
        return 1;
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
     * Get the area of the plane
     * @return - plane area
     */
    public float[] getPlaneAreas()
    {
        return new float[]{dim.getX()*dim.getZ()};
    }
}

