/*  SCCS - @(#)NslDistance.java	1.3 --- 09/01/99 --00:18:25 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////
//
// Distance routines
//
//

package nslj.src.math;

import nslj.src.lang.*;

/**
 * Distance routines.
 * There are two basic format for the evaluation method in
 * this routine:
 * 1, eval(a, b) -> c
 * a, b are the parameter of the evaluation function to do
 * a pow b pointwise and the result is passed out as c
 * 2. eval(dest, a, b) -> c
 * a, b are the parameter of the evaluation function and
 * <tt>dest</tt> is the temporary space to hold the result.
 * The method returns the reference to <tt>dest</tt>.
 */

public final class NslDistance
{

    public static int eval(int a, int b)
    {
        return (int) Math.sqrt((double) (a * a) + (double) (b * b));
    }

    public static float eval(float a, float b)
    {
        return (float) Math.sqrt((double) (a * a) + (double) (b * b));
    }

    public static double eval(double a, double b)
    {
        return Math.sqrt((a * a) + (b * b));
    }

    public static double eval(NslDoutDouble0 a, NslDoutDouble0 b)
    {
        return eval(a.getdouble(), b.getdouble());
    }

    public static double eval(int a, NslDoutDouble0 b)
    {
        return eval((double)a, b.getdouble());
    }

    public static int[] eval(int[][] a, int[][] b)
    {
        int[] sumSq=new int[a.length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
                sumSq[i]+=(a[i][j]-b[i][j])*(a[i][j]-b[i][j]);
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static double[] eval(double[][] a, double[][] b)
    {
        double[] sumSq=new double[a.length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
                sumSq[i]+=(a[i][j]-b[i][j])*(a[i][j]-b[i][j]);
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static float[] eval(float[][] a, float[][] b)
    {
        float[] sumSq=new float[a.length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
                sumSq[i]+=(a[i][j]-b[i][j])*(a[i][j]-b[i][j]);
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static int[][] eval(int[][][] a, int[][][] b)
    {
        int[][] sumSq=new int[a.length][a[0].length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
            {
                for(int k=0; k<a[i][j].length; k++)
                    sumSq[i][j]+=(a[i][j][k]-b[i][j][k])*(a[i][j][k]-b[i][j][k]);
            }
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static double[][] eval(double[][][] a, double[][][] b)
    {
        double[][] sumSq=new double[a.length][a[0].length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
            {
                for(int k=0; k<a[i][j].length; k++)
                    sumSq[i][j]+=(a[i][j][k]-b[i][j][k])*(a[i][j][k]-b[i][j][k]);
            }
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static float[][] eval(float[][][] a, float[][][] b)
    {
        float[][] sumSq=new float[a.length][a[0].length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
            {
                for(int k=0; k<a[i][j].length; k++)
                    sumSq[i][j]+=(a[i][j][k]-b[i][j][k])*(a[i][j][k]-b[i][j][k]);
            }
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static int[][][] eval(int[][][][] a, int[][][][] b)
    {
        int[][][] sumSq=new int[a.length][a[0].length][a[0][0].length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
            {
                for(int k=0; k<a[i][j].length; k++)
                {
                    for(int l=0; l<a[i][j][k].length; l++)
                        sumSq[i][j][k]=(a[i][j][k][l]-b[i][j][k][l])*(a[i][j][k][l]-b[i][j][k][l]);
                }
            }
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static double[][][] eval(double[][][][] a, double[][][][] b)
    {
        double[][][] sumSq=new double[a.length][a[0].length][a[0][0].length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
            {
                for(int k=0; k<a[i][j].length; k++)
                {
                    for(int l=0; l<a[i][j][k].length; l++)
                        sumSq[i][j][k]+=(a[i][j][k][l]-b[i][j][k][l])*(a[i][j][k][l]-b[i][j][k][l]);
                }
            }
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static float[][][] eval(float[][][][] a, float[][][][] b)
    {
        float[][][] sumSq=new float[a.length][a[0].length][a[0][0].length];
        for(int i=0; i<a.length; i++)
        {
            for(int j=0; j<a[i].length; j++)
            {
                for(int k=0; k<a[i][j].length; k++)
                {
                    for(int l=0; l<a[i][j][k].length; l++)
                        sumSq[i][j][k]+=(a[i][j][k][l]-b[i][j][k][l])*(a[i][j][k][l]-b[i][j][k][l]);
                }
            }
        }
        return NslOperator.sqrt.eval(sumSq);
    }

    public static float eval(double[] a, float[] b)
    {
        float sumSq=0;
        for(int i=0; i<a.length; i++)
        {
            sumSq+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return (float)Math.sqrt(sumSq);
    }

    public static float eval(float[] a, double[] b)
    {
        return eval(b,a);
    }

    public static float eval(float[] a, float[] b)
    {
        float sumSq=0;
        for(int i=0; i<a.length; i++)
        {
            sumSq+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return (float)Math.sqrt(sumSq);
    }

    public static int eval(int[] a, int[] b)
    {
        int sumSq=0;
        for(int i=0; i<a.length; i++)
        {
            sumSq+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return (int)Math.sqrt(sumSq);
    }

    public static double eval(double[] a, double[] b)
    {
        double sumSq=0;
        for(int i=0; i<a.length; i++)
        {
            sumSq+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return Math.sqrt(sumSq);
    }

    public static int eval(NslInt1 a, NslInt1 b)
    {
        return eval(a.get(),b.get());
    }

    public static double eval(NslDouble1 a, NslDouble1 b)
    {
        return eval(a.get(),b.get());
    }

    public static double eval(double[] a, NslDouble1 b)
    {
        return eval(a,b.get());
    }

    public static double eval(NslDouble1 a, double[] b)
    {
        return eval(a.get(),b);
    }

    public static float eval(NslFloat1 a, NslFloat1 b)
    {
        return eval(a.get(),b.get());
    }

    public static float eval(NslDouble1 a, NslFloat1 b)
    {
        return eval(a.get(), b.get());
    }

    public static float eval(NslFloat1 a, NslDouble1 b)
    {
        return eval(a.get(), b.get());
    }

    public static int[] eval(NslInt2 a, NslInt2 b)
    {
        return eval(a.getint2(),b.getint2());
    }

    public static double[] eval(NslDouble2 a, NslDouble2 b)
    {
        return eval(a.getdouble2(),b.getdouble2());
    }

    public static float[] eval(NslFloat2 a, NslFloat2 b)
    {
        return eval(a.getfloat2(),b.getfloat2());
    }
}
