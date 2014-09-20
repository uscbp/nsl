package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.*;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.GeometryInfo;

public class Cone extends NslObject3d
{
    /**
     * Cone constructor
     * @param name - name of the cone
     * @param app - cone surface appearance
     * @param dim - cone size (radius, height)
     * @param translate - cone location
     * @param eulerAngles - cone orientation
     */
    public Cone(String name, Appearance app, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, dim, translate, eulerAngles);
    }

    /**
     * Cone constructor
     * @param name - name of the cone
     * @param app - cone surface appeareance
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - cone size
     * @param translate - cone location
     * @param eulerAngles - cone orientation
     */
    public Cone(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate, float[] eulerAngles)
    {
        super(name, app, textureFile, dim, translate, eulerAngles);
    }

    /**
     * Initialize the visual component
     */
    protected void initVisual()
    {
        if(textureFile==null || textureFile.length()==0)
            primitive=new com.sun.j3d.utils.geometry.Cone(dim.getX(), dim.getY(), appearance);
        else
            primitive=new com.sun.j3d.utils.geometry.Cone(dim.getX(), dim.getY(), Primitive.GENERATE_TEXTURE_COORDS, appearance);
        TransformGroup tg=new TransformGroup();
        Transform3D t3d=new Transform3D();
        // Need to rotate by pi/2 around x-axis because visual cylinder is oriented upright in y-axis, while GeomCapsule
        // is oriented in z-direction
        t3d.setEuler(new Vector3d(-Math.PI/2,0,0));
        tg.setTransform(t3d);
        tg.addChild(primitive);
        transformGroup.addChild(tg);
    }

    /**
     * Initialize the physical component
     * @param world - world to add the cone to
     * @param collSpace - collision space to use to detect collisions with other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
        geom=new GeomCylinder(name, .75f*dim.getX(), dim.getY());
        if(!Float.isInfinite(density))
        {
            body = new org.odejava.Body(name, world, geom);
            Mass m=new Mass();
            m.setMass(geom, density);
            body.adjustMass(m.getMass());
            body.setLinearVel(0,0,0);
            body.setAngularVel(0, 0, 0);  // clockwise around y-axis
            if(collSpace!=null)
                collSpace.addBodyGeoms( body );
        }
        else if(collSpace!=null)
            collSpace.addGeom(geom);
    }

    /**
     * Get the number of planes
     * @return - 1 = number of planes in a cone
     */
    public int getNumPlanes()
    {
        return 1;
    }

    /**
     * Get shape
     * @return - cone shape
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_CONE;
    }

    /**
     * Get plane areas
     * @return - area of plane
     */
    public float[] getPlaneAreas()
    {
        return new float[]{(float)(Math.PI*(dim.getX()*dim.getX()))};
    }

    /**
     * Return the coordinates of the vertex of each plane
     * @return Vector3f[][]
     */
    public Vector3f[][] getPlaneCoordinates()
    {
        Vector3f[] centers=getPlaneCenters();
        Vector3f[][] coords=new Vector3f[1][4];
        coords[0][0]=new Vector3f(centers[0].getX()-.5f*dim.getX(),centers[0].getY(),centers[0].getZ()-.5f*dim.getZ());
        coords[0][1]=new Vector3f(centers[0].getX()-.5f*dim.getX(),centers[0].getY(),centers[0].getZ()+.5f*dim.getZ());
        coords[0][2]=new Vector3f(centers[0].getX()+.5f*dim.getX(),centers[0].getY(),centers[0].getZ()+.5f*dim.getZ());
        coords[0][3]=new Vector3f(centers[0].getX()+.5f*dim.getX(),centers[0].getY(),centers[0].getZ()-.5f*dim.getZ());
        return coords;
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
        Vector3f[] normals=new Vector3f[1];

        info.reset((GeometryArray)primitive.getShape(com.sun.j3d.utils.geometry.Cone.CAP).getGeometry());
        normalG.generateNormals(info);
        trans.transform(info.getNormals()[0]);
        normals[0]= NslVecmathUtils.convertVector3f(info.getNormals()[0]);

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
        Vector3f[] centers=new Vector3f[1];

        info.reset((GeometryArray)primitive.getShape(com.sun.j3d.utils.geometry.Cone.CAP).getGeometry());
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

        return centers;
    }
}                                                                                
