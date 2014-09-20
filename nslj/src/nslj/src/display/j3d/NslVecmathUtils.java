package nslj.src.display.j3d;

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;

import javax.vecmath.Quat4f;

/**
 * Utilities for vector math
 */
public class NslVecmathUtils
{
    /**
     * Convert a quaternion to euler angles
     * @param q1 - quaternion
     * @return - euler angles
     */
    public static javax.vecmath.Vector3f quatToEuler(Quat4f q1)
    {
        double sqw=q1.w*q1.w;
        double sqx=q1.x*q1.x;
        double sqy=q1.y*q1.y;
        double sqz=q1.z*q1.z;
        double heading=Math.atan2(2.0*(q1.x*q1.y+q1.z*q1.w),(sqx-sqy-sqz+sqw));
        double bank=Math.atan2(2.0*(q1.y*q1.z+q1.x*q1.w),(-sqx-sqy+sqz+sqw));
        double x=-2.0f*(q1.x*q1.z-q1.y*q1.w);
        if(x>1)
            x=1;
        if(x<-1)
            x=-1;
        double attitude=Math.asin(x);
        return new javax.vecmath.Vector3f((float)bank,(float)attitude,(float)heading);
    }

    /**
     * Create a rotation matrix from euler angles
     * @param eulerAngles - euler angles
     * @return - rotation matrix
     */
    public static javax.vecmath.Matrix3f createRotationMatrix(float[] eulerAngles)
    {
        if(eulerAngles!=null && eulerAngles.length>2)
        {
            javax.vecmath.Matrix3f mat=new javax.vecmath.Matrix3f();
            double A = Math.cos(eulerAngles[0]);
            double B = Math.sin(eulerAngles[0]);
            double C = Math.cos(eulerAngles[1]);
            double D = Math.sin(eulerAngles[1]);
            double E = Math.cos(eulerAngles[2]);
            double F = Math.sin(eulerAngles[2]);
            double AD = A * D;
            double BD = B * D;
            mat.m00 = (float)(C * E);
            mat.m10 = (float)(-C * F);
            mat.m20 = (float)D;
            mat.m01 = (float)(BD * E + A * F);
            mat.m11 = (float)(-BD * F + A * E);
            mat.m21 = (float)(-B * C);
            mat.m02 = (float)(-AD * E + B * F);
            mat.m12 = (float)(AD * F + B * E);
            mat.m22 = (float)(A * C);
            return mat;
        }
        return null;
    }

    /**
     * Computes the Euler (YPR order) angles from the <code>matrix</code>.
     *
     * Check element [2][0] of <code>matrix</code> for <code>rowFlag</code>.
     * Depending on the value of <code>rowFlag</code> the values of pitch, yaw
     * and roll are computed as follows:<BR>
     *
     * <code>rowFlag</code> = 1.0:<BR>
     * Y = atan2(-matrix[0][1], matrix[1][1]), P = -90.0, R = 0.0 <BR>
     * <BR>
     * <code>rowFlag</code> = -1.0:<BR>
     * Y = atan2(matrix[0][1], matrix[1][1]), P = 90.0, R = 0.0 <BR>
     * <BR>
     * otherwise:<BR>
     * Y = atan2(matrix[1][0], matrix[0][0]), P = asin(-matrix[2][0]),
     * R = atan2(matrix[2][1], matrix[2][2]) <BR>
     */
    public static float[] computeEulerAngles(javax.vecmath.Matrix3f matrix)
    {
        float[] angles=new float[3];
        int rowFlag = (int) matrix.m20;
        switch (rowFlag) {
        case 1:
            angles[0] = (float)(Math.atan2(-matrix.m01, matrix.m11));
            angles[1] = -(float)(Math.PI/2);
            angles[2] = 0.0f;
            break;
        case -1:
            angles[0] = (float)Math.atan2(-matrix.m01, matrix.m11);
            angles[1] = (float)(Math.PI/2);
            angles[2] = 0.0f;
            break;
        default:
            angles[0] = (float)Math.atan2(matrix.m10, matrix.m00);
            angles[1] = (float)Math.asin(-matrix.m20);
            angles[2] = (float)Math.atan2(matrix.m21, matrix.m22);
            break;
        }
        return angles;
    }

    /**
     * Convert from org.openmali.vecmath2.Matrix3f to javax.vecmath.Matrix3f
     * @param m - org.openmali.vecmath2.Matrix3f
     * @return javax.vecmath.Matrix3f
     */
    public static javax.vecmath.Matrix3f convertMatrix3f(Matrix3f m)
    {
        javax.vecmath.Matrix3f n=new javax.vecmath.Matrix3f();
        n.m00=m.m00();
        n.m01=m.m01();
        n.m02=m.m02();
        n.m10=m.m10();
        n.m11=m.m11();
        n.m12=m.m12();
        n.m20=m.m20();
        n.m21=m.m21();
        n.m22=m.m22();
        return n;
    }

    /**
     * Convert from javax.vecmath.Matrix3f to org.openmali.vecmath2.Matrix3f
     * @param m - javax.vecmath.Matrix3f
     * @return org.openmali.vecmath2.Matrix3f
     */
    public static Matrix3f convertMatrix3f(javax.vecmath.Matrix3f m)
    {
        Matrix3f n=new Matrix3f();
        n.m00(m.m00);
        n.m01(m.m01);
        n.m02(m.m02);
        n.m10(m.m10);
        n.m11(m.m11);
        n.m12(m.m12);
        n.m20(m.m20);
        n.m21(m.m21);
        n.m22(m.m22);
        return n;
    }

    /**
     * Convert from org.openmali.vecmath2.Vector3f to javax.vecmath.Vector3f
     * @param a - org.openmali.vecmath2.Vector3f
     * @return javax.vecmath.Vector3f
     */
    public static javax.vecmath.Vector3f convertVector3f(Vector3f a)
    {
        return new javax.vecmath.Vector3f(a.getX(), a.getY(), a.getZ());
    }

    /**
     * Convert from javax.vecmath.Vector3f to org.openmali.vecmath2.Vector3f
     * @param a - javax.vecmath.Vector3f
     * @return org.openmali.vecmath2.Vector3f
     */
    public static Vector3f convertVector3f(javax.vecmath.Vector3f a)
    {
        return new Vector3f(a.x, a.y, a.z);
    }

    /**
     * Convert from org.openmali.vecmath2.Quaternion4f to javax.vecmath.Quat4f
     * @param a - org.openmali.vecmath2.Quaternion4f
     * @return javax.vecmath.Quat4f
     */
    public static Quat4f convertQuat4f(Quaternion4f a)
    {
        return new Quat4f(a.getA(), a.getB(), a.getC(), a.getD());
    }

    /**
     * Convert from javax.vecmath.Quat4f to org.openmali.vecmath2.Quaternion4f
     * @param a - javax.vecmath.Quat4f
     * @return org.openmali.vecmath2.Quaternion4f
     */
    public static Quaternion4f convertQuat4f(Quat4f a)
    {
        return new Quaternion4f(a.x, a.y, a.z, a.w);
    }

    /**
     * Convert from javax.vecmath.Tuple3f to org.openmali.vecmath2.Tuple3f
     * @param a - javax.vecmath.Tuple3f
     * @return org.openmali.vecmath2.Tuple3f
     */
    public static Tuple3f convertTuple3f(javax.vecmath.Tuple3f a)
    {
        return new Tuple3f(a.x, a.y, a.z);
    }

    /**
     * Convert from org.openmali.vecmath2.Tuple3f to javax.vecmath.Tuple3f
     * @param a - org.openmali.vecmath2.Tuple3f 
     * @return javax.vecmath.Tuple3f
     */
    public static javax.vecmath.Vector3f convertTuple3f(Tuple3f a)
    {
        return new javax.vecmath.Vector3f(a.getX(), a.getY(), a.getZ());
    }
}
