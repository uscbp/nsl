package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;

/**
 * Proportional Derivative (PD) controller for controller body joints
 */
public class PDController extends BodySegmentAxisController
{
    // Gain (p) and damping (d) parameters
    float p=1, d=1;
    // Last joint angle
    float lastAng=0;
    // Target joint angle
    float target;
    // Minimum torque that can be applied
    float minTorque=0.0f;
    // Maximum torque that can be applied
    float maxTorque=10000.0f;
    
    /**
     * PDController constructor
     * @param limb - limb to control
     * @param axis - limb axis index to control
     */
    public PDController(BodySegment limb, int axis)
    {
        super(limb, axis);
    }

    /**
     * PDController constructor
     * @param limb - limb to control
     * @param axis - axis to control
     * @param p - gain parameter value
     * @param d - damping parameter value
     */
    public PDController(BodySegment limb, int axis, float p, float d)
    {
        super(limb, axis);
        this.p=p;
        this.d=d;
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
        
        // Torque is gain*error - damping*velocity
        float torque=p*error-d*velocity;
        if(Math.abs(torque)<minTorque && Math.abs(error)>0.01 && Math.abs(velocity)<0.1)
        {
            if(torque<0.0f)
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
     * Get gain parameter
     * @return p - gain parameter value
     */
    public float getP()
    {
        return p;
    }

    /**
     * Set gain parameter
     * @param p - gain parameter value
     */
    public void setP(float p)
    {
        this.p = p;
    }

    /**
     * Get damping parameter
     * @return d - damping parameter value
     */
    public float getD()
    {
        return d;
    }

    /**
     * Set damping parameter
     * @param d - damping parameter value
     */
    public void setD(float d)
    {
        this.d = d;
    }

    /**
     * Get target angle
     * @return target - target angle value
     */
    public float getTarget()
    {
        return target;
    }

    /**
     * Set target angle
     * @param target - target angle value
     */
    public void setTarget(float target)
    {
        // Make sure target is within joints rotation limits
        this.target = Math.min(controlledLimb.getMaxRotationLimits()[controlledAxis],
                Math.max(controlledLimb.getMinRotationLimits()[controlledAxis],target));
    }

    /**
     * Get the minimum torque
     * @return minTorque
     */
    public float getMinTorque()
    {
        return minTorque;
    }

    /**
     * Set the minimum torque
     * @param minTorque - minimum torque that can be applied
     */
    public void setMinTorque(float minTorque)
    {
        this.minTorque = minTorque;
    }

    /**
     * Get the maximum torque
     * @return maxTorque
     */
    public float getMaxTorque()
    {
        return maxTorque;
    }

    /**
     * Set the maximum torque
     * @param maxTorque - maximum torque that can be applied
     */
    public void setMaxTorque(float maxTorque)
    {
        this.maxTorque = maxTorque;
    }
}
