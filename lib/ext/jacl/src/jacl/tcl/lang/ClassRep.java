/*
 * ClassRep.java --
 *
 *	This class implements the internal representation of a Java
 *	class name.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: ClassRep.java,v 1.1.1.1 1998/10/14 21:09:13 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

/*
 * This class implements the internal representation of a Java class
 * name.
 */

class ClassRep extends InternalRep
{

/*
 * The class referred to by this ClassRep.
 */

    Class cls;

    
/*
 *----------------------------------------------------------------------
 *
 * ClassRep --
 *
 *	Creates a new ClassRep instance.
 *
 * Side effects:
 *	Member fields are initialized.
 *
 *----------------------------------------------------------------------
 */

    ClassRep(
            Class c)            // Initial value for cls.
    {
        cls = c;
    }

    
/*
 *----------------------------------------------------------------------
 *
 * duplicate --
 *
 *	Make a copy of an object's internal representation.
 *
 * Results:
 *	Returns a newly allocated instance of the appropriate type.
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    protected InternalRep
            duplicate()
    {
        return new ClassRep(cls);
    }

    
/*
 *----------------------------------------------------------------------
 *
 * get --
 *
 *	Returns the Class object referred to by the TclObject.
 *
 * Results:
 *	The Class object referred to by the TclObject.
 *
 * Side effects:
 *	When successful, the internalRep of the signature object is
 *	converted to ClassRep.
 *
 *----------------------------------------------------------------------
 */


    static Class
            get(
            Interp interp,         // Current interpreter.
            TclObject tclObj)        // TclObject that contains a valid Java
        // class name.
            throws
            TclException        // If the TclObject doesn't contain a valid
    // class name.
    {
        InternalRep rep = tclObj.getInternalRep();

        if (rep instanceof ClassRep)
        {
            //If a ClassRep is already cached, return it right away.
            return ((ClassRep) rep).cls;
        }
        else
        {
            Class c = JavaInvoke.getClassByName(interp, tclObj.toString());
            tclObj.setInternalRep(new ClassRep(c));
            return c;
        }
    }

} // end ClassRep


