/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//
// NslMain.java
//
//////////////////////////////////////////////////////////////////////
package nslj.src.main;

import nslj.src.cmd.*;
import nslj.src.display.*;
import nslj.src.exceptions.NslParseArgumentException;
import nslj.src.lang.NslHierarchy;
import nslj.src.lang.NslModule;
import nslj.src.math.NslDiff;
import nslj.src.math.NslDiffEuler;
import nslj.src.math.NslDiffRungeKutta2;
import nslj.src.system.NslInterpreter;
import nslj.src.system.NslMultiClockScheduler;
import nslj.src.system.NslScheduler;
import nslj.src.system.NslSystem;
import nslj.src.nsls.Executive;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * The main routine.
 * Parses the command line options, instantiates all essential
 * components in the simulator, launches the interpreter and
 * loads the model.
 * <p/>
 * The command line options are:
 * <p/>
 * -schedulerMethod  [Select the scheduling method
 * -noDisplay        [Determines if the graphic interface should be used]
 * -stdout & -stderr [Select the display where the messages should be displayed]
 * -debug	     [Determines if the debug messages should be printed or not]
 */

public class NslMain
{

    // Public variables

    /**
     * Indicates if the model has been loaded or not.
     */

    static public boolean TopLoaded = false;

    // Private members

    static NslInterpreter interpreter;
    static NslExecutive executive;
    static NslScheduler scheduler;
    static NslSystem system;

    public static String modelName = null;
    public static String modelPath = null;
    public static String libDir=null;
    static String schedulerMethod = "pre";
    static boolean noDisplay = false;
    static boolean stdout = true;
    static boolean stderr = false;
    static int debug = 0;
    public static boolean standAlone = true;
    public static boolean doubleBuffering=true;

    // Private methods

    /**
     * Parses command line arguments such as:
     * <p/>
     * schedulerMethod, noDisplay, stdout, stderr, debug
     *
     * @param argv The vector containing the command line arguments.
     * @throws NslParseArgumentException This exception is thrown if the user
     *                                   does not provide enough or correct information in the command
     *                                   line.
     */

    private static void parseArguments(String argv[]) throws NslParseArgumentException
    {

        if (argv.length < 1)
        {
            throw new NslParseArgumentException();
        }

        modelName = argv[0];
        modelPath = null;

        for (int i = 1; i < argv.length; i++)
        {

            if (argv[i].compareTo("-stdout") == 0)
            {

                if (i == argv.length)
                {
                    System.err.println("-stdout requires an argument");
                    throw new NslParseArgumentException();
                }
                i++;
                stdout = argv[i].compareTo("executive") == 0;

            }
            else if (argv[i].compareTo("-stderr") == 0)
            {

                if (i == argv.length)
                {
                    System.err.println("-stderr requires an argument");
                    throw new NslParseArgumentException();
                }
                i++;
                stderr = argv[i].compareTo("executive") == 0;

            }
            else if (argv[i].compareTo("-noDisplay") == 0)
            {

                noDisplay = true;

            }
            else if (argv[i].compareTo("-debug") == 0)
            {

                if (i == argv.length)
                {
                    System.err.println("-debug requires a positive numeric level");
                    throw new NslParseArgumentException();
                }
                i++;
                try
                {
                    debug = (Integer.valueOf(argv[i]));
                }
                catch (Exception e)
                {
                    System.err.println("-debug requires an integer argument");
                    throw new NslParseArgumentException();
                }

            }
            else if (argv[i].compareTo("-schedulerMethod") == 0)
            {

                if (i == argv.length)
                {
                    System.err.println("-schedulerMethod requires a string");
                    throw new NslParseArgumentException();
                }
                i++;
                try
                {
                    schedulerMethod = argv[i];
                }
                catch (Exception e)
                {
                    System.err.println("-schedulerMethod requires a string");
                    throw new NslParseArgumentException();
                }

            }
            else if(argv[i].compareTo("-doubleBuffering")==0)
            {
                if(i==argv.length)
                {
                    System.err.println("-doubleBuffering requires true/false");
                    throw new NslParseArgumentException();
                }
                i++;
                try
                {
                    doubleBuffering=Boolean.parseBoolean(argv[i]);
                }
                catch(Exception e)
                {
                    System.err.println("-doubleBuffering requires true/false");
                }
            }
            else if (argv[i].equals("-classpath") || argv[i].equals("-cp"))
            {
                if (i == argv.length)
                {
                    System.err.println("-classpath requires a string");
                    throw new NslParseArgumentException();
                }
                i++;
                try
                {
                    modelPath = argv[i];
                }
                catch (Exception e)
                {
                    System.err.println("-classpath requires a string");
                    throw new NslParseArgumentException();
                }
            }
            else
            {
                throw new NslParseArgumentException();
            }

        }
    }


    /**
     * Sets global system parameters such as:
     * <p/>
     * schedulerMethod, noDisplay, debug
     * <p/>
     * TODO change the method names that begin with nsl
     */

    private static void setSystemParameters()
    {
        system.setNoDisplay(noDisplay);
        system.setDebug(debug);
        system.setStdOut(stdout);
        system.setStdErr(stderr);
        system.setInterpreter(interpreter);
        system.nslSetScheduler(scheduler);
        system.nslSetSchedulerMethod(schedulerMethod);

    }


    /**
     * Add all differential / numerical methods to the system.
     * Setup the default numerical methods to run.
     * <p/>
     * TODO fix Runge-kutta method.
     */

    private static void addDefaultDiffMethods()
    {

        NslDiff diffEuler = new NslDiffEuler(system);
        NslDiff diffRK2 = new NslDiffRungeKutta2(system);

        system.addApproximationMethod(diffEuler);
        system.addApproximationMethod(diffRK2);
        system.nslSetApproximationMethod(diffEuler);
    }


    /**
     * Add user commands to the system and interpreter.
     */

    private static void addDefaultCmd()
    {

        NslCmd.nslSetSystem(system);

        system.addCommand(new NslCmdInitSys());

        system.addCommand(new NslCmdInitModule());

        system.addCommand(new NslCmdInitTrainEpoch());
        system.addCommand(new NslCmdInitTrain());
        system.addCommand(new NslCmdDoTrainEpochTimes());
        system.addCommand(new NslCmdSimTrain());
        system.addCommand(new NslCmdTrain());
        system.addCommand(new NslCmdEndTrain());
        system.addCommand(new NslCmdEndTrainEpoch());

        system.addCommand(new NslCmdInitRunEpoch());
        system.addCommand(new NslCmdInitRun());
        system.addCommand(new NslCmdDoRunEpochTimes());
        system.addCommand(new NslCmdSimRun());
        system.addCommand(new NslCmdRun());
        system.addCommand(new NslCmdEndRun());
        system.addCommand(new NslCmdEndRunEpoch());

        system.addCommand(new NslCmdTrainAndRunAll());

        system.addCommand(new NslCmdStepCycle());
        system.addCommand(new NslCmdStepEpoch());
        system.addCommand(new NslCmdStepModule());

        system.addCommand(new NslCmdCont());
        system.addCommand(new NslCmdContCycle());
        system.addCommand(new NslCmdContEpoch());
        system.addCommand(new NslCmdContModule());

        system.addCommand(new NslCmdBreak());
        system.addCommand(new NslCmdBreakCycles());
        system.addCommand(new NslCmdBreakEpochs());
        system.addCommand(new NslCmdBreakModules());

        system.addCommand(new NslCmdEndModule());

        system.addCommand(new NslCmdEndSys());

        system.addCommand(new NslCmdExit());
    }

    /**
     * Add the model to the system. All the corresponding
     * children modules are instantiated and registered
     * in the schduler.
     * <p/>
     * TODO Make the system flexible, so it can load dynamically any compiled model.
     */

    private static void loadModel()
    {

        try
        {
            NslHierarchy.nslSetSystem(system);

            NslModule model;
            if (modelPath == null)
            {
                Class classRef = Class.forName(modelName);
                libDir=classRef.getProtectionDomain().getCodeSource().getLocation().getPath();
                libDir=libDir.substring(0,libDir.indexOf(modelName)+modelName.indexOf('.'));
                model = (NslModule) classRef.newInstance();
            }
            else
            {
                URL pathURL = (new File(modelPath)).toURI().toURL();
                URLClassLoader loader = URLClassLoader.newInstance(new URL[]{pathURL});
                model = (NslModule) (loader.loadClass(modelName)).newInstance();
            }
            if (model != null)
            {
                executive = new NslExecutive(system);

                if (noDisplay)
                {
                    system.nslDebug("NslMain", "NSL 3.1 terminal version");

                    system.addModel(model);
                    NslInterpreter.execute();
                }
                else
                {
                    system.nslDebug("NslMain", "NSL 3.1 GUI version");

                    NslFrame.nslSetSystem(system);

                    executive.setVisible(true);

                    system.addModel(model);

                }
            }

        }
        catch (ClassNotFoundException classNotFoundError)
        {

            System.err.println("NslMain [Error]: The model \"" + modelName + "\" was not found in the classpath");
            System.err.println("NslMain [Suggestion]: Check for possible misspelling in the name of the model");

        }
        catch (ExceptionInInitializerError initializerError)
        {

            System.err.println("NslMain [Error]: Generated while initializing \"" + modelName + ".class\"");

        }
        catch (LinkageError linkageError)
        {

            System.err.println("NslMain [Error]: Class linkage failed while loading \"" + modelName + ".class\"");
            System.err.println("NslMain [Suggestion]: Recompile the model and/or the nsl simulator");
            if (debug > 0)
            {
                linkageError.printStackTrace();
            }

        }
        catch (InternalError internalError)
        {

            System.err.println("NslMain [Error]: An unexpected internal error has occurred in the Java VM");
            System.err.println("NslMain [Error]: " + internalError.toString().substring(internalError.toString().indexOf(':') + 2));

            System.exit(0);

        }
        catch (Exception e)
        {

            System.err.println("NslMain [Error]: \"" + e.toString() + "\"  when loading/constructing model " + modelName + ".class");
            if (debug > 0)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * Main routine for NslMain application.
     * It instantiates essential components in the
     * simulator and launch the interpreter for user input.
     * It also adds user commands, differential methods
     * and modules into the simulator.
     *
     * @param argv The vector containing the command line arguments.
     */

    public static void main(String argv[])
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            parseArguments(argv);                             // Parse command line arguments

            system = new NslSystem();                    // Create System
            interpreter = new NslInterpreter(system);         // Create Interpreter
            scheduler = new NslMultiClockScheduler(system); // Create Scheduler

            setSystemParameters();

            addDefaultDiffMethods();
            addDefaultCmd();

            loadModel();

        }
        catch (NslParseArgumentException argumentError)
        {

            System.err.println();
            System.err.println("Usage:   java NslMain \"ModelName\" [-options]");
            System.err.println();
            System.err.println("Options: -noDisplay");
            System.err.println("         -debug <level>");
            System.err.println("         -stdout <console|executive>");
            System.err.println("         -stderr <console|executive>");
            System.err.println("         -schedulerMethod <pre|post|mixed>");
            System.err.println("         -doubleBuffering <true|false>");
            System.err.println("         -classpath <path list>");

        }
        catch (Exception unrecognizedError)
        {

            System.err.println("NslMain [Error]: \"" + unrecognizedError.toString() + "\" when loading/constructing model " + modelName + ".class");

            if (debug > 0)
            {
                unrecognizedError.printStackTrace();
            }
        }
    }

    public static void close()
    {
        if(system!=null)
        {
            if(((NslMultiClockScheduler)system.scheduler).clockRunList!=null)
                ((NslMultiClockScheduler)system.scheduler).clockRunList.clear();
            if(((NslMultiClockScheduler)system.scheduler).clockTrainList!=null)
                ((NslMultiClockScheduler)system.scheduler).clockTrainList.clear();
            system.endModule();
            system.endSys();
            system.shutdown();
            system = null;
        }
        if(executive!=null)
        {
            executive.protocols.clear();
            executive.dispose();
            executive = null;
        }
        interpreter = null;
        scheduler = null;
        NslExecutive.system=null;
        NslCmd.system=null;
        NslThreadCommands.executive=null;
        NslFrame.interpreter=null;
        NslFrame.system=null;
        NslPlotInput.system=null;
        NslThreadCommands.executive=null;
        NslVariable.system=null;
        NslVariableInput.system=null;
        NslHierarchy.system=null;
        Executive.interpreter=null;
        Executive.system=null;
        Executive.interp=null;
        NslInterpreter.system=null;
        Nsl3dCanvas.objects.clear();
        NslModule.clearDisplaySystems();
    }
}
