/*  SCCS @(#)NslGetRow.java	1.4 ---09/01/99 --00:18:24 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////
//
// NslGetRow routines
//
//

/**
 NslGetRow
 There are two basic format for the evaluation method in
 this routine:
 1, eval(a) -> c
 a is the parameter to evaluate the Get of
 a pointwise and the result is passed out as c
 2. eval(dest, a) -> c
 a is the parameter of the evaluation function and
 <tt>dest</tt> is the temporary space to hold the result.
 The method returns the reference to <tt>dest</tt>.
 NslGetRow always returns what was passed in.
 */

package nslj.src.math;

//import java.util.Arrays;

import nslj.src.lang.*;

public final class NslGetRow
{
//-----------------------------------------------
//native 0d double

    public static double eval(double a)
    {
        return a;
    }
//native 1d double

    public static double[] eval(double[] dest, double[]_data, int i)
    {
        int size1 = _data.length;
        System.arraycopy(_data, 0, dest, 0, size1);
        return dest;
    }

    public static double[] eval(double[] _data, int i)
    {
        int size1 = _data.length;

        double[] dest = new double[size1];

        return (eval(dest, _data, i));
    }

//native 2d double

    public static double[] eval(double[] dest, double[][] _data, int i)
    {

        int j;
        int size2 = _data[0].length;

        if (dest.length != size2)
        {
            dest = new double[size2];
        }

        for (j = 0; j < size2; j++)
        {
            dest[j] = _data[i][j];  //copy
        }

        return dest;
    }

    public static double[] eval(double[][] _data, int i)
    {

        int size2 = _data[0].length;
        double[] dest = new double[size2];
        return (eval(dest, _data, i));
    }
//-----------------------------------------------
//native 0d float

    public static float eval(float a)
    {
        return a;
    }
//native 1d float

    public static float[] eval(float[] dest, float[]_data, int i)
    {
        int size1 = _data.length;
        System.arraycopy(_data, 0, dest, 0, size1);
        return dest;
    }

    public static float[] eval(float[] _data, int i)
    {
        int size1 = _data.length;

        float[] dest = new float[size1];

        return (eval(dest, _data, i));
    }

//native 2d float

    public static float[] eval(float[] dest, float[][] _data, int i)
    {

        int j;
        int size2 = _data[0].length;

        if (dest.length != size2)
        {
            dest = new float[size2];
        }

        for (j = 0; j < size2; j++)
        {
            dest[j] = _data[i][j];  //copy
        }

        return dest;
    }

    public static float[] eval(float[][] _data, int i)
    {
        int size2 = _data[0].length;
        float[] dest = new float[size2];
        return (eval(dest, _data, i));
    }
//-----------------------------------------------
//native 0d int

    public static int eval(int a)
    {
        return a;
    }
//native 1d int

    public static int[] eval(int[] dest, int[]_data, int i)
    {
        int size1 = _data.length;
        System.arraycopy(_data, 0, dest, 0, size1);
        return dest;
    }

    public static int[] eval(int[] _data, int i)
    {
        int size1 = _data.length;

        int[] dest = new int[size1];

        return (eval(dest, _data, i));
    }

//native 2d int

    public static int[] eval(int[] dest, int[][] _data, int i)
    {

        int j;
        int size2 = _data[0].length;

        if (dest.length != size2)
        {
            dest = new int[size2];
        }

        for (j = 0; j < size2; j++)
        {
            dest[j] = _data[i][j]; //copy
        }

        return dest;
    }

    public static int[] eval(int[][] _data, int i)
    {
        int size2 = _data[0].length;
        int[] dest = new int[size2];
        return (eval(dest, _data, i));
    }
//-----------------------------------------------
//native 0d boolean

    public static boolean eval(boolean a)
    {
        return a;
    }
//native 1d boolean

    public static boolean[] eval(boolean[] dest, boolean[]_data, int i)
    {
        int size1 = _data.length;
        System.arraycopy(_data, 0, dest, 0, size1);
        return dest;
    }

    public static boolean[] eval(boolean[] _data, int i)
    {
        int size1 = _data.length;

        boolean[] dest = new boolean[size1];

        return (eval(dest, _data, i));
    }

//native 2d boolean

    public static boolean[] eval(boolean[] dest, boolean[][] _data, int i)
    {

        int j;
        int size2 = _data[0].length;

        if (dest.length != size2)
        {
            dest = new boolean[size2];
        }

        for (j = 0; j < size2; j++)
        {
            dest[j] = _data[i][j];  //copy
        }

        return dest;
    }

    public static boolean[] eval(boolean[][] _data, int i)
    {
        int size2 = _data[0].length;
        boolean[] dest = new boolean[size2];
        return (eval(dest, _data, i));
    }

//------------------------------------------------------

//--------------------------------------------------------
//NslDouble 0d NslNumeric

    public static double eval(NslDouble0 a)
    {
        return a.getdouble();
    }
//NslDouble 1d NslNumeric

    public static double eval(double dest, NslDouble1 _data, int i)
    {
        dest = _data.getdouble(i);
        return dest;
    }

    public static double eval(NslDouble1 _data, int i)
    {
        double dest = 0;
        return (eval(dest, _data, i));
    }

//NslDouble 2d NslNumeric

    public static double[] eval(double[] dest, NslDouble2 _data, int i)
    {
        dest = _data.getdouble1(i);
        return dest;
    }

    public static double[] eval(NslDouble2 _data, int i)
    {
        int[] sizes = _data.getSizes();
        int size2 = sizes[1];
        double[] dest = new double[size2];
        return (eval(dest, _data, i));
    }
//--------------------------------------------------------
//NslFloat 0d NslNumeric

    public static float eval(NslFloat0 a)
    {
        return a.getfloat();
    }
//NslFloat 1d NslNumeric

    public static float eval(float dest, NslFloat1 _data, int i)
    {
        dest = _data.getfloat(i);
        return dest;
    }

    public static float eval(NslFloat1 _data, int i)
    {
        float dest = 0;
        return (eval(dest, _data, i));
    }

//NslFloat 2d NslNumeric

    public static float[] eval(float[] dest, NslFloat2 _data, int i)
    {
        dest = _data.getfloat1(i);
        return dest;
    }

    public static float[] eval(NslFloat2 _data, int i)
    {
        int[] sizes = _data.getSizes();
        int size2 = sizes[1];
        float[] dest = new float[size2];
        return (eval(dest, _data, i));
    }
//--------------------------------------------------------
//NslInt 0d NslNumeric

    public static int eval(NslInt0 a)
    {
        return a.getint();
    }
//NslInt 1d NslNumeric 

    public static int eval(int dest, NslInt1 _data, int i)
    {
        dest = _data.getint(i);
        return dest;
    }

    public static int eval(NslInt1 _data, int i)
    {
        int dest = 0;
        return (eval(dest, _data, i));
    }

//NslInt 2d NslNumeric

    public static int[] eval(int[] dest, NslInt2 _data, int i)
    {
        dest = _data.getint1(i);
        return dest;
    }

    public static int[] eval(NslInt2 _data, int i)
    {
        int[] sizes = _data.getSizes();
        int size2 = sizes[1];
        int[] dest = new int[size2];
        return (eval(dest, _data, i));
    }

//--------------------------------------------------------
//NslBoolean 0d NslNumeric

    public static boolean eval(NslBoolean0 a)
    {
        return a.getboolean();
    }
//NslBoolean 1d NslNumeric

    public static boolean eval(boolean dest, NslBoolean1 _data, int i)
    {
        dest = _data.getboolean(i);
        return dest;
    }

    public static boolean eval(NslBoolean1 _data, int i)
    {
        boolean dest = false;
        return (eval(dest, _data, i));
    }

//NslBoolean 2d NslNumeric

    public static boolean[] eval(boolean[] dest, NslBoolean2 _data, int i)
    {
        dest = _data.getboolean1(i);
        return dest;
    }

    public static boolean[] eval(NslBoolean2 _data, int i)
    {
        int[] sizes = _data.getSizes();
        int size2 = sizes[1];
        boolean[] dest = new boolean[size2];
        return (eval(dest, _data, i));
    }

}  // end NslGetRow










