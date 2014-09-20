package nslj.src.display.j3d;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.TransformGroup;
import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;

import org.odejava.*;
import org.odejava.Body;
import org.odejava.ode.*;
import org.openmali.vecmath2.Vector3f;

/**
 * A limb of the body including the joint connecting it to its parent limb and a set of controllers - one for
 * each degree of freedom (DOF)
 */
public class BodySegment extends NslObject3d
{
    // Maximum force to exert on the joint
    public final static float MAX_JOINT_FORCE=10000.0f;

    // Motor controlling this segment's joint
    protected JointAMotor motor;
    // Joint rotation minimum limits for each axis
    protected float[] minRotationLimits;
    // Joint rotation maximum limits for each axis
    protected float[] maxRotationLimits;
    // Sphere capping the main visual cylinder
    protected Sphere sphere;
    // Transform to place sphere at end of limb
    protected Transform3D sphereTransform;
    // Transform group for sphere
    protected TransformGroup sphereTransformGroup;
    // PD controllers for each DOF
    protected PDController[] controllers;
    // Joint feedback
    protected JointFeedback feedback;
    // Whether or not to rotate the torque according to the current angle
    public boolean rotate=false;
    // Final transform including rotation to make visual representation match physical one
    protected TransformGroup finalTransformGroup;


    /**
     * Body segment constructor
     * @param name - name of segment
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translate - local position
     * @param eulerAngles - local orienation
     * @param minRot - min joint angles
     * @param maxRot - max joint angles
     */
    public BodySegment(String name, int jointType, Vector3f[] jointAxes, Appearance app, Vector3f dim,
                       Vector3f translate, float[] eulerAngles, float[] minRot, float[] maxRot)
    {
        super(name, jointType, jointAxes, app, dim, translate, eulerAngles);
        this.minRotationLimits=minRot;
        this.maxRotationLimits=maxRot;
        this.density=1;
    }

    /**
     * Body segment constructor with parent
     * @param name - name of segment
     * @param parent - segment parent
     * @param jointType - type of joint
     * @param jointAxes - joint axes
     * @param app - visual appearance
     * @param dim - size
     * @param translate - local position
     * @param eulerAngles - local orienation
     * @param minRot - min joint angles
     * @param maxRot - max joint angles
     */
    public BodySegment(String name, BodySegment parent, int jointType, Vector3f[] jointAxes, Appearance app,
                       Vector3f dim, Vector3f translate, float[] eulerAngles, float[] minRot, float[] maxRot)
    {
        super(name, parent, jointType, jointAxes, app, dim, translate, eulerAngles);
        this.minRotationLimits=minRot;
        this.maxRotationLimits=maxRot;
        this.density=1;
        this.allowedCollisions.add(parent.name);
    }

    /**
     * Initialize visual com ponent
     */
    protected void initVisual()
    {
        // Create a cylinder with the specified size and appearance
        primitive=new Cylinder(dim.getX(),dim.getY(),appearance);
        // Need to rotate by pi/2 around x-axis because visual cylinder is oriented upright in y-axis, while GeomCapsule
        // is oriented in z-direction
        Transform3D t=new Transform3D();
        t.setEuler(new Vector3d(Math.PI/2,0,0));
        finalTransformGroup=new TransformGroup(t);
        finalTransformGroup.setTransform(t);
        finalTransformGroup.addChild(primitive);
        transformGroup.addChild(finalTransformGroup);

        // Put a sphere on the end of the cylinder
        sphereTransform=new Transform3D();
        // Translate to the end of the cylinder
        sphereTransform.set(new javax.vecmath.Vector3d(0.0, .5*dim.getY(), 0.0));
        sphereTransformGroup=new TransformGroup(sphereTransform);
        sphere=new Sphere(dim.getX(), appearance);
        sphereTransformGroup.addChild(sphere);
        finalTransformGroup.addChild(sphereTransformGroup);
    }

    /**
     * Initialize physical component
     * @param world - world to add segment to
     * @param collSpace - collision space to use to detect collisions between this and other objects
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {
        // Create a capsule the geometry
        geom=new GeomCapsule(name, dim.getX(), dim.getY());
        // Create the body
        body = new Body(name, world, geom);
        // Update the mass based on the size and density
        Mass m=new Mass();
        m.setMass(geom, density);
        body.adjustMass(m.getMass());
        body.setLinearVel(0,0,0);
        body.setAngularVel(0, 0, 0);
        // Add the body geometry to the collision space
        if(collSpace!=null)
            collSpace.addBodyGeoms(body);
    }

    /**
     * Initialize joint - this function intializes the joint controllers and calls the parent method to
     * initialize the actual joint
     * @param world - world to add joint to
     */
    protected void initJoint(World world)
    {
        // Get position and orientation of object in world reference frame
        javax.vecmath.Vector3f pos=getJointPosition();
        javax.vecmath.Matrix3f rot=new javax.vecmath.Matrix3f();
        worldTransform.get(rot);
        if(rotate)
        {
            Matrix3f extraRotation=new Matrix3f();
            extraRotation.rotX((float)(-Math.PI/2));
            rot.mul(extraRotation);
        }

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
                joint.setAxis1(Math.round(a1.x),Math.round(a1.y),Math.round(a1.z));
                javax.vecmath.Vector3f a3= NslVecmathUtils.convertVector3f(jointAxes[2]);
                rot.transform(a3);
                joint.setAxis2(Math.round(a3.x),Math.round(a3.y),Math.round(a3.z));

                joint.setParam(OdeConstants.dParamLoStop, minRotationLimits[0]);
                joint.setParam(OdeConstants.dParamHiStop, maxRotationLimits[0]);
                joint.setParam(OdeConstants.dParamLoStop2, minRotationLimits[1]);
                joint.setParam(OdeConstants.dParamHiStop2, maxRotationLimits[1]);
                joint.setParam(OdeConstants.dParamLoStop3, minRotationLimits[2]);
                joint.setParam(OdeConstants.dParamHiStop3, maxRotationLimits[2]);
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
                joint.setParam(OdeConstants.dParamLoStop, minRotationLimits[0]);
                joint.setParam(OdeConstants.dParamHiStop, maxRotationLimits[0]);
                joint.setParam(OdeConstants.dParamLoStop2, minRotationLimits[1]);
                joint.setParam(OdeConstants.dParamHiStop2, maxRotationLimits[1]);
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
                joint.setParam(OdeConstants.dParamLoStop, minRotationLimits[0]);
                joint.setParam(OdeConstants.dParamHiStop, maxRotationLimits[0]);
                break;
            case JOINT_TYPE_HINGE2:
                // Create joint
                joint=new JointHinge2(world);
                // Use joint to attach object to parent object
                if(parent!=null)
                    joint.attach(parent.body,body);
                // Use joint to attach body to space
                else
                    joint.attach(body, null);
                // Set anchor to position of object
                ((JointHinge2)joint).setAnchor(pos.x, pos.y, pos.z);
                // Set joint axis
                javax.vecmath.Vector3f d1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(d1);
                joint.setAxis1(d1.x,d1.y,d1.z);
                javax.vecmath.Vector3f d2= NslVecmathUtils.convertVector3f(jointAxes[1]);
                rot.transform(d2);
                joint.setAxis2(d2.x,d2.y,d2.z);
                joint.setParam(OdeConstants.dParamLoStop, minRotationLimits[0]);
                joint.setParam(OdeConstants.dParamHiStop, maxRotationLimits[0]);
                joint.setParam(OdeConstants.dParamLoStop2, minRotationLimits[1]);
                joint.setParam(OdeConstants.dParamHiStop2, maxRotationLimits[1]);
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
        joint.enableFeedbackTracking();
        initMotor(world, rot);
    }

    /**
     * Initialize joint motor
     * @param world - world that the joint exists in
     * @param rot - limb rotation
     */
    private void initMotor(World world, Matrix3f rot)
    {
        // set axis relative to 1st body by default
        int rel=1;

        // Create joint motor to move the joint
        motor=new JointAMotor(world);

        // Attach motor to body segments
        if(parent!=null)
            motor.attach(parent.body,body);
        else
            motor.attach(body, null);

        switch(jointType)
        {
            // Setup controllers for ball joint
            case JOINT_TYPE_BALL:
                // 3 PD controllers for 3 DOFs
                controllers=new PDController[3];
                controllers[0]=new PDController(this, 0);
                controllers[1]=new PDController(this, 1);
                controllers[2]=new PDController(this, 2);

                // 3 DOFs
                motor.setNumAxes(3);
                // Use Euler angles
                motor.setMode(OdeConstants.dAMotorEuler);

                // Set the motor axes to be the same as the joint axes
                javax.vecmath.Vector3f a1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(a1);
                motor.setAxis(0,rel,a1.x,a1.y,a1.z);
                javax.vecmath.Vector3f a3= NslVecmathUtils.convertVector3f(jointAxes[2]);
                rot.transform(a3);
                motor.setAxis(2,rel,a3.x,a3.y,a3.z);
                break;

            // Setup controllers for universal joint
            case JOINT_TYPE_UNIVERSAL:
                // 2 PD controllers for 2 DOFs
                controllers=new PDController[2];
                controllers[0]=new PDController(this, 0);
                controllers[1]=new PDController(this, 1);

                // 2 DOFs
                motor.setNumAxes(2);
                // Use Euler angles
                motor.setMode(OdeConstants.dAMotorEuler);

                // Set the motor axes to be the same as the joint axes
                javax.vecmath.Vector3f b1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(b1);
                motor.setAxis(0,rel,b1.x,b1.y,b1.z);
                javax.vecmath.Vector3f b2= NslVecmathUtils.convertVector3f(jointAxes[1]);
                rot.transform(b2);
                motor.setAxis(2,rel,b2.x,b2.y,b2.z);
                break;

            // Setup controllers for hinge joint
            case JOINT_TYPE_HINGE:
                // 1 PD controller for 1 DOF
                controllers=new PDController[1];
                controllers[0]=new PDController(this, 0);

                // 1 DOF
                motor.setNumAxes(1);

                // Set the motor axes to be the same as the joint axes
                javax.vecmath.Vector3f c1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(c1);
                motor.setAxis(0,rel,c1.x,c1.y,c1.z);
                break;

            // Setup controllers for hinge2 joint
            case JOINT_TYPE_HINGE2:
                // 1 PD controller for 1 DOF
                controllers=new PDController[2];
                controllers[0]=new PDController(this, 0);
                controllers[1]=new PDController(this, 1);

                // 1 DOF
                motor.setNumAxes(2);
                // Use Euler angles
                motor.setMode(OdeConstants.dAMotorEuler);
                
                // Set the motor axes to be the same as the joint axes
                // Set the motor axes to be the same as the joint axes
                javax.vecmath.Vector3f d1= NslVecmathUtils.convertVector3f(jointAxes[0]);
                rot.transform(d1);
                motor.setAxis(0,rel,d1.x,d1.y,d1.z);
                javax.vecmath.Vector3f d2= NslVecmathUtils.convertVector3f(jointAxes[1]);
                rot.transform(d2);
                motor.setAxis(2,rel,d2.x,d2.y,d2.z);
                break;

            // Setup controllers for fixed joint
            case JOINT_TYPE_FIXED:
                // No PD controllers
                controllers=new PDController[0];

                // 0 DOFs
                motor.setNumAxes(0);
                break;
        }
    }

    /**
     * Get minimum joint angle limits
     * @return float[]
     */
    public float[] getMinRotationLimits()
    {
        return minRotationLimits;
    }

    /**
     * Get maximum joint angle limits
     * @return float[]
     */
    public float[] getMaxRotationLimits()
    {
        return maxRotationLimits;
    }

    /**
     * Get the number of controllers
     * @return int
     */
    public int getNumControllers()
    {
        if(controllers!=null)
            return controllers.length;
        else
            return 0;
    }

    /**
     * Get the controller for the given DOF
     * @param i - the DOF to get the controller for
     * @return BodySegmentController
     */
    public BodySegmentAxisController getController(int i)
    {
        if(controllers!=null && controllers.length>i)
            return controllers[i];
        return null;
    }

    /**
     * Run joint controllers - called recursively on child segments
     */
    public void runControllers()
    {
        // Call process on each joint controller
        if(controllers!=null)
        {
            for (PDController controller : controllers)
            {
                if (controller != null)
                    controller.process();
            }

        }
        
        //feedback=motor.getFeedback();
        feedback=joint.getFeedback();

        for (NslObject3d aChildren : children)
        {
            if (aChildren instanceof BodySegment)
                ((BodySegment) aChildren).runControllers();
        }
    }

    /**
     * Get joint angle
     * @param anum - index of DOF
     * @return - angle of joint at specified DOF
     */
    public float getJointAngle(int anum)
    {
        // Get hinge joint angle
        if(joint instanceof JointHinge)
            return ((JointHinge)joint).getAngle();
        // Get universal joint angle depending on DOF index
        else if(joint instanceof JointUniversal)
        {
            if(anum==0)
                return ((JointUniversal)joint).getAngle1();
            else if(anum==1)
                return ((JointUniversal)joint).getAngle2();
        }
        // Get ball joint angle depending on DOF index
        else if(joint instanceof JointBall)
        {
            return motor.getAngle(anum);
        }

        return 0;
    }

    /**
     * Get the rate of an angle change
     * @param anum - DOF to get the angle change rate
     * @return - change in angle
     */
    public float getAngleRate(int anum)
    {
        float rate = 0;

        // Get angle rate for ball joint
        if(jointType==JOINT_TYPE_BALL)
        {
            // Get joint DOF axis
            javax.vecmath.Vector3f axis= NslVecmathUtils.convertVector3f(getJointAxes()[anum]);
            Matrix3f rot=new Matrix3f();
            localTransform.get(rot);
            rot.transform(axis);
            // Compute based on relative angular velocities of segment and parent segment
            if(parent!=null)
            {
                Vector3f vel0=parent.body.getAngularVel();
                rate += axis.x*vel0.getX() + axis.y*vel0.getY() + axis.z*vel0.getZ();
            }
            if(body!=null)
            {
                Vector3f vel1=body.getAngularVel();
                rate -= axis.x*vel1.getX() + axis.y*vel1.getY() + axis.z*vel1.getZ();
            }
            // If there is no parent
            if (parent == null && body != null)
                rate = -rate;
            return rate;
        }
        // Get angle rate for universal joint
        else if(jointType==JOINT_TYPE_UNIVERSAL)
        {
            // Can read angle rate straight from the joint
            if(anum==0)
                rate=((JointUniversal)joint).getAngle1Rate();
            else
                rate=((JointUniversal)joint).getAngle2Rate();
        }
        // Get angle rate for hinge joint
        else if(jointType==JOINT_TYPE_HINGE)
        {
            // Can read angle rate straight from the joint
            rate=((JointHinge)joint).getAngleRate();
        }
        return rate;
    }

    /**
     * Get the joint motor
     * @return JointAMotor
     */
    public JointAMotor getMotor()
    {
        return motor;
    }

    /**
     * Get the body segment shape
     * @return int
     */
    public int getShape()
    {
        return NslObject3d.SHAPE_CYLINDER;
    }

    /**
     * Get the position of the end of the limb
     * @return - limb coordinates
     */
    public javax.vecmath.Vector3f getLimbEndPosition()
    {
        javax.vecmath.Vector3f pos=new javax.vecmath.Vector3f(0,0,0.5f*dim.getY());
        Transform3D wTransform=new Transform3D(worldTransform);
        Transform3D moveTransform=new Transform3D();
        moveTransform.set(pos);
        wTransform.mul(moveTransform);
        javax.vecmath.Vector3f newPos=new javax.vecmath.Vector3f();
        wTransform.get(newPos);
        return newPos;
    }

    /**
     * Get the position of the joint
     * @return - joint coordinates
     */
    public javax.vecmath.Vector3f getJointPosition()
    {
        javax.vecmath.Vector3f pos=new javax.vecmath.Vector3f(0,0,-0.5f*dim.getY());
        Transform3D wTransform=new Transform3D(worldTransform);
        Transform3D moveTransform=new Transform3D();
        moveTransform.set(pos);
        wTransform.mul(moveTransform);
        javax.vecmath.Vector3f newPos=new javax.vecmath.Vector3f();
        wTransform.get(newPos);
        return newPos;
    }
}
