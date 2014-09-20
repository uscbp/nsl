package nslj.src.math;

import nslj.src.lang.NslInt1;
import nslj.src.lang.NslFloat1;
import nslj.src.lang.NslDouble1;

/**
 * User: jbonaiuto
 * Date: Oct 7, 2007
 * Time: 12:48:42 AM
 */
public class NslCross
{
    public static int[] eval(int[] p1, int[] p2)
    {
        int x,y,z;
        x = p1[1]*p2[2] - p1[2]*p2[1];
        y = p1[2]*p2[0] - p1[0]*p2[2];
        z = p1[0]*p2[1] - p1[1]*p2[0];
        return new int[]{x,y,z};
    }
    /*public static Point3d cross(Point3d p1,Point3d p2)
    {
        double x,y,z;
        x = p1.y*p2.z - p1.z*p2.y;
        y = p1.z*p2.x - p1.x*p2.z;
        z = p1.x*p2.y - p1.y*p2.x;
        return new Point3d(x,y,z);
    }*/

    public static double[] eval(double[] p1, double[] p2)
    {
        double x,y,z;
        x = p1[1]*p2[2] - p1[2]*p2[1];
        y = p1[2]*p2[0] - p1[0]*p2[2];
        z = p1[0]*p2[1] - p1[1]*p2[0];
        return new double[]{x,y,z};
    }

    public static float[] eval(float[] p1, float[] p2)
    {
        float x,y,z;
        x = p1[1]*p2[2] - p1[2]*p2[1];
        y = p1[2]*p2[0] - p1[0]*p2[2];
        z = p1[0]*p2[1] - p1[1]*p2[0];
        return new float[]{x,y,z};
    }

    public static int[] eval(NslInt1 x, NslInt1 y)
    {
        return eval(x.get(), y.get());
    }

    public static float[] eval(NslFloat1 x, NslFloat1 y)
    {
        return eval(x.get(), y.get());
    }

    public static double[] eval(NslDouble1 x, NslDouble1 y)
    {
        return eval(x.get(), y.get());
    }
}
