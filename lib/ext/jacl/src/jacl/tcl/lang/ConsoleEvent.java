/*
 * ConsoleEvent.java --
 *
 *	This type of event is generated when the user has entered a
 *	complete command string on the command line.
 *	
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: ConsoleEvent.java,v 1.1.1.1 1998/10/14 21:09:20 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

/*
 * This type of event is generated when the user has entered a complete
 * command script on the command line.
 */

class ConsoleEvent extends TclEvent
{

/*
 * Interpreter to evaluate the user input.
 */

    Interp interp;

/*
 * The result of evaluating the user input string. Is null if an exception
 * occurred during the script evaluation.
 */

    TclObject evalResult;

/*
 * If an exception occurred during the script evaluation, contains the
 * exception. Otherwise is null.
 */

    TclException evalException;

/*
 * The user input string.
 */

    String script;

    
/*
 *----------------------------------------------------------------------
 *
 * ConsoleEvent --
 *
 *	Creates a new ConsoleEvent instance.
 *
 * side effects:
 *	Member fields are initialized.
 *
 *----------------------------------------------------------------------
 */

    ConsoleEvent(
            Interp i,        // Interpreter to evaluate the user input string.
            String s)        // User input string.
    {
        interp = i;
        script = s;
        evalResult = null;
        evalException = null;
    }

    
/*
 *----------------------------------------------------------------------
 *
 * processEvent --
 *
 *	Process the console event.
 *
 * Results:	
 *	Always returns 1 -- the event has been processed and can be
 *	removed from the event queue.
 *
 * Side effects:
 *	The user input is eval'ed. It may may have arbitrary side
 *	effects.
 *
 *----------------------------------------------------------------------
 */

    public int
            processEvent(
            int flags)        // Same as flags passed to Notifier.doOneEvent.
    {
        try
        {
            interp.eval(script, 0);
            evalResult = interp.getResult();
            evalResult.preserve();
        }
        catch (TclException e)
        {
            evalException = e;
        }

        return 1;
    }

}

