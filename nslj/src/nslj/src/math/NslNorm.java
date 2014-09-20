package nslj.src.math;

/**
 * User: jbonaiuto
 * Date: Oct 6, 2007
 * Time: 9:21:05 PM
 */
public class NslNorm extends NslUnaryOperator
{
    public int value(int x)
    {
        return x;
    }

    public float value(float x)
    {
        return x;
    }

    public double value(double x)
    {
        return x;
    }

    public int[] eval(int[] dest, int[] a)
    {
        double l=(new NslSqrt()).eval(NslSum.eval((new NslPow()).eval(a,2)));
        if(l!=0)
            dest= NslDiv.eval(a,(int)l);
        else
            dest= new int[]{0,0,0};
        return dest;
    }

    public double[] eval(double[] dest, double[] a)
    {
        double l=(new NslSqrt()).eval(NslSum.eval((new NslPow()).eval(a,2)));
        if(l!=0)
            dest=NslDiv.eval(a,l);
        else
            dest=new double[]{0,0,0};
        return dest;
    }

    public float[] eval(float[] dest, float[] a)
    {
        float l=(float)(new NslSqrt()).eval(NslSum.eval((new NslPow()).eval(a,2)));
        if(l!=0)
            dest=NslDiv.eval(a,l);
        else
            dest=new float[]{0,0,0};
        return dest;
    }

    public static float[] eval(float[] a, float[]b, float[]c)
    {
        float[] v1=NslSub.eval(a,b);
        float[] v2=NslSub.eval(b,c);
        return NslCross.eval(v1,v2);
    }

    public static double[] eval(double[] a, double[]b, double[]c)
    {
        double[] v1=NslSub.eval(a,b);
        double[] v2=NslSub.eval(b,c);
        return NslCross.eval(v1,v2);
    }

    public static int[] eval(int[] a, int[]b, int[]c)
    {
        int[] v1=NslSub.eval(a,b);
        int[] v2=NslSub.eval(b,c);
        return NslCross.eval(v1,v2);
    }
}
