/*  SCCS - @(#)NslRint.java	1.3 - 09/01/99 - 00:18:23 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

////////////////////////////////////////////////////////////
//
// Rint routines
//
//

package nslj.src.math;

/**
 * Rint routines.
 * There are two basic format for the evaluation method in
 * this routine:
 * 1, eval(a) -> c
 * a is the parameter of the evaluation function to round
 * a pointwise and the result is passed out as c
 * 2. eval(dest, a) -> c
 * a, b are the parameter of the evaluation function and
 * <tt>dest</tt> is the temporary space to hold the result.
 * The method returns the reference to <tt>dest</tt>.
 */

public final class NslRint extends NslUnaryOperator
{

    public int value(int a)
    {
        return (int) Math.rint((double) a);
    }

    public float value(float a)
    {
        return (float) Math.rint(a);
    }

    public double value(double a)
    {
        return Math.rint(a);
    }

}
