package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.*;
import org.odejava.ode.OdeConstants;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3f;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import java.awt.*;

public class Hammer extends Capsule
{
    private Box head;


    /**
     * Hammer constructor
     * @param name - name of the capsule
     * @param app - appearance of the capsule surface
     * @param dim - size (radius, length)
     * @param translation - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Hammer(String name, Appearance app, Vector3f dim, Vector3f translation,
                       float[] eulerAngles)
    {
        super(name, app, dim, translation, eulerAngles);
        Appearance appear = new Appearance();
	    // Material
	    // ambient color = gray
	    javax.vecmath.Color3f aColor=new javax.vecmath.Color3f(.3f, .3f, .3f);
	    // diffuse color = black
	    javax.vecmath.Color3f dColor=new javax.vecmath.Color3f(0,0,0);
	    // emissive color = brown
	    javax.vecmath.Color3f eColor=new javax.vecmath.Color3f(.2f, .2f, .2f);
        // specular color = brown
	    javax.vecmath.Color3f sColor=new javax.vecmath.Color3f(eColor);
        TransparencyAttributes attr=new TransparencyAttributes();
        attr.setTransparencyMode(TransparencyAttributes.BLENDED);
        attr.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        attr.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(attr);
        Material material = new Material(aColor, dColor, eColor, sColor, 100);
        material.setLightingEnable(true);
        appear.setMaterial(material);
        head=new Box(name+"_head", this, BodySegment.JOINT_TYPE_HINGE,
                new Vector3f[]{new Vector3f(1,0,0)}, appear,
                new Vector3f(.1f,.3f,.1f), new Vector3f(.03f, 0, .2f), new float[]{0,0,(float)-Math.PI/2});
    }

    /**
     * Hammer constructor
     * @param name - name of the capsule
     * @param app - appearance of the capsule surface
     * @param textureFile - file containing the image to use as a surface texture
     * @param dim - size (radius, length)
     * @param translate - location
     * @param eulerAngles - orientation in 3 dimensions
     */
    public Hammer(String name, Appearance app, String textureFile, Vector3f dim, Vector3f translate,
                    float[] eulerAngles)
    {
        super(name, app, textureFile, dim, translate, eulerAngles);
        Appearance appear = new Appearance();
	    // Material
	    // ambient color = gray
	    javax.vecmath.Color3f aColor=new javax.vecmath.Color3f(.3f, .3f, .3f);
	    // diffuse color = black
	    javax.vecmath.Color3f dColor=new javax.vecmath.Color3f(0,0,0);
	    // emissive color = brown
	    javax.vecmath.Color3f eColor=new javax.vecmath.Color3f(.2f, .2f, .2f);
        // specular color = brown
	    javax.vecmath.Color3f sColor=new javax.vecmath.Color3f(eColor);
        TransparencyAttributes attr=new TransparencyAttributes();
        attr.setTransparencyMode(TransparencyAttributes.BLENDED);
        attr.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        attr.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(attr);
        Material material = new Material(aColor, dColor, eColor, sColor, 100);
        material.setLightingEnable(true);
        appear.setMaterial(material);
        head=new Box(name+"_head", this, BodySegment.JOINT_TYPE_HINGE,
                new Vector3f[]{new Vector3f(1,0,0)}, appear,
                new Vector3f(.1f,.3f,.1f), new Vector3f(.03f, 0, .2f), new float[]{0,0,(float)-Math.PI/2});
    }

    public void init(World world, HashSpace collSpace)
    {
        super.init(world, collSpace);
        head.joint.setParam(OdeConstants.dParamHiStop,0);
        head.joint.setParam(OdeConstants.dParamLoStop,0);
    }

    /**
     * Get the object size
     * @return Vector3f
     */
    public Vector3f getDim()
    {
        return dim;
    }
}
