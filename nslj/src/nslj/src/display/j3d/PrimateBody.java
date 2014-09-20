package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import org.odejava.World;
import org.odejava.HashSpace;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4f;
import javax.vecmath.Matrix3f;

/**
 * A primate arm and hand
 */
public class PrimateBody extends Body
{
    // IDs for each body segment
    public static int UPPER_ARM=0;
    public static int LOWER_ARM=1;
    public static int UPPER_WRIST=2;
    public static int LOWER_WRIST=3;
    public static int THUMB_1=4;
    public static int THUMB_2=5;
    public static int THUMB_3=6;
    public static int INDEX_1=7;
    public static int INDEX_2=8;
    public static int INDEX_3=9;
    public static int INDEX_4=10;
    public static int MIDDLE_1=11;
    public static int MIDDLE_2=12;
    public static int MIDDLE_3=13;
    public static int MIDDLE_4=14;
    public static int RING_1=15;
    public static int RING_2=16;
    public static int RING_3=17;
    public static int RING_4=18;
    public static int PINKY_1=19;
    public static int PINKY_2=20;
    public static int PINKY_3=21;
    public static int PINKY_4=22;

    // Jacobian matrix
    Matrix J;
    // Jacobian matrix pseudo-inverse
    Matrix Jinv;
    float[][] Jinv_array;

    /**
     * PrimateBody constructor
     * @param name - body name
     * @param app = visual appearance
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public PrimateBody(String name, Appearance app, Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, app, dim, translation, eulerAngles);
    }

    /**
     * Initialize visual component
     */
    protected void initVisual()
    {
        J=new Matrix(3,4);
        Jinv=new Matrix(4,3);
        Jinv_array=new float[4][3];
    }

    /**
     * Initialize physical component
     * @param world - world to add body to
     * @param collSpace - collision space to add body to
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {        
    }

    /**
     * Create body segments
     */
    protected void createBody()
    {
        // Initialize body segment array
        bodySegments=new BodySegment[24];

        // Get body position
        javax.vecmath.Vector3f translation=new javax.vecmath.Vector3f();
        localTransform.get(translation);

        javax.vecmath.Matrix3f rotation=new javax.vecmath.Matrix3f();
        localTransform.get(rotation);

        float[] initAngles=NslVecmathUtils.computeEulerAngles(rotation);

        // Create upper arm
        bodySegments[UPPER_ARM]=new BodySegment(name+"-upperArm", BodySegment.JOINT_TYPE_BALL,
                                                new Vector3f[]{new Vector3f(1,0,0),new Vector3f(0,1,0),
                                                        new Vector3f(0,0,1)},
                                                appearance, new Vector3f(0.175f, 1.1f, 0.175f),
                                                NslVecmathUtils.convertVector3f(translation),
                                                new float[]{initAngles[0]+(float)-Math.PI/2,initAngles[1],initAngles[2]},
                                                new float[]{(float)(-Math.PI/2),(float)(-89*Math.PI/180),(float)(-Math.PI/2)},
                                                new float[]{(float)(Math.PI/2),(float)(89*Math.PI/180),(float)(Math.PI/2)});
        children.add(bodySegments[UPPER_ARM]);

        // Create lower arm
        bodySegments[LOWER_ARM]=new BodySegment(name+"-lowerArm", bodySegments[UPPER_ARM], BodySegment.JOINT_TYPE_HINGE,
                                                new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                                new Vector3f(0.15f, 1.25f, 0.15f),
                                                new Vector3f(0, -.5f*1.25f, .5f*bodySegments[UPPER_ARM].dim.getY()),
                                                new float[]{(float)-Math.PI/2,0,0},
                                                new float[]{(float)(-90*Math.PI/180)},
                                                new float[]{(float)(80*Math.PI/180)});

        // Create upper wrist segment
        bodySegments[UPPER_WRIST]=new BodySegment(name+"-upperWrist", bodySegments[LOWER_ARM],
                                                  BodySegment.JOINT_TYPE_UNIVERSAL,
                                                  new Vector3f[]{new Vector3f(1,0,0), new Vector3f(0,1,0)},
                                                  appearance, new Vector3f(0.14f, 0.1f, 0.14f),
                                                  new Vector3f(0, 0, .5f*bodySegments[LOWER_ARM].dim.getY()+.5f*.1f),
                                                  new float[]{0,0,0},
                                                  new float[]{(float)(0), (float)(-50*Math.PI/180)},
                                                  new float[]{(float)(0), (float)(170*Math.PI/180)});
        bodySegments[UPPER_WRIST].rotate=true;

        // Create lower wrist segment
        bodySegments[LOWER_WRIST]=new BodySegment(name+"-lowerWrist", bodySegments[UPPER_WRIST],
                                                  BodySegment.JOINT_TYPE_UNIVERSAL,
                                                  new Vector3f[]{new Vector3f(1,0,0), new Vector3f(0,0,1)},
                                                  appearance, new Vector3f(0.14f, 0.1f, 0.14f),
                                                  new Vector3f(0, 0, .5f*bodySegments[UPPER_WRIST].dim.getY()+.5f*.1f),
                                                  new float[]{0,0,0},
                                                  new float[]{(float)(-60*Math.PI/180), (float)(-30*Math.PI/180)},
                                                  new float[]{(float)(90*Math.PI/180), (float)(20*Math.PI/180)});
        bodySegments[LOWER_WRIST].rotate=true;
        bodySegments[LOWER_WRIST].allowCollision(name+"-lowerArm");

        // Create first thumb segment
        bodySegments[THUMB_1]=new BodySegment(name+"-thumb1", bodySegments[LOWER_WRIST], BodySegment.JOINT_TYPE_UNIVERSAL,
                                              new Vector3f[]{new Vector3f(1,0,0), new Vector3f(0,0,1)}, appearance,
                                              new Vector3f(0.075f, 0.2f, 0.075f),
                                              new Vector3f(-.12f, .03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.2f),
                                              new float[]{0,(float)(60*Math.PI/180),0},
                                              new float[]{(float)(-1*Math.PI/180), (float)(-70*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2), (float)(1*Math.PI/180)});
        bodySegments[THUMB_1].rotate=true;
        bodySegments[THUMB_1].allowCollision(name+"-upperWrist");
        
        // Create second thumb segment
        bodySegments[THUMB_2]=new BodySegment(name+"-thumb2", bodySegments[THUMB_1], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.05f, 0.15f, 0.05f),
                                              new Vector3f(0, 0, .5f*bodySegments[THUMB_1].getDim().getY()+.5f*.15f),
                                              new float[]{0,(float)(10*Math.PI/180),(float)(Math.PI/2)},
                                              new float[]{(float)(-1*Math.PI/180)}, new float[]{(float)(Math.PI/2)});

        // Create first index finger segment
        bodySegments[INDEX_1]=new BodySegment(name+"-index1", bodySegments[LOWER_WRIST], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)},
                                              appearance, new Vector3f(0.0575f, 0.3f, 0.0575f),
                                              new Vector3f(-.05f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.3f),
                                              new float[]{0,0.12f,0}, new float[]{0}, new float[]{0});
        bodySegments[INDEX_1].allowCollision(name+"-lowerArm");
        bodySegments[INDEX_1].allowCollision(name+"-lowerWrist");
        bodySegments[INDEX_1].allowCollision(name+"-upperWrist");
        bodySegments[INDEX_1].allowCollision(name+"-thumb1");

        // Create second index finger segment
        bodySegments[INDEX_2]=new BodySegment(name+"-index2", bodySegments[INDEX_1], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.048f, 0.15f, 0.048f),
                                              new Vector3f(0, 0, .5f*bodySegments[INDEX_1].getDim().getY()+.5f*.15f),
                                              new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2)});

        // Create third index finger segment
        bodySegments[INDEX_3]=new BodySegment(name+"-index3", bodySegments[INDEX_2], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.045f, 0.1f, 0.045f),
                                              new Vector3f(0, 0, .5f*bodySegments[INDEX_2].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2)});

        // Create fourth index finger segment
        bodySegments[INDEX_4]=new BodySegment(name+"-index4", bodySegments[INDEX_3], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.04f, 0.1f, 0.04f),
                                              new Vector3f(0, 0, .5f*bodySegments[INDEX_3].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2)});

        // Create first middle finger segment
        bodySegments[MIDDLE_1]=new BodySegment(name+"-middle1", bodySegments[LOWER_WRIST], BodySegment.JOINT_TYPE_HINGE,
                                               new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                               new Vector3f(0.0575f, 0.33f, 0.0575f),
                                               new Vector3f(-0.025f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.33f),
                                               new float[]{0,0,0}, new float[]{0}, new float[]{0});
        bodySegments[MIDDLE_1].allowCollision(name+"-upperWrist");
        bodySegments[MIDDLE_1].allowCollision(name+"-lowerArm");
        bodySegments[MIDDLE_1].allowCollision(name+"-index1");
        bodySegments[MIDDLE_1].allowCollision(name+"-index2");
        bodySegments[MIDDLE_1].allowCollision(name+"-thumb1");

        // Create second middle finger segment
        bodySegments[MIDDLE_2]=new BodySegment(name+"-middle2", bodySegments[MIDDLE_1], BodySegment.JOINT_TYPE_HINGE,
                                               new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                               new Vector3f(0.048f, 0.15f, 0.048f),
                                               new Vector3f(0, 0, .5f*bodySegments[MIDDLE_1].getDim().getY()+.5f*.15f),
                                               new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                               new float[]{(float)(Math.PI/2)});
        bodySegments[MIDDLE_2].allowCollision(name+"-index1");
        bodySegments[MIDDLE_2].allowCollision(name+"-index2");
        bodySegments[MIDDLE_2].allowCollision(name+"-index3");

        // Create third middle finger segment
        bodySegments[MIDDLE_3]=new BodySegment(name+"-middle3", bodySegments[MIDDLE_2], BodySegment.JOINT_TYPE_HINGE,
                                               new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                               new Vector3f(0.045f, 0.1f, 0.045f),
                                               new Vector3f(0, 0, .5f*bodySegments[MIDDLE_2].getDim().getY()+.5f*.1f),
                                               new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                               new float[]{(float)(Math.PI/2)});
        bodySegments[MIDDLE_3].allowCollision(name+"-index2");
        bodySegments[MIDDLE_3].allowCollision(name+"-index3");
        bodySegments[MIDDLE_3].allowCollision(name+"-index4");
        bodySegments[MIDDLE_3].allowCollision(name+"-ring2");

        // Create fourth middle finger segment
        bodySegments[MIDDLE_4]=new BodySegment(name+"-middle4", bodySegments[MIDDLE_3], BodySegment.JOINT_TYPE_HINGE,
                                               new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                               new Vector3f(0.04f, 0.1f, 0.04f),
                                               new Vector3f(0, 0, .5f*bodySegments[MIDDLE_3].getDim().getY()+.5f*.1f),
                                               new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                               new float[]{(float)(Math.PI/2)});
        bodySegments[MIDDLE_4].allowCollision(name+"-index3");
        bodySegments[MIDDLE_4].allowCollision(name+"-index4");

        // Create first ring finger segment
        bodySegments[RING_1]=new BodySegment(name+"-ring1", bodySegments[LOWER_WRIST], BodySegment.JOINT_TYPE_HINGE,
                                             new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                             new Vector3f(0.0575f, 0.32f, 0.0575f),
                                             new Vector3f(0.025f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.32f),
                                             new float[]{0,-0.05f,0}, new float[]{0}, new float[]{0});
        bodySegments[RING_1].allowCollision(name+"-upperWrist");
        bodySegments[RING_1].allowCollision(name+"-lowerArm");
        bodySegments[RING_1].allowCollision(name+"-index1");
        bodySegments[RING_1].allowCollision(name+"-index2");
        bodySegments[RING_1].allowCollision(name+"-middle1");
        bodySegments[RING_1].allowCollision(name+"-middle2");
        bodySegments[RING_1].allowCollision(name+"-thumb1");

        // Create second ring finger segment
        bodySegments[RING_2]=new BodySegment(name+"-ring2", bodySegments[RING_1], BodySegment.JOINT_TYPE_HINGE,
                                             new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                             new Vector3f(0.04f, 0.15f, 0.04f),
                                             new Vector3f(0, 0, .5f*bodySegments[RING_1].getDim().getY()+.5f*.15f),
                                             new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                             new float[]{(float)(Math.PI/2)});
        bodySegments[RING_2].allowCollision(name+"-middle1");
        bodySegments[RING_2].allowCollision(name+"-middle2");
        bodySegments[RING_2].allowCollision(name+"-pinky1");
        bodySegments[RING_2].allowCollision(name+"-pinky2");
        bodySegments[RING_2].allowCollision(name+"-pinky3");

        // Create third ring finger segment
        bodySegments[RING_3]=new BodySegment(name+"-ring3", bodySegments[RING_2], BodySegment.JOINT_TYPE_HINGE,
                                             new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                             new Vector3f(0.035f, 0.1f, 0.035f),
                                             new Vector3f(0, 0, .5f*bodySegments[RING_2].getDim().getY()+.5f*.1f),
                                             new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                             new float[]{(float)(Math.PI/2)});
        bodySegments[RING_3].allowCollision(name+"-middle2");
        bodySegments[RING_3].allowCollision(name+"-middle3");
        bodySegments[RING_3].allowCollision(name+"-middle4");
        bodySegments[RING_3].allowCollision(name+"-pinky3");

        // Create fourth ring finger segment
        bodySegments[RING_4]=new BodySegment(name+"-ring4", bodySegments[RING_3], BodySegment.JOINT_TYPE_HINGE,
                                             new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                             new Vector3f(0.03f, 0.1f, 0.03f),
                                             new Vector3f(0, 0, .5f*bodySegments[RING_3].getDim().getY()+.5f*.1f),
                                             new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                             new float[]{(float)(Math.PI/2)});
        bodySegments[RING_4].allowCollision(name+"-middle3");

        // Create first pinky finger segment
        bodySegments[PINKY_1]=new BodySegment(name+"-pinky1", bodySegments[LOWER_WRIST], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.0575f, 0.28f, 0.0575f),
                                              new Vector3f(0.075f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.28f),
                                              new float[]{0,-0.1f,0}, new float[]{0}, new float[]{0});
        bodySegments[PINKY_1].allowCollision(name+"-upperWrist");
        bodySegments[PINKY_1].allowCollision(name+"-lowerArm");
        bodySegments[PINKY_1].allowCollision(name+"-index1");
        bodySegments[PINKY_1].allowCollision(name+"-middle1");
        bodySegments[PINKY_1].allowCollision(name+"-ring1");
        bodySegments[PINKY_1].allowCollision(name+"-thumb1");

        // Create second pinky finger segment
        bodySegments[PINKY_2]=new BodySegment(name+"-pinky2", bodySegments[PINKY_1], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.04f, 0.12f, 0.04f),
                                              new Vector3f(0,0,.5f*bodySegments[PINKY_1].getDim().getY()+.5f*.12f),
                                              new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2)});
        bodySegments[PINKY_2].allowCollision(name+"-ring1");

        // Create third pinky finger segment
        bodySegments[PINKY_3]=new BodySegment(name+"-pinky3", bodySegments[PINKY_2], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.035f, 0.1f, 0.035f),
                                              new Vector3f(0,0,.5f*bodySegments[PINKY_2].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2)});
        
        // Create fourth pinky finger segment
        bodySegments[PINKY_4]=new BodySegment(name+"-pinky4", bodySegments[PINKY_3], BodySegment.JOINT_TYPE_HINGE,
                                              new Vector3f[]{new Vector3f(1,0,0)}, appearance,
                                              new Vector3f(0.03f, 0.1f, 0.03f),
                                              new Vector3f(0,0,.5f*bodySegments[PINKY_3].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0}, new float[]{(float)(-1*Math.PI/180)},
                                              new float[]{(float)(Math.PI/2)});
        bodySegments[PINKY_4].allowCollision(name+"-ring3");
    }

    /**
     * Setup body and body segment controllers
     * Other PID parameters can be used, but these seem to work with a 1ms timestep, other timesteps may require
     * differetn parameter values
     */
    public void setupControllers()
    {
        // Initialize IK controller
        controller=new IKController(this);

        if(bodySegments[UPPER_ARM]!=null)
        {
            bodySegments[UPPER_ARM].controllers[0]=new PDController(bodySegments[UPPER_ARM], 0, 100000f, 1000000f);
            bodySegments[UPPER_ARM].controllers[1]=new PDController(bodySegments[UPPER_ARM], 1, 30000f, 1000000f);
            bodySegments[UPPER_ARM].controllers[2]=new PDController(bodySegments[UPPER_ARM], 2, 30000f, 1000000f);
        }
        if(bodySegments[LOWER_ARM]!=null)
            bodySegments[LOWER_ARM].controllers[0]=new PDController(bodySegments[LOWER_ARM], 0, 30000f, 1000000f);

        if(bodySegments[UPPER_WRIST]!=null)
        {
            bodySegments[UPPER_WRIST].controllers[0]=new PDController(bodySegments[UPPER_WRIST], 0, 0f, 0f);
            bodySegments[UPPER_WRIST].controllers[1]=new PDController(bodySegments[UPPER_WRIST], 1, 8000f, 1000000f);
        }

        if(bodySegments[LOWER_WRIST]!=null)
        {
            bodySegments[LOWER_WRIST].controllers[0]=new PDController(bodySegments[LOWER_WRIST], 0, 7000f, 1000000f);
            bodySegments[LOWER_WRIST].controllers[1]=new PDController(bodySegments[LOWER_WRIST], 1, 5000f, 800000f);
        }

        if(bodySegments[THUMB_1]!=null)
        {
            bodySegments[THUMB_1].controllers[0]=new PDController(bodySegments[THUMB_1], 0, 5000f, 320000f);
            bodySegments[THUMB_1].controllers[1]=new PDController(bodySegments[THUMB_1], 1, 5000f, 320000f);
        }
        if(bodySegments[THUMB_2]!=null)
            bodySegments[THUMB_2].controllers[0]=new PDController(bodySegments[THUMB_2], 0, 5000f, 240000f);

        if(bodySegments[INDEX_1]!=null)
            bodySegments[INDEX_1].controllers[0]=new PDController(bodySegments[INDEX_1], 0, 8000f, 220000f);
        if(bodySegments[INDEX_2]!=null)
            bodySegments[INDEX_2].controllers[0]=new PDController(bodySegments[INDEX_2], 0, 5000f, 350000f);
        if(bodySegments[INDEX_3]!=null)
            bodySegments[INDEX_3].controllers[0]=new PDController(bodySegments[INDEX_3], 0, 5000f, 320000f);
        if(bodySegments[INDEX_4]!=null)
            bodySegments[INDEX_4].controllers[0]=new PDController(bodySegments[INDEX_4], 0, 5000f, 240000f);

        if(bodySegments[MIDDLE_1]!=null)
            bodySegments[MIDDLE_1].controllers[0]=new PDController(bodySegments[MIDDLE_1], 0, 8000f, 220000f);
        if(bodySegments[MIDDLE_2]!=null)
            bodySegments[MIDDLE_2].controllers[0]=new PDController(bodySegments[MIDDLE_2], 0, 5000f, 350000f);
        if(bodySegments[MIDDLE_3]!=null)
            bodySegments[MIDDLE_3].controllers[0]=new PDController(bodySegments[MIDDLE_3], 0, 5000f, 320000f);
        if(bodySegments[MIDDLE_4]!=null)
            bodySegments[MIDDLE_4].controllers[0]=new PDController(bodySegments[MIDDLE_4], 0, 5000f, 240000f);

        if(bodySegments[RING_1]!=null)
            bodySegments[RING_1].controllers[0]=new PDController(bodySegments[RING_1], 0, 8000f, 220000f);
        if(bodySegments[RING_2]!=null)
            bodySegments[RING_2].controllers[0]=new PDController(bodySegments[RING_2], 0, 5000f, 350000f);
        if(bodySegments[RING_3]!=null)
            bodySegments[RING_3].controllers[0]=new PDController(bodySegments[RING_3], 0, 5000f, 320000f);
        if(bodySegments[RING_4]!=null)
            bodySegments[RING_4].controllers[0]=new PDController(bodySegments[RING_4], 0, 5000f, 240000f);

        if(bodySegments[PINKY_1]!=null)
            bodySegments[PINKY_1].controllers[0]=new PDController(bodySegments[PINKY_1], 0, 8000f, 220000f);
        if(bodySegments[PINKY_2]!=null)
            bodySegments[PINKY_2].controllers[0]=new PDController(bodySegments[PINKY_2], 0, 5000f, 350000f);
        if(bodySegments[PINKY_3]!=null)
            bodySegments[PINKY_3].controllers[0]=new PDController(bodySegments[PINKY_3], 0, 5000f, 320000f);
        if(bodySegments[PINKY_4]!=null)
            bodySegments[PINKY_4].controllers[0]=new PDController(bodySegments[PINKY_4], 0, 5000f, 240000f);
    }

    /**
     * Get inverse Jacobian matrix for arm - gives angle rotations needed for a given end effector displacement
     * @return float[][]
     */
    public float[][] getInverseJacobian()
    {
        // Get joint angles and limb lengths
        float j1=bodySegments[UPPER_ARM].getJointAngle(0);
        float j2=bodySegments[UPPER_ARM].getJointAngle(1);
        float j3=bodySegments[UPPER_ARM].getJointAngle(2);
        float j4=bodySegments[LOWER_ARM].getJointAngle(0);
        float l1=bodySegments[UPPER_ARM].dim.getY();
        float l2=bodySegments[LOWER_ARM].dim.getY();

        // Constants needed for matrix
        double s1=Math.sin(j1), s2=Math.sin(j2), s3=Math.sin(j3), s4=Math.sin(j4);
        double c1=Math.cos(j1), c2=Math.cos(j2), c3=Math.cos(j3), c4=Math.cos(j4);

        // Construct matrix - row number equals number of spatial dimension in the end effector position (3), column
        // number equals number of degrees of freedom controlled in the arm (4, 3 DOF in the shoulder, 1 in the elbow).
        // This matrix was computed in matlab by constructing a forward kinematics matrix and taking its partial
        // derivative with respect to each spatial dimension of the end effector position
        J.setValue(0,0,-((s3*c1-c3*s2*s1+6.1232e-17*s3*s1+6.1232e-17*c3*s2*c1)*c4-(-s3*s1-c3*s2*c1+6.1232e-17*s3*c1-
                6.1232e-17*c3*s2*s1)*s4)*l2-(s3*s1+c3*s2*c1)*l1);
        J.setValue(0,1,-((6.1232e-17*c3*c2*s1+c3*c2*c1)*c4-(-c3*c2*s1+6.1232e-17*c3*c2*c1)*s4)*l2-c3*c2*s1*l1);
        J.setValue(0,2,-((-6.1232e-17*c3*c1-6.1232e-17*s3*s2*s1+c3*s1-s3*s2*c1)*c4-(c3*c1+s3*s2*s1+6.1232e-17*c3*s1-
                6.1232e-17*s3*s2*c1)*s4)*l2-(-c3*c1-s3*s2*s1)*l1);
        J.setValue(0,3,-(-(-6.1232e-17*s3*c1+6.1232e-17*c3*s2*s1+s3*s1+c3*s2*c1)*s4-(s3*c1-c3*s2*s1+6.1232e-17*s3*s1+
                6.1232e-17*c3*s2*c1)*c4)*l2);
        J.setValue(1,0,-((-c3*c1-s3*s2*s1-6.1232e-17*c3*s1+6.1232e-17*s3*s2*c1)*c4-(-6.1232e-17*c3*c1-
                6.1232e-17*s3*s2*s1+c3*s1-s3*s2*c1)*s4)*l2-(-c3*s1+s3*s2*c1)*l1);
        J.setValue(1,1,-((6.1232e-17*s3*c2*s1+s3*c2*c1)*c4-(-s3*c2*s1+6.1232e-17*s3*c2*c1)*s4)*l2-s3*c2*s1*l1);
        J.setValue(1,2,-((-6.1232e-17*s3*c1+6.1232e-17*c3*s2*s1+s3*s1+c3*s2*c1)*c4-(s3*c1-c3*s2*s1+6.1232e-17*s3*s1+
                6.1232e-17*c3*s2*c1)*s4)*l2-(-s3*c1+c3*s2*s1)*l1);
        J.setValue(1,3,-(-(6.1232e-17*c3*c1+6.1232e-17*s3*s2*s1-c3*s1+s3*s2*c1)*s4-(-c3*c1-s3*s2*s1-6.1232e-17*c3*s1+
                6.1232e-17*s3*s2*c1)*c4)*l2);
        J.setValue(2,0,-((-c2*s1+6.1232e-17*c2*c1)*c4-(-c2*c1-6.1232e-17*c2*s1)*s4)*l2-c2*c1*l1);
        J.setValue(2,1,-((-6.1232e-17*s2*s1-s2*c1)*c4-(s2*s1-6.1232e-17*s2*c1)*s4)*l2+s2*s1*l1);
        J.setValue(2,2,0);
        J.setValue(2,3,-(-(6.1232e-17*c2*s1+c2*c1)*s4-(-c2*s1+6.1232e-17*c2*c1)*c4)*l2);

        // The rectangular Jacobian is not invertible, so compute pseudo-inverse
        Jinv=J.transpose().times(J.times(J.transpose()).inverse());

        // Set inverse array
        // rows=(shoulder x, shoulder y, shoulder z, elbow)
        // cols=(x, y, z)
        for(int i=0; i<4; i++)
        {
            for(int j=0; j<3; j++)
            {
                Jinv_array[i][j]=(float)Jinv.getValue(i,j);
            }
        }
        return Jinv_array;
    }

    /**
     * Performs inverse kinematics - set controller targets to achieve joint angles required to achieve given difference
     * in end effector (wrist) position
     * @param DX - desired difference in wrist position
     */
    public void inverseKinematics(javax.vecmath.Vector3f DX)
    {
        // Compute the inverse Jacobian
        Jinv_array=getInverseJacobian();

        // Set target angles for shoulder controllers
        for(int i=0; i<3; i++)
        {
            // Multiple out a row of the Jacobian with the displacement vector to get change in theta
            float dTheta=Jinv_array[i][0]*DX.x+Jinv_array[i][1]*DX.y+Jinv_array[i][2]*DX.z;
            // Get current angle
            float curAngle=bodySegments[UPPER_ARM].getJointAngle(i);
            // Update target angle for controller
            ((PDController)bodySegments[UPPER_ARM].getController(i)).setTarget(dTheta+curAngle);
        }
        // Set target angle for elbow controller
        // Multiple out a row of the Jacobian with the displacement vector to get change in theta
        float dTheta=Jinv_array[3][0]*DX.x+Jinv_array[3][1]*DX.y+Jinv_array[3][2]*DX.z;
        // Get current angle
        float curAngle=bodySegments[LOWER_ARM].getJointAngle(0);
        // Update target angle for controller
        ((PDController)bodySegments[LOWER_ARM].getController(0)).setTarget(dTheta+curAngle);
    }

    /**
     * Forward kinematics - returns end effector (wrist) position given arm joint angles
     * @param angles - arm joint angles to use
     * @return Vector3d
     */
    public javax.vecmath.Vector3d forwardKinematics(float angles[])
    {
        //Shoulder displacement
        javax.vecmath.Vector3f shoulderPos=bodySegments[UPPER_ARM].getJointPosition();
        Matrix4f shoulderTranslation=new Matrix4f();
        shoulderTranslation.setIdentity();
        shoulderTranslation.setTranslation(shoulderPos);

        // Shoulder rotation
        Matrix4f shoulderRotX=new Matrix4f();
        shoulderRotX.setIdentity();
        shoulderRotX.rotX(angles[0]);
        Matrix4f shoulderRotY=new Matrix4f();
        shoulderRotY.setIdentity();
        shoulderRotY.rotY(angles[1]);
        Matrix4f shoulderRotZ=new Matrix4f();
        shoulderRotZ.setIdentity();
        shoulderRotZ.rotZ(angles[2]);

        // Upper arm displacement
        Matrix4f upperArmTranslation=new Matrix4f();
        upperArmTranslation.setIdentity();
        upperArmTranslation.setTranslation(new javax.vecmath.Vector3f(0,-bodySegments[UPPER_ARM].getDim().getY(),0));

        // Elbow rotation
        Matrix4f elbowPos=new Matrix4f();
        elbowPos.rotX((float)Math.PI/2);
        Matrix4f elbowRot=new Matrix4f();
        elbowRot.rotX(-angles[3]);

        // Wrist translation
        Matrix4f wristTranslation=new Matrix4f();
        wristTranslation.setIdentity();
        wristTranslation.setTranslation(new javax.vecmath.Vector3f(0,-bodySegments[LOWER_ARM].getDim().getY(),0));

        // Multiply matrices to get final position
        shoulderTranslation.mul(shoulderRotZ);
        shoulderTranslation.mul(shoulderRotY);
        shoulderTranslation.mul(shoulderRotX);
        shoulderTranslation.mul(upperArmTranslation);
        shoulderTranslation.mul(elbowPos);
        shoulderTranslation.mul(elbowRot);
        shoulderTranslation.mul(wristTranslation);
        return new javax.vecmath.Vector3d(shoulderTranslation.m03,shoulderTranslation.m13,shoulderTranslation.m23);
    }

    /**
     * Gets the current end-effector position - returns wrist position
     * @return Vector3f
     */
    public javax.vecmath.Vector3f getEndEffectorPosition()
    {
        if(bodySegments[UPPER_WRIST]!=null)
            return bodySegments[UPPER_WRIST].getLimbEndPosition();
        else if(bodySegments[LOWER_ARM]!=null)
            return bodySegments[LOWER_ARM].getLimbEndPosition();
        else
            return bodySegments[UPPER_ARM].getLimbEndPosition();
    }

    /**
     * Gets the current palm position - returns lower wrist position
     * @return Vector3f
     */
    public javax.vecmath.Vector3f getPalmPosition()
    {
        return NslVecmathUtils.convertTuple3f(bodySegments[LOWER_WRIST].getBody().getPosition());
    }

    /**
     * Gets the palm normal vector
     * @return Vector3f
     */
    public javax.vecmath.Vector3f getPalmNormalVector()
    {
        // Palm normal when arm is at reset position
        javax.vecmath.Vector3f palmNormal=new javax.vecmath.Vector3f(0,0,1);

        // Rotate by wrist angles
        if(bodySegments[LOWER_WRIST]!=null)
        {
            Matrix3f wristRotX=new Matrix3f();
            wristRotX.rotX(-bodySegments[LOWER_WRIST].getJointAngle(0));
            Transform3D transform=new Transform3D();
            transform.setRotation(wristRotX);
            transform.transform(palmNormal);
        }
        if(bodySegments[UPPER_WRIST]!=null)
        {
            Matrix3f wristRotY=new Matrix3f();
            wristRotY.rotY(-bodySegments[UPPER_WRIST].getJointAngle(1));
            Transform3D transform=new Transform3D();
            transform.setRotation(wristRotY);
            transform.transform(palmNormal);
        }
        if(bodySegments[LOWER_WRIST]!=null)
        {
            Matrix3f wristRotZ=new Matrix3f();
            wristRotZ.rotZ(bodySegments[LOWER_WRIST].getJointAngle(1));
            Transform3D transform=new Transform3D();
            transform.setRotation(wristRotZ);
            transform.transform(palmNormal);
        }

        // Rotate by elbow angle
        if(bodySegments[LOWER_ARM]!=null)
        {
            Matrix3f elbowRot=new Matrix3f();
            elbowRot.rotX((float)(Math.PI/2.0)-bodySegments[LOWER_ARM].getJointAngle(0));
            Transform3D transform=new Transform3D();
            transform.setRotation(elbowRot);
            transform.transform(palmNormal);
        }

        // Rotate by shoulder angles
        if(bodySegments[UPPER_ARM]!=null)
        {
            Matrix3f shoulderRotX=new Matrix3f();
            shoulderRotX.rotX(bodySegments[UPPER_ARM].getJointAngle(0));
            Transform3D transform=new Transform3D();
            transform.setRotation(shoulderRotX);
            transform.transform(palmNormal);

            Matrix3f shoulderRotY=new Matrix3f();
            shoulderRotY.rotY(bodySegments[UPPER_ARM].getJointAngle(1));
            transform=new Transform3D();
            transform.setRotation(shoulderRotY);
            transform.transform(palmNormal);

            Matrix3f shoulderRotZ=new Matrix3f();
            shoulderRotZ.rotZ(bodySegments[UPPER_ARM].getJointAngle(2));
            transform=new Transform3D();
            transform.setRotation(shoulderRotZ);
            transform.transform(palmNormal);
        }

        return palmNormal;
    }

    /**
     * Reset arm and hand to starting posture
     */
    public void resetPosture()
    {
        javax.vecmath.Vector3f translation=new javax.vecmath.Vector3f();
        localTransform.get(translation);

        if(bodySegments[UPPER_ARM]!=null)
            bodySegments[UPPER_ARM].move(NslVecmathUtils.convertVector3f(translation),
                                                new float[]{(float)-Math.PI/2,0,0});

        if(bodySegments[LOWER_ARM]!=null)
            bodySegments[LOWER_ARM].moveRelative(new Vector3f(0, -.5f*1.25f, .5f*bodySegments[UPPER_ARM].dim.getY()),
                                                new float[]{(float)-Math.PI/2,0,0});

        if(bodySegments[UPPER_WRIST]!=null)
            bodySegments[UPPER_WRIST].moveRelative(new Vector3f(0, 0, .5f*bodySegments[LOWER_ARM].dim.getY()+.5f*.1f),
                                                  new float[]{0,0,0});

        if(bodySegments[LOWER_WRIST]!=null)
            bodySegments[LOWER_WRIST].moveRelative(new Vector3f(0, 0, .5f*bodySegments[UPPER_WRIST].dim.getY()+.5f*.1f),
                                                  new float[]{0,0,0});

        if(bodySegments[THUMB_1]!=null)
            bodySegments[THUMB_1].moveRelative(new Vector3f(-.12f, .03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.2f),
                                              new float[]{0,(float)(60*Math.PI/180),0});
        
        if(bodySegments[THUMB_2]!=null)
            bodySegments[THUMB_2].moveRelative(new Vector3f(0, 0, .5f*bodySegments[THUMB_1].getDim().getY()+.5f*.15f),
                                              new float[]{0,(float)(10*Math.PI/180),(float)(Math.PI/2)});

        if(bodySegments[INDEX_1]!=null)
            bodySegments[INDEX_1].moveRelative(new Vector3f(-.05f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.3f),
                                              new float[]{0,0.12f,0});
        if(bodySegments[INDEX_2]!=null)
            bodySegments[INDEX_2].moveRelative(new Vector3f(0, 0, .5f*bodySegments[INDEX_1].getDim().getY()+.5f*.15f),
                                              new float[]{0,0,0});
        if(bodySegments[INDEX_3]!=null)
            bodySegments[INDEX_3].moveRelative(new Vector3f(0, 0, .5f*bodySegments[INDEX_2].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0});
        if(bodySegments[INDEX_4]!=null)
            bodySegments[INDEX_4].moveRelative(new Vector3f(0, 0, .5f*bodySegments[INDEX_3].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0});

        if(bodySegments[MIDDLE_1]!=null)
            bodySegments[MIDDLE_1].moveRelative(new Vector3f(-0.025f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.33f),
                                               new float[]{0,0,0});
        if(bodySegments[MIDDLE_2]!=null)
            bodySegments[MIDDLE_2].moveRelative(new Vector3f(0, 0, .5f*bodySegments[MIDDLE_1].getDim().getY()+.5f*.15f),
                                               new float[]{0,0,0});
        if(bodySegments[MIDDLE_3]!=null)
            bodySegments[MIDDLE_3].moveRelative(new Vector3f(0, 0, .5f*bodySegments[MIDDLE_2].getDim().getY()+.5f*.1f),
                                               new float[]{0,0,0});
        if(bodySegments[MIDDLE_4]!=null)
            bodySegments[MIDDLE_4].moveRelative(new Vector3f(0, 0, .5f*bodySegments[MIDDLE_3].getDim().getY()+.5f*.1f),
                                               new float[]{0,0,0});

        if(bodySegments[RING_1]!=null)
            bodySegments[RING_1].moveRelative(new Vector3f(0.025f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.32f),
                                             new float[]{0,-0.05f,0});
        if(bodySegments[RING_2]!=null)
            bodySegments[RING_2].moveRelative(new Vector3f(0, 0, .5f*bodySegments[RING_1].getDim().getY()+.5f*.15f),
                                             new float[]{0,0,0});
        if(bodySegments[RING_3]!=null)
            bodySegments[RING_3].moveRelative(new Vector3f(0, 0, .5f*bodySegments[RING_2].getDim().getY()+.5f*.1f),
                                             new float[]{0,0,0});
        if(bodySegments[RING_4]!=null)
            bodySegments[RING_4].moveRelative(new Vector3f(0, 0, .5f*bodySegments[RING_3].getDim().getY()+.5f*.1f),
                                             new float[]{0,0,0});

        if(bodySegments[PINKY_1]!=null)
            bodySegments[PINKY_1].moveRelative(new Vector3f(0.075f, -.03f, .5f*bodySegments[LOWER_WRIST].dim.getY()+.5f*.28f),
                                              new float[]{0,-0.1f,0});
        if(bodySegments[PINKY_2]!=null)
            bodySegments[PINKY_2].moveRelative(new Vector3f(0,0,.5f*bodySegments[PINKY_1].getDim().getY()+.5f*.12f),
                                              new float[]{0,0,0});
        if(bodySegments[PINKY_3]!=null)
            bodySegments[PINKY_3].moveRelative(new Vector3f(0,0,.5f*bodySegments[PINKY_2].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0});
        if(bodySegments[PINKY_4]!=null)
            bodySegments[PINKY_4].moveRelative(new Vector3f(0,0,.5f*bodySegments[PINKY_3].getDim().getY()+.5f*.1f),
                                              new float[]{0,0,0});
    }

    public boolean isPalmContact()
    {
        int[] palmIdx=new int[]{INDEX_1,MIDDLE_1,RING_1,PINKY_1};
        for(int i=0; i<palmIdx.length; i++)
        {
            for(int j=0; j<bodySegments[palmIdx[i]].contactNormals.size(); j++)
            {
                javax.vecmath.Vector3f normal=NslVecmathUtils.convertVector3f(bodySegments[palmIdx[i]].contactNormals.get(j));
                Transform3D invertedTransform=new Transform3D();
                bodySegments[palmIdx[i]].finalTransformGroup.getLocalToVworld(invertedTransform);
                invertedTransform.invert();
                invertedTransform.transform(normal);
                //System.out.println(bodySegments[palmIdx[i]].getName()+"\tnormal="+normal.x+","+normal.y+","+normal.z);
                // Check that the contact normal is pointing towards the palm
                if(normal.y<0)
                    return true;
            }
        }
        return false;
    }

    public boolean isInnerFingerContact()
    {
        int[] fingerIdx=new int[]{INDEX_2,INDEX_3,INDEX_4,MIDDLE_2,MIDDLE_3,MIDDLE_4,RING_2,RING_3,RING_4,PINKY_2,PINKY_3,PINKY_4};
        for(int i=0; i<fingerIdx.length; i++)
        {
            for(int j=0; j<bodySegments[fingerIdx[i]].contactNormals.size(); j++)
            {
                javax.vecmath.Vector3f normal=NslVecmathUtils.convertVector3f(bodySegments[fingerIdx[i]].contactNormals.get(j));
                Transform3D invertedTransform=new Transform3D();
                bodySegments[fingerIdx[i]].finalTransformGroup.getLocalToVworld(invertedTransform);
                invertedTransform.invert();
                invertedTransform.transform(normal);
                //System.out.println(bodySegments[fingerIdx[i]].getName()+"\tnormal="+normal.x+","+normal.y+","+normal.z);
                // Check that the contact normal is pointing towards the palm
                if(normal.y<0)
                    return true;
            }
        }
        return false;
    }

    public boolean isInnerThumbContact()
    {
        int[] thumbIdx=new int[]{THUMB_1,THUMB_2};
        for(int i=0; i<thumbIdx.length; i++)
        {
            for(int j=0; j<bodySegments[thumbIdx[i]].contactPoints.size(); j++)
            {
                javax.vecmath.Vector3f normal=NslVecmathUtils.convertVector3f(bodySegments[thumbIdx[i]].contactNormals.get(j));
                Transform3D invertedTransform=new Transform3D();
                bodySegments[thumbIdx[i]].finalTransformGroup.getLocalToVworld(invertedTransform);
                invertedTransform.invert();
                invertedTransform.transform(normal);
                //System.out.println(bodySegments[thumbIdx[i]].getName()+"\tnormal="+normal.x+","+normal.y+","+normal.z);
                // Check that the contact normal is pointing towards the inside of the thumb
                if((thumbIdx[i]==THUMB_1 && normal.y>0) || (thumbIdx[i]==THUMB_2 && normal.y<0))
                    return true;
            }
        }
        return false;
    }
}


