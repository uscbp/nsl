package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.World;
import org.odejava.HashSpace;

import javax.media.j3d.Appearance;

import com.sun.j3d.utils.geometry.Primitive;

/**
 * Represents a visual target with no physical component
 */
public class VisualTarget extends NslObject3d
{
    /**
     * VisualTarget constructor
     * @param name - name of visual target
     * @param app - visual appearance
     * @param dim - size
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public VisualTarget(String name, Appearance app, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, dim, translate, eulerAngles);
    }

    /**
     * Initialize visual component
     */
    protected void initVisual()
    {
        // If there is no texture file - create the sphere using the given dimension and appearance
        if(textureFile==null || textureFile.length()==0)
            primitive=new com.sun.j3d.utils.geometry.Sphere(dim.getX(), appearance);
        // If there is a texture file - create the sphere with generated coordinates to attach the texture
        else
            primitive=new com.sun.j3d.utils.geometry.Sphere(dim.getX(), Primitive.GENERATE_TEXTURE_COORDS, appearance);
        // Add visual target to the visual scene
        transformGroup.addChild(primitive);
    }

    /**
     * Initialize physical component - empty
     * @param world - world to add visual target to
     * @param collSpace - collision space to use to detect collisions with other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
    }

    /**
     * Get number of planes in visual target
     * @return 0
     */
    public int getNumPlanes()
    {
        return 0;
    }

    /**
     * Get shape of visual target
     * @return NslObject3d.SHAPE_SPHERE
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_SPHERE;
    }

    /**
     * Get area of planes in object
     * @return empty array
     */
    public float[] getPlaneAreas()
    {
        return new float[]{};
    }
}
