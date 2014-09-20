/*  SCCS - @(#)NslDinDouble0.java	1.12 - 09/01/99 - 00:16:35 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.lang;

public class NslDinDouble0 extends NslDouble0
{

    NslInport inport = null;

    /**
     * Constructor with default value 0
     */
    public NslDinDouble0(NslModule owner)
    {
        super();
        attachPort(owner);
    }

    /**
     * This constructs a number with specified name
     *
     * @param name - name of the variable
     */
    public NslDinDouble0(NslModule owner, String name)
    {
        super(name);
        attachPort(owner);
    }

    public NslDinDouble0(String name, NslModule owner)
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

// NslDinDouble.java
////////////////////////////////////////////////////////////////////////////////
