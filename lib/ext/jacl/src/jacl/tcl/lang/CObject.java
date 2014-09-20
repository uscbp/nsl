/*
 * CObject.java --
 *
 *	A stub class that represents objects created by the NativeTcl
 *	interpreter.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: CObject.java,v 1.1.1.1 1998/10/14 21:09:20 cvsadmin Exp $
 */

package jacl.tcl.lang;

/*
 * This is a stub class used in Jacl to represent objects created by
 * the NativeTcl interpreter. Actually CObjects will never appear inside
 * Jacl. However, since TclObject (which is shared between the NativeTcl
 * and Jacl implementations) makes some references to CObject, we include
 * a stub class here to make the compiler happy.
 *
 * None of the methods in this implementation will ever be called.
 */

class CObject extends InternalRep
{

    public void dispose()
    {
        throw new TclRuntimeError("This shouldn't be called");
    }

    public InternalRep duplicate()
    {
        throw new TclRuntimeError("This shouldn't be called");
    }

    final void makeReference(TclObject tobj)
    {
        throw new TclRuntimeError("This shouldn't be called");
    }

    public String toString()
    {
        throw new TclRuntimeError("This shouldn't be called");
    }

} // end CObject
