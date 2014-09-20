package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;

/**
 * Proportional Integrate Derivative (PID) controller for controller body joints
 */
public class PIDController extends PDController
{
    // Integration parameter
    float i=0;
    // Total angle error
    float totalError=0;
    // Integration time
    int integralTime=100;

    /**
     * PIDController constructor
     * @param limb - limb to control
     * @param axis - limb axis index to control
     */
    public PIDController(BodySegment limb, int axis)
    {
        super(limb, axis);
    }

    /**
     * PIDController constructor
     * @param limb - limb to control
     * @param axis - axis to control
     * @param p - gain parameter value
     * @param i - integration parameter value
     * @param d - damping parameter value
     */
    public PIDController(BodySegment limb, int axis, float p, float i, float d)
    {
        super(limb, axis, p, d);
        this.i=i;
    }

    /**
     * Compute torque and apply it to the controller limb axis
     */
    public void process()
    {
        // Get the joint angle along the controlled axis
        float angle=controlledLimb.getJointAngle(controlledAxis);
        // Compute the angular velocity
        float velocity=angle-lastAng;
        lastAng=angle;
        // Compute the angle error
        float error=(target-angle);
        // Reset integration of total error if error is small enough
        if(Math.abs(error)<0.001)
            totalError=0;
        // Otherwise add to total error
        else
            totalError+=error;

        // Torque is gain*error +integration*totalError - damping*velocity
        float torque=p*error+i*totalError-d*velocity;
        // Apply no less than mininum torque in either direction
        if(Math.abs(torque)<minTorque && Math.abs(error)>0.01 && Math.abs(velocity)<0.1)
        {
            if(error<0.0f)
                torque=-1*minTorque;
            else
                torque=minTorque;
        }
        // Apply no more than maximum torque in either direction
        if(Math.abs(torque)>maxTorque)
        {
            if(torque<0.0f)
                torque=-1*maxTorque;
            else
                torque=maxTorque;
        }
        // Apply torque to joint motor
        float[] torques=new float[]{0,0,0};
        if(controlledLimb.jointType==BodySegment.JOINT_TYPE_BALL || controlledLimb.jointType==BodySegment.JOINT_TYPE_HINGE)
            torques[this.controlledAxis]=torque;
        else if(controlledLimb.jointType==BodySegment.JOINT_TYPE_UNIVERSAL ||
                controlledLimb.jointType== BodySegment.JOINT_TYPE_HINGE2)
        {
            if(this.controlledAxis==0)
                torques[this.controlledAxis]=torque;
            else
                torques[2]=torque;
        }
        controlledLimb.motor.addTorque(new Vector3f(torques));     
    }

    /**
     * Get integration parameter
     * @return i - integration parameter value
     */
    public float getI()
    {
        return i;
    }

    /**
     * Set integration parameter
     * @param i - integration parameter value
     */
    public void setI(float i)
    {
        this.i = i;
    }
}
