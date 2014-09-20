/*  SCCS @(#)NslGetColumn.java	1.4 ---09/01/99 --00:18:24 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////
//
// NslGetColumn
//
//

/**
 Get of Column routines.
 There are two basic format for the evaluation method in
 this routine:
 1, eval(a) -> c
 a is the parameter to evaluate the Get of
 a pointwise and the result is passed out as c
 2. eval(dest, a) -> c
 a is the parameter of the evaluation function and
 <tt>dest</tt> is the temporary space to hold the result.
 The method returns the reference to <tt>dest</tt>.
 NslGetColumn always returns a vector.
 */

package nslj.src.math;

//import java.util.Arrays;

import nslj.src.lang.*;

public final class NslGetColumn
{

//------------------------------------------------

//native 0d double

    public static double eval(double a)
    {
        return a;
    }
//native 1d double

    public static double eval(double dest, double[]_data, int j)
    {
        dest = _data[j];
        return dest;
    }

    public static double eval(double[] _data, int j)
    {
        double dest = 0;

        return (eval(dest, _data, j));
    }

//native 2d double

    public static double[] eval(double[] dest, double[][] _data, int j)
    {

        int i;
        int size1 = _data.length;

        if (dest.length != size1)
        {
            dest = new double[size1];
        }

        for (i = 0; i < size1; i++)
        {
            dest[i] = _data[i][j];
        }
        return dest;
    }

    public static double[] eval(double[][] _data, int j)
    {
        int size1 = _data.length;
        double[] dest = new double[size1];
        return (eval(dest, _data, j));
    }

//------------------------------------------------------
//native 0d float

//native 0d float

    public static float eval(float a)
    {
        return a;
    }
//native 1d float

    public static float eval(float dest, float[]_data, int j)
    {
        dest = _data[j];
        return dest;
    }

    public static float eval(float[] _data, int j)
    {
        float dest = 0;

        return (eval(dest, _data, j));
    }

//native 2d float

    public static float[] eval(float[] dest, float[][] _data, int j)
    {

        int i;
        int size1 = _data.length;
        if (dest.length != size1)
        {
            dest = new float[size1];
        }

        for (i = 0; i < size1; i++)
        {
            dest[i] = _data[i][j];
        }
        return dest;
    }

    public static float[] eval(float[][] _data, int j)
    {
        int size1 = _data.length;
        float[] dest = new float[size1];
        return (eval(dest, _data, j));
    }

//--------------------------------------------------------

// native 0d int

//native 0d int

    public static int eval(int a)
    {
        return a;
    }
//native 1d int

    public static int eval(int dest, int[]_data, int j)
    {
        dest = _data[j];
        return dest;
    }

    public static int eval(int[] _data, int j)
    {
        int dest = 0;

        return (eval(dest, _data, j));
    }

//native 2d int

    public static int[] eval(int[] dest, int[][] _data, int j)
    {

        int i;
        int size1 = _data.length;

        if (dest.length != size1)
        {
            dest = new int[size1];
        }

        for (i = 0; i < size1; i++)
        {
            dest[i] = _data[i][j];
        }
        return dest;
    }

    public static int[] eval(int[][] _data, int j)
    {
        int size1 = _data.length;
        int[] dest = new int[size1];
        return (eval(dest, _data, j));
    }
//------------------------------------------------

//native 0d boolean

    public static boolean eval(boolean a)
    {
        return a;
    }
//native 1d boolean

    public static boolean eval(boolean dest, boolean[]_data, int j)
    {
        dest = _data[j];
        return dest;
    }

    public static boolean eval(boolean[] _data, int j)
    {
        boolean dest = false;

        return (eval(dest, _data, j));
    }

//native 2d boolean

    public static boolean[] eval(boolean[] dest, boolean[][] _data, int j)
    {

        int i;
        int size1 = _data.length;

        if (dest.length != size1)
        {
            dest = new boolean[size1];
        }

        for (i = 0; i < size1; i++)
        {
            dest[i] = _data[i][j];
        }
        return dest;
    }

    public static boolean[] eval(boolean[][] _data, int j)
    {
        int size1 = _data.length;
        boolean[] dest = new boolean[size1];
        return (eval(dest, _data, j));
    }

//--------------------------------------------------------
//NslDouble 0d NslNumeric

    public static double eval(NslDouble0 a)
    {
        return a.getdouble();
    }
//NslDouble 1d NslNumeric

    public static double eval(double dest, NslDouble1 nslnumeric, int j)
    {
        dest = nslnumeric.get(j);
        return dest;
    }

    public static double eval(NslDouble1 nslnumeric, int j)
    {
        double dest = 0;
        return (eval(dest, nslnumeric, j));
    }

//NslDouble 2d NslNumeric

    public static double[] eval(double[] dest, NslDouble2 nslnumeric, int j)
    {
        int i;
        int [] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];

        for (i = 0; i < size1; i++)
        {
            dest[i] = nslnumeric.getdouble(i, j);
        }
        return dest;
    }

    public static double[] eval(NslDouble2 nslnumeric, int j)
    {
        int[] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];
        double[] dest = new double[size1];
        return (eval(dest, nslnumeric, j));
    }
//--------------------------------------------------------------------
//NslFloat 0d NslNumeric

    public static float eval(NslFloat0 a)
    {
        return a.getfloat();
    }
//NslFloat 1d NslNumeric

    public static float eval(float dest, NslFloat1 nslnumeric, int j)
    {
        dest = nslnumeric.get(j);
        return dest;
    }

    public static float eval(NslFloat1 nslnumeric, int j)
    {
        float dest = 0;
        return (eval(dest, nslnumeric, j));
    }

//NslFloat 2d NslNumeric

    public static float[] eval(float[] dest, NslFloat2 nslnumeric, int j)
    {
        int i;
        int [] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];

        for (i = 0; i < size1; i++)
        {
            dest[i] = nslnumeric.getfloat(i, j);
        }
        return dest;
    }

    public static float[] eval(NslFloat2 nslnumeric, int j)
    {
        int[] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];
        float[] dest = new float[size1];
        return (eval(dest, nslnumeric, j));
    }

//-------------------------------------------------------

//NslInt 0d NslNumeric

    public static int eval(NslInt0 a)
    {
        return a.getint();
    }
//NslInt 1d NslNumeric

    public static int eval(int dest, NslInt1 nslnumeric, int j)
    {
        dest = nslnumeric.get(j);
        return dest;
    }

    public static int eval(NslInt1 nslnumeric, int j)
    {
        int dest = 0;
        return (eval(dest, nslnumeric, j));
    }

//NslInt 2d NslNumeric

    public static int[] eval(int[] dest, NslInt2 nslnumeric, int j)
    {
        int i;
        int [] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];

        for (i = 0; i < size1; i++)
        {
            dest[i] = nslnumeric.getint(i, j);
        }
        return dest;
    }

    public static int[] eval(NslInt2 nslnumeric, int j)
    {
        int[] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];
        int[] dest = new int[size1];
        return (eval(dest, nslnumeric, j));
    }

//--------------------------------------------------------
//NslBoolean 0d NslNumeric

    public static boolean eval(NslBoolean0 a)
    {
        return a.getboolean();
    }
//NslBoolean 1d NslNumeric

    public static boolean eval(boolean dest, NslBoolean1 nslnumeric, int j)
    {
        dest = nslnumeric.get(j);
        return dest;
    }

    public static boolean eval(NslBoolean1 nslnumeric, int j)
    {
        boolean dest = false;
        return (eval(dest, nslnumeric, j));
    }

//NslBoolean 2d NslNumeric

    public static boolean[] eval(boolean[] dest, NslBoolean2 nslnumeric, int j)
    {
        int i;
        int [] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];

        for (i = 0; i < size1; i++)
        {
            dest[i] = nslnumeric.getboolean(i, j);
        }
        return dest;
    }

    public static boolean[] eval(NslBoolean2 nslnumeric, int j)
    {
        int[] sizes = nslnumeric.getSizes();
        int size1 = sizes[0];
        boolean[] dest = new boolean[size1];
        return (eval(dest, nslnumeric, j));
    }


}  // end NslGetColumn





