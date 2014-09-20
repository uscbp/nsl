/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// NslHierarchy.java
//
//////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////
// Documentation about documentation
//
// command      explanation
// -------      ------------
// @param       parameter list
// @return      return value
// @throw       exception that could be thrown in run-time
// @see         see also "class"#"method"
// <tt>...</tt> prints out text anchored in typewriter font
//              for parameter, method or class names
// <b>,,.</b>   prints out text anchored in bold type font
//              for warning messages
// <i>...</i>   prints out test anchored in italic type font
//              for concepts in the program
// <br>         new line
//////////////////////////////////////////////////////////////////////

/**
 * The NslHierarchy class is inherited by NslModel, NslModule, and NslClass.
 * It exists so that we have the same structure as C++, which does
 * not have reflection.
 */

package nslj.src.lang;

import nslj.src.system.NslSystem;

import java.lang.*;
import java.util.Vector;
import java.util.Enumeration;

import jacl.tcl.lang.TclException;

public class NslHierarchy
        extends NslBase
{

    public static NslSystem system = null;// instance of the NslSystem
    protected Vector<NslClass> nslClassInstances; // list of nslClassInstances inside this module
    protected Vector<NslData> nslDataVars;     // NslData only and ports


    public NslHierarchy()
    {
        super();
        initNslHierarchy();
        addToTclCommandList();
    }

    public NslHierarchy(String label)
    {
        super(label);
        initNslHierarchy();
        addToTclCommandList();
    }

    /**
     * Default constructor. Call bare constructor to setup internal
     * variables and lists. Attach itself to parent  module. It calls
     * <tt>makeinst</tt> for child modules creation.
     *
     * @param label  - the name of the module
     * @param parent - parent module, null if this is the top level
     */

    public NslHierarchy(String label, NslModule parent)
    {
        super(label, parent);

        if (parent != null)
        {
            nslSetAccess(parent.nslGetAccess());
        }

        initNslHierarchy();
        addToTclCommandList();
    }

    public NslHierarchy(String label, NslClass parent)
    {
        super(label, parent);

        if (parent != null)
        {
            nslSetAccess(parent.nslGetAccess());
        }

        initNslHierarchy();
        addToTclCommandList();
    }

    /**
     * Default constructor. Call bare constructor to setup internal
     * variables and lists. Attach itself to parent  module. It calls
     * <tt>makeinst</tt> for child modules creation.
     *
     * @param label  - the name of the module
     * @param parent - parent module, null if this is the top level
     */

    public NslHierarchy(String label, NslModule parent, char desiredAccess)
    {
        super(label, parent, desiredAccess);
        initNslHierarchy();
        addToTclCommandList();
    }

    public NslHierarchy(String label, NslClass parent, char desiredAccess)
    {
        super(label, parent, desiredAccess);
        initNslHierarchy();
        addToTclCommandList();
    }

    //-------------------------------------------------------------------

    public void initNslHierarchy()
    {
        nslClassInstances = new Vector<NslClass>(10, 10);
        nslDataVars = new Vector<NslData>(10, 10);
    }

    /////
    // Get the reference to any nsl variable anywhere in the
    // Datatree hierarchy that is readable and is either
    // outport, inport, nsldata
    // @param name the name of variable to search 
    // @return the reference pointer, null if not found
    ///

    //99/5/1 aa

    public static NslData nslGetValue(String name)
    {
        return system.nslGetValue(name);
    }

    ///// 
    // Get the reference to any nsl Writable variable anywhere in the
    // Datatree hierarchy either
    // outport, inport, or nsldata and then copy the data
    // to that location if nslWritable.
    // The format is always (target,value).
    // @param target- the name of variable to set.
    // @param num- the value the variable should be set to.
    // @return the boolean, false if not found or nsl Writable.
    ///

    //99/5/1 aa

    public static boolean nslSetValue(String target, NslData data)
    {
        return system.nslSetValue(target, data);
    }

    ///// 
    // Get the reference to any nslWritable variable anywhere in the
    // Datatree hierarchy either
    // outport, inport, or nsldata and then copy the data
    // to that location if nslWritable.
    // The format is always (target,value).
    // @param target- the name of variable to set.
    // @param num- the value the variable should be set to.
    // @return the boolean, false if not found or nslWritable.
    ///

    public static boolean nslSetValue(NslData target, String num)
    {
        return system.nslSetValue(target, num);
    }

    private void addToTclCommandList()
    {
        if (system == null)
        {
            System.out.println("System is null");
        }
        try
        {
            String objectName = jacl.tcl.lang.ReflectObject.newInstance(nslj.src.nsls.Executive.interp, this.getClass(), this).toString();
            system.addNslsCommand(nslGetFullName(), objectName);
        }
        catch (TclException e)
        {
        }
    }

    /**
     * define the NslSystem to use
     *
     * @param sys - NslSystem to use
     */

    public static void nslSetSystem(NslSystem sys)
    {
        system = sys;
    }

    public static NslSystem nslGetSystem()
    {
        return system;
    }

    public String nslGetNameAndParent()
    {
        if (nslGetParent() != null)
        {
            return "Nsl Object " + nslGetName() +
                    " Parent Object " + nslGetParent().nslGetName();
        }
        else
        {
            return "Nsl Object " + nslGetName();
        }
    }

    public String nslGetNameAndParentRecursive()
    {
        if (nslGetParent() != null)
        {
            return ':' + nslGetName() + '.' + nslGetParent().nslGetNameAndParentRecursive();
        }
        else
        {
            return ':' + nslGetName();
        }
    }

    // class instances --------------------------------------

    public boolean nslAddToClassInstances(NslClass nc)
    {
        if (nc == null)
        {
            throw new NullPointerException();
        }

        //System.out.println("Debug: NslModule: Making variable child nslDataName: ["+n.nslGetName()+"] scope : " +this.name);

        Enumeration E = nslClassInstances.elements();
        NslBase temp;

        while (E.hasMoreElements())
        {
            temp = (NslBase) E.nextElement();
            if (nc.nslGetName().equals(temp.nslGetName()))
            {
                //System.out.println("Debug: NslHierarchy: Variable: ["+nc.nslGetName()+"] was already in nslClassInstances");
                return false;
            }
        }

        nslClassInstances.addElement(nc);
        return true;
    }

    /*
    * Get the reference pointer of a registered variable of type NslClass
    * @param name the name of variable to search
    * @return the reference pointer, null if not found
    * The major place this is called from is system.nslGetDataVar("string")
    */

    public NslClass nslGetClassInstance(String searchstring)
    {

        //System.out.println("Debug: NslModule: getNslDataname: ["+searchstring.nslGetName()+"] scope : " +this.name);

        Enumeration E = nslClassInstances.elements();
        NslClass temp;

        while (E.hasMoreElements())
        {
            temp = (NslClass) E.nextElement();
            if (searchstring.equals(temp.nslGetName()))
            {
                return temp;
            }
        }

        return null;
    }

    /*public void removeFromNslClassInstance (NslData data) {
   	nslClassInstances.removeElement(data);
    }*/

    public void nslRemoveFromClassInstances(NslClass nc)
    {
        if (nc == null)
        {
            throw new NullPointerException();
        }

        nslClassInstances.removeElement(nc);
    }

    public Vector nslGetClassInstancesVector()
    {
        // list of modules inside this module
        return nslClassInstances;
    }

    public void nslRemoveAllClassInstances()
    {
        nslClassInstances.removeAllElements();
    }

    // nslDataVar methods -------------------------------------

    public boolean nslAddToDataVars(NslData data)
    {

        if (data == null)
        {
            throw new NullPointerException();
        }

        //System.out.println("Debug: NslModule: Making variable child nslDataname: ["+data.nslGetName()+"] scope : " +this.name);

        Enumeration E = nslDataVars.elements();
        NslData temp;

        while (E.hasMoreElements())
        {
            temp = (NslData) E.nextElement();

            if (data.nslGetName().equals(temp.nslGetName()))
            {
                //System.out.println("Debug: NslHierarchy: Variable: ["+data.nslGetName()+"] was already nslData");
                return false;
            }
        } // end while

        nslDataVars.addElement(data);

        return true;
    }

    /**
     * Get the reference pointer of a registered variable, either
     * outport, inport, nsldata
     *
     * @param searchstring the name of variable to search
     * @return the reference pointer, null if not found
     *         The major place this is called from is system.getDataVar("string")
     */

    public NslData nslGetDataVar(String searchstring)
    {

        //System.out.println("Debug: NslModule: getNslDataname: ["+searchstring.nslGetName()+"] scope : " +this.name);

        Enumeration E = nslDataVars.elements();
        NslData temp;

        while (E.hasMoreElements())
        {
            temp = (NslData) E.nextElement();
            if (searchstring.equals(temp.nslGetName()))
            {
                return temp;
            }
        }

        return null;
    }

    /**
     * Get the reference pointer of a registered variable, either
     * outport, inport, nsldata
     *
     * @param searchstring the name of variable to search
     * @return the reference pointer, null if not found
     *         The major place this is called from is system.getDataVar("string")
     */

    public NslData nslGetDataVar(String searchstring, char desiredAccess)
    {

        NslData data;

        data = nslGetDataVar(searchstring);
        if (data == null)
        {
            return null;
        }
        if (data.nslGetAccess() == desiredAccess || (data.nslGetAccess() == 'W' && desiredAccess == 'R'))
        {
            return data;
        }
        else
        {
            return null;
        }
    }

    /**
     */

    public void removeFromNslDataVar(NslData data)
    {
        nslDataVars.removeElement(data);
    }

    /**
     * Get a vector contains all data memeber registered in this
     * module
     *
     * @return a vector of NslData objects
     */

    public Vector nslGetDataVarsVector()
    {
        // list of modules inside this module
        return nslDataVars;
    }

    public void nslRemoveAllDataVars()
    {
        nslDataVars.removeAllElements();
    }

    public NslData nslRemoveFromDataVars(NslData data)
    {

        if (data == null)
        {
            throw new NullPointerException();
        }

        nslDataVars.removeElement(data);

        return data;
    }

    // set desiredAccesses

    public void nslSetAccessRecursive(char new_desiredAccess)
    {

        Enumeration E;
        char old_desiredAccess;

        old_desiredAccess = nslGetAccess();

        if (old_desiredAccess == new_desiredAccess)
        {
            // do nothing
            return;
        }

        // set desiredAccessibility for this module, class, or data

        nslSetAccess(new_desiredAccess);

        E = nslDataVars.elements();
        while (E.hasMoreElements())
        {
            NslData child = (NslData) E.nextElement();
            child.nslSetAccess(new_desiredAccess);
        }

        E = nslClassInstances.elements();
        while (E.hasMoreElements())
        {
            NslClass child = (NslClass) E.nextElement();
            child.nslSetAccessRecursive(new_desiredAccess);
        }

    } //nslSetAccessRecursive

    public boolean nslHasChildClass(String s)
    {
        Enumeration E = nslClassInstances.elements();
        NslHierarchy child;

        while (E.hasMoreElements())
        {
            child = (NslHierarchy) E.nextElement();
            //System.out.println("Debug: NslHierarchy: haschildren "+child.nslGetName());
            if (s.equals(child.nslGetName()))
            {
                return true;
            }
        }// end while

        return false;
    }

    public void nslPrintChildClasses()
    {
        Enumeration E = nslClassInstances.elements();
        NslHierarchy child;

        while (E.hasMoreElements())
        {
            child = (NslModule) E.nextElement();
            System.out.print("Debug: NslHierarchy: nslPrintChildClasses ");
            System.out.print(child.nslGetName());
            System.out.print('\n');
        }
    }


} // end class
