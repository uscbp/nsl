/*
 * Procedure.java --
 *
 *	This class implements the body of a Tcl procedure.
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: Procedure.java,v 1.1.1.1 1998/10/14 21:09:20 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

/*
 * This class implements the body of a Tcl procedure.
 */

class Procedure implements Command, CommandWithDispose
{

/*
 * The formal parameters of the procedure and their default values.
 *     argList[0][0] = name of the 1st formal param
 *     argList[0][1] = if non-null, default value of the 1st formal param
 */

    TclObject argList[][];

/*
 * True if this proc takes an variable number of arguments. False
 * otherwise.
 */

    boolean isVarArgs;

/*
 * The body of the procedure.
 */

    CharPointer body;
    int body_length;

/*
* Name of the source file that contains this procedure. May be null, which
* indicates that the source file is unknown.
*/

    String srcFileName;

/*
 * Position where the body of the procedure starts in the source file.
 * 1 means the first line in the source file.
 */

    int srcLineNumber;

    
/*
 *----------------------------------------------------------------------
 *
 * Procedure --
 *
 *	Creates a procedure instance.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	The procedure body object as well as the argument objects
 *	are locked.
 *
 *----------------------------------------------------------------------
 */

    Procedure(
            Interp interp,        // Current interpreter.
            String name,        // Name of the procedure.
            TclObject args,        // The formal arguments of this procedure.
            TclObject b,        // The body of the procedure.
            String sFileName,        // Initial value for the srcFileName member.
            int sLineNumber)        // Initial value for the srcLineNumber member.
            throws
            TclException        // Standard Tcl exception.
    {
        srcFileName = sFileName;
        srcLineNumber = sLineNumber;

        /*
        * Break up the argument list into argument specifiers, then process
        * each argument specifier.
        */

        int numArgs = TclList.getLength(interp, args);
        argList = new TclObject[numArgs][2];

        for (int i = 0; i < numArgs; i++)
        {
            /*
        * Now divide the specifier up into name and default.
        */

            TclObject argSpec = TclList.index(interp, args, i);
            int specLen = TclList.getLength(interp, argSpec);

            if (specLen == 0)
            {
                throw new TclException(interp, "procedure \"" + name +
                        "\" has argument with no name");
            }
            if (specLen > 2)
            {
                throw new TclException(interp, "too many fields in argument " +
                        "specifier \"" + argSpec + "\"");
            }

            argList[i][0] = TclList.index(interp, argSpec, 0);
            argList[i][0].preserve();
            if (specLen == 2)
            {
                argList[i][1] = TclList.index(interp, argSpec, 1);
                argList[i][1].preserve();
            }
            else
            {
                argList[i][1] = null;
            }
        }

        if (numArgs > 0 && (argList[numArgs - 1][0].toString().equals("args")))
        {
            isVarArgs = true;
        }
        else
        {
            isVarArgs = false;
        }

        body = new CharPointer(b.toString());
        body_length = body.length();
    }

    
/*
 *----------------------------------------------------------------------
 *
 * cmdProc --
 *
 *	When a Tcl procedure gets invoked, this routine gets invoked
 *	to interpret the procedure.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	Depends on the commands in the procedure.
 *
 *----------------------------------------------------------------------
 */

    public void
            cmdProc(
            Interp interp,        // Current interpreter.
            TclObject argv[])         // Argument list.
            throws
            TclException        // Standard Tcl exception.
    {
        /*
        * Create the call frame and parameter bindings
        */

        CallFrame frame = interp.newCallFrame(this, argv);

        /*
        * Execute the body
        */

        interp.pushDebugStack(srcFileName, srcLineNumber);
        try
        {
            //interp.eval(body, 0);
            Parser.eval2(interp, body.array, body.index, body_length, 0);
        }
        catch (TclException e)
        {
            int code = e.getCompletionCode();
            if (code == TCL.RETURN)
            {
                int realCode = interp.updateReturnInfo();
                if (realCode != TCL.OK)
                {
                    e.setCompletionCode(realCode);
                    throw e;
                }
            }
            else if (code == TCL.ERROR)
            {
                interp.addErrorInfo(
                        "\n    (procedure \"" + argv[0] + "\" line " +
                                interp.errorLine + ")");
                throw e;
            }
            else if (code == TCL.BREAK)
            {
                throw new TclException(interp,
                        "invoked \"break\" outside of a loop");
            }
            else if (code == TCL.CONTINUE)
            {
                throw new TclException(interp,
                        "invoked \"continue\" outside of a loop");
            }
            else
            {
                throw e;
            }
        }
        finally
        {
            interp.popDebugStack();

            /*
        * The check below is a hack.  The problem is that there
        * could be unset traces on the variables, which cause
        * scripts to be evaluated.  This will clear the
        * errInProgress flag, losing stack trace information if
        * the procedure was exiting with an error.  The code
        * below preserves the flag.  Unfortunately, that isn't
        * really enough: we really should preserve the errorInfo
        * variable too (otherwise a nested error in the trace
        * script will trash errorInfo).  What's really needed is
        * a general-purpose mechanism for saving and restoring
        * interpreter state.
        */

            if (interp.errInProgress)
            {
                frame.dispose();
                interp.errInProgress = true;
            }
            else
            {
                frame.dispose();
            }
        }
    }

    
/*
 *----------------------------------------------------------------------
 *
 * disposeCmd --
 *
 * 	This method is called when the object command has been deleted
 * 	from an interpreter.
 *
 * Results:
 *	None.
 *
 * Side effects:
 *	It releases the procedure body object as well as all the
 *	argument objects that were previously locked.
 *
 *----------------------------------------------------------------------
 */

    public void
            disposeCmd()
    {
        //body.release();
        body = null;
        for (int i = 0; i < argList.length; i++)
        {
            argList[i][0].release();
            argList[i][0] = null;

            if (argList[i][1] != null)
            {
                argList[i][1].release();
                argList[i][1] = null;
            }
        }
        argList = null;
    }

} // end Procedure
