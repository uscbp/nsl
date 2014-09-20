package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.World;
import org.odejava.HashSpace;
import org.odejava.GeomSphere;
import org.odejava.Mass;

import javax.media.j3d.Appearance;

import com.sun.j3d.utils.geometry.Primitive;

/**
 * Represents a generic sphere
 */
public class Sphere extends NslObject3d
{
    /**
     * Sphere constructor
     * @param name - name of the sphere
     * @param app - appearance of the sphere surface
     * @param dim - size (radius)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Sphere(String name, Appearance app, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, dim, translate, eulerAngles);
        this.density=1;
    }

    /**
     * Sphere constructor
     * @param name - name of the sphere
     * @param app - appearance of the sphere surface
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size (radius)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Sphere(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, textureFile, dim, translate, eulerAngles);
        this.density=1;
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
        // Add the cone to the visual scene
        transformGroup.addChild(primitive);
    }

    /**
     * Initialize physical component
     * @param world - world to add sphere to
     * @param collSpace - collision space used to detect collisions with other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
        // Create the body geometry
        geom=new GeomSphere(name, dim.getX());
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
            // Add body to collision space
            if(collSpace!=null)
                collSpace.addBodyGeoms( body );
        }
        else if(collSpace!=null)
            collSpace.addGeom(geom);
    }

    /**
     * Get number of planes in object
     * @return 0
     */
    public int getNumPlanes()
    {
        return 0;
    }

    /**
     * Get object shape
     * @return NslObject3d.SHAPE_SPHERE
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_SPHERE;
    }

    /**
     * Get plane areas
     * @return empty array
     */
    public float[] getPlaneAreas()
    {
        return new float[]{};
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
}
