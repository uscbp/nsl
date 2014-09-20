/*
 * ParseResult.java
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: ParseResult.java,v 1.1.1.1 1998/10/14 21:09:20 cvsadmin Exp $
 *
 */

package jacl.tcl.lang;

/**
 * This class stores a single word that's generated inside the Tcl parser
 * inside the Interp class.
 */
class ParseResult
{

    /**
     * Returns a value of a parse operation. For calls to Interp.intEval(),
     * this variable is the same as interp.m_result.
     * <p/>
     * This value is never preserve()'ed to by the creator of ParseResult.
     * It doesn't need to be release()'ed.
     */
    TclObject value;

    /**
     * Points to the next character to be parsed.
     */
    int nextIndex;

    /**
     * Create an empty parsed word.
     */
    ParseResult()
    {
        value = TclString.newInstance("");
    }

    ParseResult(String s, int ni)
    {
        value = TclString.newInstance(s);
        nextIndex = ni;
    }

    ParseResult(TclObject o, int ni)
    {
        value = o;
        nextIndex = ni;
    }

    ParseResult(StringBuffer sbuf, int ni)
    {
        value = TclString.newInstance(sbuf.toString());
        nextIndex = ni;
    }
}

