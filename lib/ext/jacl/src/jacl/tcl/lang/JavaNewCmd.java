/*
 * JavaNewCmd.java --
 *
 *	Implements the built-in "java::new" command.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All rights reserved.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: JavaNewCmd.java,v 1.1.1.1 1998/10/14 21:09:13 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

/*
 * This class implements the built-in "java::new" command.
 */

class JavaNewCmd implements Command
{

    
/*
 *----------------------------------------------------------------------
 *
 * cmdProc --
 *
 *	This procedure is invoked to process the "java::new" Tcl
 *	comamnd.  See the user documentation for details on what
 *	it does.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	A standard Tcl result is stored in the interpreter.
 *
 *----------------------------------------------------------------------
 */

    public void
            cmdProc(
            Interp interp,            // Current interpreter.
            TclObject argv[])            // Argument list.
            throws
            TclException            // A standard Tcl exception.
    {
        if (argv.length < 2)
        {
            throw new TclNumArgsException(interp, 1, argv,
                    "signature ?arg arg ...?");
        }

        /*
        * The "java::new" command can take both array signatures and
        * constructor signatures. We want to know what type of signature
        * is given without throwing and catching exceptions. Thus, we
        * call ArraySig.looksLikeArraySig() to determine quickly whether
        * a argv[1] can be interpreted as an array signature or a
        * constructor signature. This is a much less expensive way than
        * calling ArraySig.get() and then calling JavaInvoke.newInstance()
        * if that fails.
        */

        if (ArraySig.looksLikeArraySig(interp, argv[1]))
        {
            /*
        * Create a new Java array object.
        */

            if ((argv.length < 3) || (argv.length > 4))
            {
                throw new TclNumArgsException(interp, 2, argv,
                        "sizeList ?valueList?");
            }

            ArraySig sig = ArraySig.get(interp, argv[1]);
            Class componentType = sig.componentType;
            int dimensions = sig.dimensions;

            TclObject sizeListObj = argv[2];
            int sizeListLen = TclList.getLength(interp, sizeListObj);

            if (sizeListLen > dimensions)
            {
                throw new TclException(interp,
                        "size list \"" + sizeListObj +
                                "\" doesn't match array dimension (" + dimensions + ")");
            }

            TclObject valueListObj = null;
            if (argv.length == 4)
            {
                valueListObj = argv[3];
            }

            /*
        * Initialize arrayObj according to dimensions of both
        * sizeListObj and valueListObj.
        */

            Object obj = ArrayObject.initArray(interp, sizeListObj,
                    sizeListLen, 0, dimensions, componentType, valueListObj);

            interp.setResult(ReflectObject.newInstance(interp, componentType, obj));
        }
        else
        {
            /*
        * Create a new (scalar) Java object.
        */

            int startIdx = 2;
            int count = argv.length - startIdx;

            interp.setResult(JavaInvoke.newInstance(interp, argv[1], argv,
                    startIdx, count));
        }
    }

} // end JavaNewCmd


/*
 * The ArraySig class is used internally by the JavaNewCmd
 * class. ArraySig implements a new Tcl object type that represents an
 * array signature used for creating Java arrays. Examples or array
 * signatures are "int[][]", "java.lang.Object[]" or "[[D".
 */

