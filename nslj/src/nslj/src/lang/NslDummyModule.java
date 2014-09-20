/* SCCS  @(#)NslOutDummyModule.java	1.5---09/02/99--21:32:33 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.


package nslj.src.lang;

public class NslDummyModule extends NslModule
{

    static int counter = 0;
    String[] strs;

    public NslDummyModule()
    {
        super("OutModule" + counter, system.nslGetModelRef());
        counter++;
    }
}
