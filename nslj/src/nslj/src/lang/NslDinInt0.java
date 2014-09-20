/*  SCCS - @(#)NslDinInt0.java	1.11 - 09/01/99 - 00:16:38 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// NslDinInt.java
////////////////////////////////////////////////////////////////////////////////

package nslj.src.lang;

public class NslDinInt0 extends NslInt0
{

    NslInport inport = null;

    public NslDinInt0(NslModule owner)
    {
        super();
        attachPort(owner);
    }

    public NslDinInt0(NslModule owner, String name)
    {
        super(name);
        attachPort(owner);
    }

    public NslDinInt0(String name, NslModule owner)
    {
        super(name, owner);
        attachPort(owner);
    }

    private void attachPort(NslModule owner)
    {
        // System.out.println("Attaching "+this+" to module "+owner);
        inport = new NslInport(this);
        inport.owner = owner;
        owner.nslAddExistingInport(inport);  /* Add to owner's inport list */
    }

    public NslInport getInport()
    {
        return (inport);
    }

    public NslPort nslGetPort()
    {
        return (inport);
    }


}

// NslDinInteger.java
////////////////////////////////////////////////////////////////////////////////
