package nslj.src.display.j3d;

import org.openmali.vecmath2.Vector3f;
import nslj.src.math.NslDistance;

/**
 * Inverse kinematics controller for body
 */
public class IKController extends BodyController
{
    // End effector position target
    protected javax.vecmath.Vector3f target;
    // End effector velocity target
    protected javax.vecmath.Vector3f velocity;
    // Last position of the end effector
    protected Vector3f lastWristPos;

    /**
     * Create the inverse kinematics controller
     * @param body - the body to control
     */
    public IKController(Body body)
    {
        super(body);
    }

    /**
     * Set the end effector target
     * @param endEffectorTarget - target location for the end effector
     */
    public void setTarget(Vector3f endEffectorTarget)
    {
        this.target= NslVecmathUtils.convertVector3f(endEffectorTarget);
        // Get the current end effector position
        Vector3f endEffectorPosition=NslVecmathUtils.convertVector3f(body.getEndEffectorPosition());
        // Compute the required change in end-effector position
        javax.vecmath.Vector3f DX=new javax.vecmath.Vector3f(target);
        DX.sub(NslVecmathUtils.convertVector3f(endEffectorPosition));
        // Compute inverse kinematics using the change in end effector position
        body.inverseKinematics(DX);
    }

    /**
     * Run the controller
     */
    public void process()
    {
        if(body.getRootSegment()!=null)
            body.getRootSegment().runControllers();
    }

    /**
     * Optimization method that uses forward kinematics and noisy gradient descent on the endpoint error
     * @return - final set of joint (shoulder and elbow) angles to reach the current target
     */
    public float[] optimize()
    {
        //array of joint angles - initialize to current controller targets
        float[] angles=new float[4];
        for(int i=0; i<3; i++)
            angles[i]=body.bodySegments[PrimateBody.UPPER_ARM].getJointAngle(i);
        angles[3]=body.bodySegments[PrimateBody.LOWER_ARM].getJointAngle(0);

        // Start from angles specified by IK
        // Get current end effector position
        Vector3f endEffectorPosition=NslVecmathUtils.convertVector3f(body.getEndEffectorPosition());
        // Compute required change in end effector position
        javax.vecmath.Vector3f DX=new javax.vecmath.Vector3f(target);
        DX.sub(NslVecmathUtils.convertVector3f(endEffectorPosition));
        // Get inverse jacobian
        float[][] J=body.getInverseJacobian();

        // Compute target shoulder angles using the change in end effector position and the inverse jacobian
        for(int i=0; i<3; i++)
        {
            // Multiple out a row of the Jacobian with the displacement vector to get change in theta
            angles[i]+=J[i][0]*DX.x+J[i][1]*DX.y+J[i][2]*DX.z;
        }
        // Compute target elbow angles using the change in end effector position and the inverse jacobian
        angles[3]+=J[3][0]*DX.x+J[3][1]*DX.y+J[3][2]*DX.z;

        // Compute the position of the end-effector with these joint angles
        javax.vecmath.Vector3d pos=body.forwardKinematics(angles);
        // Compute the error between this position and the target position
        double error= NslDistance.eval(new double[]{pos.x,pos.y,pos.z}, new double[]{target.x,target.y,target.z});

        // End-effector position error we're willing to tolerate
        double errorTolerance=0.000001;

        // While the end-effector error is above threshold
        while(error>errorTolerance)
        {
            // Compute new set of joint angles
            float newAngles[]=new float[4];
            System.arraycopy(angles, 0, newAngles, 0, 4);
            // Pick one angle to change at random
            int i=(int)(Math.min(3,Math.random()*4));
            //Change the angle by a small random amount
            newAngles[i]+=0.001*(Math.random()-.5);
            // Compute the position of the end-effector at these new joint angles
            pos=body.forwardKinematics(newAngles);
            // Calculate new error
            double newErr=NslDistance.eval(new double[]{pos.x,pos.y,pos.z}, new double[]{target.x,target.y,target.z});

            // If error is less than old error, keep the new angle
            if(newErr<error)
            {
                angles[i]=newAngles[i];
                error=newErr;
            }
        }
        return angles;
    }
}
