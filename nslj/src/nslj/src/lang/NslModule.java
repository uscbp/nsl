/* SCCS @(#)NslModule.java	1.34 --- 09/01/99 -- 16:12:06 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//
// NslModule.java
//
//////////////////////////////////////////////////////////////////////

/**
 * A basic module.
 |###########|
 ----->o| NslModule |o---->
 |###########|
 ^             ^
 |             |
 Inport       Outport
 */

package nslj.src.lang;

import nslj.src.system.NslSystem;
import nslj.src.display.*;
import nslj.src.math.NslDiff;

import java.awt.Color;
import java.lang.*;
import java.util.*;

import javax.swing.JComponent;

/**
 * NslModule base class
 */
public class NslModule extends NslHierarchy implements Runnable
{
    protected double _runDelta; // delta t, dt
    protected double _trainDelta;

    protected boolean _trainDeltaChanged;
    protected boolean _runDeltaChanged;

    boolean _run_enable_fg; // this module executes when it is true, idle otherwise

    boolean _train_enable_fg; // this module executes when it is true, idle otherwise

    protected double _timeConstantTM = 1.0;

    protected double _approximationDelta; // integration step size, tm
    protected NslDiff _diff_method; // differentiation method

    protected boolean _doubleBuffering = false; // Double buffering default value
    protected boolean _bufferingChanged;

    Vector<NslConnection> _missing_links; // temporary vector to store the links
    // not nslConnected in the instantiation stage
    // for later reconstruction

    // We have all of these list here because it is faster to build the lists
    // once and have them available, than to keep rebuilding these lists
    // every time you need them.

    protected Vector<String> _protocols;
    protected Vector<NslModule> _moduleChildren;  // list of modules inside this module
    protected Vector<NslInport> _inports;  // inport list
    protected Vector<NslOutport> _outports;  // outport list
    // nslNumericVars and classInstances in NslHierarchy super class

    /**
     * *******************
     */
    public String ID_str;
    public String order_str;
    public int ID = 0;
    public int child_c = 0;
    //public boolean postorder;
    static final int MAXDIGIT = 3; // max child=10^MAXDIGIT

    /**
     * unified display for all modules 
     */
    //protected NslDisplaySystem ds;
    private static Map<String, NslDisplaySystem> dsPair = new HashMap<String, NslDisplaySystem>();
    private List<NslUserPanel> panelList = new Vector<NslUserPanel>();
    
    /**
     * Bare Constructor. Setup internal variables and lists.
     */

    public NslModule()
    {
        super();
        nslSetModuleSpecificParams();
    }

    /**
     * Default constructor. Call bare constructor to setup internal
     * variables and lists. Attach itself to parent  module. It calls
     * <tt>makeinst</tt> for child modules creation.
     *
     * @param label - the name of the module
     */
    public NslModule(String label)
    {
        super(label);
        nslSetModuleSpecificParams();
    }

    /**
     * Default constructor. Call bare constructor to setup internal
     * variables and lists. Attach itself to parent  module. It calls
     * <tt>makeinst</tt> for child modules creation.
     *
     * @param label  - the name of the module
     * @param parent - the reference of the module's parent module
     */
    public NslModule(String label, NslModule parent)
    {
        super(label, parent, parent.nslGetAccess());
        nslSetModuleSpecificParams(label, parent);
    }

    /*
    public NslModule(String label, NslModule parent, String options)
    {
        this(label, parent);
        System.err.println("Error in NslModule: option parsing not ready. options:" + options);

    }
    */

    public void nslSetModuleSpecificParams()
    {
        //system is set statically before the model is created in NslMain
        _runDelta = system.nslGetRunDelta();
        _trainDelta = system.nslGetTrainDelta();
        _approximationDelta = system.nslGetApproximationDelta();
        _timeConstantTM = 1.0;
        _diff_method = system.nslGetApproximationMethod();
        nslSetModuleSpecificParams2();
    }

    public void nslSetModuleSpecificParams(String label, NslModule parent)
    {
        // modules are added to the parent here: models use above constructor with parent== null.
        if (parent == null)
        {
            System.out.println("NslModule: nslSetModuleSpecificParams: parent is null");
        }
        else
        {
            _runDelta = parent.nslGetRunDelta();
            _trainDelta = parent.nslGetTrainDelta();
            _approximationDelta = parent.nslGetApproximationDelta();
            parent.nslAddToModuleChildren(this);
        }
        nslSetModuleSpecificParams2();
    }

    public void nslSetModuleSpecificParams2()
    {
        _moduleChildren = new Vector<NslModule>(10, 10);
        // assume at least one port, increment one at a time.
        _inports = new Vector<NslInport>(1, 1);
        _outports = new Vector<NslOutport>(1, 1);
        _protocols = new Vector<String>(1, 1);

        _protocols.addElement("manual");

        _run_enable_fg = true;
        _train_enable_fg = true;


        _doubleBuffering = false; // system.nslGetBuffering();
        _bufferingChanged = false;
        _trainDeltaChanged = false;
        _runDeltaChanged = false;


        ID = 0;
        ID_str = makestr(ID);
        order_str = ID_str;
        child_c = 0;
    }

    public Vector nslGetModuleChildrenVector()
    {
        // list of modules inside this module
        return _moduleChildren;
    }

    public Vector nslGetInportsVector()
    {
        return _inports;  // inport list
    }

    public Vector nslGetOutportsVector()
    {
        return _outports;  // outport list
    }

    private static String makestr(int a)
    {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < MAXDIGIT; i++)
        {
            s.insert(0, (a % 10));
            a /= 10;
        }
        return s.toString();
    }

    public void nslSetId(NslModule parent, int id)
    {
        ID = id;
        ID_str = parent.ID_str + ' ' + makestr(id);
        order_str = ID_str;
    }

    public int nslGetId()
    {
        return (ID);
    }

    public String nslGetId_str()
    {
        return (ID_str);
    }

    public String nslGetOrderStr()
    {
        return (order_str);
    }

    /**
     * Add child module into this module and into the scheduler
     *
     * @param child - child NslModule
     */
    public void nslAddToModuleChildren(NslModule child)
    {
        if (child == null)
            throw new NullPointerException();

        _moduleChildren.addElement(child);

        child_c++;                  // Having one more baby
        child.nslSetId(this, child_c);  // give new baby a name

        /* */
        if ((system.schedulerMethod.equals("pre")) || (system.schedulerMethod.equals("mixed")))
        {
            //preorder - parent goes before childeren
            //TODO: mixed not implemented yet
        }
        if (system.schedulerMethod.equals("post"))
        {
            //postorder - parent goes after childeren
            //TODO: change this so we are not dependent on string lengths
            order_str = child.nslGetOrderStr() + '$';
        }
        //system.addToScheduler (child);
    }


    /**
     * Get a child module with  <tt>name</tt>
     *
     * @param name- name of target child module
     * @return children module, null if not found
     */
    public NslModule nslGetModuleRef(String name)
    {
        Enumeration E = _moduleChildren.elements();
        NslModule child;

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                child = (NslModule) E.nextElement();
                if (this.nslGetName().equals(child.nslGetName()))
                {
                    return child;
                }
            }
        }
        return null;
    }

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

        super.nslSetAccessRecursive(new_desiredAccess);

        E = _moduleChildren.elements();
        while (E.hasMoreElements())
        {
            NslModule child = (NslModule) E.nextElement();
            child.nslSetAccessRecursive(new_desiredAccess);
        }
    }

    public boolean nslHasChildModule(String s)
    {
        Enumeration E = _moduleChildren.elements();
        NslModule child;

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                child = (NslModule) E.nextElement();
                if (s.equals(child.nslGetName()))
                {
                    return (true);
                }
            }
        }
        return (false);
    }

    /**
     * Create an outport which makes reference to an Object
     *
     * @param name name of this outport
     * @param n    reference to a single object
     * @return n
     * @throws NullPointerException if n is not defined
     */
    // initialize outport of this object
    // The object itself should be fully initialized
    public NslNumeric nslAddOutport(String name, NslNumeric n)
    {
        if (n == null)
        {
            throw new NullPointerException();
        }
        _outports.addElement(new NslOutport(name, n));
        return n;
    }

    /**
     * Create an outport which makes reference to a NslNumeric
     *
     * @param n    reference to a NslNumeric, can be NslNumeric0, NslNumeric1 or NslNumeric2
     * @return n
     * @throws NullPointerException if n is not defined
     */
    // initialize outport of this object
    // The object itself should be fully initialized
    public NslNumeric nslAddOutport(NslNumeric n)
    {
        if (n == null)
        {
            throw new NullPointerException();
        }
        _outports.addElement(new NslOutport(n));
        return n;
    }

    /**
     * Add an Outport to the current outport list
     *
     * @param outport the outport to be added
     * @throws NullPointerException if outport is null
     */
    public void nslAddExistingOutport(NslOutport outport)
    {
        if (outport == null)
        {
            throw new NullPointerException();
        }

        _outports.addElement(outport);
    }

    /**
     * Create an inport which makes reference to an Object
     *
     * @param name name of this intport
     * @param n    reference to a NslNumeric, can be NslNumeric0, NslNumeric1 or NslNumeric2
     * @return n
     * @throws NullPointerException if n is null
     */
    // initialize inport of this object
    public NslNumeric nslAddInport(String name, NslNumeric n)
    {
        if (n == null)
        {
            throw new NullPointerException();
        }
        _inports.addElement(new NslInport(name, n));
        return n;
    }

    /**
     * Create an inport which makes reference to a Numeric object
     *
     * @param n    reference to a NslNumeric,
     *             can be NslNumeric0, NslNumeric1 or NslNumeric2 or NslNumeric3 or NslNumeric4
     * @return n
     * @throws NullPointerException if n is null
     */
    // initialize inport of this object
    public NslNumeric nslAddInport(NslNumeric n)
    {
        if (n == null)
        {
            throw new NullPointerException();
        }
        _inports.addElement(new NslInport(n));
        return n;
    }

    /**
     * Add an inport to the current inport list
     *
     * @param inport the inport to be added
     * @throws NullPointerException if inport is null
     */
    public void nslAddExistingInport(NslInport inport)
    {
        if (inport == null)
        {
            throw new NullPointerException();
        }
        _inports.addElement(inport);
    }

    /**
     * Get an inport with name <tt>name</tt>
     *
     * @param name name of target inport
     * @return inport, null if not found
     */

    NslInport getInport(String name)
    {
        Enumeration E = _inports.elements();
        NslInport port;

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                port = (NslInport) E.nextElement();
                if (name.equals(port.nslGetName()))
                {
                    return port;
                }
            }
        }
        return null;
    }

    /**
     * Get an outport with name <tt>name</tt>
     *
     * @param name name of target outport
     * @return outport, null if not found
     */

    NslOutport getOutport(String name)
    {
        Enumeration E = _outports.elements();
        NslOutport port;

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                port = (NslOutport) E.nextElement();
                if (name.equals(port.nslGetName()))
                {
                    return port;
                }
            }
        }
        return null;
    }

    /**
     * Get a port with name <tt>name</tt>
     *
     * @param name name of target port
     * @return port, null if not found
     */
    public NslPort nslGetPort(String name)
    {
        NslPort port = getOutport(name);
        if (port == null)
        {
            port = getInport(name);
        }
        return port;
    }

    /**
     * Connect two child modules and add the nslConnection to the scheduler<br>
     * The nslConnection is done in two phases. First, it will nslConnect
     * outport of a child module to the inport of another child module,
     * and outport of a child module to the outport of the parent module
     * (ie the wall of module in one level higher in hierarchy). Second,
     * the nslConnection between the inport of the parent module and the
     * intport of the child module is established. Two phases are needed
     * because the content of the inport of the parent module is not
     * available in the first phase. Here is an illustration: <br>
     * <pre>
     * +-NslModule Top-----------------------------------------------+
     * | +-NslModule A----------+       +-NslModule B-------------+  |
     * | | +-NslModule a---+    |       |     +-NslModule b-----+ |  |
     * | | | variable x    | 1  | 2     | 3   | variable y      | |  |
     * | | |               |o##o|o#####i|i###i|i                | |  |
     * | | |               |    |       |     |                 | |  |
     * | | +---------------+    |       |     +-----------------+ |  |
     * | +----------------------+       +-------------------------+  |
     * +-------------------------------------------------------------+
     * o   outport
     * i   intport
     * ### nslConnection
     * </pre>
     * Assume we are at the top level in the hierarchy: NslModule Top
     * and we are going to nslConnect two modules, NslModule A and
     * NslModule B. All links #1, #2, #3 are not available. In general
     * case, we need to nslConnect all the links internal to the child
     * modules first, ie #1, and #3. <br>
     * <p/>
     * However, in our implementation of ports, the variable, instead
     * of referencing through ports, references the corresponding
     * variable directly. For instance, for a variable y in NslModule b
     * to read a variable x in NslModule a, y makes a direct reference
     * to x. <br>
     * <p/>
     * This implementation is very efficient in the simulation running
     * phase, but not the initialization, as well as the nslConneciton,
     * phase. The nslConnection #1 works fine, since it can reference
     * variable x. But nslConnection #3 does not work, since at the time
     * of nslConnection, NslModule b cannot see variable x in NslModule a.
     * In this implementation, we "remember" this link and delay the
     * construction. <br>
     * <p/>
     * Go back to the highest level. Link #2 can be done since #1 is
     * established. Link #2 can see variable x in NslModule a now.
     * At this time we can construct the <i>Missing Links</i>.<br>
     * <p/>
     * The missing links in the first phase is stored in <tt>
     * _missing_links</tt>. The system will call another method
     * <tt>nslConnMissingLinks</tt> to construct the links in the second
     * phase.
     *
     * @param child1 From module <tt>child1</tt>
     * @param name1  Port of child1
     * @param child2 To module <tt>child2</tt>
     * @param name2  Port of child2
     * @return true if nslConnection is successful
     * @see NslModule#nslConnMissingLinks
     *      <p/>
     *      Side Effect: change missing_link: all links not established
     *      is stored in missing_link
     */
    // nslConnect from child1.name1 to child2.name2
    // return true is success
    public boolean connect(NslModule child1, String name1, NslModule child2, String name2)
    {
        return (nslConnect(child1, name1, child2, name2));
    }

    public boolean nslConnect(NslData num1, NslData num2)
    {
        NslModule child1, child2; // these are the owners of num1 & num2
        NslPort port1;
        NslPort port2;

        String name1;
        String name2;

        boolean success = true;

        port1 = num1.nslGetPort();
        port2 = num2.nslGetPort();
        name1 = port1.nslGetName();
        name2 = port2.nslGetName();
        child1 = port1.getOwner();
        child2 = port2.getOwner();


        // inter child nslConnection
        // the outport of first child is nslConnected to the inport of the
        // second child.
        // todo: we have to ensure that both children are in the current
        //       module.
        if (port1.getType() == NslPort.OUTPORT && port2.getType() == NslPort.INPORT)
        {
            ((NslInport) port2).setPort((NslOutport) port1);
        }
        // outport to outport nslConnection
        // The outport of child module is nslConnected to the outport of this
        // module.
        // todo: we have to ensure that port1 is the outport of child module of
        //       this module and port2 is the outport of this module.
        else if (port1.getType() == NslPort.OUTPORT && port2.getType() == NslPort.OUTPORT)
        {
            if (port1.isInitialized())
            {
                ((NslOutport) port2).setPort((NslOutport) port1);
            }
            else
            {
                if (_missing_links == null)
                {
                    _missing_links = new Vector<NslConnection>(1, 1);
                }
                _missing_links.addElement(new NslConnection(child1, name1, child2, name2));
                success = false;
            }
        }
        // inport to inport nslConnection
        // The inport of this module is nslConnected to the outport of the
        // child module.
        // todo: we have to ensure that port1 is the inport of this module
        //       and port2 is the inport of the child module.
        else if (port1.getType() == NslPort.INPORT && port2.getType() == NslPort.INPORT)
        {
            if (port1.isInitialized())
            {
                ((NslInport) port2).setPort((NslInport) port1);
            }
            else
            {
                if (_missing_links == null)
                {
                    _missing_links = new Vector<NslConnection>(1, 1);
                }
                _missing_links.addElement(new NslConnection(child1, name1, child2, name2));
                success = false;
            }

        }
        // nslConnect inport of this module to the outport of this module
        // it is only for the direct nslConnection from inport of this
        // module to the outport.
        // todo: establish the nslConnection
        //       ensure that both inport/outport are from this module
        else
        {
            success = false;
            System.err.println("Error in NslModule: in nslConnecting " + name1 + " and " + name2);
        }
        return success;
    }


    public boolean nslConnect(NslModule child1, String name1, NslModule child2, String name2)
    {
        NslPort port1;
        NslPort port2;

        boolean success = true;

        port1 = child1.nslGetPort(name1);
        port2 = child2.nslGetPort(name2);
        if (port1 == null)
        {
            System.err.println("Error in NslModule: Cannot find port '" + name1 +
                    "' in module '" + child1.nslGetName() +
                    "' in the nslConnection with port '" + name2 +
                    "' of module '" + child2.nslGetName() + "'.");
            return false;
        }
        if (port2 == null)
        {
            System.err.println("Error in NslModule: Cannot find port '" + name2 +
                    "' in module '" + child2.nslGetName() +
                    "' in the nslConnection with port '" + name1 +
                    "' of module '" + child1.nslGetName() + "'.");
            return false;
        }

        // inter child nslConnection
        // the outport of first child is nslConnected to the inport of the
        // second child.
        // todo: we have to ensure that both children are in the current
        //       module.
        if (port1.getType() == NslPort.OUTPORT
                && port2.getType() == NslPort.INPORT)
        {
            ((NslInport) port2).setPort((NslOutport) port1);
        }
        // outport to outport nslConnection
        // The outport of child module is nslConnected to the outport of this
        // module.
        // todo: we have to ensure that port1 is the outport of child module of
        //       this module and port2 is the outport of this module.
        else if (port1.getType() == NslPort.OUTPORT
                && port2.getType() == NslPort.OUTPORT)
        {
            if (port1.isInitialized())
            {
                ((NslOutport) port2).setPort((NslOutport) port1);
            }
            else
            {
                if (_missing_links == null)
                {
                    _missing_links = new Vector<NslConnection>(1, 1);
                }
                _missing_links.addElement(new NslConnection(child1, name1, child2, name2));
                success = false;
            }
        }
        // inport to inport nslConnection
        // The inport of this module is nslConnected to the outport of the
        // child module.
        // todo: we have to ensure that port1 is the inport of this module
        //       and port2 is the inport of the child module.
        else if (port1.getType() == NslPort.INPORT
                && port2.getType() == NslPort.INPORT)
        {
            if (port1.isInitialized())
            {
                ((NslInport) port2).setPort((NslInport) port1);
            }
            else
            {
                if (_missing_links == null)
                {
                    _missing_links = new Vector<NslConnection>(1, 1);
                }
                _missing_links.addElement(new NslConnection(child1, name1, child2, name2));
                success = false;
            }
        }
        // nslConnect inport of this module to the outport of this module
        // it is only for the direct nslConnection from inport of this
        // module to the outport.
        // todo: establish the nslConnection
        //       ensure that both inport/outport are from this module
        else
        {
            success = false;
            System.err.println("Error in NslModule nslConnecting " + name1 + " and " + name2);
        }
        return success;
    }

    /**
     * Connect two modules using defined link
     *
     * @param link The nslConnection between two modules
     * @return true if the nslConnection is successful
     */
    public boolean nslConnect(NslConnection link)
    {
        return nslConnect(link.child1, link.name1, link.child2, link.name2);
    }

    /**
     * Call child modules to nslConnect their own internal modules
     * recursively. It is the first stage of nslConnection in makeinst.
     * The links not well defined will be put in <tt>_missing_links</tt>
     * vector for the second stage
     *
     //* @see NslModule#nslConn
     //* @see NslModule#nslConnMissingLinks
     */
    // call makeConn recursively to nslConnect all modules in the
    // module

    // todo: ensure the nslConnection is valid. Otherwise, throw
    //       exception of do something else that will warning
    //       the programmer that the link is not in correct fashion

    public void nslConnChildren()
    {
        Enumeration E = _moduleChildren.elements();
        NslModule child;

        // nslConnect the components inside the children of this module first
        while (E.hasMoreElements())
        {
            child = (NslModule) E.nextElement();
            child.nslConnChildren();
        }
        // nslConnect the components of this module
        makeConn();
    }

    /**
     * Call child modules to nslConnect their missing links, if possible
     * It checks the <tt>_missing_links</tt> vector for the nslConnection
     * required.
     *
     //* @see NslModule#nslConn
     //* @see NslModule#nslConnChildren_callinitsys
     */
    // todo: ensure the nslConnection is valid. Otherwise, throw
    //       exception or do something else that will warning
    //       the programmer that the link is not in correct fashion
    public void nslConnMissingLinks()
    {
        Enumeration E;
        NslConnection link;
        NslModule child;
        if (_missing_links != null)
        {
            E = _missing_links.elements();
            if (E.hasMoreElements())
            {
                while (E.hasMoreElements())
                {
                    link = (NslConnection) E.nextElement();
                    if (!nslConnect(link))
                    {
                        // some problem in nslConnection even in second phase
                        System.err.print("Error in NslModule: Cannot nslConnect module '");
                        System.err.print(link.child1);
                        System.err.print("' port '");
                        System.err.print(link.name1);
                        System.err.print("' -> module '");
                        System.err.print(link.child2);
                        System.err.print("' port '");
                        System.err.print(link.name2);
                        System.err.print('\'');
                        System.err.print('\n');
                    }
                }
            }
            _missing_links = null;
        }

        E = _moduleChildren.elements();
        // nslConnect the components inside the children of this module first
        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                child = (NslModule) E.nextElement();
                child.nslConnMissingLinks();
            }
        }
    }

    /**
     * get run or train step size depending on simulation mode, delta t
     *
     * @return step size
     */

    public double nslGetDelta()
    {
        switch (system.getScheduler().schedulerMode)
        {
            case 'R':
                return _runDelta;
            case 'T':
                return _trainDelta;
            default:
                System.out.println("Taking 0.0 step as size");
                return 0.0;
        }

    }

    /**
     * get run step size, delta t
     *
     * @return step size
     */

    public double nslGetRunDelta()
    {
        return _runDelta;
    }

    /**
     * set run step size, delta t.  The setting will propagate to all child modules
     * in the hierarchy if the module is an internal node.
     *
     * @param t step size
     */

    public void nslSetRunDeltaRecursive(double t)
    {
        _runDelta = t;

        Enumeration E = _moduleChildren.elements();
        NslModule child;

        while (E.hasMoreElements())
        {
            child = (NslModule) E.nextElement();
            child.nslSetRunDelta(t);
        }

        _runDeltaChanged = true;
        system._runDeltaChanged = true;
    }

    public void nslSetRunDelta(double t)
    {
        nslSetRunDeltaRecursive(t);
    }

    public void nslResetRunDelta()
    {
        if (!_runDeltaChanged)
        {
            _runDelta = system.nslGetRunDelta();
        }

        Enumeration E = _moduleChildren.elements();
        NslModule child;

        while (E.hasMoreElements())
        {
            child = (NslModule) E.nextElement();
            child.nslResetRunDelta();
        }
    }

    /**
     * get train step size, delta t
     *
     * @return step size
     */

    public double nslGetTrainDelta()
    {
        return _trainDelta;
    }

    /**
     * set train step size, delta t.  The setting will propagate to all child modules
     * in the hierarchy if the module is an internal node.
     *
     * @param t step size
     */

    public void nslSetTrainDeltaRecursive(double t)
    {
        _trainDelta = t;

        Enumeration E = _moduleChildren.elements();
        NslModule child;

        while (E.hasMoreElements())
        {
            child = (NslModule) E.nextElement();
            child.nslSetTrainDelta(t);
        }

        _trainDeltaChanged = true;
        system._trainDeltaChanged = true;
    }

    public void nslSetTrainDelta(double t)
    {
        nslSetTrainDeltaRecursive(t);
    }

    public void nslResetTrainDelta()
    {
        if (!_trainDeltaChanged)
        {
            _trainDelta = system.nslGetTrainDelta();
        }

        Enumeration E = _moduleChildren.elements();
        NslModule child;

        while (E.hasMoreElements())
        {
            child = (NslModule) E.nextElement();
            child.nslResetTrainDelta();
        }

    }

    /**
     * Examine whether the module runs on execution command <tt>run</tt>
     * or <tt>step</tt>
     *
     * @return true if it is active, false if idle on execution command
     */
    public boolean nslGetTrainEnableFlag()
    {
        return _train_enable_fg;
    }

    /**
     * To set if the module runs on execution command <tt>run</tt>
     * or <tt>step</tt>. The setting will propagate to all child modules
     * in the hierarchy if the module is an internal node.
     *
     * @param b true if active, false if idle
     */
    public void nslSetTrainEnableFlag(boolean b)
    {
        Enumeration E = _moduleChildren.elements();
        NslModule child;
        _train_enable_fg = b;

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                child = (NslModule) E.nextElement();
                child.nslSetTrainEnableFlag(b);
            }
        }
    }

    /**
     * Examine whether the module runs on execution command <tt>run</tt>
     * or <tt>step</tt>
     *
     * @return true if it is active, false if idle on execution command
     */

    public boolean nslGetRunEnableFlag()
    {
        return _run_enable_fg;
    }

    public boolean nslGetEnableFlag()
    {
        switch (system.getScheduler().schedulerMode)
        {
            case 'R':
                return _run_enable_fg;
            case 'T':
                return _train_enable_fg;
            default:
                System.out.println("Error: invalid module state");
                return false;
        }
    }

    /**
     * To set if the module runs on execution command <tt>run</tt>
     * or <tt>step</tt>. The setting will propagate to all child modules
     * in the hierarchy if the module is an internal node.
     *
     * @param b true if active, false if idle
     */
    public void nslSetRunEnableFlag(boolean b)
    {
        Enumeration E = _moduleChildren.elements();
        NslModule child;
        _run_enable_fg = b;

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                child = (NslModule) E.nextElement();
                child.nslSetRunEnableFlag(b);
            }
        }
    }

    /**
     * get integration time step / numerical method time step tm
     *
     * @return time step size
     */
    public double nslGetApproximationDelta()
    {
        return _approximationDelta;
    }

    /**
     * To set the time step size
     *
     * @param t time step size
     */
    public void nslSetApproximationDelta(double t)
    {
        _approximationDelta = t;
    }

    public NslDiff nslGetApproximationMethod()
    {
        return _diff_method;
    }

    public void nslSetApproximationMethod(NslDiff dm)
    {
        _diff_method = dm;
    }

    /**
     * Update the status of all _outports. It is done after each major
     * numerical calculation
     */
    public void nslUpdateBuffers()
    {
        for(int i=0; i<_outports.size(); i++)
        {
            _outports.get(i).nslUpdateBuffers();
        }
    }

    public void nslSetBuffering(boolean v)
    {
        _doubleBuffering = v;
        _bufferingChanged = true;
        system.resetPorts();
    }

    public boolean nslGetBuffering()
    {
        return _doubleBuffering;
    }

    public void nslResetBuffering()
    {
        if (!_bufferingChanged)
        {
            _doubleBuffering = system.nslGetBuffering();
        }

        NslModule child;
        Enumeration e = _moduleChildren.elements();

        nslUpdateBuffering();

        if (e.hasMoreElements())
        {
            while (e.hasMoreElements())
            {
                child = (NslModule) e.nextElement();
                child.nslResetBuffering();
            }
        }
    }

    public void nslUpdateBuffering()
    {
        Enumeration e = _outports.elements();
        NslOutport port;

        while (e.hasMoreElements())
        {
            port = (NslOutport) e.nextElement();
            if (port != null)
            {
                port.nslResetBuffering();
            }
        }
    }

    /**
     * Call when multi-threaded. It simply calls execute() defined by
     * the user
     * This is needed by the Runnable interface to make the applets work
     *
     */
    public void run()
    {
        runsim();
    }

    protected void finalize() throws Throwable
    {
        _missing_links.removeAllElements();
        nslNullifyParent();
        _moduleChildren.removeAllElements();
        _inports.removeAllElements();
        _outports.removeAllElements();
        super.nslRemoveAllClassInstances();
        super.nslRemoveAllDataVars();
        super.finalize();
    }

    /**
     * Instantiation the internal variable of this object. It is called
     * automatically at the construction phase of this module. It must
     * be defined by user.
     * @param name - String
     * @param p - NslModule
     */
    public void makeinst(String name, NslModule p)
    {
        makeInst(name, p);
    }

    public void makeInst(String name, NslModule p)
    {
    }

    public void initsys()
    {
        initSys();
    }

    public void initSys()
    {
    }

    public void endsys()
    {
        endSys();
    }

    public void endSys()
    {
    }

    public void initModule()
    {
    }

    public void endModule()
    {
    }

    public void callFromConstructorTop(Object[] args)
    {
    }

    public void callFromConstructorBottom()
    {
    }

    /**
     * Initialization step of this module. It is called automatically
     * at the start of the simulation, or by <tt>init</tt> command
     * in interactive environment.
     */
    public void initrun()
    {
        initRun();
    }

    // abstract public void initRun();
    public void initRun()
    {
    }

    /* need to have initrun abstract, since NPP
fills something inside through MethodNode. Unless you
change this, they must be abstract */
    public void endrun()
    {
        endRun();
    }

    public void endRun()
    {
    }


    /**
     * Make the nslConnection between child modules. It is automatically
     * called at the construction phase of this module, but after the
     * instantiation of those internal modules
     */
    public void makeConn()
    {
    } // nslConnect between modules (using ports)

    /**
     * The <b>run</b> block of this module
     */
//note that schedular calls runsim()
    public void runsim()
    {
//register module's name for error reporting
        NslSystem.module_executing = this.nslGetName();
        simRun();
    }

    public void simrun()
    { // main run method
        simRun();
    }

    public void initRunEpoch()
    {
    }

    public void simRun()
    {
    } // this the latest syntax 01/19/98

    public void endRunEpoch()
    {
    }

    public void initTrainEpoch()
    {
    }

    public void initTrain()
    {
    }

    public void simTrain()
    {
    }

    public void endTrain()
    {
    }

    public void endTrainEpoch()
    {
    }

    public Vector nslGetProtocols()
    {
        return _protocols;
    }

    public void nslRemoveFromLocalProtocols(String name)
    {
        Enumeration e = _protocols.elements();
        String _protocolName;
        boolean found = false;
        int i = 0;
        while (e.hasMoreElements() && !found)
        {

            _protocolName = (String) e.nextElement();

            if (name.equals(_protocolName))
            {
                found = true;
            }
            else
            {
                i++;
            }
        }
        if (found)
        {
            _protocols.removeElementAt(i);
        }
    }

    public void nslAddProtocolRecursiveDown(String name)
    {
        if (system.protocolExist(name) || name.equals("manual"))
        {
            // insert only if it is not in list
            Enumeration e = _protocols.elements();
            String _protocolName;
            boolean found = false;

            while (e.hasMoreElements() && !found)
            {

                _protocolName = (String) e.nextElement();

                if (name.equals(_protocolName))
                {
                    found = true;
                }
            }

            if (!found)
            {
                _protocols.addElement(name);
            }

            Enumeration E = _moduleChildren.elements();
            NslModule child;

            while (E.hasMoreElements())
            {
                child = (NslModule) E.nextElement();
                child.nslAddProtocolRecursiveDown(name);
            }
        }
    }

    public void nslAddProtocolRecursiveUp(String name)
    {
        if (system.protocolExist(name) || name.equals("manual"))
        {
            // insert only if it is not in list
            Enumeration e = _protocols.elements();
            String _protocolName;
            boolean found = false;

            while (e.hasMoreElements() && !found)
            {
                _protocolName = (String) e.nextElement();
                if (name.equals(_protocolName))
                    found = true;
            }

            if (!found)
            {
                _protocols.addElement(name);
                if (nslGetParentModule() != null)
                    nslGetParentModule().nslAddProtocolRecursiveUp(name);
            }
        }
    }

    public void nslDeclareProtocol(String name, String label)
    {
        system.nslCreateProtocol(name, label, this);
    }

    public void nslDeclareProtocol(String name)
    {
        system.nslCreateProtocol(name, name, this);
    }


    public void nslSetProtocolFlagRecursiveDown(String name)
    {
        Enumeration e = _protocols.elements();
        String _protocolName;
        boolean found = false;

        while (e.hasMoreElements() && !found)
        {
            _protocolName = (String) e.nextElement();

            if (name.equals(_protocolName))
            {
                nslSetRunEnableFlag(true);
                nslSetTrainEnableFlag(true);

                e = _moduleChildren.elements();
                NslModule child;

                while (e.hasMoreElements())
                {
                    child = (NslModule) e.nextElement();
                    child.nslSetProtocolFlagRecursiveDown(name);
                }
                found = true;
            }
        }
        if (!found)
        {
            nslSetRunEnableFlag(false);
            nslSetTrainEnableFlag(false);
        }
    }

    /**
     * **********************************************************************
     */
    public void nslInitTempRun()
    {
    }

    public void nslInitTempTrain()
    {
    }

    public void nslInitTempModule()
    {
    }

    public void nslInitTempRunEpoch()
    {
    }

    public void nslInitTempTrainEpoch()
    {
    }
    
    /**
     * All addXXXCanvas functions 
     */
    public NslDisplaySystem nslCreateDisplaySystem(String name)
    {
    	NslDisplaySystem ds = nslFindDisplaySystem(name);
    	if(ds != null)
    		return ds;
    	
        if (!system.getNoDisplay())
        {
            ds = new NslDisplaySystem(name, system);
            system.addDisplaySystem(ds);
            dsPair.put(name, ds);
        }
    	
    	return ds;
    }
        
    public NslDisplaySystem nslFindDisplaySystem(String name)
    {
    	if(dsPair.isEmpty())
    		return null;
    	
    	Object[] keys = dsPair.keySet().toArray();
    	for(int i=0; i<keys.length; i++)
    	{
    		if(((String)keys[i]).equals(name))
    			return dsPair.get(keys[i]);
    	}
    	return null;
    }
    
    public Nsl3dCanvas nslAdd3dCanvas(String displayName, String canvasName)
    {
        NslFrame frame=null;
        if(!system.getNoDisplay())
        {
            NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
            if (ds == null)
                System.out.println("NslModule: nslAdd3dCanvas: Error display system is null");
            else
                frame=ds.frame;
        }
        Nsl3dCanvas c=new Nsl3dCanvas(frame, canvasName);
        if(frame!=null)
            frame.addCanvas(displayName, c);
        return c;
    }

    public Nsl3dCanvas nslAdd3dCanvas(String displayName, String canvasName, float g)
    {
        NslFrame frame=null;
        if(!system.getNoDisplay())
        {
            NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
            if (ds == null)
                System.out.println("NslModule: nslAdd3dCanvas: Error display system is null");
            else
                frame=ds.frame;
        }
        Nsl3dCanvas c=new Nsl3dCanvas(frame, canvasName, g);
        if(frame!=null)
            frame.addCanvas(displayName, c);
        return c;
    }

    public Nsl3dCanvas nslAdd3dCanvas(String displayName, String canvasName, javax.media.j3d.Locale locale)
    {
        NslFrame frame=null;
        if(!system.getNoDisplay())
        {
            NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
            if (ds == null)
                System.out.println("NslModule: nslAdd3dCanvas: Error display system is null");
            else
                frame=ds.frame;
        }
        Nsl3dCanvas c=new Nsl3dCanvas(frame,canvasName,locale);
        if(frame!=null)
            frame.addCanvas(displayName, c);
        return c;
    }

    public NslGraphCanvas nslAddGraphCanvas(String displayName, String canvasName, int maxCoord, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
            System.out.println("NslModule: nslAddAreaCanvas: Error display system is null");
        else
        {
            NslGraphCanvas c=new NslGraphCanvas(ds.frame,canvasName,maxCoord,min,max);
            ds.frame.addCanvas(displayName, c);
            return c;
        }
        return null;
    }

    public NslCanvas nslAddAreaCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode, double min, double max)
    {
        NslCanvas c=nslAddAreaCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddAreaCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddAreaCanvas: Error display system is null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "Area");
    }

    /**
     *
     * @param displayName
     * @param canvasName
     * @param numvar
     * @param displayDims - A string of the dimensions to display (WX, WY, WZ, XY. XZ, or YZ)
     * @param slicingDims - Indices of dimensions to slice along (max length of 2)
     * @param min - min value
     * @param max - max value
     * @return
     */
    public NslCanvas nslAddAreaCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                      int[] slicingDims, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddAreaCanvas: Error display system is null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                return ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Area");
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddAreaCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                      int[] slicingDims, int temporalMode, double min, double max)
    {
        NslCanvas c=nslAddAreaCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddBarCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                     double min, double max)
    {
        NslCanvas c=nslAddBarCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddBarCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddBarCanvas: Error display system is null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "Bar");
    }

    public NslCanvas nslAddBarCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                      int[] slicingDims, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddBarCanvas: Error display system is null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                return ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Bar");
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddBarCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                      int[] slicingDims, int temporalMode, double min, double max)
    {
        NslCanvas c=nslAddBarCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddDotCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                     double min, double max)
    {
        NslCanvas c=nslAddDotCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddDotCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddDotCanvas: Error display system is null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "Dot");
    }

    public NslCanvas nslAddInputImageCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddImageCanvas: Error display system is null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "InputImage");
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                         double min, double max)
    {
        NslCanvas c=nslAddSpatialCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                         double min, double max, Color color)
    {
        NslCanvas c=nslAddSpatialCanvas(displayName, canvasName, numvar, temporalMode, min, max);
        if(color!=null)
            c.setVarColor(color);
        return c;
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, double min,
                                         double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddSpatialCanvas: Error display system is null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "Spatial");
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                        int[] slicingDims, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddSpatialCanvas: Error display system is null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                return ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Spatial");
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, double min,
                                         double max, Color color)
    {
        NslCanvas c=nslAddSpatialCanvas(displayName, canvasName, numvar, min, max);
        if(color!=null)
            c.setVarColor(color);
        return c;
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                        int[] slicingDims, double min, double max, Color color)
    {
        NslCanvas c=nslAddSpatialCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max);
        if(color!=null)
            c.setVarColor(color);
        return c;
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                        int[] slicingDims, int temporalMode, double min, double max)
    {
        NslCanvas c=nslAddSpatialCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddSpatialCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                        int[] slicingDims, int temporalMode, double min, double max, Color color)
    {
        NslCanvas c=nslAddSpatialCanvas(displayName, canvasName, numvar, displayDims, slicingDims, temporalMode, min, max);
        if(color!=null)
            c.setVarColor(color);
        return c;
    }

    public NslCanvas nslAddStringCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                        double min, double max)
    {
        NslCanvas c=nslAddStringCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddStringCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.out.println("NslModule: nslAddStringCanvas: Error display system is null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "String");
    }

    public NslCanvas nslAddTemporalCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                          double min, double max, Color varColor)
    {
        NslCanvas c=nslAddTemporalCanvas(displayName, canvasName, numvar, min, max, varColor);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddTemporalCanvas(String displayName, String canvasName, NslNumeric numvar, double min,
                                          double max, Color varColor)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddTemporalCanvas: Error display system is null");
            return null;
        }
        else
        {        	
        	NslTemporalCanvas ndc = (NslTemporalCanvas)ds.frame.addCanvas(numvar, canvasName, min, max, "Temporal");
            ndc.setVarColor(varColor);
            return ndc;
        }
    }

    public NslCanvas nslAddTemporalCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, double min, double max, Color varColor)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddTemporalCanvas: Error display system is null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                NslTemporalCanvas ndc = (NslTemporalCanvas)ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Temporal");
                ndc.setVarColor(varColor);
                return ndc;
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddTemporalCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, int temporalMode, double min, double max, Color varColor)
    {
        NslCanvas c=nslAddTemporalCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max, varColor);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddHistogramCanvas(String displayName, String canvasName, NslNumeric numvar, int mode,
                                     double min, double max, int binSize, Color varColor)
    {
        NslCanvas c=nslAddHistogramCanvas(displayName, canvasName, numvar, min, max, binSize, varColor);
        if(c!=null)
            ((NslHistogramCanvas)c).setMode(mode);
        return c;
    }

    public NslCanvas nslAddHistogramCanvas(String displayName, String canvasName, NslNumeric numvar, double min,
                                           double max, int binSize, Color varColor)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddHistogramCanvas: Error display system is null");
            return null;
        }
        else
        {
            NslHistogramCanvas c = (NslHistogramCanvas)ds.frame.addCanvas(numvar, canvasName, min, max, "Histogram");
            c.setVarColor(varColor);
            c.setBinSize(binSize);
            return c;
        }
    }

    public NslCanvas nslAddHistogramCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, double min, double max, int binSize, Color varColor)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddHistogramCanvas: Error display system is null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                NslHistogramCanvas c = (NslHistogramCanvas)ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Histogram");
                c.setVarColor(varColor);
                c.setBinSize(binSize);
                return c;
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddHistogramCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, int mode, double min, double max, int binSize, Color varColor)
    {
        NslCanvas c=nslAddHistogramCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max, binSize, varColor);
        if(c!=null)
            ((NslHistogramCanvas)c).setMode(mode);
        return c;
    }

    public NslCanvas nslAddRasterCanvas(String displayName, String canvasName, NslNumeric numvar, int binSize, Color varColor)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddRasterCanvas: Error display system is null");
            return null;
        }
        else
        {
        	NslRasterCanvas c = (NslRasterCanvas)ds.frame.addCanvas(numvar, canvasName, 0, 1, "Raster");
            c.setVarColor(varColor);
            c.setBinSize(binSize);
        	return c;
        }
    }

    public NslCanvas nslAddRasterCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, int binSize, Color varColor)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddRasterCanvas: Error display system is null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                NslRasterCanvas c = (NslRasterCanvas)ds.frame.addCanvas(var_sel_info, canvasName, 0, 1, "Raster");
                c.setVarColor(varColor);
                c.setBinSize(binSize);
        	    return c;
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddGrayscaleCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                     double min, double max)
    {
        NslCanvas c=nslAddGrayscaleCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddGrayscaleCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddGrayscaleCanvas: Error display system null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "Grayscale");
    }

    public NslCanvas nslAddGrayscaleCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddGrayscaleCanvas: Error display system null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                return ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Grayscale");
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddGrayscaleCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, int temporalMode, double min, double max)
    {
        NslCanvas c=nslAddGrayscaleCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddThermalCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                     double min, double max)
    {
        NslCanvas c=nslAddThermalCanvas(displayName, canvasName, numvar, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddThermalCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddThermalCanvas: Error display system null");
            return null;
        }
        else
            return ds.frame.addCanvas(numvar, canvasName, min, max, "Thermal");
    }

    public NslCanvas nslAddThermalCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, double min, double max)
    {
        if (system.getNoDisplay())
            return null;

    	NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddThermalCanvas: Error display system null");
            return null;
        }
        else
        {
            try
            {
                NslVariableInfo var_sel_info=(NslVariableInfo)NslFrame.getVarInfo(numvar).clone();
                var_sel_info.setDimensionChoice(displayDims);
                for(int i=0; i<Math.min(2, slicingDims.length); i++)
                    var_sel_info.setSlicingPoint(i, slicingDims[i]);
                return ds.frame.addCanvas(var_sel_info, canvasName, min, max, "Thermal");
            }
            catch(CloneNotSupportedException e)
            {
                return null;
            }
        }
    }

    public NslCanvas nslAddThermalCanvas(String displayName, String canvasName, NslNumeric numvar, String displayDims,
                                          int[] slicingDims, int temporalMode, double min, double max)
    {
        NslCanvas c=nslAddThermalCanvas(displayName, canvasName, numvar, displayDims, slicingDims, min, max);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddUserCanvas(String displayName, String canvasName, NslNumeric numvar, int temporalMode,
                                     double min, double max, String libPath, String type)
    {
        NslCanvas c=nslAddUserCanvas(displayName, canvasName, numvar, min, max, libPath, type);
        if(c!=null)
            c.setTemporalMode(temporalMode);
        return c;
    }

    public NslCanvas nslAddUserCanvas(String displayName, String canvasName, NslNumeric numvar, double min, double max,
                                       String libPath, String type)
    {
        if (system.getNoDisplay())
            return null;
        NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
        {
            System.err.println("NslModule: nslAddUserCanvas: Error display system is null");
            return null;
        }
        else
        {
            return ds.frame.addUserCanvas(canvasName, numvar, min, max, libPath, type);
        }
    }

    public void nslAddUserComponent(java.awt.Component component)
    {
        nslAddUserComponent("Independent", component);
    }
    
    public void nslAddUserComponent(String displayName, java.awt.Component component)
    {
        if (system.getNoDisplay())
            return;
        NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
            System.err.println("NslModule: nslAddUserPanel: Error display system is null");
        else
            ds.frame.addComponent(component);
    }

    /**
     * Canvas component 
     */
    
    public void nslAddNumericEditorCanvas(String displayName, NslNumeric numvar)
    {
        if (system.getNoDisplay())
            return;

        NslDisplaySystem ds = nslCreateDisplaySystem(displayName);
        if (ds == null)
            System.err.println("NslModule: nslAddNumericEditor: Error display system is null");
        else
            ds.frame.addVariableInfo(numvar);
    }

    public NslUserPanel addPanel(String panel, String display)
    {
    	NslDisplaySystem ds = nslCreateDisplaySystem(display);
        if (ds != null)
        {
            NslUserPanel np = new NslUserPanel(panel + "@" + display);
            boolean found=false;
            for(int i=0; i<panelList.size(); i++)
            {
                if(panelList.get(i).nslGetName().equals(np.nslGetName()))
                {
                    for(int j=0; j<ds.frame.getComponentCount(); j++)
                    {
                        if(ds.frame.getComponent(j).equals(panelList.get(i)))
                        {
                            ds.frame.remove(panelList.get(i));
                            ds.frame.add(np, j);
                            break;
                        }
                    }
                    panelList.set(i,np);
                    found=true;
                    break;
                }
            }
            if(!found)
            {
                panelList.add(np);
                ds.frame.addComponent(np);
            }
            return np;
        }
        return null;
    }
    
    protected boolean addComponentToPanel(JComponent component, String panelName, String display)
    {
    	NslDisplaySystem ds = nslFindDisplaySystem(display);
        if (ds != null)
        {
            for(int i=0; i<panelList.size(); i++)
            {
                NslUserPanel np = panelList.get(i);
                if (np.nslGetName().equals(panelName + "@" + display))
                {
                    np.addComponent(component);
                    return true;
                }
            }
            System.out.println("Error: Panel " + panelName + " was not found");
        }
        return false;
    }

    public NslButton addButtonToPanel(String name, String label, String panel, String display)
    {
        NslButton b=new NslButton(name, label, this);
        if(addComponentToPanel(b, panel, display))
            return b;
        else
            return null;
    }

    public NslLabel addLabelToPanel(String label, String panel, String display)
    {
        NslLabel l=new NslLabel(label);
        if(addComponentToPanel(l, panel, display))
            return l;
        else
            return null;
    }
    
    public NslSlider addSliderToPanel(String name, String panel, String display, int orientation)
    {
        NslSlider sb=new NslSlider(name, this);
        if(addComponentToPanel(sb, panel, display))
        {
            sb.setOrientation(orientation);        	
            return sb;
        }
        else
            return null;
    }

    public NslSlider addSliderToPanel(String name, String panel, String display, int orientation, int min, int max, int value)
    {
        NslSlider sb=addSliderToPanel(name, panel, display, orientation);
        if(sb!=null)
        {
            sb.setMinimum(min);
            sb.setMaximum(max);
            sb.setValue(value);
        }
        return sb;
    }

    public void show(String display)
    {
    	NslDisplaySystem ds = nslFindDisplaySystem(display);
        ds.show();
    }

    public void hide(String display)
    {
    	NslDisplaySystem ds = nslFindDisplaySystem(display);
        ds.hide();
    }
    
    public void nslSetColumns(int numCols, String display)
    {
    	NslDisplaySystem ds = nslFindDisplaySystem(display);
    	if(ds == null)
    		return;
    	
        ds.frame.setColumns(numCols);
        ds.frame.getPanel().validate();
    }

    public static void clearDisplaySystems()
    {
        dsPair.clear();
    }
}
