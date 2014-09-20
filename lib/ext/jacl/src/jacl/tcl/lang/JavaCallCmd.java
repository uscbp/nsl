/*
 * JavaCallCmd.java
 *
 *	Implements the built-in "java::call" command.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: JavaCallCmd.java,v 1.1.1.1 1998/10/14 21:09:14 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

/*
 * Implements the built-in "java::call" command.
 */

class JavaCallCmd implements Command
{

    
/*----------------------------------------------------------------------
 *
 * cmdProc --
 *
 * 	This procedure is invoked to process the "java::call" Tcl
 * 	command. See the user documentation for details on what it
 * 	does.
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
        boolean convert;
        int classIdx;

        if (argv.length < 3)
        {
            throw new TclNumArgsException(interp, 1, argv,
                    "?-noconvert? class signature ?arg arg ...?");
        }

        String arg1 = argv[1].toString();
        if ((arg1.length() >= 2) && ("-noconvert".startsWith(arg1)))
        {
            convert = false;
            classIdx = 2;
        }
        else
        {
            convert = true;
            classIdx = 1;
        }

        if (argv.length < classIdx + 2)
        {
            throw new TclNumArgsException(interp, 1, argv,
                    "?-noconvert? class signature ?arg arg ...?");
        }

        int startIdx = classIdx + 2;
        int count = argv.length - startIdx;

        interp.setResult(JavaInvoke.callStaticMethod(interp, argv[classIdx],
                argv[classIdx + 1], argv, startIdx, count, convert));
    }

} // end JavaCallCmd

