/*  SCCS - @(#)NslMax.java	1.2 - 05/21/99 - 17:43:12 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

/*
 * $Log: NslMax.java,v $
 * Revision 1.2  1997/11/18 01:28:54  erhan
 * NslMin/Max now works on doubles also
 *
 * Revision 1.1  1997/07/30 21:19:30  erhan
 * nsl3.0
 *
 * Revision 1.1.1.1  1997/03/12 22:52:20  nsl
 * new dir structure
 *
 * Revision 1.1.1.1  1997/02/08 00:40:40  nsl
 *  Imported the Source directory
 *
*/
////////////////////////////////////////////////////////////
//
// Maximum number routines
//
//

/**
 Maximum number routines.
 There are two basic format for the evaluation method in
 this routine:
 1, eval(a, b) -> c
 a, b are the parameter to evaluate the maximum value of
 a and b pointwise and the result is passed out as c
 2. eval(dest, a, b) -> c
 a, b are the parameter of the evaluation function and
 <tt>dest</tt> is the temporary space to hold the result.
 The method returns the reference to <tt>dest</tt>.

 */

package nslj.src.math;

import nslj.src.lang.*;

public final class NslMax
{
    public static int eval(NslInt0 a, int b)
    {
        return eval(a.getint(), b);
    }

    public static int eval(int a, NslInt0 b)
    {
        return eval(a, b.getint());
    }

    public static int eval(NslInt0 a, NslInt0 b)
    {
        return eval(a.getint(), b.getint());
    }

    public static int eval(NslDinInt0 a, int b)
    {
        return eval(a.getint(), b);
    }

    public static int eval(int a, NslDinInt0 b)
    {
        return eval(a, b.getint());
    }

    public static int eval(NslDinInt0 a, NslDinInt0 b)
    {
        return eval(a.getint(), b.getint());
    }

    public static int eval(int a, int b)
    {
        if (a > b)
        {
            return a;
        }
        else
        {
            return b;
        }
    }

    public static int[] eval(int[] a, int[] b)
    {
        return eval(new int[a.length], a, b);
    }

    public static int[] eval(NslInt1 a, int[]b)
    {
        return eval(a.getint1(), b);
    }

    public static int[] eval(int[] a, NslInt1 b)
    {
        return eval(a, b.getint1());
    }

    public static int[] eval(NslInt1 a, NslInt1 b)
    {
        return eval(a.getint1(), b.getint1());
    }

    public static int[] eval(NslDinInt1 a, int[]b)
    {
        return eval(a.getint1(), b);
    }

    public static int[] eval(int[] a, NslDinInt1 b)
    {
        return eval(a, b.getint1());
    }

    public static int[] eval(NslDinInt1 a, NslDinInt1 b)
    {
        return eval(a.getint1(), b.getint1());
    }

    public static int[] eval(int[] dest, int[] a, int[] b)
    {
        int i;
        int len = dest.length;

        if (len != a.length || len != b.length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i = 0; i < len; i++)
        {
            if (a[i] > b[i])
            {
                dest[i] = a[i];
            }
            else
            {
                dest[i] = b[i];
            }
        }
        return dest;
    }

    public static int[] eval(int[] dest, NslInt1 a, int[] b)
    {
        return eval(dest, a.getint1(), b);
    }

    public static int[] eval(int[] dest, int[] a, NslInt1 b)
    {
        return eval(dest, a, b.getint1());
    }

    public static int[] eval(int[] dest, NslInt1 a, NslInt1 b)
    {
        return eval(dest, a.getint1(), b.getint1());
    }

    public static int[] eval(int[] dest, NslDinInt1 a, int[] b)
    {
        return eval(dest, a.getint1(), b);
    }

    public static int[] eval(int[] dest, int[] a, NslDinInt1 b)
    {
        return eval(dest, a, b.getint1());
    }

    public static int[] eval(int[] dest, NslDinInt1 a, NslDinInt1 b)
    {
        return eval(dest, a.getint1(), b.getint1());
    }

    public static int[] eval(int[] a, int b)
    {
        return eval(new int[a.length], a, b);
    }

    public static int[] eval(int[] a, NslInt0 b)
    {
        return eval(a, b.getint0());
    }

    public static int[] eval(NslInt1 a, int b)
    {
        return eval(a.getint1(), b);
    }

    public static int[] eval(NslInt1 a, NslInt0 b)
    {
        return eval(a.getint1(), b.getint());
    }

    public static int[] eval(int[] a, NslDinInt0 b)
    {
        return eval(a, b.getint0());
    }

    public static int[] eval(NslDinInt1 a, int b)
    {
        return eval(a.getint1(), b);
    }

    public static int[] eval(NslDinInt1 a, NslDinInt0 b)
    {
        return eval(a.getint1(), b.getint());
    }

    public static int[] eval(int[] dest, int[] a, int b)
    {
        int i;
        int len = dest.length;

        if (len != a.length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i = 0; i < len; i++)
        {
            if (a[i] > b)
            {
                dest[i] = a[i];
            }
            else
            {
                dest[i] = b;
            }
        }
        return dest;
    }

    public static int[] eval(int[] dest, int[] a, NslInt0 b)
    {
        return eval(dest, a, b.getint0());
    }

    public static int[] eval(int[] dest, NslInt1 a, int b)
    {
        return eval(dest, a.getint1(), b);
    }

    public static int[] eval(int[] dest, NslInt1 a, NslInt0 b)
    {
        return eval(dest, a.getint1(), b.getint());
    }

    public static int[] eval(int[] dest, int[] a, NslDinInt0 b)
    {
        return eval(dest, a, b.getint0());
    }

    public static int[] eval(int[] dest, NslDinInt1 a, int b)
    {
        return eval(dest, a.getint1(), b);
    }

    public static int[] eval(int[] dest, NslDinInt1 a, NslDinInt0 b)
    {
        return eval(dest, a.getint1(), b.getint());
    }

    public static int[] eval(int a, int[] b)
    {
        return eval(b, a);
    }

    public static int[] eval(NslInt0 a, int[] b)
    {
        return eval(b, a.getint());
    }

    public static int[] eval(int a, NslInt1 b)
    {
        return eval(b.getint1(), a);
    }

    public static int[] eval(NslInt0 a, NslInt1 b)
    {
        return eval(b.getint1(), a.getint());
    }

    public static int[] eval(NslDinInt0 a, int[] b)
    {
        return eval(b, a.getint());
    }

    public static int[] eval(int a, NslDinInt1 b)
    {
        return eval(b.getint1(), a);
    }

    public static int[] eval(NslDinInt0 a, NslDinInt1 b)
    {
        return eval(b.getint1(), a.getint());
    }

    public static int[] eval(int[] dest, int a, int[] b)
    {
        return eval(dest, b, a);
    }

    public static int[] eval(int[] dest, NslInt0 a, int[] b)
    {
        return eval(dest, b, a.getint());
    }

    public static int[] eval(int[] dest, int a, NslInt1 b)
    {
        return eval(dest, b.getint1(), a);
    }

    public static int[] eval(int[] dest, NslInt0 a, NslInt1 b)
    {
        return eval(dest, b.getint1(), a.getint());
    }

    public static int[] eval(int[] dest, NslDinInt0 a, int[] b)
    {
        return eval(dest, b, a.getint());
    }

    public static int[] eval(int[] dest, int a, NslDinInt1 b)
    {
        return eval(dest, b.getint1(), a);
    }

    public static int[] eval(int[] dest, NslDinInt0 a, NslDinInt1 b)
    {
        return eval(dest, b.getint1(), a.getint());
    }

    public static int[][] eval(int[][] a, int[][] b)
    {
        return eval(new int[a.length][a[0].length], a, b);
    }

    public static int[][] eval(NslInt2 a, int[][] b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(NslInt2 a, NslInt2 b)
    {
        return eval(a.getint2(), b.getint2());
    }

    public static int[][] eval(NslDinInt2 a, int[][] b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslDinInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(NslDinInt2 a, NslDinInt2 b)
    {
        return eval(a.getint2(), b.getint2());
    }

    public static int[][] eval(int[][] dest, int[][] a, int[][] b)
    {
        int i1, i2;
        int len1 = dest.length;
        int len2 = dest[0].length;

        if (len1 != a.length || len1 != b.length ||
                len2 != a[0].length || len2 != b[0].length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                if (a[i1][i2] > b[i1][i2])
                {
                    dest[i1][i2] = a[i1][i2];
                }
                else
                {
                    dest[i1][i2] = b[i1][i2];
                }
            }
        }
        return dest;
    }

    public static int[][] eval(int[][] dest, NslInt2 a, int[][] b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, int[][] a, NslInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(int[][] dest, NslInt2 a, NslInt2 b)
    {
        return eval(dest, a.getint2(), b.getint2());
    }

    public static int[][] eval(int[][] dest, NslDinInt2 a, int[][] b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, int[][] a, NslDinInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(int[][] dest, NslDinInt2 a, NslDinInt2 b)
    {
        return eval(dest, a.getint2(), b.getint2());
    }

    public static int[][] eval(int[][] a, int b)
    {
        return eval(new int[a.length][a[0].length], a, b);
    }

    public static int[][] eval(NslInt2 a, int b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslInt0 b)
    {
        return eval(a, b.getint());
    }

    public static int[][] eval(NslInt2 a, NslInt0 b)
    {
        return eval(a.getint2(), b.getint());
    }

    public static int[][] eval(NslDinInt2 a, int b)
    {
        return eval(a.getint2(), b);
    }

    public static int[][] eval(int[][] a, NslDinInt0 b)
    {
        return eval(a, b.getint());
    }

    public static int[][] eval(NslDinInt2 a, NslDinInt0 b)
    {
        return eval(a.getint2(), b.getint());
    }

    public static int[][] eval(int[][] dest, int[][] a, int b)
    {
        int i1, i2;
        int len1 = dest.length;
        int len2 = dest[0].length;

        if (len1 != a.length || len2 != a[0].length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                if (a[i1][i2] > b)
                {
                    dest[i1][i2] = a[i1][i2];
                }
                else
                {
                    dest[i1][i2] = b;
                }
            }
        }
        return dest;
    }

    public static int[][] eval(int[][] dest, NslInt2 a, int b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, int[][] a, NslInt0 b)
    {
        return eval(dest, a, b.getint());
    }

    public static int[][] eval(int[][] dest, NslInt2 a, NslInt0 b)
    {
        return eval(dest, a.getint2(), b.getint());
    }

    public static int[][] eval(int[][] dest, NslDinInt2 a, int b)
    {
        return eval(dest, a.getint2(), b);
    }

    public static int[][] eval(int[][] dest, int[][] a, NslDinInt0 b)
    {
        return eval(dest, a, b.getint());
    }

    public static int[][] eval(int[][] dest, NslDinInt2 a, NslDinInt0 b)
    {
        return eval(dest, a.getint2(), b.getint());
    }

    public static int[][] eval(int a, int[][] b)
    {
        return eval(b, a);
    }

    public static int[][] eval(NslInt0 a, int[][] b)
    {
        return eval(a.getint(), b);
    }

    public static int[][] eval(int a, NslInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(NslInt0 a, NslInt2 b)
    {
        return eval(a.getint(), b.getint2());
    }

    public static int[][] eval(NslDinInt0 a, int[][] b)
    {
        return eval(a.getint(), b);
    }

    public static int[][] eval(int a, NslDinInt2 b)
    {
        return eval(a, b.getint2());
    }

    public static int[][] eval(NslDinInt0 a, NslDinInt2 b)
    {
        return eval(a.getint(), b.getint2());
    }

    public static int[][] eval(int[][] dest, int a, int[][] b)
    {
        return eval(dest, b, a);
    }

    public static int[][] eval(int[][] dest, NslInt0 a, int[][] b)
    {
        return eval(dest, a.getint(), b);
    }

    public static int[][] eval(int[][] dest, int a, NslInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(int[][] dest, NslInt0 a, NslInt2 b)
    {
        return eval(dest, a.getint(), b.getint2());
    }

    public static int[][] eval(int[][] dest, NslDinInt0 a, int[][] b)
    {
        return eval(dest, a.getint(), b);
    }

    public static int[][] eval(int[][] dest, int a, NslDinInt2 b)
    {
        return eval(dest, a, b.getint2());
    }

    public static int[][] eval(int[][] dest, NslDinInt0 a, NslDinInt2 b)
    {
        return eval(dest, a.getint(), b.getint2());
    }

/* doubles */

    public static double eval(double a, double b)
    {
        if (a > b)
        {
            return a;
        }
        else
        {
            return b;
        }
    }

    public static double eval(NslDouble0 a, double b)
    {
        return eval(a.getdouble0(), b);
    }

    public static double eval(double a, NslDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double eval(NslDouble0 a, NslDouble0 b)
    {
        return eval(a.getdouble0(), b.getdouble0());
    }

    public static double eval(NslDinDouble0 a, double b)
    {
        return eval(a.getdouble0(), b);
    }

    public static double eval(double a, NslDinDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double eval(NslDinDouble0 a, NslDinDouble0 b)
    {
        return eval(a.getdouble0(), b.getdouble0());
    }

    public static double[] eval(double[] a, double[] b)
    {
        return eval(new double[a.length], a, b);
    }

    public static double[] eval(NslDouble1 a, double[] b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[] eval(double[] a, NslDouble1 b)
    {
        return eval(a, b.getdouble1());
    }

    public static double[] eval(NslDouble1 a, NslDouble1 b)
    {
        return eval(a.getdouble1(), b.getdouble1());
    }

    public static double[] eval(NslDinDouble1 a, double[] b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[] eval(double[] a, NslDinDouble1 b)
    {
        return eval(a, b.getdouble1());
    }

    public static double[] eval(NslDinDouble1 a, NslDinDouble1 b)
    {
        return eval(a.getdouble1(), b.getdouble1());
    }

    public static double[] eval(double[] dest, double[] a, double[] b)
    {
        int i;
        double len = dest.length;

        if (len != a.length || len != b.length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i = 0; i < len; i++)
        {
            if (a[i] > b[i])
            {
                dest[i] = a[i];
            }
            else
            {
                dest[i] = b[i];
            }
        }
        return dest;
    }

    public static double[] eval(double[] dest, NslDouble1 a, double[] b)
    {
        return eval(dest, a.getdouble1(), b);
    }

    public static double[] eval(double[] dest, double[] a, NslDouble1 b)
    {
        return eval(dest, a, b.getdouble1());
    }

    public static double[] eval(double[] dest, NslDouble1 a, NslDouble1 b)
    {
        return eval(dest, a.getdouble1(), b.getdouble1());
    }

    public static double[] eval(double[] dest, NslDinDouble1 a, double[] b)
    {
        return eval(dest, a.getdouble1(), b);
    }

    public static double[] eval(double[] dest, double[] a, NslDinDouble1 b)
    {
        return eval(dest, a, b.getdouble1());
    }

    public static double[] eval(double[] dest, NslDinDouble1 a, NslDinDouble1 b)
    {
        return eval(dest, a.getdouble1(), b.getdouble1());
    }

    public static double[] eval(double[] a, double b)
    {
        return eval(new double[a.length], a, b);
    }

    public static double[] eval(NslDouble1 a, double b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[] eval(double[] a, NslDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double[] eval(NslDouble1 a, NslDouble0 b)
    {
        return eval(a.getdouble1(), b.getdouble0());
    }

    public static double[] eval(NslDinDouble1 a, double b)
    {
        return eval(a.getdouble1(), b);
    }

    public static double[] eval(double[] a, NslDinDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double[] eval(NslDinDouble1 a, NslDinDouble0 b)
    {
        return eval(a.getdouble1(), b.getdouble0());
    }

    public static double[] eval(double[] dest, double[] a, double b)
    {
        int i;
        double len = dest.length;

        if (len != a.length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i = 0; i < len; i++)
        {
            if (a[i] > b)
            {
                dest[i] = a[i];
            }
            else
            {
                dest[i] = b;
            }
        }
        return dest;
    }

    public static double[] eval(double[] dest, NslDouble1 a, double b)
    {
        return eval(dest, a.getdouble1(), b);
    }

    public static double[] eval(double[] dest, double[] a, NslDouble0 b)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[] eval(double[] dest, NslDouble1 a, NslDouble0 b)
    {
        return eval(dest, a.getdouble1(), b.getdouble0());
    }

    public static double[] eval(double[] dest, NslDinDouble1 a, double b)
    {
        return eval(dest, a.getdouble1(), b);
    }

    public static double[] eval(double[] dest, double[] a, NslDinDouble0 b)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[] eval(double[] dest, NslDinDouble1 a, NslDinDouble0 b)
    {
        return eval(dest, a.getdouble1(), b.getdouble0());
    }

    public static double[] eval(double a, double[] b)
    {
        return eval(b, a);
    }

    public static double[] eval(NslDouble0 a, double[] b)
    {
        return eval(b, a.getdouble0());
    }

    public static double[] eval(double a, NslDouble1 b)
    {
        return eval(b.getdouble1(), a);
    }

    public static double[] eval(NslDouble0 a, NslDouble1 b)
    {
        return eval(b.getdouble1(), a.getdouble0());
    }

    public static double[] eval(NslDinDouble0 a, double[] b)
    {
        return eval(b, a.getdouble0());
    }

    public static double[] eval(double a, NslDinDouble1 b)
    {
        return eval(b.getdouble1(), a);
    }

    public static double[] eval(NslDinDouble0 a, NslDinDouble1 b)
    {
        return eval(b.getdouble1(), a.getdouble0());
    }

    public static double[] eval(double[] dest, double a, double[] b)
    {
        return eval(dest, b, a);
    }

    public static double[] eval(double[] dest, NslDouble0 a, double[] b)
    {
        return eval(dest, b, a.getdouble0());
    }

    public static double[] eval(double[] dest, double a, NslDouble1 b)
    {
        return eval(dest, b.getdouble1(), a);
    }

    public static double[] eval(double[] dest, NslDouble0 a, NslDouble1 b)
    {
        return eval(dest, b.getdouble1(), a.getdouble0());
    }

    public static double[] eval(double[] dest, NslDinDouble0 a, double[] b)
    {
        return eval(dest, b, a.getdouble0());
    }

    public static double[] eval(double[] dest, double a, NslDinDouble1 b)
    {
        return eval(dest, b.getdouble1(), a);
    }

    public static double[] eval(double[] dest, NslDinDouble0 a, NslDinDouble1 b)
    {
        return eval(dest, b.getdouble1(), a.getdouble0());
    }

    public static double[][] eval(double[][] a, double[][] b)
    {
        return eval(new double[a.length][a[0].length], a, b);
    }

    public static double[][] eval(NslDouble2 a, double[][] b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDouble2 b)
    {
        return eval(a, b.getdouble2());
    }

    public static double[][] eval(NslDouble2 a, NslDouble2 b)
    {
        return eval(a.getdouble2(), b.getdouble2());
    }

    public static double[][] eval(NslDinDouble2 a, double[][] b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDinDouble2 b)
    {
        return eval(a, b.getdouble2());
    }

    public static double[][] eval(NslDinDouble2 a, NslDinDouble2 b)
    {
        return eval(a.getdouble2(), b.getdouble2());
    }

    public static double[][] eval(double[][] dest, double[][] a, double[][] b)
    {
        int i1, i2;
        double len1 = dest.length;
        double len2 = dest[0].length;

        if (len1 != a.length || len1 != b.length ||
                len2 != a[0].length || len2 != b[0].length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                if (a[i1][i2] > b[i1][i2])
                {
                    dest[i1][i2] = a[i1][i2];
                }
                else
                {
                    dest[i1][i2] = b[i1][i2];
                }
            }
        }
        return dest;
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, double[][] b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDouble2 b)
    {
        return eval(dest, a, b.getdouble2());
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, NslDouble2 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble2());
    }

    public static double[][] eval(double[][] dest, NslDinDouble2 a, double[][] b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDinDouble2 b)
    {
        return eval(dest, a, b.getdouble2());
    }

    public static double[][] eval(double[][] dest, NslDinDouble2 a, NslDinDouble2 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble2());
    }

    public static double[][] eval(double[][] a, double b)
    {
        return eval(new double[a.length][a[0].length], a, b);
    }

    public static double[][] eval(NslDouble2 a, double b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double[][] eval(NslDouble2 a, NslDouble0 b)
    {
        return eval(a.getdouble2(), b.getdouble0());
    }

    public static double[][] eval(NslDinDouble2 a, double b)
    {
        return eval(a.getdouble2(), b);
    }

    public static double[][] eval(double[][] a, NslDinDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double[][] eval(NslDinDouble2 a, NslDinDouble0 b)
    {
        return eval(a.getdouble2(), b.getdouble0());
    }

    public static double[][] eval(double[][] dest, double[][] a, double b)
    {
        int i1, i2;
        double len1 = dest.length;
        double len2 = dest[0].length;

        if (len1 != a.length || len2 != a[0].length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                if (a[i1][i2] > b)
                {
                    dest[i1][i2] = a[i1][i2];
                }
                else
                {
                    dest[i1][i2] = b;
                }
            }
        }
        return dest;
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, double b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDouble0 b)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[][] eval(double[][] dest, NslDouble2 a, NslDouble0 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble0());
    }

    public static double[][] eval(double[][] dest, NslDinDouble2 a, double b)
    {
        return eval(dest, a.getdouble2(), b);
    }

    public static double[][] eval(double[][] dest, double[][] a, NslDinDouble0 b)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[][] eval(double[][] dest, NslDinDouble2 a, NslDinDouble0 b)
    {
        return eval(dest, a.getdouble2(), b.getdouble0());
    }

    public static double[][] eval(double a, double[][] b)
    {
        return eval(b, a);
    }

    public static double[][] eval(NslDouble0 a, double[][] b)
    {
        return eval(b, a.getdouble0());
    }

    public static double[][] eval(double a, NslDouble2 b)
    {
        return eval(b.getdouble2(), a);
    }

    public static double[][] eval(NslDouble0 a, NslDouble2 b)
    {
        return eval(b.getdouble2(), a.getdouble0());
    }

    public static double[][] eval(NslDinDouble0 a, double[][] b)
    {
        return eval(b, a.getdouble0());
    }

    public static double[][] eval(double a, NslDinDouble2 b)
    {
        return eval(b.getdouble2(), a);
    }

    public static double[][] eval(NslDinDouble0 a, NslDinDouble2 b)
    {
        return eval(b.getdouble2(), a.getdouble0());
    }

    public static double[][] eval(double[][] dest, double a, double[][] b)
    {
        return eval(dest, b, a);
    }

    public static double[][] eval(double[][] dest, NslDouble0 a, double[][] b)
    {
        return eval(dest, b, a.getdouble0());
    }

    public static double[][] eval(double[][] dest, double a, NslDouble2 b)
    {
        return eval(dest, b.getdouble2(), a);
    }

    public static double[][] eval(double[][] dest, NslDouble0 a, NslDouble2 b)
    {
        return eval(dest, b.getdouble2(), a.getdouble0());
    }

    public static double[][] eval(double[][] dest, NslDinDouble0 a, double[][] b)
    {
        return eval(dest, b, a.getdouble0());
    }

    public static double[][] eval(double[][] dest, double a, NslDinDouble2 b)
    {
        return eval(dest, b.getdouble2(), a);
    }

    public static double[][] eval(double[][] dest, NslDinDouble0 a, NslDinDouble2 b)
    {
        return eval(dest, b.getdouble2(), a.getdouble0());
    }

/* floats */

    public static float eval(float a, float b)
    {
        if (a > b)
        {
            return a;
        }
        else
        {
            return b;
        }
    }

    public static float eval(NslFloat0 a, float b)
    {
        return eval(a.getfloat0(), b);
    }

    public static float eval(float a, NslFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float eval(NslFloat0 a, NslFloat0 b)
    {
        return eval(a.getfloat0(), b.getfloat0());
    }

    public static float eval(NslDinFloat0 a, float b)
    {
        return eval(a.getfloat0(), b);
    }

    public static float eval(float a, NslDinFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float eval(NslDinFloat0 a, NslDinFloat0 b)
    {
        return eval(a.getfloat0(), b.getfloat0());
    }

    public static float[] eval(float[] a, float[] b)
    {
        return eval(new float[a.length], a, b);
    }

    public static float[] eval(NslFloat1 a, float[] b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[] eval(float[] a, NslFloat1 b)
    {
        return eval(a, b.getfloat1());
    }

    public static float[] eval(NslFloat1 a, NslFloat1 b)
    {
        return eval(a.getfloat1(), b.getfloat1());
    }

    public static float[] eval(NslDinFloat1 a, float[] b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[] eval(float[] a, NslDinFloat1 b)
    {
        return eval(a, b.getfloat1());
    }

    public static float[] eval(NslDinFloat1 a, NslDinFloat1 b)
    {
        return eval(a.getfloat1(), b.getfloat1());
    }

    public static float[] eval(float[] dest, float[] a, float[] b)
    {
        int i;
        float len = dest.length;

        if (len != a.length || len != b.length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i = 0; i < len; i++)
        {
            if (a[i] > b[i])
            {
                dest[i] = a[i];
            }
            else
            {
                dest[i] = b[i];
            }
        }
        return dest;
    }

    public static float[] eval(float[] dest, NslFloat1 a, float[] b)
    {
        return eval(dest, a.getfloat1(), b);
    }

    public static float[] eval(float[] dest, float[] a, NslFloat1 b)
    {
        return eval(dest, a, b.getfloat1());
    }

    public static float[] eval(float[] dest, NslFloat1 a, NslFloat1 b)
    {
        return eval(dest, a.getfloat1(), b.getfloat1());
    }

    public static float[] eval(float[] dest, NslDinFloat1 a, float[] b)
    {
        return eval(dest, a.getfloat1(), b);
    }

    public static float[] eval(float[] dest, float[] a, NslDinFloat1 b)
    {
        return eval(dest, a, b.getfloat1());
    }

    public static float[] eval(float[] dest, NslDinFloat1 a, NslDinFloat1 b)
    {
        return eval(dest, a.getfloat1(), b.getfloat1());
    }

    public static float[] eval(float[] a, float b)
    {
        return eval(new float[a.length], a, b);
    }

    public static float[] eval(NslFloat1 a, float b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[] eval(float[] a, NslFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float[] eval(NslFloat1 a, NslFloat0 b)
    {
        return eval(a.getfloat1(), b.getfloat0());
    }

    public static float[] eval(NslDinFloat1 a, float b)
    {
        return eval(a.getfloat1(), b);
    }

    public static float[] eval(float[] a, NslDinFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float[] eval(NslDinFloat1 a, NslDinFloat0 b)
    {
        return eval(a.getfloat1(), b.getfloat0());
    }

    public static float[] eval(float[] dest, float[] a, float b)
    {
        int i;
        float len = dest.length;

        if (len != a.length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i = 0; i < len; i++)
        {
            if (a[i] > b)
            {
                dest[i] = a[i];
            }
            else
            {
                dest[i] = b;
            }
        }
        return dest;
    }

    public static float[] eval(float[] dest, NslFloat1 a, float b)
    {
        return eval(dest, a.getfloat1(), b);
    }

    public static float[] eval(float[] dest, float[] a, NslFloat0 b)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[] eval(float[] dest, NslFloat1 a, NslFloat0 b)
    {
        return eval(dest, a.getfloat1(), b.getfloat0());
    }

    public static float[] eval(float[] dest, NslDinFloat1 a, float b)
    {
        return eval(dest, a.getfloat1(), b);
    }

    public static float[] eval(float[] dest, float[] a, NslDinFloat0 b)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[] eval(float[] dest, NslDinFloat1 a, NslDinFloat0 b)
    {
        return eval(dest, a.getfloat1(), b.getfloat0());
    }

    public static float[] eval(float a, float[] b)
    {
        return eval(b, a);
    }

    public static float[] eval(NslFloat0 a, float[] b)
    {
        return eval(b, a.getfloat0());
    }

    public static float[] eval(float a,  NslFloat1 b)
    {
        return eval(b.getfloat1(), a);
    }

    public static float[] eval(NslFloat0 a, NslFloat1 b)
    {
        return eval(b.getfloat1(), a.getfloat0());
    }

    public static float[] eval(NslDinFloat0 a, float[] b)
    {
        return eval(b, a.getfloat0());
    }

    public static float[] eval(float a,  NslDinFloat1 b)
    {
        return eval(b.getfloat1(), a);
    }

    public static float[] eval(NslDinFloat0 a, NslDinFloat1 b)
    {
        return eval(b.getfloat1(), a.getfloat0());
    }

    public static float[] eval(float[] dest, float a, float[] b)
    {
        return eval(dest, b, a);
    }

    public static float[] eval(float[] dest, NslFloat0 a, float[] b)
    {
        return eval(dest, b, a.getfloat0());
    }

    public static float[] eval(float[] dest, float a,  NslFloat1 b)
    {
        return eval(dest, b.getfloat1(), a);
    }

    public static float[] eval(float[] dest, NslFloat0 a, NslFloat1 b)
    {
        return eval(dest, b.getfloat1(), a.getfloat0());
    }

    public static float[] eval(float[] dest, NslDinFloat0 a, float[] b)
    {
        return eval(dest, b, a.getfloat0());
    }

    public static float[] eval(float[] dest, float a,  NslDinFloat1 b)
    {
        return eval(dest, b.getfloat1(), a);
    }

    public static float[] eval(float[] dest, NslDinFloat0 a, NslDinFloat1 b)
    {
        return eval(dest, b.getfloat1(), a.getfloat0());
    }

    public static float[][] eval(float[][] a, float[][] b)
    {
        return eval(new float[a.length][a[0].length], a, b);
    }

    public static float[][] eval(NslFloat2 a, float[][] b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslFloat2 b)
    {
        return eval(a, b.getfloat2());
    }

    public static float[][] eval(NslFloat2 a, NslFloat2 b)
    {
        return eval(a.getfloat2(), b.getfloat2());
    }

    public static float[][] eval(NslDinFloat2 a, float[][] b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslDinFloat2 b)
    {
        return eval(a, b.getfloat2());
    }

    public static float[][] eval(NslDinFloat2 a, NslDinFloat2 b)
    {
        return eval(a.getfloat2(), b.getfloat2());
    }

    public static float[][] eval(float[][] dest, float[][] a, float[][] b)
    {
        int i1, i2;
        float len1 = dest.length;
        float len2 = dest[0].length;

        if (len1 != a.length || len1 != b.length ||
                len2 != a[0].length || len2 != b[0].length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                if (a[i1][i2] > b[i1][i2])
                {
                    dest[i1][i2] = a[i1][i2];
                }
                else
                {
                    dest[i1][i2] = b[i1][i2];
                }
            }
        }
        return dest;
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, float[][] b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, float[][] a, NslFloat2 b)
    {
        return eval(dest, a, b.getfloat2());
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, NslFloat2 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat2());
    }

    public static float[][] eval(float[][] dest, NslDinFloat2 a, float[][] b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, float[][] a, NslDinFloat2 b)
    {
        return eval(dest, a, b.getfloat2());
    }

    public static float[][] eval(float[][] dest, NslDinFloat2 a, NslDinFloat2 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat2());
    }

    public static float[][] eval(float[][] a, float b)
    {
        return eval(new float[a.length][a[0].length], a, b);
    }

    public static float[][] eval(NslFloat2 a, float b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float[][] eval(NslFloat2 a, NslFloat0 b)
    {
        return eval(a.getfloat2(), b.getfloat0());
    }

    public static float[][] eval(NslDinFloat2 a, float b)
    {
        return eval(a.getfloat2(), b);
    }

    public static float[][] eval(float[][] a, NslDinFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float[][] eval(NslDinFloat2 a, NslDinFloat0 b)
    {
        return eval(a.getfloat2(), b.getfloat0());
    }

    public static float[][] eval(float[][] dest, float[][] a, float b)
    {
        int i1, i2;
        float len1 = dest.length;
        float len2 = dest[0].length;

        if (len1 != a.length || len2 != a[0].length)
        {
            System.out.println("Inconsistent array size");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                if (a[i1][i2] > b)
                {
                    dest[i1][i2] = a[i1][i2];
                }
                else
                {
                    dest[i1][i2] = b;
                }
            }
        }
        return dest;
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, float b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, float[][] a, NslFloat0 b)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[][] eval(float[][] dest, NslFloat2 a, NslFloat0 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat0());
    }

    public static float[][] eval(float[][] dest, NslDinFloat2 a, float b)
    {
        return eval(dest, a.getfloat2(), b);
    }

    public static float[][] eval(float[][] dest, float[][] a, NslDinFloat0 b)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[][] eval(float[][] dest, NslDinFloat2 a, NslDinFloat0 b)
    {
        return eval(dest, a.getfloat2(), b.getfloat0());
    }

    public static float[][] eval(float a, float[][] b)
    {
        return eval(b, a);
    }

    public static float[][] eval(NslFloat0 a, float[][] b)
    {
        return eval(b, a.getfloat0());
    }

    public static float[][] eval(float a, NslFloat2 b)
    {
        return eval(b.getfloat2(), a);
    }

    public static float[][] eval(NslFloat0 a, NslFloat2 b)
    {
        return eval(b.getfloat2(), a.getfloat0());
    }

    public static float[][] eval(NslDinFloat0 a, float[][] b)
    {
        return eval(b, a.getfloat0());
    }

    public static float[][] eval(float a, NslDinFloat2 b)
    {
        return eval(b.getfloat2(), a);
    }

    public static float[][] eval(NslDinFloat0 a, NslDinFloat2 b)
    {
        return eval(b.getfloat2(), a.getfloat0());
    }

    public static float[][] eval(float[][] dest, float a, float[][] b)
    {
        return eval(dest, b, a);
    }

    public static float[][] eval(float[][] dest, NslFloat0 a, float[][] b)
    {
        return eval(dest, b, a.getfloat0());
    }

    public static float[][] eval(float[][] dest, float a, NslFloat2 b)
    {
        return eval(dest, b.getfloat2(), a);
    }

    public static float[][] eval(float[][] dest, NslFloat0 a, NslFloat2 b)
    {
        return eval(dest, b.getfloat2(), a.getfloat0());
    }

    public static float[][] eval(float[][] dest, NslDinFloat0 a, float[][] b)
    {
        return eval(dest, b, a.getfloat0());
    }

    public static float[][] eval(float[][] dest, float a, NslDinFloat2 b)
    {
        return eval(dest, b.getfloat2(), a);
    }

    public static float[][] eval(float[][] dest, NslDinFloat0 a, NslDinFloat2 b)
    {
        return eval(dest, b.getfloat2(), a.getfloat0());
    }

    public static float[][][] eval(float[][][] a, float b)
    {
        float[][][] dest = new float[a.length][a[0].length][a[0][0].length];
        return eval(dest, a, b);
    }

    public static float[][][] eval(NslFloat3 a, float b)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] a, NslFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float[][][] eval(NslFloat3 a, NslFloat0 b)
    {
        return eval(a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(NslDinFloat3 a, float b)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] a, NslDinFloat0 b)
    {
        return eval(a, b.getfloat0());
    }

    public static float[][][] eval(NslDinFloat3 a, NslDinFloat0 b)
    {
        return eval(a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, float b)
    {
        int i1, i2, i3;
        int len1 = dest.length;
        int len2 = dest[0].length;
        int len3 = dest[0][0].length;
        if (len1 != a.length || len2 != a[0].length || len3 != a[0][0].length)
        {
            System.out.println("Array size inconsistent");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                for (i3 = 0; i3 < len3; i3++)
                {
                    if (a[i1][i2][i3] > b)
                    {
                        dest[i1][i2][i3] = a[i1][i2][i3];
                    }
                    else
                    {
                        dest[i1][i2][i3] = b;
                    }
                }
            }
        }
        return dest;
    }

    public static float[][][] eval(float[][][] dest, NslFloat3 a, float b)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, NslFloat0 b)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, NslFloat3 a, NslFloat0 b)
    {
        return eval(dest, a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, NslDinFloat3 a, float b)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, NslDinFloat0 b)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, NslDinFloat3 a, NslDinFloat0 b)
    {
        return eval(dest, a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(float b, float[][][] a)
    {
        return eval(a, b);
    }

    public static float[][][] eval(NslFloat0 b, float[][][] a)
    {
        return eval(a, b.getfloat0());
    }

    public static float[][][] eval(float b, NslFloat3 a)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(NslFloat0 b, NslFloat3 a)
    {
        return eval(a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(NslDinFloat0 b, float[][][] a)
    {
        return eval(a, b.getfloat0());
    }

    public static float[][][] eval(float b, NslDinFloat3 a)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(NslDinFloat0 b, NslDinFloat3 a)
    {
        return eval(a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, float b, float[][][] a)
    {
        return eval(dest, a, b);
    }

    public static float[][][] eval(float[][][] dest, NslFloat0 b, float[][][] a)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, float b, NslFloat3 a)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, NslFloat0 b, NslFloat3 a)
    {
        return eval(dest, a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, NslDinFloat0 b, float[][][] a)
    {
        return eval(dest, a, b.getfloat0());
    }

    public static float[][][] eval(float[][][] dest, float b, NslDinFloat3 a)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, NslDinFloat0 b, NslDinFloat3 a)
    {
        return eval(dest, a.getfloat3(), b.getfloat0());
    }

    public static float[][][] eval(float[][][] a, float[][][] b)
    {
        float[][][] dest = new float[a.length][a[0].length][a[0][0].length];
        return eval(dest, a, b);
    }

    public static float[][][] eval(NslFloat3 a, float[][][] b)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] a, NslFloat3 b)
    {
        return eval(a, b.getfloat3());
    }

    public static float[][][] eval(NslFloat3 a, NslFloat3 b)
    {
        return eval(a.getfloat3(), b.getfloat3());
    }

    public static float[][][] eval(NslDinFloat3 a, float[][][] b)
    {
        return eval(a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] a, NslDinFloat3 b)
    {
        return eval(a, b.getfloat3());
    }

    public static float[][][] eval(NslDinFloat3 a, NslDinFloat3 b)
    {
        return eval(a.getfloat3(), b.getfloat3());
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, float[][][] b)
    {
        int i1, i2, i3;
        int len1 = dest.length;
        int len2 = dest[0].length;
        int len3 = dest[0][0].length;
        if (len1 != a.length || len2 != a[0].length || len3 != a[0][0].length ||
                len1 != b.length || len2 != b[0].length || len3 != b[0][0].length)
        {
            System.out.println("Array size inconsistent");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                for (i3 = 0; i3 < len3; i3++)
                {
                    if (a[i1][i2][i3] > b[i1][i2][i3])
                    {
                        dest[i1][i2][i3] = a[i1][i2][i3];
                    }
                    else
                    {
                        dest[i1][i2][i3] = b[i1][i2][i3];
                    }
                }
            }
        }
        return dest;
    }

    public static float[][][] eval(float[][][] dest, NslFloat3 a, float[][][] b)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, NslFloat3 b)
    {
        return eval(dest, a, b.getfloat3());
    }

    public static float[][][] eval(float[][][] dest, NslFloat3 a, NslFloat3 b)
    {
        return eval(dest, a.getfloat3(), b.getfloat3());
    }

    public static float[][][] eval(float[][][] dest, NslDinFloat3 a, float[][][] b)
    {
        return eval(dest, a.getfloat3(), b);
    }

    public static float[][][] eval(float[][][] dest, float[][][] a, NslDinFloat3 b)
    {
        return eval(dest, a, b.getfloat3());
    }

    public static float[][][] eval(float[][][] dest, NslDinFloat3 a, NslDinFloat3 b)
    {
        return eval(dest, a.getfloat3(), b.getfloat3());
    }

// native 3d by karan, Karan, KARAN DOUBLE

    public static double[][][] eval(double[][][] a, double b)
    {
        double[][][] dest = new double[a.length][a[0].length][a[0][0].length];
        return eval(dest, a, b);
    }

    public static double[][][] eval(NslDouble3 a, double b)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] a, NslDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double[][][] eval(NslDouble3 a, NslDouble0 b)
    {
        return eval(a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(NslDinDouble3 a, double b)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] a, NslDinDouble0 b)
    {
        return eval(a, b.getdouble0());
    }

    public static double[][][] eval(NslDinDouble3 a, NslDinDouble0 b)
    {
        return eval(a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, double b)
    {
        int i1, i2, i3;
        int len1 = dest.length;
        int len2 = dest[0].length;
        int len3 = dest[0][0].length;
        if (len1 != a.length || len2 != a[0].length || len3 != a[0][0].length)
        {
            System.out.println("Array size inconsistent");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                for (i3 = 0; i3 < len3; i3++)
                {
                    if (a[i1][i2][i3] > b)
                    {
                        dest[i1][i2][i3] = a[i1][i2][i3];
                    }
                    else
                    {
                        dest[i1][i2][i3] = b;
                    }
                }
            }
        }
        return dest;
    }

    public static double[][][] eval(double[][][] dest, NslDouble3 a, double b)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, NslDouble0 b)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, NslDouble3 a, NslDouble0 b)
    {
        return eval(dest, a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, NslDinDouble3 a, double b)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, NslDinDouble0 b)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, NslDinDouble3 a, NslDinDouble0 b)
    {
        return eval(dest, a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(double b, double[][][] a)
    {
        return eval(a, b);
    }

    public static double[][][] eval(NslDouble0 b, double[][][] a)
    {
        return eval(a, b.getdouble0());
    }

    public static double[][][] eval(double b, NslDouble3 a)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(NslDouble0 b, NslDouble3 a)
    {
        return eval(a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(NslDinDouble0 b, double[][][] a)
    {
        return eval(a, b.getdouble0());
    }

    public static double[][][] eval(double b, NslDinDouble3 a)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(NslDinDouble0 b, NslDinDouble3 a)
    {
        return eval(a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, double b, double[][][] a)
    {
        return eval(dest, a, b);
    }

    public static double[][][] eval(double[][][] dest, NslDouble0 b, double[][][] a)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, double b, NslDouble3 a)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, NslDouble0 b, NslDouble3 a)
    {
        return eval(dest, a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, NslDinDouble0 b, double[][][] a)
    {
        return eval(dest, a, b.getdouble0());
    }

    public static double[][][] eval(double[][][] dest, double b, NslDinDouble3 a)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, NslDinDouble0 b, NslDinDouble3 a)
    {
        return eval(dest, a.getdouble3(), b.getdouble0());
    }

    public static double[][][] eval(double[][][] a, double[][][] b)
    {
        double[][][] dest = new double[a.length][a[0].length][a[0][0].length];
        return eval(dest, a, b);
    }

    public static double[][][] eval(NslDouble3 a, double[][][] b)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] a, NslDouble3 b)
    {
        return eval(a, b.getdouble3());
    }

    public static double[][][] eval(NslDouble3 a, NslDouble3 b)
    {
        return eval(a.getdouble3(), b.getdouble3());
    }

    public static double[][][] eval(NslDinDouble3 a, double[][][] b)
    {
        return eval(a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] a, NslDinDouble3 b)
    {
        return eval(a, b.getdouble3());
    }

    public static double[][][] eval(NslDinDouble3 a, NslDinDouble3 b)
    {
        return eval(a.getdouble3(), b.getdouble3());
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, double[][][] b)
    {
        int i1, i2, i3;
        int len1 = dest.length;
        int len2 = dest[0].length;
        int len3 = dest[0][0].length;
        if (len1 != a.length || len2 != a[0].length || len3 != a[0][0].length ||
                len1 != b.length || len2 != b[0].length || len3 != b[0][0].length)
        {
            System.out.println("Array size inconsistent");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                for (i3 = 0; i3 < len3; i3++)
                {
                    if (a[i1][i2][i3] > b[i1][i2][i3])
                    {
                        dest[i1][i2][i3] = a[i1][i2][i3];
                    }
                    else
                    {
                        dest[i1][i2][i3] = b[i1][i2][i3];
                    }
                }
            }
        }
        return dest;
    }

    public static double[][][] eval(double[][][] dest, NslDouble3 a, double[][][] b)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, NslDouble3 b)
    {
        return eval(dest, a, b.getdouble3());
    }

    public static double[][][] eval(double[][][] dest, NslDouble3 a, NslDouble3 b)
    {
        return eval(dest, a.getdouble3(), b.getdouble3());
    }

    public static double[][][] eval(double[][][] dest, NslDinDouble3 a, double[][][] b)
    {
        return eval(dest, a.getdouble3(), b);
    }

    public static double[][][] eval(double[][][] dest, double[][][] a, NslDinDouble3 b)
    {
        return eval(dest, a, b.getdouble3());
    }

    public static double[][][] eval(double[][][] dest, NslDinDouble3 a, NslDinDouble3 b)
    {
        return eval(dest, a.getdouble3(), b.getdouble3());
    }

//------------------------------------
// 3d native ints by karan, Karan, KARAN


    public static int[][][] eval(int[][][] a, int b)
    {
        int[][][] dest = new int[a.length][a[0].length][a[0][0].length];
        return eval(dest, a, b);
    }

    public static int[][][] eval(NslInt3 a, int b)
    {
        return eval(a.getint3(), b);
    }

    public static int[][][] eval(int[][][] a, NslInt0 b)
    {
        return eval(a, b.getint0());
    }

    public static int[][][] eval(NslInt3 a, NslInt0 b)
    {
        return eval(a.getint3(), b.getint0());
    }

    public static int[][][] eval(NslDinInt3 a, int b)
    {
        return eval(a.getint3(), b);
    }

    public static int[][][] eval(int[][][] a, NslDinInt0 b)
    {
        return eval(a, b.getint0());
    }

    public static int[][][] eval(NslDinInt3 a, NslDinInt0 b)
    {
        return eval(a.getint3(), b.getint0());
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, int b)
    {
        int i1, i2, i3;
        int len1 = dest.length;
        int len2 = dest[0].length;
        int len3 = dest[0][0].length;
        if (len1 != a.length || len2 != a[0].length || len3 != a[0][0].length)
        {
            System.out.println("Array size inconsistent");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                for (i3 = 0; i3 < len3; i3++)
                {
                    if (a[i1][i2][i3] > b)
                    {
                        dest[i1][i2][i3] = a[i1][i2][i3];
                    }
                    else
                    {
                        dest[i1][i2][i3] = b;
                    }
                }
            }
        }
        return dest;
    }

    public static int[][][] eval(int[][][] dest, NslInt3 a, int b)
    {
        return eval(dest, a.getint3(), b);
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, NslInt0 b)
    {
        return eval(dest, a, b.getint0());
    }

    public static int[][][] eval(int[][][] dest, NslInt3 a, NslInt0 b)
    {
        return eval(dest, a.getint3(), b.getint0());
    }

    public static int[][][] eval(int[][][] dest, NslDinInt3 a, int b)
    {
        return eval(dest, a.getint3(), b);
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, NslDinInt0 b)
    {
        return eval(dest, a, b.getint0());
    }

    public static int[][][] eval(int[][][] dest, NslDinInt3 a, NslDinInt0 b)
    {
        return eval(dest, a.getint3(), b.getint0());
    }

    public static int[][][] eval(int[][][] a, int[][][] b)
    {
        int[][][] dest = new int[a.length][a[0].length][a[0][0].length];
        return eval(dest, a, b);
    }

    public static int[][][] eval(NslInt3 a, int[][][] b)
    {
        return eval(a.getint3(), b);
    }

    public static int[][][] eval(int[][][] a, NslInt3 b)
    {
        return eval(a, b.getint3());
    }

    public static int[][][] eval(NslInt3 a, NslInt3 b)
    {
        return eval(a.getint3(), b.getint3());
    }

    public static int[][][] eval(NslDinInt3 a, int[][][] b)
    {
        return eval(a.getint3(), b);
    }

    public static int[][][] eval(int[][][] a, NslDinInt3 b)
    {
        return eval(a, b.getint3());
    }

    public static int[][][] eval(NslDinInt3 a, NslDinInt3 b)
    {
        return eval(a.getint3(), b.getint3());
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, int[][][] b)
    {
        int i1, i2, i3;
        int len1 = dest.length;
        int len2 = dest[0].length;
        int len3 = dest[0][0].length;
        if (len1 != a.length || len2 != a[0].length || len3 != a[0][0].length ||
                len1 != b.length || len2 != b[0].length || len3 != b[0][0].length)
        {
            System.out.println("Array size inconsistent");
            return dest;
        }

        for (i1 = 0; i1 < len1; i1++)
        {
            for (i2 = 0; i2 < len2; i2++)
            {
                for (i3 = 0; i3 < len3; i3++)
                {
                    if (a[i1][i2][i3] > b[i1][i2][i3])
                    {
                        dest[i1][i2][i3] = a[i1][i2][i3];
                    }
                    else
                    {
                        dest[i1][i2][i3] = b[i1][i2][i3];
                    }
                }
            }
        }
        return dest;
    }

    public static int[][][] eval(int[][][] dest, NslInt3 a, int[][][] b)
    {
        return eval(dest, a.getint3(), b);
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, NslInt3 b)
    {
        return eval(dest, a, b.getint3());
    }

    public static int[][][] eval(int[][][] dest, NslInt3 a, NslInt3 b)
    {
        return eval(dest, a.getint3(), b.getint3());
    }

    public static int[][][] eval(int[][][] dest, NslDinInt3 a, int[][][] b)
    {
        return eval(dest, a.getint3(), b);
    }

    public static int[][][] eval(int[][][] dest, int[][][] a, NslDinInt3 b)
    {
        return eval(dest, a, b.getint3());
    }

    public static int[][][] eval(int[][][] dest, NslDinInt3 a, NslDinInt3 b)
    {
        return eval(dest, a.getint3(), b.getint3());
    }

// 4d natives
// 4d nsltypes

}

