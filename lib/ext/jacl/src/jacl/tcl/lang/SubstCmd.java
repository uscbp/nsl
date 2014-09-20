/*
 * SubstCmd.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: SubstCmd.java,v 1.1.1.1 1998/10/14 21:09:19 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

import java.util.*;

/**
 * This class implements the built-in "subst" command in Tcl.
 */

class SubstCmd implements Command
{
    static final private String validCmds[] = {
            "-nobackslashes",
            "-nocommands",
            "-novariables"
    };

    static final int OPT_NOBACKSLASHES = 0;
    static final int OPT_NOCOMMANDS = 1;
    static final int OPT_NOVARS = 2;

    /**
     * This procedure is invoked to process the "subst" Tcl command.
     * See the user documentation for details on what it does.
     *
     * @param interp the current interpreter.
     * @param argv   command arguments.
     * @throws TclException if wrong # of args or invalid argument(s).
     */

    public void cmdProc(Interp interp, TclObject argv[])
            throws TclException
    {
        int currentObjIndex, len, i;
        int objc = argv.length - 1;
        boolean doBackslashes = true;
        boolean doCmds = true;
        boolean doVars = true;
        String result = "";
        String s;
        char c;

        for (currentObjIndex = 1; currentObjIndex < objc; currentObjIndex++)
        {
            if (!argv[currentObjIndex].toString().startsWith("-"))
            {
                break;
            }
            int opt = TclIndex.get(interp, argv[currentObjIndex],
                    validCmds, "switch", 0);
            switch (opt)
            {
                case OPT_NOBACKSLASHES:
                    doBackslashes = false;
                    break;
                case OPT_NOCOMMANDS:
                    doCmds = false;
                    break;
                case OPT_NOVARS:
                    doVars = false;
                    break;
                default:
                    throw new TclException(interp,
                            "SubstCmd.cmdProc: bad option " + opt
                                    + " index to cmds");
            }
        }
        if (currentObjIndex != objc)
        {
            throw new TclNumArgsException(interp, currentObjIndex, argv,
                    "?-nobackslashes? ?-nocommands? ?-novariables? string");
        }

        /*
       * Scan through the string one character at a time, performing
       * command, variable, and backslash substitutions.
       */

        s = argv[currentObjIndex].toString();
        len = s.length();
        i = 0;
        while (i < len)
        {
            c = s.charAt(i);

            if ((c == '[') && doCmds)
            {
                ParseResult res;
                try
                {
                    interp.evalFlags = Parser.TCL_BRACKET_TERM;
                    interp.eval(s.substring(i + 1, len));
                    res = new ParseResult(interp.getResult(),
                            i + interp.termOffset);
                }
                catch (TclException e)
                {
                    i = e.errIndex + 1;
                    throw e;
                }
                i = res.nextIndex + 2;
                result = result + res.value.toString();

            }
            else if (c == '\r')
            {
                /*
             * (ToDo) may not be portable on Mac
             */

                i++;
            }
            else if ((c == '$') && doVars)
            {
                ParseResult vres = Parser.parseVar(interp,
                        s.substring(i, len));
                i += vres.nextIndex;
                result = result + vres.value.toString();
            }
            else if ((c == '\\') && doBackslashes)
            {
                BackSlashResult bs = interp.backslash(s, i, len);
                i = bs.nextIndex;
                if (bs.isWordSep)
                {
                    break;
                }
                else
                {
                    result = result + bs.c;
                }
            }
            else
            {
                result = result + c;
                i++;
            }
            if (i >= len)
            {
                break;
            }
        }

        interp.setResult(result);
    }
}

