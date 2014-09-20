/*  SCCS - %W% - %G% - %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////
//
// ArcCos routines
//
//

package nslj.src.math;

import nslj.src.lang.NslInt1;
import nslj.src.lang.NslDouble1;
import nslj.src.lang.NslFloat1;

/**
 * ArcCos routines.
 * There are two basic format for the evaluation method in
 * this routine:
 * 1, eval(a) -> c
 * a is the parameter of the evaluation function to do
 * acos(a) pointwise and the result is passed out as c
 * 2. eval(dest, a) -> c
 * a, b are the parameter of the evaluation function and
 * <tt>dest</tt> is the temporary space to hold the result.
 * The method returns the reference to <tt>dest</tt>.
 */

public final class NslArcCos extends NslUnaryOperator
{

    public int value(int a)
    {
        return (int) Math.acos((double) a);
    }

    public float value(float a)
    {
        return (float) Math.acos(a);
    }

    public double value(double a)
    {
        return Math.acos(a);
    }

    public static int value(int[] a, int[] b)
    {
        double c=NslCos.value(a,b);
        if (Math.abs(c)>1)
        {
            if (c>0)
                return 0;
            else
                return (int)Math.PI;
        }
        return (int)Math.acos(c);
    }

    public static int value(NslInt1 a, NslInt1 b)
    {
        return value(a.get(), b.get());
    }

    public static double value(double[] a, double[] b)
    {
        double c=NslCos.value(a,b);
        if (Math.abs(c)>1)
        {
            if (c>0)
                return 0;
            else
                return Math.PI;
        }
        return Math.acos(c);
    }

    public static double value(NslDouble1 a, NslDouble1 b)
    {
        return value(a.get(),b.get());
    }

    public static float value(float[] a, float[] b)
    {
        double c=NslCos.value(a,b);
        if (Math.abs(c)>1)
        {
            if (c>0)
                return 0;
            else
                return (float)Math.PI;
        }
        return (float)Math.acos(c);
    }

    public static float value(NslFloat1 a, NslFloat1 b)
    {
        return value(a.get(),b.get());
    }
}


