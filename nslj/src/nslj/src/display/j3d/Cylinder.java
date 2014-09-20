package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.*;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.GeometryArray;

import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

import java.awt.*;

/**
 * Represents a generic cylinder
 */
public class Cylinder extends NslObject3d
{
    /**
     * Cylinder constructor
     * @param name - name of the cylinder
     * @param app - appearance of the cylinder surface
     * @param dim - size (radius, length)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Cylinder(String name, Appearance app, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, dim, translate, eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor
     * @param name - name of the cylinder
     * @param app - appearance of the cylinder surface
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size (radius, length)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Cylinder(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate,
                    float[] eulerAngles)
    {
        super(name, app, textureFile, dim, translate, eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor with joint
     * @param name - name of object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public Cylinder(String name, int jointType, Vector3f[] jointAxes, Appearance app, Vector3f dim,
                       Vector3f translation, float[] eulerAngles)
    {
        super(name, jointType, jointAxes, app, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor with joint and texture
     * @param name - name of object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public Cylinder(String name, int jointType, Vector3f[] jointAxes, Appearance app, String textureFile,
                       Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, jointType, jointAxes, app, textureFile, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor with parent object
     * @param name - name of the object
     * @param parent - parent object
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public Cylinder(String name, NslObject3d parent, Appearance app, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        super(name,parent,app,dim,translation,eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor with texture and parent object
     * @param name - name of the object
     * @param parent - parent object
     * @param app - visual appearance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public Cylinder(String name, NslObject3d parent, Appearance app, String textureFile, Vector3f dim,
                       Vector3f translation, float[] eulerAngles)
    {
        super(name, parent, app, textureFile, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor with parent and joint
     * @param name - name of the object
     * @param parent - parent object
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position (relative to parent)
     * @param eulerAngles - local orienation (relative to parent)
     */
    public Cylinder(String name, NslObject3d parent, int jointType, Vector3f[] jointAxes, Appearance app,
                       Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, parent, jointType, jointAxes, app, dim, translation, eulerAngles);
        this.density=1;
    }

    /**
     * Cylinder constructor with parent, joint, and texture
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
    public Cylinder(String name, NslObject3d parent, int jointType, Vector3f[] jointAxes, Appearance app,
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
        // If there is no texture file - create the cylinder using the given dimension and appearance        
        if(textureFile==null || textureFile.length()==0)
            primitive=new com.sun.j3d.utils.geometry.Cylinder(dim.getX(), dim.getY(), appearance);
        // If there is a texture file - create the cylinder with generated coordinates to attach the texture
        else
            primitive=new com.sun.j3d.utils.geometry.Cylinder(dim.getX(), dim.getY(),
                                                              Primitive.GENERATE_TEXTURE_COORDS, appearance);
        // Add the cylinder to the visual scene
        transformGroup.addChild(primitive);
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
        geom=new GeomBox(name, dim.getX()*2, dim.getY(),dim.getX()*2);
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
     * @return - 2 = number of planes in a cylinder
     */
    public int getNumPlanes()
    {
        return 2;
    }

    /**
     * Get the shape of the object
     * @return NslObject3d.SHAPE_CYLINDER
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_CYLINDER;
    }

    /**
     * Return the coordinates of the vertex of each plane
     * @return Vector3f[][]
     */
    public Vector3f[][] getPlaneCoordinates()
    {
        Vector3f[] centers=getPlaneCenters();
        Vector3f[][] coords=new Vector3f[2][4];
        coords[0][0]=new Vector3f(centers[0].getX()-.5f*dim.getX(),centers[0].getY(),centers[0].getZ()-.5f*dim.getZ());
        coords[0][1]=new Vector3f(centers[0].getX()-.5f*dim.getX(),centers[0].getY(),centers[0].getZ()+.5f*dim.getZ());
        coords[0][2]=new Vector3f(centers[0].getX()+.5f*dim.getX(),centers[0].getY(),centers[0].getZ()+.5f*dim.getZ());
        coords[0][3]=new Vector3f(centers[0].getX()+.5f*dim.getX(),centers[0].getY(),centers[0].getZ()-.5f*dim.getZ());
        coords[1][0]=new Vector3f(centers[1].getX()-.5f*dim.getX(),centers[1].getY(),centers[1].getZ()-.5f*dim.getZ());
        coords[1][1]=new Vector3f(centers[1].getX()-.5f*dim.getX(),centers[1].getY(),centers[1].getZ()+.5f*dim.getZ());
        coords[1][2]=new Vector3f(centers[1].getX()+.5f*dim.getX(),centers[1].getY(),centers[1].getZ()+.5f*dim.getZ());
        coords[1][3]=new Vector3f(centers[1].getX()+.5f*dim.getX(),centers[1].getY(),centers[1].getZ()-.5f*dim.getZ());
        return coords;
    }

    /**
     * Get the area of each plane
     * @return - array of plane areas
     */
    public float[] getPlaneAreas()
    {
        return new float[]{(float)Math.PI*dim.getX()*dim.getX(),(float)Math.PI*dim.getX()*dim.getX()};
    }

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
        Vector3f[] normals=new Vector3f[2];

        info.reset((GeometryArray)primitive.getShape(com.sun.j3d.utils.geometry.Cylinder.TOP).getGeometry());
        normalG.generateNormals(info);
        trans.transform(info.getNormals()[0]);
        normals[0]= NslVecmathUtils.convertVector3f(info.getNormals()[0]);

        info.reset((GeometryArray)primitive.getShape(com.sun.j3d.utils.geometry.Cylinder.BOTTOM).getGeometry());
        normalG.generateNormals(info);
        trans.transform(info.getNormals()[0]);
        normals[1]= NslVecmathUtils.convertVector3f(info.getNormals()[0]);

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
        Vector3f[] centers=new Vector3f[2];

        info.reset((GeometryArray)primitive.getShape(com.sun.j3d.utils.geometry.Cylinder.TOP).getGeometry());
        centers[0]=new Vector3f();
        for(int j=0; j<info.getCoordinates().length; j++)
        {
            centers[0].setX(centers[0].getX()+info.getCoordinates()[j].x);
            centers[0].setY(centers[0].getY()+info.getCoordinates()[j].y);
            centers[0].setZ(centers[0].getZ()+info.getCoordinates()[j].z);
        }
        centers[0].setX(centers[0].getX()/info.getCoordinates().length);
        centers[0].setY(centers[0].getY()/info.getCoordinates().length);
        centers[0].setZ(centers[0].getZ()/info.getCoordinates().length);

        info.reset((GeometryArray)primitive.getShape(com.sun.j3d.utils.geometry.Cylinder.BOTTOM).getGeometry());
        centers[1]=new Vector3f();
        for(int j=0; j<info.getCoordinates().length; j++)
        {
            centers[1].setX(centers[1].getX()+info.getCoordinates()[j].x);
            centers[1].setY(centers[1].getY()+info.getCoordinates()[j].y);
            centers[1].setZ(centers[1].getZ()+info.getCoordinates()[j].z);
        }
        centers[1].setX(centers[1].getX()/info.getCoordinates().length);
        centers[1].setY(centers[1].getY()/info.getCoordinates().length);
        centers[1].setZ(centers[1].getZ()/info.getCoordinates().length);
        return centers;
    }
}
