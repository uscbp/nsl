/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.

// Copyright: This software may be freely copied provided the toplevel

// Copyright: COPYRIGHT file is included with each such copy.

// Copyright: Email nsl@java.usc.edu.

//

// NslSystem.java

//

//////////////////////////////////////////////////////////////////////

/**

 * The main system. Checks functional modules like interpreter,

 file system, command system, simulation module, numerical method,

 simulation status.

 */
package nslj.src.system;

import jmatlink.JMatLink;
import nslj.src.cmd.NslCmd;
import nslj.src.display.NslDisplaySystem;
import nslj.src.display.NslDisplaySystemVector;
import nslj.src.display.NslExecutive;
import nslj.src.display.NslFrame;
import nslj.src.lang.*;
import nslj.src.math.NslDiff;
import nslj.src.math.NslDiffEuler;
import nslj.src.math.NslDiffRungeKutta2;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.math.MathContext;
import java.math.RoundingMode;
import java.math.BigDecimal;

public class NslSystem
{
    NslInterpreter interpreter;
    
    public static int runSemaphore = 0;
    
    public NslScheduler scheduler;

    public NslSync interpMonitor;

    public NslSync stepMonitor;

    public NslDoubleSync displayMonitor;

    public NslDoubleSync displayMonitorAck;

    int displayCounter;

    public boolean breakState = true;

    public boolean stepCmdRun = false;

    public boolean _runDeltaChanged = false;

    public boolean _trainDeltaChanged = false;

    public char _accessibilityChar = 'R';

    public static boolean applet = false;

    public NslDisplaySystemVector display_system_list;

    Vector module_list;

    Vector<NslCmd> cmd_list;

    Vector<NslDiff> nsldiff_list;

    Vector<String> nslsCommandNames;

    Vector<String> nslsCommandObjects;

    public static String module_executing = "";

    public static char init_run_char = 'B'; // B=before init, D=during, A=after

    NslModule modelRef;  // current module in context. It is for faster actions like inspection.

    long totalEpochTimer;    // Number of seconds taken by all executed epochs

    long totalCycleTimer;    // Number of seconds taken by all executed cycles

    long epochStartingTime; // Starting time when current epoch execution began

    long cycleStartingTime; // Starting time when current cycle execution began

    BigDecimal cur_time;      // current simulation time

    int numRunEpochs = 1;      // Number of running epochs

    int runEpoch = 1;         // Actual run epoch

    int cur_cycle;     // current run simulation cycle

    double end_time;      // run simulation "end" time

    double _runDelta; // delta t, dt

    int numTrainEpochs = 1;     // Number of training epochs

    int trainEpoch = 1;          // Actual train epoch

    public boolean endEpochChanged = false;

    int train_cur_cycle;     // current train simulation cycle

    double train_end_time;      // train "end" time

    double _trainDelta;     // delta t, dt

    public NslDiff nsldiff;  // current differential method

    double _approximationTimeConstantTM = 1.0; // time constant tm default

    double _approximationDelta; // integration delta or time step size

    boolean _approximationDeltaManuallySet = false; //

    public boolean doubleBuffering = false;  //98/9/12 aa: flag -TODO move to module

    //98/8/4 aa added noDisplay flag
    public boolean noDisplay = false;      //noDisplay mode: run without graphics windows

    // decide to make debug public for speed
    public int debug = 0;      //debug mode: has levels 0=none 1=some

    public boolean stdout;

    public boolean stderr;

    // should add a nsldebug flag to get actual nslj debug statements

    //98/9/11 aa added schedulerMethod string

    public String schedulerMethod = "pre";  // "pre" parent before child

    // "post" parent after child

    // "mixed" init methods do p before c;

    //         run methods do p after c.

    protected String protocolName = "manual";

    NslExecutive executive;

    protected static JMatLink matlabEngine=null;

    /**
     * Set up all system lists for module and command registration.
     * <p/>
     * Set up simulation clock and other system parameter to a
     * <p/>
     * default value
     */
    public NslSystem()
    {
        double deltaDefault = 0.1;
        modelRef = null;
        cmd_list = new Vector<NslCmd>(30, 10);
        nsldiff_list = new Vector<NslDiff>(2, 1);
        nslsCommandNames = new Vector<String>(20, 10);
        nslsCommandObjects = new Vector<String>(20, 10);
        display_system_list = new NslDisplaySystemVector(1, 1);
        modelRef = null;
        cur_time = new BigDecimal(0);
        cur_cycle = 1;
        end_time = 0.0;
        _runDelta = deltaDefault;
        train_cur_cycle = 1;
        train_end_time = 0.0;
        _trainDelta = deltaDefault;
        init_run_char = 'B';
        schedulerMethod = "pre";
        _approximationDelta = deltaDefault;
        _approximationTimeConstantTM = 1.0;
        nsldiff = new NslDiffEuler(this);//98/7/21 aa : passes in system
        nsldiff.nslSetApproximationDelta(_approximationDelta);//98/7/21 aa
        nsldiff.nslSetApproximationTimeConstant(_approximationTimeConstantTM);
        // command line input parameters
        noDisplay = false;   //98/8/4 aa: added noDisplay flag
        debug = 0;           //98/8/4 aa: added debug level
        interpMonitor = new NslSync();
        stepMonitor = new NslSync();
        displayMonitor = new NslDoubleSync();
        displayMonitorAck = new NslDoubleSync();
    }

    public static void closeMatlab()
    {
        if(matlabEngine != null)
            matlabEngine.engClose();
    }

    public static JMatLink getMatlabEngine()
    {
        if(matlabEngine==null)
        {
            matlabEngine=new JMatLink();
        }
        if(!matlabEngine.engIsRunning())
        {
            matlabEngine.engOpen();
        }
        return matlabEngine;
    }

    public void shutdown()
    {
        stopCycleTimer();
        stopEpochTimer();
        scheduler.breakAll();
        scheduler.endSys();
        modelRef=null;
        if(matlabEngine!=null && matlabEngine.engIsRunning())
        {
            matlabEngine.engClose();
            matlabEngine=null;
        }
        for (Object aDisplay_system_list : display_system_list)
        {
            ((NslDisplaySystem) aDisplay_system_list).hide();
            ((NslDisplaySystem) aDisplay_system_list).frame.getPanel().getCanvasList().clear();
            ((NslDisplaySystem) aDisplay_system_list).frame.dispose();
            ((NslDisplaySystem) aDisplay_system_list).frame=null;
            aDisplay_system_list=null;
        }
        display_system_list.clear();
    }

    public String toString()
    {
        String tmp;

        if (nslGetModelRef() != null)
        {
            tmp = "System: model name: " + nslGetModelRef().nslGetName();
        }
        else
        {
            tmp = "System: no model yet";
        }

        return tmp;
    }

    public void SetEndTime(double val)
    {
        end_time = val;
    }

    public double endTime()
    {
        return end_time;
    }

    public void initSys()
    {
    }

    public void endSys()
    {
    }

    public void initModule()
    {
        modelRef.nslSetProtocolFlagRecursiveDown(protocolName);
        display_system_list.init();
    }

    public void initRunEpoch()
    {
        totalEpochTimer = 0;
        runEpoch = 1;
        init_run_char = 'D';  //during
        modelRef.nslSetProtocolFlagRecursiveDown(protocolName);
        display_system_list.initEpoch();
        init_run_char = 'A';  //after
    }

    public void initTrainEpoch()
    {
        totalEpochTimer = 0;
        trainEpoch = 1;
        init_run_char = 'D';  //during
        modelRef.nslSetProtocolFlagRecursiveDown(protocolName);
        display_system_list.initEpoch();
        init_run_char = 'A';  //after
    }

    /**
     * Initialize the system. Set the clock to zero.
     * <p/>
     * Ready for next simulation to run.
     * <p/>
     * init_run_flag=before, during, after B,D,A - after = after nslUpdateBuffers
     */
    public void initRun()
    {
        init_run_char = 'D';  //during
        cur_time = new BigDecimal(0);
        cur_cycle = 1;
        totalCycleTimer = 0;
        nsldiff.setDelta(_runDelta);
        modelRef.nslSetProtocolFlagRecursiveDown(protocolName);
    }

    public void endRun()
    {
        //display_system_list.init();
    }

    public void endRunEpoch()
    {
        //display_system_list.init();
        display_system_list.endEpoch(runEpoch);
    }

    public void endSingleRunEpoch()
    {
        display_system_list.endEpoch(runEpoch);
    }

    /**
     * Initialize the system. Set the clock to zero. Initialize
     * <p/>
     * scheduler. Ready for next simulation to run.
     * <p/>
     * init_run_flag=before, during, after B,D,A - after = after nslUpdateBuffers
     */
    public void initTrain()
    {
        init_run_char = 'D';  //during
        cur_time = new BigDecimal(0);
        cur_cycle = 1;
        totalCycleTimer = 0;
        nsldiff.setDelta(_trainDelta);// _trainDelta
        modelRef.nslSetProtocolFlagRecursiveDown(protocolName);
    }

    public void endTrain()
    {
        //display_system_list.init();
    }

    public void endTrainEpoch()
    {
        //display_system_list.init();
        display_system_list.endEpoch(runEpoch);
    }

    public void endSingleTrainEpoch()
    {
        display_system_list.endEpoch(trainEpoch);
    }

    public void endModule()
    {
    }

    public boolean isSchedulerInRunMode()
    {
        return scheduler.schedulerMode == 'R';
    }

    public boolean isSchedulerInTrainMode()
    {
        return scheduler.schedulerMode == 'T';
    }

    /**
     * Add a well-defined module into the system. Connect the internal
     * <p/>
     * modules.
     *
     * @param module module to be added
     */

    public void addModel(NslModule module)
    {
        modelRef = module;

        reflect();

        modelRef = module;

        module.nslResetBuffering();
        module.nslConnChildren();
        module.nslConnMissingLinks();

        if(executive!=null)
            executive.addProtocol("manual", module, false);
        
        if (!scheduler.isAlive())
        {
            scheduler.start();
        }
    }

    /**
     * Get the module with label name
     *
     * @param name The name to search
     * @return module, null if not found
     */
    public NslModule nslGetModuleRef(String name)
    {
        StringTokenizer str_token = new StringTokenizer(name, ".");
        Enumeration E = module_list.elements();
        NslModule leaf;
        String token;
        if (!str_token.hasMoreTokens())
        {
            return null;
        }

        token = str_token.nextToken();

        // skip first token if it is null string
        if (token.length() == 0)
        {
            if (!str_token.hasMoreTokens())
            {
                return null;
            }
            token = str_token.nextToken();
        }

        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                leaf = (NslModule) E.nextElement();
                if (token.equals(leaf.nslGetName()))
                {
                    // found it!
                    if (str_token.hasMoreTokens())
                    {
                        // search in recursively
                        if (leaf.nslGetModuleChildrenVector() == null)
                        {
                            // dead end, it is a leaf module
                            return null;
                        }
                        // keep searching next level
                        E = leaf.nslGetModuleChildrenVector().elements();
                        token = str_token.nextToken();
                    }
                    else
                    {
                        // bingo
                        return leaf;
                    }
                } // if match name
            }
        }
        return null;
    }

    /**
     * Get the module with label name
     *
     * @param name The name to search
     * @return module, null if not found
     */

    public NslModule nslGetModuleRef(String name, char desiredAccess)
    {
        NslModule leaf;
        String token;
        StringTokenizer str_token = new StringTokenizer(name, ".");
        // todo: ?? system knows models modules ??
        Enumeration E = module_list.elements();
        if (!str_token.hasMoreTokens())
        {
            return null;
        }

        token = str_token.nextToken();
        // skip first token if it is null string
        if (token.length() == 0)
        {
            if (!str_token.hasMoreTokens())
            {
                return null;
            }
            token = str_token.nextToken();
        }
        while (E.hasMoreElements())
        {
            leaf = (NslModule) E.nextElement();
            if (token.equals(leaf.nslGetName()))
            {
                // found it!
                if (((desiredAccess == 'R') && (leaf.nslGetAccess() == 'R') || (leaf.nslGetAccess() == 'W')) ||
                        ((desiredAccess == 'W') && (leaf.nslGetAccess() == 'W')))
                {
                    // good access!
                    if (!str_token.hasMoreTokens())
                    {
                        // end of string
                        return leaf;
                        // else search recursively
                    }
                    else if (leaf.nslGetModuleChildrenVector() == null)
                    {
                        // dead end, it is a leaf module
                        return null;
                    }
                    else
                    {
                        // keep searching next level
                        E = leaf.nslGetModuleChildrenVector().elements();
                        token = str_token.nextToken();
                    } // end if (!str_token.hasMoreTokens
                } // end if (((access
            } // end if (token.equals.
        } // end while (E.hasMoreElements
        return null;
    }

    /**
     * Get the ClassInstance or NslModule with label name
     *
     * @param name          -The name to search
     * @param desiredAccess - what type of desiredAccess is desired
     * @return moduleOrClass - NslBase, or null if not found
     */
    public NslHierarchy getRefToModuleOrClass(String name, char desiredAccess)
    {
        NslHierarchy parent = modelRef;
        NslModule moduleOrClass1;
        NslClass moduleOrClass2;
        NslHierarchy moduleOrClass = null;
        char moduleOrClassAccess;
        boolean found;
        String token;
        Vector parentmodulevector = null;
        Vector parentclassvector = null;
        Enumeration moduleinstances;
        Enumeration classinstances;
        NslHierarchy modelref;
        StringTokenizer str_token = new StringTokenizer(name, ".");
        if (!str_token.hasMoreTokens())
        {
            return null;
        }
        else
        {
            // get first token which should be the model name
            token = str_token.nextToken();
        }
        // skip first token if it is null string due to dot
        // the string name must start with model or .model
        if (token.length() == 0)
        {
            if (!str_token.hasMoreTokens())
            {
                return null;
            }
            else
            {
                token = str_token.nextToken();
            }
        }
        // process the model name
        modelref = nslGetModelRef();
        if (!(modelref.nslGetName().equals(token)))
        {
            return null;
        }
        else if (((desiredAccess == 'R') && (modelref.nslGetAccess() == 'N'))
                || ((desiredAccess == 'W') && (modelref.nslGetAccess() != 'W')))
        {
            return null;
        }
        else if (!str_token.hasMoreTokens())
        { // model was all there was
            return modelref;
        }

        while (str_token.hasMoreTokens())
        {
            token = str_token.nextToken();
            if (parent instanceof NslModule)
            {
                parentmodulevector = ((NslModule) parent).nslGetModuleChildrenVector();
                parentclassvector = parent.nslGetClassInstancesVector();
            }
            else if (parent != null)
            {
                parentclassvector = parent.nslGetClassInstancesVector();
            }

            found = false;
            if ((parentmodulevector == null) && (parentclassvector == null))
            {
                return null;
            }
            if (parentmodulevector != null)
            {
                moduleinstances = parentmodulevector.elements();
                while (moduleinstances.hasMoreElements())
                {
                    moduleOrClass1 = (NslModule) moduleinstances.nextElement();
                    if (token.equals(moduleOrClass1.nslGetName()))
                    {
                        found = true;
                        moduleOrClass = moduleOrClass1;
                        break;  //found it
                    }
                }//end while
            }
            if (!found)
            {
                if (parentclassvector != null)
                {
                    classinstances = parentclassvector.elements();
                    while (classinstances.hasMoreElements())
                    {
                        moduleOrClass2 = (NslClass) classinstances.nextElement();
                        if (token.equals(moduleOrClass2.nslGetName()))
                        {
                            found = true;
                            moduleOrClass = moduleOrClass2;
                            break;  //found it
                        }
                    } // end while
                } // end if parentclassvector not null
            }// end if not found

            if (!found)
            {
                return null;
            }
            else
            {
                moduleOrClassAccess = moduleOrClass.nslGetAccess();
                if ((moduleOrClassAccess == 'N') || ((desiredAccess == 'W') && (moduleOrClassAccess == 'R')))
                {
                    return null;
                }
                else
                {
                    // good access!
                    if (!str_token.hasMoreTokens())
                    {  // if at end
                        return moduleOrClass;  //success
                        // else search recursively
                    }
                    else
                    {
                        //keep searching next level by doing next outer loop
                        parent = moduleOrClass;
                    } // end if more tokens
                } // end if good access
            } // end if (found)
        } // end while more tokens

        System.err.println("NslSystem:getRefToModuleOrClass:bottom returning null");
        return null;
    }

    public void nslCallMethod(String name, String method, Object[] parameters)
    {
        NslHierarchy moduleOrClass = getRefToModuleOrClass(name, 'R');
        StringBuilder params=new StringBuilder();
        Class[] classes=new Class[parameters.length];
        for(int i=0; i<parameters.length; i++)
        {
            classes[i]=parameters[i].getClass();
            if(i>0)
                params.append(", ");
            params.append(classes[i].getName());
        }
        try
        {
            Method m=moduleOrClass.getClass().getMethod(method, classes);
            m.invoke(moduleOrClass, parameters);
        }
        catch(Exception e)
        {
            System.out.println("Error: Couldn't find method " + name + '.'+method+"("+params.toString()+") {}");
        }

    }
    public NslData nslGetDataVar(String name)
    {
        char desiredAccess = 'R';
        NslData nslnum;
        nslnum = nslGetDataVar(name, desiredAccess);
        return nslnum;
    }

    public NslData nslGetDataVar(String name, char desiredAccess)
    {
        int index;
        String moduleOrClass_name;
        String nsl_data_name;
        NslHierarchy moduleOrClass;
        index = name.lastIndexOf('.');
        if (index == -1)
        {
            // parse error
            return null;
        }
        moduleOrClass_name = name.substring(0, index);
        nsl_data_name = name.substring(index + 1);
        moduleOrClass = getRefToModuleOrClass(moduleOrClass_name, desiredAccess);
        if (moduleOrClass == null)
        {
            return null;
        }
        else
        {
            return moduleOrClass.nslGetDataVar(nsl_data_name, desiredAccess);
        }
    }

    public NslData nslGetValue(String name)
    {
        NslData temp;
        temp = nslGetDataVar(name, 'R');
        if (temp == null)
        {
            System.err.println("NslSystem: nslGetValue: variable is wrong or is not readable " + name);
            System.err.println("Note: Use whole name (hierarchical name) starting with the model name.");
            return null;
        }
        else
        {
            return temp.duplicateThis();
        }
    }

    public boolean nslSetValue(String target, NslData value)
    {
        NslData temp;
        temp = nslGetDataVar(target, 'W');
        if (temp == null)
        {
            return false;
        }
        else
        {
            return nslSetValueGeneric(temp, value);
        }
    }

    public boolean nslSetValue(NslData target, String name)
    {
        NslData temp;
        if (target.nslGetAccess() != 'W')
        {
            return false;
        }
        // todo: do not allow write if parents, grandparent access is not write.
        temp = nslGetDataVar(name, 'R');
        if (temp == null)
        {
            return false;
        }
        else
        {
            return nslSetValueGeneric(target, temp);
        }
    }

    public static boolean nslSetValueGeneric(NslData target, NslData provider)
    {
        int targetdim = target.getDimensions();
        int providerdim = provider.getDimensions();

        if (target instanceof NslNumeric &&
                provider instanceof NslNumeric)
        {
            if (targetdim == providerdim)
            {
                switch (targetdim)
                {
                    case 0:
                        ((NslNumeric0) target)._set((NslNumeric0) provider);
                        break;
                    case 1:
                        ((NslNumeric1) target)._set((NslNumeric1) provider);
                        break;
                    case 2:
                        ((NslNumeric2) target)._set((NslNumeric2) provider);
                        break;
                    case 3:
                        ((NslNumeric3) target)._set((NslNumeric3) provider);
                        break;
                    case 4:
                        ((NslNumeric4) target)._set((NslNumeric4) provider);
                        break;
                }
                return true;
            }
            else if (providerdim == 0)
            {
                switch (targetdim)
                {
                    case 1:
                        ((NslNumeric1) target)._set((NslNumeric0) provider);
                        break;
                    case 2:
                        ((NslNumeric2) target)._set((NslNumeric0) provider);
                        break;
                    case 3:
                        ((NslNumeric3) target)._set((NslNumeric0) provider);
                        break;
                    case 4:
                        ((NslNumeric4) target)._set((NslNumeric0) provider);
                        break;
                }
                return true;
            } // else
        }
        else if (target instanceof NslString &&
                provider instanceof NslString)
        {
            if (targetdim == providerdim)
            {
                switch (targetdim)
                {
                    case 0:
                        ((NslString0) target).set((NslString0) provider);
                        break;
                }
                return true;
            }
        }
        else if (target instanceof NslBoolean &&
                provider instanceof NslBoolean)
        {
            if (targetdim == providerdim)
            {
                switch (targetdim)
                {
                    case 0:
                        ((NslBoolean0) target).set((NslBoolean0) provider);
                        break;
                    case 1:
                        ((NslBoolean1) target).set((NslBoolean1) provider);
                        break;
                    case 2:
                        ((NslBoolean2) target).set((NslBoolean2) provider);
                        break;
                    case 3:
                        ((NslBoolean3) target).set((NslBoolean3) provider);
                        break;
                    case 4:
                        ((NslBoolean4) target).set((NslBoolean4) provider);
                        break;
                }
                return true;
            }
            else if (providerdim == 0)
            {
                switch (targetdim)
                {
                    case 1:
                        ((NslBoolean1) target).set((NslBoolean0) provider);
                        break;
                    case 2:
                        ((NslBoolean2) target).set((NslBoolean0) provider);
                        break;
                    case 3:
                        ((NslBoolean3) target).set((NslBoolean0) provider);
                        break;
                    case 4:
                        ((NslBoolean4) target).set((NslBoolean0) provider);
                        break;
                }
                return true;
            }
        }
        return false;
    }

    public void nslPrintAllVariables()
    {
        printModuleVariablesRecursively(modelRef);
    }

    public static void printModuleVariablesRecursively(NslModule module)
    {
        Vector moduleChildren = module.nslGetModuleChildrenVector();
        Vector dataChildrenVector = module.nslGetDataVarsVector();
        Vector classChildren = module.nslGetClassInstancesVector();
        System.out.println("Module " + module.nslGetName());
        System.out.println("");
        Enumeration d = dataChildrenVector.elements();
        NslData data;
        while (d.hasMoreElements())
        {
            data = (NslData) d.nextElement();
            System.out.println(data.nslGetName());
            System.out.println(data);
            System.out.println("");
        }

        Enumeration c = classChildren.elements();
        while (c.hasMoreElements())
        {
            printClassVariablesRecursively((NslClass) c.nextElement());
        }

        Enumeration e = moduleChildren.elements();
        while (e.hasMoreElements())
        {
            printModuleVariablesRecursively((NslModule) e.nextElement());
        }
    }

    public static void printClassVariablesRecursively(NslClass cl)
    {
        Vector dataChildrenVector = cl.nslGetDataVarsVector();
        Vector classChildren = cl.nslGetClassInstancesVector();
        Enumeration d = dataChildrenVector.elements();
        NslData data;
        while (d.hasMoreElements())
        {
            data = (NslData) d.nextElement();
            System.out.println(data.nslGetName());
            System.out.println(data);
            System.out.println("");
        }

        Enumeration c = classChildren.elements();
        while (c.hasMoreElements())
        {
            printClassVariablesRecursively((NslClass) c.nextElement());
        }
    }

    /**
     * Get the current module context
     *
     * @return currnet module
     */
    public NslModule nslGetModelRef()
    {
        return modelRef;
    }

    /**
     * Add user command into the system
     *
     * @param command
     */
    public void addCommand(NslCmd command)
    {
        cmd_list.addElement(command);
    }

    /**
     * get the user command with name <tt>name</tt>
     *
     * @param name the command name
     * @return the command with the name, null if not found
     */
    public NslCmd getCommand(String name)
    {
        Enumeration E = cmd_list.elements();
        NslCmd command;
        if (E.hasMoreElements())
        {
            while (E.hasMoreElements())
            {
                command = (NslCmd) E.nextElement();
                if (name.equals(command.nslGetName()))
                {
                    return command;
                }
            }
        }
        return null;
    }

    /**
     * Set the current user command interpreter
     *
     * @param interp
     */
    public void setInterpreter(NslInterpreter interp)
    {
        interpreter = interp;
    }

    /**
     * Get the current user command interpreter
     *
     * @return Interpreter
     */
    public NslInterpreter getInterpreter()
    {
        return interpreter;
    }

    public void waitScheduler()
    {
        interpMonitor.nslWait();
    }

    public void breakTrainingCycles()
    {
        breakStep();
        setCurrentCycle((int) (train_end_time / nslGetTrainDelta()));
        setCurrentTime(train_end_time);
        continueStep();
    }

    public void breakRunCycles()
    {
        breakStep();
        setCurrentCycle((int) (end_time / nslGetRunDelta()));
        setCurrentTime(end_time);
        continueStep();
    }

    public void breakCycles()
    {
        switch (scheduler.schedulerMode)
        {
            case 'R':
                breakRunCycles();
                break;
            case 'T':
                breakTrainingCycles();
                break;
            default:
                System.out.println("Error breakCycles");
        }
    }

    public void breakEpochs()
    {
        scheduler.breakCycles();
        continueCmd();
    }

    public void breakSim()
    {
        scheduler.breakCycles();
        continueCmd();
    }

    public void continueCmd()
    {
        interpMonitor.nslNotify();
    }

    public void waitStep()
    {
        stepMonitor.nslWait();
    }

    public void notifyStep()
    {
        stepMonitor.nslNotify();
    }

    public void waitTheScheduler()
    {
        displayMonitor.nslRecv();
    }

    public synchronized void notifyDisplays()
    {
        displayMonitor.nslSend();
    }

    public void waitDisplayAck()
    {
        displayMonitorAck.nslRecv();
    }

    public synchronized void notifySchedulerAck()
    {
        displayMonitorAck.nslSend();
    }

    public synchronized boolean isStepHalted()
    {
        return breakState;
    }

    public synchronized void breakStep()
    {
        breakState = true;
    }

    public synchronized void continueStep()
    {
        breakState = false;
        notifyStep();
    }

    public synchronized boolean isStepCmdRunning()
    {
        return stepCmdRun;
    }

    public synchronized void stepCmdStarted()
    {
        stepCmdRun = true;
        continueStep();
    }

    public synchronized void stepCmdFinished()
    {
        stepCmdRun = false;
    }

    /**
     * Set the current run-time scheduler
     *
     * @param sch
     */
    public void nslSetScheduler(NslScheduler sch)
    {
        scheduler = sch;
    }

    /**
     * Get the current run-time scheduler
     *
     * @return Scheduler
     */
    public NslScheduler getScheduler()
    {
        return scheduler;
    }

    public String getDiffApproxMethod()
    {
        if (nsldiff instanceof NslDiffEuler)
        {
            return "Euler";
        }
        else if (nsldiff instanceof NslDiffRungeKutta2)
        {
            return "RungeKutta2";
        }
        else
        {
            return "";
        }
    }

    public void setDiffApproxMethod(String method)
    {
        double delta = nsldiff.getDelta();
        double timeConstant = nsldiff.getTimeConstant();
        if (method.equals("Euler"))
        {
            nsldiff = new NslDiffEuler(this);
        }
        else if (method.equals("RungeKutta2"))
        {
            nsldiff = new NslDiffRungeKutta2(this);
        }
        nsldiff.setDelta(delta);
        nsldiff.nslSetApproximationTimeConstant(timeConstant);
    }

    public double getDiffDelta()
    {
        return nsldiff.getDelta();
    }

    public void setDiffDelta(double d)
    {
        nsldiff.setDelta(d);
    }

    public double getDiffTimeConstant()
    {
        return nsldiff.getTimeConstant();
    }

    public void setDiffTimeConstant(double t)
    {
        nsldiff.nslSetApproximationTimeConstant(t);
    }

    /**
     * Add a numerical method to the database
     *
     * @param m the numerical method
     */
    public void addApproximationMethod(NslDiff m)
    {
        nsldiff_list.addElement(m);
    }

    /**
     * Set current differetial (numerical method) to use
     *
     * @param m numerical method.
     */
    public void nslSetApproximationMethod(NslDiff m)
    {
        nsldiff = m;
    }

    /**
     * Get current differential method in use
     */
    public NslDiff nslGetApproximationMethod()
    {
        return nsldiff;
    }

    public double getEpochTimer()
    {
        return (double) totalEpochTimer;
    }

    public double getCycleTimer()
    {
        return (double) totalCycleTimer;
    }

    public double getEpochAvgTime()
    {
        return (double) totalEpochTimer / (double) (getFinishedEpochs() == 0 ? 1 : getFinishedEpochs());
    }

    public double getCycleAvgTime()
    {
        return (double) totalCycleTimer / (double) getFinishedCycles();
    }

    public void startCycleTimer()
    {
        cycleStartingTime = System.currentTimeMillis();
    }

    public void startEpochTimer()
    {
        epochStartingTime = System.currentTimeMillis();
    }

    public void stopCycleTimer()
    {
        long finishTime = System.currentTimeMillis();
        totalCycleTimer += finishTime - cycleStartingTime;
    }

    public void stopEpochTimer()
    {
        long finishTime = System.currentTimeMillis();
        totalEpochTimer += finishTime - epochStartingTime;
    }

    public void resetCycleTimer()
    {
        totalCycleTimer = 0;
    }

    public void resetEpochTimer()
    {
        totalEpochTimer = 0;
    }

    /**
     * Get the current time in simulation environment
     *
     * @return cur_time - current time
     */
    public synchronized double getCurTime()
    {
        return cur_time.doubleValue();
    }

    /**
     * Set the current time in simulation environment
     *
     * @param t - current time
     */
    public synchronized void setCurTime(double t)
    {
        cur_time = new BigDecimal(t);
    }

    /**
     * Get the current time in simulation environment
     *
     * @return cur_time - current time
     */
    public synchronized double getCurrentTime()
    {
        return cur_time.doubleValue();
    }

    /**
     * Set the current time in simulation environment
     *
     * @param t - current time
     */
    public synchronized void setCurrentTime(double t)
    {
        cur_time = new BigDecimal(t);
        /* must also set the current cycle */
    }

    /**
     * Get the current cycle in simulation environment
     *
     * @return cur_cycle - current cycle
     */
    public synchronized int getCurrentCycle()
    {
        return cur_cycle;
        /* must also set the current time */
    }

    /**
     * Get the current cycle in simulation environment
     *
     * @return cur_cycle - current cycle
     */
    public synchronized int getFinishedCycles()
    {
        return cur_cycle - 1;
        /* must also set the current time */
    }

    /**
     * Set the current cycle in simulation environment
     *
     * @param cyc - current cycle int
     */
    public synchronized void setCurCycle(int cyc)
    {
        cur_cycle = cyc;
    }

    public synchronized void setCurrentCycle(int cyc)
    {
        cur_cycle = cyc;
    }

    /**
     * Increment by a one the cycle
     */
    public synchronized void incCycle()
    {
        cur_cycle += 1;
    }

    public double getRunDelta()
    {
        return _runDelta;
    }

    public void setRunDelta(double d)
    {
        _runDelta = d;
        _runDeltaChanged = true;
    }

    public int getRunEpoch()
    {
        return runEpoch;
    }

    public double getTrainDelta()
    {
        return _trainDelta;
    }

    public void setTrainDelta(double d)
    {
        _trainDelta = d;
        _trainDeltaChanged = true;
    }

    public int getTrainEpoch()
    {
        return trainEpoch;
    }

    public void setRunEpoch(int epoch)
    {
        runEpoch = epoch;
    }

    public void setTrainEpoch(int epoch)
    {
        trainEpoch = epoch;
    }

    public void incRunEpoch()
    {
        runEpoch++;
    }

    public void incTrainEpoch()
    {
        trainEpoch++;
    }

    /**
     * Increment by a <tt>run_time_step</tt>.
     */
    public void incTime()
    {
        double temp;
        switch (scheduler.schedulerMode)
        {
            case 'R':
                temp = _runDelta;
                break;
            case 'T':
                temp = _trainDelta;
                break;
            default:
                System.out.println("Error incTime");
                temp = 0.0;
        }

        // Need to round time to significant figures in delta
        cur_time=cur_time.add(new BigDecimal(temp, new MathContext(1,RoundingMode.DOWN)));
    }

    public void setNumTrainEpochs(int n)
    {
        numTrainEpochs = n;
        endEpochChanged = true;
    }

    public void setNumRunEpochs(int n)
    {
        numRunEpochs = n;
        endEpochChanged = true;
    }

    public int getNumTrainEpochs()
    {
        return numTrainEpochs;
    }

    public int getNumRunEpochs()
    {
        return numRunEpochs;
    }

    public int getNumEpochs()
    {
        switch (scheduler.schedulerMode)
        {
            case 'R':
                return numRunEpochs;
            case 'T':
                return numTrainEpochs;
            default:
                System.out.println("error: Get Epochs");
                return 0;
        }
    }

    public int getEpochs()
    {
        return getNumEpochs();
    }

    /**
     * Get the end time in simulation environment
     *
     * @return end_time - end time
     */
    public double getEndTime()
    {
        switch (scheduler.schedulerMode)
        {
            case 'R':
                return end_time;
            case 'T':
                return train_end_time;
            default:
                System.out.println("errorGetEndTime");
                return 0.0;
        }
    }

    /**
     * Set the end time in simulation environment
     *
     * @param t - end time
     */
    public void setEndTime(double t)
    {
        end_time = t;
    }

    public void setTrainEndTime(double val)
    {
        train_end_time = val;
    }

    public double getTrainEndTime()
    {
        return train_end_time;
    }

    /**
     * Get the end time in simulation environment
     *
     * @return end_time - end time
     */
    public double getRunEndTime()
    {
        return end_time;
    }

    /**
     * Set the end time in simulation environment
     *
     * @param t - end time
     */
    public void setRunEndTime(double t)
    {
        end_time = t;
    }

    /**
     * get run step size, delta t
     *
     * @return step size
     */
    public double getDelta()
    {
        switch (scheduler.schedulerMode)
        {
            case 'R':
                return _runDelta;
            case 'T':
                return _trainDelta;
            default:
                System.out.println("Error getDelta");
                return 0.0;
        }
    }

    public int getCurrentEpoch()
    {
        switch (scheduler.schedulerMode)
        {
            case 'R':
                return runEpoch;
            case 'T':
                return trainEpoch;
            default:
                System.out.println("Error getCurrentEpoch");
                return 0;
        }
    }

    public int getFinishedEpochs()
    {
        switch (scheduler.schedulerMode)
        {
            case 'R':
                return runEpoch - 1;
            case 'T':
                return trainEpoch - 1;
            default:
                System.out.println("Error getFinishedEpochs");
                return 0;
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
     * set run step size, delta t
     *
     * @param t step size
     */
    public void nslSetRunDelta(double t)
    {
        _runDelta = t;
        if (scheduler.schedulerMode == 'T')
        {
            nsldiff.setDelta(t);
        }
        if (modelRef != null)
        {
            modelRef.nslResetRunDelta();
        }
        _runDeltaChanged = true;
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
     * set train step size, delta t
     *
     * @param t step size
     */
    public void nslSetTrainDelta(double t)
    {
        // aa: 99/9/1 what about the display delta as well???
        _trainDelta = t;
        if (scheduler.schedulerMode == 'T')
        {
            nsldiff.setDelta(t);
        }

        if (modelRef != null)
        {
            modelRef.nslResetTrainDelta();
        }
        _trainDeltaChanged = true;
    }

    /**
     * get approximation delta - integration time step / numerical method time step tm
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
        _approximationDeltaManuallySet = true;
        //98/7/21 aa: had to do since NslDiff is dependent on delta and tm
        nsldiff.nslSetApproximationDelta(_approximationDelta); //new
    }

    /**
     * get approximation timeConstant - integration time step / numerical method time step tm
     *
     * @return time step size
     */
    public double nslGetApproximationTimeConstant()
    {
        return _approximationTimeConstantTM;
    }

    /**
     * To set the time step size
     *
     * @param t time step size
     */
    public void nslSetApproximationTimeConstant(double t)
    {
        _approximationTimeConstantTM = t;
        //99/9/1 aa: had to do since NslDiff is dependent on delta and tm
        nsldiff.nslSetApproximationTimeConstant(_approximationTimeConstantTM); //new
    }

    public synchronized void addDisplaySystem(NslDisplaySystem ds)
    {
        display_system_list.addElement(ds);
        ds.start();
    }

    public synchronized void remove(NslFrame df)
    {
        for (int i = 0; i < display_system_list.size(); i++)
        {
            NslDisplaySystem ds = display_system_list.elementAt(i);
            if (ds.frame == df)
            {
                display_system_list.removeElementAt(i);
                break;
            }
        }
    }

    /**
     * getNoDisplay
     *
     * @return noDisplay
     *         <p/>
     *         98/8/4 aa
     */
    public boolean getNoDisplay()
    {
        return noDisplay;
    }

    /**
     * setNoDisplay
     *
     * @param flag
     */
    public void setNoDisplay(boolean flag)
    {
        noDisplay = flag;
    }

    /**
     * Sets the variable that defines if the standard error
     * <p/>
     * is going to be redirected to the executive or not
     *
     * @param flag The boolean value to be assigned
     */
    public void setStdErr(boolean flag)
    {
        stderr = flag;
    }

    /**
     * Sets the variable that defines if the standard output
     * <p/>
     * is going to be redirected to the executive or not
     *
     * @param flag The boolean value to be assigned
     */
    public void setStdOut(boolean flag)
    {
        stdout = flag;
    }

    /**
     * getDebug
     *
     * @return debug
     *         <p/>
     *         98/8/4 aa
     */
    public int getDebug()
    {
        return debug;
    }

    /**
     * setDebug
     *
     * @param flag
     */
    public void setDebug(int flag)
    {
        // note: 0=off; 1=some debug; 2= some more debug messages , etc
        // use if (system.debug>=x) in code to print debug messages
        debug = flag;
    }

    /**
     * getSchedulerMethod
     *
     * @return schedulerMethod
     *         <p/>
     *         98/9/11 aa
     */
    public String getSchedulerMethod()
    {
        return schedulerMethod;
    }

    /**
     * nslSetSchedulerMethod
     *
     * @param method <p/>
     *               note:
     *               <p/>
     *               <p/>
     *               <p/>
     *               "pre" parent before child
     *               <p/>
     *               "post" parent after child
     *               <p/>
     *               "mixed" init methods do p before c;
     *               <p/>
     *               run methods do p after c.
     */
    public void nslSetSchedulerMethod(String method)
    {
        schedulerMethod = method;
    }

    public void nslSetAccess(char v)
    {
        _accessibilityChar = v;
        nslSetAccessRecursive(v);
    }

    public char nslGetAccess()
    {
        return _accessibilityChar;
    }

    public void nslSetAccessRecursive(char v)
    {
        if (modelRef != null)
        {
            modelRef.nslSetAccessRecursive(v);
        }
    }

    public void nslSetBuffering(boolean v)
    {
        doubleBuffering = v;
        resetPorts();
    }

    public boolean nslGetBuffering()
    {
        return doubleBuffering;
    }

    public void resetPorts()
    {
        if (modelRef != null)
        {
            modelRef.nslResetBuffering();
            modelRef.nslConnChildren();
            modelRef.nslConnMissingLinks();
        }
    }

    public void addNslsCommand(String name, String object)
    {
        nslsCommandNames.addElement(name);
        nslsCommandObjects.addElement(object);
    }

    public String getNslsObject(String name)
    {
        String temp, object = "";
        int pos = 0;
        Enumeration e = nslsCommandNames.elements();
        while (e.hasMoreElements())
        {
            temp = (String) e.nextElement();
            if (temp.equals(name))
            {
                object = nslsCommandObjects.elementAt(pos);
                break;
            }
            pos++;
        }
        return object;
    }

    public void nslAddCommand(String name, String className)
    {
        try
        {
            Class commandType = Class.forName(className);
            Class paramTypes[] = new Class[2];
            paramTypes[0] = Class.forName("java.lang.String");
            paramTypes[1] = Class.forName("nslj.src.lang.NslModule");
            Constructor commandConstructor = commandType.getConstructor(paramTypes);
            Object params[] = new Object[2];
            params[0] = null;
            params[1] = nslGetModelRef();
            nslj.src.nsls.NslCommand command = (nslj.src.nsls.NslCommand) commandConstructor.newInstance(params);
            nslj.src.nsls.Executive.interp.createCommand(name, command);
        }
        catch (Exception e)
        {
            nslPrintln("Command \"" + name + "\" couldn't be created");
            nslPrintln("Class \"" + className + "\" was not found");
        }
    }

    public void nslAddCommand(String name, nslj.src.nsls.NslCommand command)
    {
        try
        {
            nslj.src.nsls.Executive.interp.createCommand(name, command);
        }
        catch (Exception e)
        {
            nslPrintln("Command \"" + name + "\" couldn't be created");
        }
    }

    public void reflect()
    {
        try
        {
            String objectName = jacl.tcl.lang.ReflectObject.newInstance(nslj.src.nsls.Executive.interp, this.getClass(), this).toString();
            addNslsCommand("system", objectName);
        }
        catch (jacl.tcl.lang.TclException e)
        {
        }
    }

    public void setExecutive(NslExecutive e)
    {
        executive = e;
    }

    public void addProtocolToAll(String name)
    {
        modelRef.nslAddProtocolRecursiveDown(name);
    }

    public void addProtocol(String name, NslModule module)
    {
        /*if (!noDisplay)
        {*/
            executive.addProtocol(name, module);
        //}
    }

    public void nslCreateProtocol(String name, String label, NslModule module)
    {
        //if (!noDisplay)
        //{
            executive.addProtocol(name, label, module);
        //}
    }

    public boolean protocolExist(String name)
    {
        //if (!noDisplay)
        //{
            return executive.protocolInList(name);
        //}
        //return false;
    }

    public String nslGetProtocol()
    {
        return protocolName;
    }

    public void nslSetProtocol(String name)
    {
        protocolName = name;
        //if (!noDisplay)
        //{
            executive.execProtocol(name);
        //}
    }

    public void nslPrint(String msg)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrint(msg);
        }
        else
        {
            System.out.print(msg);
        }
    }

    public void nslPrint(int value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrint(Integer.toString(value));
        }
        else
        {
            System.out.print(value);
        }
    }

    public void nslPrint(float value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrint(Float.toString(value));
        }
        else
        {
            System.out.print(value);
        }
    }

    public void nslPrint(double value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrint(Double.toString(value));
        }
        else
        {
            System.out.print(value);
        }
    }

    public void nslPrint(boolean value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrint(Boolean.toString(value));
        }
        else
        {
            System.out.print(value);
        }
    }

    public void nslPrint(Object value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrint(value.toString());
        }
        else
        {
            System.out.print(value.toString());
        }
    }

    public void nslPrintln(String msg)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(msg);
        }
        else
        {
            System.out.println(msg);
        }
    }

    public void nslPrintln(int value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(Integer.toString(value));
        }
        else
        {
            System.out.println(value);
        }
    }

    public void nslPrintln(float value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(Float.toString(value));
        }
        else
        {
            System.out.println(value);
        }
    }

    public void nslPrintln(double value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(Double.toString(value));
        }
        else
        {
            System.out.println(value);
        }
    }

    public void nslPrintln(boolean value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(Boolean.toString(value));
        }
        else
        {
            System.out.println(value);
        }
    }

    public void nslPrintln(Object value)
    {
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(value.toString());
        }
        else
        {
            System.out.println(value.toString());
        }
    }

    public void nslDebug(String className, String msg)
    {
        if (debug <= 0)
        {
            return;
        }
        if (!noDisplay && stdout && executive != null)
        {
            executive.shell.nslPrintln(className + " [Debug]: " + msg);
        }
        else
        {
            System.out.println(className + " [Debug]: " + msg);
        }
    }

    public void nslPrintStatistics()
    {
        nslPrintln("#");
        nslPrintln("# Model Name: \t" + modelRef.nslGetName());
        nslPrintln("# Protocol Name: \t" + nslGetProtocol());
        if (isSchedulerInTrainMode())
        {
            nslPrintln("# Phase: \t\tTrain");
        }
        else if (isSchedulerInRunMode())
        {
            nslPrintln("# Phase: \t\tRun");
        }
        else
        {
            nslPrintln("# Phase: \t\tEnd");
        }
        nslPrintln("# Finished Epochs:\t" + getFinishedEpochs());
        nslPrintln("# Simulation stopped at:\t" + rounded(getCurrentTime()));
        nslPrintln("# Simulation delta size :\t" + getDelta());
        nslPrintln("# Finished Cycles:\t" + getFinishedCycles());
        nslPrintln("# Average Cycle Time (ms):\t" + getCycleAvgTime());
        nslPrintln("#");
    }

    /*ERH:if you need n decimal points call this with v=10^n.  */

    private static String rounded(double t, double v, int dec)
    {
        String s = Double.toString(((long) (0.5 + t * v)) / v);
        //unfortunately s may still contain round off stuff...
        int ix = s.indexOf('.');
        if (ix == -1)
        {
            return s;
        }
        if (s.length() - ix - 1 < dec)
        {
            dec = s.length() - ix - 1;
        }
        return s.substring(0, ix) + s.substring(ix, ix + dec + 1);
    }

    private static String rounded(double t)
    {
        return rounded(t, 1000, 3);
    }

    //
    // System methods for display system managment
    //
    public void init_displays()
    {
        display_system_list.init();
    }

    public int getNumberOfDisplays()
    {
        return display_system_list.size();
    }

    public boolean frameExist(String name)
    {
        Enumeration E = display_system_list.elements();
        NslDisplaySystem ds;
        while (E.hasMoreElements())
        {
            ds = (NslDisplaySystem) E.nextElement();
            if (ds != null && ds.frame != null
                    && ds.frame.frameName.equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public NslFrame getFrame(String name)
    {
        Enumeration E = display_system_list.elements();
        NslDisplaySystem ds;
        while (E.hasMoreElements())
        {
            ds = (NslDisplaySystem) E.nextElement();
            if (ds != null && ds.frame != null
                    && ds.frame.frameName.equals(name))
            {
                return ds.frame;
            }
        }
        return null;
    }
}

