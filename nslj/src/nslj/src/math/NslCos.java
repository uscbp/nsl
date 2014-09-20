/*  SCCS - @(#)NslCos.java	1.3 - 09/01/99 - 00:18:20 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////
//
// Cos routines
//
//

package nslj.src.math;

/**
 * Cos routines.
 * There are two basic format for the evaluation method in
 * this routine:
 * 1, eval(a) -> c
 * a is the parameter of the evaluation function to do
 * cos(a) pointwise and the result is passed out as c
 * 2. eval(dest, a) -> c
 * a, b are the parameter of the evaluation function and
 * <tt>dest</tt> is the temporary space to hold the result.
 * The method returns the reference to <tt>dest</tt>.
 */

public final class NslCos extends NslUnaryOperator
{

    public int value(int a)
    {
        return (int) Math.cos((double) a);
    }

    public float value(float a)
    {
        return (float) Math.cos(a);
    }

    public double value(double a)
    {
        return Math.cos(a);
    }

    public static int value(int[] a, int[] b)
    {
        double l=NslOperator.sqrt.eval(NslSum.eval(NslOperator.pow.eval(a,2)))*NslOperator.sqrt.eval(NslSum.eval(NslOperator.pow.eval(b,2)));
        if(l==0)
            return 1;
        else
            return (int)NslDiv.eval(NslProd.eval(a,b),l);
    }

    public static double value(double[] a, double[] b)
    {
        double l=NslOperator.sqrt.eval(NslSum.eval(NslOperator.pow.eval(a,2.0)))*NslOperator.sqrt.eval(NslSum.eval(NslOperator.pow.eval(b,2.0)));
        if(l==0)
            return 1.0;
        else
            return NslDiv.eval(NslProd.eval(a,b),l);
    }

    public static float value(float[] a, float[] b)
    {
        double l=NslOperator.sqrt.eval(NslSum.eval(NslOperator.pow.eval(a,2)))*NslOperator.sqrt.eval(NslSum.eval(NslOperator.pow.eval(b,2)));
        if(l==0)
            return 1f;
        else
            return (float)NslDiv.eval(NslProd.eval(a,b),l);
    }
}

