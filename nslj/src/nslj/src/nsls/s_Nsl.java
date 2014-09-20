/* SCCS  %W%---%G%--%U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// $Log: s_Nsl.java,v $
// Revision 1.2  1998/02/03 00:23:51  erhan
// adapted new nsls structure
//
package nslj.src.nsls;

import jacl.tcl.lang.*;
import nslj.src.cmd.NslThreadCommands;
import nslj.src.display.*;
import nslj.src.lang.*;
import nslj.src.math.*;

import java.util.Vector;


public class s_Nsl implements Command {

    private Interp tclInterp;

    // nsl commands

    static final private String opts[] = {
            "source",    // 0

            "makeInst",    // 1
            "makeConn",    // 2

            "initSys",    // 3

            "initRun",    // 4
            "simRun",    // 5
            "stepRun",    // 6
            "contRun",    // 7
            "endRun",    // 8
            "run",        // 9
            "doRunEpochTimes",    // 10

            "initTrain",    // 11
            "simTrain",    // 12
            "stepTrain",    // 13
            "contTrain",    // 14
            "endTrain",    // 15
            "train",    // 16
            "doTrainEpochTimes",    // 17

            "step",        // 18
            "cont",        // 19

            "endSys",     // 20

            "break",    // 21
            "get",        // 22
            "set",        // 23
            "create",    // 24
            "exit",        // 25
            "quit",        // 26

            "initModule",    // 27
            "endModule",    // 28
            "call",        // 29
            "getObject",    // 30

            "initRunEpoch",    // 31
            "endRunEpoch",    // 32
            "initTrainEpoch",    // 33
            "endTrainEpoch",    // 34

            "breakModules",    // 35
            "breakCycles",    // 36
            "breakEpochs",    // 37

            "contModule",    // 38
            "contCycle",    // 39
            "contEpoch",    // 40

            "stepModule",    // 41
            "stepCycle",    // 42
            "stepEpoch",    // 43

            "trainAndRunAll",    // 44

            "trans",    //45

            "product",    //46

            "dotProduct",    //47

            "add",        //48

            "sub",        //49

            "elemMult",    //50
            "elemDiv",    //51

            "conv",        //52

            "convZero",    //53

            "abs",        //54

            "arcCos",    //55
            "arcSin",    //56
            "arcTan",    //57

            "cos",        //58

            "distance",    //59

            "exp",        //60

            "gausian",    //61
            "log",        //62

            "maxMerge",    //63
            "minMerge",    //64

            "pow",        //65

            "random",    //66

            "rint",        //67

            "sin",        //68

            "sqrt",        //69

            "tan",        //70

            "maxElem",    //71
            "maxValue",    //72
            "minElem",    //73
            "minValue",    //74

            "sum",        //75

            "fillRows",    //76
            "fillColumns",    //77

            "equ",        //78

            "geq",        //79

            "gtr",        //80

            "leq",        //81

            "les",        //82

            "neq",        //83

            "and",        //84
            "or",        //85

            "none",        //86
            "some",        //87
            "all",        //88

            "save",      //89
            "export",    //90

            "protocol",   //91

    };

    // nsl get options
    static final private String getOpts[] = {
            "-dim"
    };

    // nsl create options
    static final private String createOpts[] = {
            "NslOutFrame",
            "NslOutCanvas",
    };

    // nsl create NslOutputFrame switches
    static final private String createSw[] = {
            "-title",
            "-rows",
            "-columns",
            "-x0",
            "-y0",
            "-width",
            "-height",
            "-font",
            "-background",
            "-foreground",
            "-freq",
    };

    // nsl create NslOutputCanvas switches
    static final private String createSwCanvas[] = {
            "-title",
            "-var",
            "-graph",
            "-position",
            "-xlabel",
            "-ylabel",
            "-wymin",
            "-wymax",
            "-wxmin",
            "-wxmax",
            "-freq",
            "-drawcolor",
            "-drawstyle",
            "-option",
    };

    // nsl save opts
    static final private String saveOpts[] = {
            "NslFrame",
            "NslCanvas",
    };

    // nsl save switches
    static final private String saveSw[] = {
            "-filename",
            "-format",
    };

    // nsl export switches
    static final private String exportSw[] = {
            "-filename",
            "-format",
    };

    // nsl create NslOutputCanvas graph switches
    static final private String graphSw[] = {
            "Area",
            "Bar",
            "Dot",
            "Image",
            "Spatial",
            "String",
            "Temporal",
            "Grayscale",
            "Thermal",
            "Histogram"
    };

    // nsl create NslOutputCanvas option switches
    static final private String optionSw[] = {
            "rescale",
            "shift",
    };

    // nsl create NslOutputCanvas position switches
    static final private String positionSw[] = {
            "first",
            "next",
            "previous",
            "last",
    };

    private static void exec(String cmd, String param)
    {

        NslThreadCommands cmdExec = new NslThreadCommands(
                cmd, param, nslj.src.nsls.Executive.system.getInterpreter());
        if (nslj.src.nsls.Executive.readingFile)
        {
            cmdExec.run();
        }
        else
        {
            cmdExec.start();
        }
    }

    private static void exec(String cmd)
    {

        NslThreadCommands cmdExec = new NslThreadCommands(
                cmd, nslj.src.nsls.Executive.system.getInterpreter());

        if (nslj.src.nsls.Executive.readingFile)
        {
            cmdExec.run();
        }
        else
        {
            cmdExec.start();
        }
    }

    public void cmdProc(Interp interp, TclObject argv[])
            throws TclException
    {

        tclInterp = interp;

        if (argv.length < 2)
        {
            echo("wrong # args: should be \"nsl\" nsl_command ?opts?");
        }
        else
        {

            String variable;
            int index;

            index = TclIndex.get(tclInterp, argv[1], opts, "option", 0);

            switch (index)
            {
                case 0:
                    /*
                       * the following code implements the source command
                       */
                    //Executive.readingFile=true;
                    //String filename = argv[2].toString();
                    //interp.evalFile(argv[2].toString());
                    exec("Source", argv[2].toString());
                    //Executive.readingFile=false;

                    break;

                case 1:
                    /*
                       * the following code implements the makeInst command
                       */
                    echo("makeinst");
                    break;

                case 2:
                    /*
                       * the following code implements the makeConn command
                       */
                    echo("makenslConn");
                    break;

                case 3:
                    /*
                       * the following code implements the initSys command
                       */
                    nslj.src.nsls.Executive.interpreter.execute("initsys");
                    // Executive.system.init_displays();
                    break;

                case 4:
                    /*
                       * the following code implements the initRun command
                       */
                    exec("InitRun");
                    break;

                case 5:
                    /*
                       * the following code implements the simRun command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInTrainMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("SimRun");
                    }
                    else
                    {
                        exec("SimRun", Double.toString(TclDouble.get(tclInterp, argv[2])));
                    }
                    break;

                case 6:
                    /*
                       * the following code implements the stepRun command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInTrainMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }

                    if (argv.length < 3)
                    {
                        exec("1");
                    }
                    else
                    {
                        // We have to check if is number or not
                        exec(Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 7:
                    /*
                       * the following code implements the contRun command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInTrainMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("Continue");
                    }
                    else
                    {
                        exec("Continue", Double.toString(TclDouble.get(tclInterp, argv[2])));
                    }
                    break;

                case 8:
                    /*
                       * the following code implements the endRun command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInTrainMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }

                    exec("EndRun");
                    break;

                case 9:
                    /*
                       * the following code implements the run command
                       */

                    nslj.src.nsls.Executive.initMade = true;
                    if (argv.length < 3)
                    {
                        exec("Run");
                    }
                    else
                    {
                        exec("Run", Double.toString(TclDouble.get(tclInterp, argv[2])));
                    }
                    //Executive.initMade = false;
                    break;

                case 10:
                    /*
                       * the following code implements the DoRunEpochTimes command
                       */
                    nslj.src.nsls.Executive.initMade = true;
                    if (argv.length < 3)
                    {
                        exec("DoRunEpochTimes");
                    }
                    else
                    {
                        exec("DoRunEpochTimes", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    //Executive.initMade = false;
                    break;

                case 11:
                    /*
                       * the following code implements the initTrain command
                       */

                    exec("InitTrain");
                    break;

                case 12:
                    /*
                       * the following code implements the simTrain command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInRunMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in train mode");
                    }
                    exec("SimTrain");
                    break;

                case 13:
                    /*
                       * the following code implements the stepTrain command
                       */

                    if (nslj.src.nsls.Executive.system.isSchedulerInRunMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in train mode");
                    }

                    if (argv.length < 3)
                    {
                        exec("1");
                    }
                    else
                    {
                        // We have to check if is number or not
                        exec(Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 14:
                    /*
                       * the following code implements the contTrain command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInRunMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in train mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("Continue");
                    }
                    else
                    {
                        exec("Continue", String.valueOf(TclDouble.get(tclInterp, argv[2])));
                    }
                    break;

                case 15:
                    /*
                       * the following code implements the endTrain command
                       */
                    if (nslj.src.nsls.Executive.system.isSchedulerInRunMode() || !nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in train mode");
                    }

                    exec("EndTrain");
                    break;

                case 16:
                    /*
                       * the following code implements the train command
                       */
                    nslj.src.nsls.Executive.initMade = true;
                    if (argv.length < 3)
                    {
                        exec("Train");
                    }
                    else
                    {
                        exec("Train", String.valueOf(TclDouble.get(tclInterp, argv[2])));
                    }
                    //Executive.initMade = false;
                    break;

                case 17:
                    /*
                       * the following code implements the DoTrainEpochTimes command
                       */
                    nslj.src.nsls.Executive.initMade = true;
                    if (argv.length < 3)
                    {
                        exec("DoTrainEpochTimes");
                    }
                    else
                    {
                        exec("DoTrainEpochTimes", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    //Executive.initMade = false;
                    break;


                case 18:
                    /*
                       * the following code implements the step command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("Mode has not been set");
                    }
                    if (argv.length < 3)
                    {
                        exec("StepCycle");
                    }
                    else
                    {
                        // We have to check if is number or not
                        exec("StepCycle", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }

                    break;

                case 19:
                    /*
                       * the following code implements the cont command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("Mode has not been set");
                    }
                    if (argv.length < 3)
                    {
                        exec("Continue");
                    }
                    else
                    {
                        exec("Continue", String.valueOf(TclDouble.get(tclInterp, argv[2])));
                    }
                    break;

                case 20:
                    /*
                       * the following code implements the endsys command
                       */

                    nslj.src.nsls.Executive.interpreter.execute("endSys");
                    break;

                case 21:
                    /*
                       * the following code implements the break command
                       */
                    exec("Break");
                    break;

                case 22:
                    /*
                       * the following code implements the get command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl get\" variable ?opts?");
                    }

                    variable = argv[2].toString();

                    if (hasParenthesis(variable))
                    {
                        //System.out.println("Detecting Parenthesis");
                        String indices;

                        try
                        {
                            indices = getVariableIndex(variable);
                            //System.out.println("Index:         "+indices);

                            variable = getVariableName(variable);
                            //System.out.println("Variable Name: "+variable);

                            Vector v = getIndices(indices);
                            //System.out.println("Number of indices: "+v.size());

                            int l = v.size();
                            /*for (int i=0; i<l; i++) {
                               Integer elem = (Integer)v.elementAt(i);
                               System.out.println("Index["+i+"]="
                                   +elem.intValue());
                           }*/

                            NslData num = resolve_var(variable);

                            if (num == null)
                            {
                                echo("can't read \""
                                        + argv[2].toString()
                                        + "\": no such nsl variable");
                            }
                            else
                            {
                                int size;
                                Integer elem1, elem2, elem3, elem4;

                                if (l > 4)
                                {
                                    echo("Incorrect number of dimensions: should be less than 4");
                                }

                                if (num instanceof NslNumeric0)
                                {
                                    echo("Incorrect number of dimensions: should be 0");
                                }
                                else if (num instanceof NslNumeric1)
                                {
                                    if (l != 1)
                                    {
                                        echo("Incorrect number of dimensions: should be 1");
                                    }

                                    size = ((NslNumeric1) num).getSize();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    tclInterp.setResult(Double.toString(((NslNumeric1) num).getdouble(elem1)));

                                }
                                else if (num instanceof NslNumeric2)
                                {
                                    if (l != 2)
                                    {
                                        echo("Incorrect number of dimensions: should be 2");
                                    }
                                    size = num.getSize1();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    size = num.getSize2();
                                    elem2 = (Integer) v.elementAt(1);
                                    if (elem2 > size || elem2 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    tclInterp.setResult(Double.toString(((NslNumeric2) num).getdouble(elem1, elem2)));

                                }
                                else if (num instanceof NslNumeric3)
                                {
                                    if (l != 3)
                                    {
                                        echo("Incorrect number of dimensions: should be 3");
                                    }
                                    size = num.getSize1();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    size = num.getSize2();
                                    elem2 = (Integer) v.elementAt(1);
                                    if (elem2 > size || elem2 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    size = num.getSize3();
                                    elem3 = (Integer) v.elementAt(2);
                                    if (elem3 > size || elem3 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    tclInterp.setResult(Double.toString(((NslNumeric3) num).getdouble(elem1,
                                            elem2, elem3)));

                                }
                                else if (num instanceof NslNumeric4)
                                {
                                    if (l != 4)
                                    {
                                        echo("Incorrect number of dimensions: should be 4");
                                    }
                                    size = num.getSize1();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    size = num.getSize2();
                                    elem2 = (Integer) v.elementAt(1);
                                    if (elem2 > size || elem2 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    size = num.getSize3();
                                    elem3 = (Integer) v.elementAt(2);
                                    if (elem3 > size || elem3 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    size = num.getSize4();
                                    elem4 = (Integer) v.elementAt(3);
                                    if (elem4 > size || elem4 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    tclInterp.setResult(Double.toString(((NslNumeric4) num).getdouble(elem1,
                                            elem2, elem3, elem4)));
                                }
                            }

                            nslj.src.nsls.Executive.system.init_displays();

                        }
                        catch (NoSuchMethodException e)
                        {
                            echo("Sintax error in variable " + variable);
                        }

                        break;
                    }

                    NslData num1 = resolve_var(argv[2].toString());

                    if (num1 == null)
                    {
                        // We have to check if its a systema variable

                        if (hasSystem(variable))
                        {
                            tclInterp.eval("nsl call system get" + nslGetSystemVariable(variable));
                        }
                        else
                        {
                            echo("can't read \""
                                    + argv[2].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        if (argv.length == 4)
                        {
                            index = TclIndex.get(tclInterp, argv[3], getOpts, "sw", 0);

                            switch (index)
                            {
                                case 0:
                                    TclObject result = TclList.newInstance();
                                    String s;
                                    if (num1 instanceof NslNumeric1)
                                    {

                                        s = Integer.toString(((NslNumeric1) num1).getSize());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        tclInterp.setResult(result);

                                    }
                                    else if (num1 instanceof NslNumeric2)
                                    {

                                        s = Integer.toString(num1.getSize1());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        s = Integer.toString(num1.getSize2());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        tclInterp.setResult(result);

                                    }
                                    else if (num1 instanceof NslNumeric3)
                                    {

                                        s = Integer.toString(num1.getSize1());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        s = Integer.toString(num1.getSize2());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        s = Integer.toString(num1.getSize3());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        tclInterp.setResult(result);

                                    }
                                    else if (num1 instanceof NslNumeric4)
                                    {

                                        s = Integer.toString(num1.getSize1());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        s = Integer.toString(num1.getSize2());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        s = Integer.toString(num1.getSize3());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        s = Integer.toString(num1.getSize4());

                                        TclList.append(tclInterp, result,
                                                TclString.newInstance(s));

                                        tclInterp.setResult(result);

                                    }
                                    else
                                    {
                                        tclInterp.setResult("");
                                    }

                                    break;
                            }
                        }
                        else
                        {
                            tclInterp.setResult(num1.toString());
                        }
                    }
                    break;

                case 23:
                    /*
                       * the following code implements the set command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl set\" variable values_in_list_form");
                    }

                    variable = argv[2].toString();

                    if (hasParenthesis(variable))
                    {
                        //System.out.println("Detecting Parenthesis");
                        String indices;

                        try
                        {
                            indices = getVariableIndex(variable);
                            //System.out.println("Index:         "+indices);

                            variable = getVariableName(variable);
                            //System.out.println("Variable Name: "+variable);

                            Vector v = getIndices(indices);
                            //System.out.println("Number of indices: "+v.size());

                            int l = v.size();
                            /*for (int i=0; i<l; i++) {
                               Integer elem = (Integer)v.elementAt(i);
                               System.out.println("Index["+i+"]="
                                   +elem.intValue());
                           }*/

                            NslData num = resolve_var(variable);

                            if (num == null)
                            {
                                echo("can't read \""
                                        + argv[2].toString()
                                        + "\": no such nsl variable");
                            }
                            else
                            {
                                int size;
                                Integer elem1, elem2, elem3, elem4;

                                if (l > 4)
                                {
                                    echo("Incorrect number of dimensions: should be less than 4");
                                }

                                if (num instanceof NslNumeric0)
                                {
                                    echo("Incorrect number of dimensions: should be 0");
                                }
                                else if (num instanceof NslNumeric1)
                                {
                                    if (l != 1)
                                    {
                                        echo("Incorrect number of dimensions: should be 1");
                                    }

                                    size = ((NslNumeric1) num).getSize();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    if (argv.length >= 4)
                                    {
                                        //System.out.println("Assigning "+TclDouble.get(tclInterp,argv[3])+
                                        //    " to the element in "+elem1.intValue());
                                        ((NslNumeric1) num)._set(elem1,
                                                TclDouble.get(tclInterp, argv[3]));
                                    }
                                    else
                                    {
                                        echo(Double.toString(((NslNumeric1) num).getdouble(elem1.intValue())));
                                    }

                                }
                                else if (num instanceof NslNumeric2)
                                {
                                    if (l != 2)
                                    {
                                        echo("Incorrect number of dimensions: should be 2");
                                    }
                                    size = num.getSize1();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    size = num.getSize2();
                                    elem2 = (Integer) v.elementAt(1);
                                    if (elem2 > size || elem2 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    if (argv.length >= 4)
                                    {
                                        //System.out.println("Assigning "+TclDouble.get(tclInterp,argv[3])+
                                        //    " to the element in "+elem1.intValue()+","+elem2.intValue());
                                        ((NslNumeric2) num)._set(elem1,
                                                elem2,
                                                TclDouble.get(tclInterp, argv[3]));
                                    }
                                    else
                                    {
                                        echo(Double.toString(((NslNumeric2) num).getdouble(elem1.intValue(),
                                                elem2)));
                                    }


                                }
                                else if (num instanceof NslNumeric3)
                                {
                                    if (l != 3)
                                    {
                                        echo("Incorrect number of dimensions: should be 3");
                                    }
                                    size = num.getSize1();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    size = num.getSize2();
                                    elem2 = (Integer) v.elementAt(1);
                                    if (elem2 > size || elem2 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    size = num.getSize3();
                                    elem3 = (Integer) v.elementAt(2);
                                    if (elem3 > size || elem3 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    if (argv.length >= 4)
                                    {
                                        ((NslNumeric3) num)._set(elem1,
                                                elem2,
                                                elem3,
                                                TclDouble.get(tclInterp, argv[3]));
                                    }
                                    else
                                    {
                                        echo(Double.toString(((NslNumeric3) num).getdouble(elem1,
                                                elem2, elem3)));
                                    }

                                }
                                else if (num instanceof NslNumeric4)
                                {
                                    if (l != 4)
                                    {
                                        echo("Incorrect number of dimensions: should be 4");
                                    }
                                    size = num.getSize1();
                                    elem1 = (Integer) v.elementAt(0);
                                    if (elem1 > size || elem1 < 0)
                                    {
                                        echo("First dimension out of range");
                                    }

                                    size = num.getSize2();
                                    elem2 = (Integer) v.elementAt(1);
                                    if (elem2 > size || elem2 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    size = num.getSize3();
                                    elem3 = (Integer) v.elementAt(2);
                                    if (elem3 > size || elem3 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    size = num.getSize4();
                                    elem4 = (Integer) v.elementAt(3);
                                    if (elem4 > size || elem4 < 0)
                                    {
                                        echo("Second dimension out of range");
                                    }

                                    if (argv.length >= 4)
                                    {
                                        ((NslNumeric4) num)._set(elem1,
                                                elem2,
                                                elem3,
                                                elem4,
                                                TclDouble.get(tclInterp, argv[3]));
                                    }
                                    else
                                    {
                                        echo(String.valueOf(((NslNumeric4) num).getdouble(elem1,
                                                elem2, elem3, elem4)));
                                    }
                                }
                            }

                            nslj.src.nsls.Executive.system.init_displays();

                        }
                        catch (NoSuchMethodException e)
                        {
                            echo("Sintax error in variable " + variable);
                        }

                        break;
                    }

                    NslData num = resolve_var(argv[2].toString());

                    if (num == null)
                    {

                        // We have to check if its a systema variable

                        if (hasSystem(variable))
                        {
                            //String functionName = nslGetSystemVariable(variable);
                            if (argv.length < 4)
                            {
                                tclInterp.eval("nsl call system get" +
                                        nslGetSystemVariable(variable));
                            }
                            else
                            {
                                tclInterp.eval("nsl call system set" +
                                        nslGetSystemVariable(variable) + ' ' + argv[3]);
                            }
                        }
                        else
                        {
                            echo("can't read \""
                                    + argv[2].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        if (argv.length < 4)
                        {
                            echo(num.toString());
                        }
                        else
                        {
                            if (argv.length == 4)
                            {
                                int size1, size2, size3, size4;

                                if(num instanceof NslBoolean0)
                                {
                                    ((NslBoolean0) num).set(TclBoolean.get(tclInterp, argv[3]));
                                }
                                else if (num instanceof NslBoolean1)
                                {

                                    size1 = ((NslBoolean1) num).getSize();
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject element = TclList.index(tclInterp, argv[3], i);
                                            ((NslBoolean1) num).set(i, TclBoolean.get(tclInterp, element));
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }

                                }
                                else if (num instanceof NslBoolean2)
                                {

                                    size1 = num.getSize1();
                                    size2 = num.getSize2();
                                    //System.out.println("List length = "+TclList.getLength(tclInterp,argv[3]));
                                    //System.out.println("Matrix length = "+size1+"x"+size2);
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject row = TclList.index(tclInterp, argv[3], i);
                                            int limit2 = TclList.getLength(tclInterp, row);
                                            if (limit2 <= size2)
                                            {
                                                for (int j = 0; j < limit2; j++)
                                                {
                                                    TclObject element = TclList.index(tclInterp, row, j);
                                                    ((NslBoolean2) num).set(i, j, TclBoolean.get(tclInterp, element));
                                                }
                                            }
                                            else
                                            {
                                                echo("Second dimension out of range");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }
                                }
                                else if (num instanceof NslBoolean3)
                                {

                                    size1 = num.getSize1();
                                    size2 = num.getSize2();
                                    size3 = num.getSize3();
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject depth = TclList.index(tclInterp, argv[3], i);
                                            int limit2 = TclList.getLength(tclInterp, depth);
                                            if (limit2 <= size2)
                                            {
                                                for (int j = 0; j < limit2; j++)
                                                {
                                                    TclObject row = TclList.index(tclInterp, depth, j);
                                                    int limit3 = TclList.getLength(tclInterp, row);
                                                    if (limit3 <= size3)
                                                    {
                                                        for (int k = 0; k < limit3; k++)
                                                        {
                                                            TclObject element = TclList.index(tclInterp, row, k);
                                                            ((NslBoolean3) num).set(i, j, k, TclBoolean.get(tclInterp, element));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        echo("Third dimension out of range");
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                echo("Second dimension out of range");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }

                                }
                                else if (num instanceof NslBoolean4)
                                {

                                    size1 = num.getSize1();
                                    size2 = num.getSize2();
                                    size3 = num.getSize3();
                                    size4 = num.getSize4();
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject time = TclList.index(tclInterp, argv[3], i);
                                            int limit2 = TclList.getLength(tclInterp, time);
                                            if (limit2 <= size2)
                                            {
                                                for (int j = 0; j < limit2; j++)
                                                {
                                                    TclObject depth = TclList.index(tclInterp, time, j);
                                                    int limit3 = TclList.getLength(tclInterp, depth);
                                                    if (limit3 <= size3)
                                                    {
                                                        for (int k = 0; k < limit3; k++)
                                                        {
                                                            TclObject row = TclList.index(tclInterp, depth, k);
                                                            int limit4 = TclList.getLength(tclInterp, row);
                                                            if (limit4 <= size4)
                                                            {
                                                                for (int l = 0; l < limit4; l++)
                                                                {
                                                                    TclObject element = TclList.index(tclInterp, row, l);
                                                                    ((NslBoolean4) num).set(i, j, k, l, TclBoolean.get(tclInterp, element));
                                                                }
                                                            }
                                                            else
                                                            {
                                                                echo("Fourth dimension out of range");
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        echo("Third dimension out of range");
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                echo("Second dimension out of range");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }

                                }
                                if(num instanceof NslString0)
                                {
                                    ((NslString0) num).set(argv[3].toString());
                                }
                                else if (num instanceof NslNumeric0)
                                {

                                    ((NslNumeric0) num)._set(TclDouble.get(tclInterp, argv[3]));

                                }
                                else if (num instanceof NslNumeric1)
                                {

                                    size1 = ((NslNumeric1) num).getSize();
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject element = TclList.index(tclInterp, argv[3], i);
                                            ((NslNumeric1) num)._set(i, TclDouble.get(tclInterp, element));
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }

                                }
                                else if (num instanceof NslNumeric2)
                                {

                                    size1 = num.getSize1();
                                    size2 = num.getSize2();
                                    //System.out.println("List length = "+TclList.getLength(tclInterp,argv[3]));
                                    //System.out.println("Matrix length = "+size1+"x"+size2);
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject row = TclList.index(tclInterp, argv[3], i);
                                            int limit2 = TclList.getLength(tclInterp, row);
                                            if (limit2 <= size2)
                                            {
                                                for (int j = 0; j < limit2; j++)
                                                {
                                                    TclObject element = TclList.index(tclInterp, row, j);
                                                    ((NslNumeric2) num)._set(i, j, TclDouble.get(tclInterp, element));
                                                }
                                            }
                                            else
                                            {
                                                echo("Second dimension out of range");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }
                                }
                                else if (num instanceof NslNumeric3)
                                {

                                    size1 = num.getSize1();
                                    size2 = num.getSize2();
                                    size3 = num.getSize3();
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject depth = TclList.index(tclInterp, argv[3], i);
                                            int limit2 = TclList.getLength(tclInterp, depth);
                                            if (limit2 <= size2)
                                            {
                                                for (int j = 0; j < limit2; j++)
                                                {
                                                    TclObject row = TclList.index(tclInterp, depth, j);
                                                    int limit3 = TclList.getLength(tclInterp, row);
                                                    if (limit3 <= size3)
                                                    {
                                                        for (int k = 0; k < limit3; k++)
                                                        {
                                                            TclObject element = TclList.index(tclInterp, row, k);
                                                            ((NslNumeric3) num)._set(i, j, k, TclDouble.get(tclInterp, element));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        echo("Third dimension out of range");
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                echo("Second dimension out of range");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }

                                }
                                else if (num instanceof NslNumeric4)
                                {

                                    size1 = num.getSize1();
                                    size2 = num.getSize2();
                                    size3 = num.getSize3();
                                    size4 = num.getSize4();
                                    int limit1 = TclList.getLength(tclInterp, argv[3]);
                                    if (limit1 <= size1)
                                    {
                                        for (int i = 0; i < limit1; i++)
                                        {
                                            TclObject time = TclList.index(tclInterp, argv[3], i);
                                            int limit2 = TclList.getLength(tclInterp, time);
                                            if (limit2 <= size2)
                                            {
                                                for (int j = 0; j < limit2; j++)
                                                {
                                                    TclObject depth = TclList.index(tclInterp, time, j);
                                                    int limit3 = TclList.getLength(tclInterp, depth);
                                                    if (limit3 <= size3)
                                                    {
                                                        for (int k = 0; k < limit3; k++)
                                                        {
                                                            TclObject row = TclList.index(tclInterp, depth, k);
                                                            int limit4 = TclList.getLength(tclInterp, row);
                                                            if (limit4 <= size4)
                                                            {
                                                                for (int l = 0; l < limit4; l++)
                                                                {
                                                                    TclObject element = TclList.index(tclInterp, row, l);
                                                                    ((NslNumeric4) num)._set(i, j, k, l, TclDouble.get(tclInterp, element));
                                                                }
                                                            }
                                                            else
                                                            {
                                                                echo("Fourth dimension out of range");
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        echo("Third dimension out of range");
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                echo("Second dimension out of range");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        echo("First dimension out of range");
                                    }

                                }

                                nslj.src.nsls.Executive.system.init_displays();
                            }
                            else
                            {
                                echo("wrong # args: should be \"nsl set\" nsl_variable values_in_list_form");
                            }
                        }
                    }
                    break;

                case 24:
                    /*
                       * the following code implements the create command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl create\" widget_type name ?opts?");
                    }

                    int type = TclIndex.get(tclInterp, argv[2], createOpts, "option", 0);
                    String name = argv[3].toString();

                    if (!name.startsWith(".nsl."))
                    {
                        echo("Bad frame name: should be .nsl.name");
                    }

                    if (type == 0)
                    {

                        int ind, rows, columns, x0, y0, width, height, freq;
                        String title, font, background, foreground;

                        rows = 1;
                        columns = 3;
                        x0 = NslDisplaySystem.left;
                        y0 = NslDisplaySystem.top;
                        width = 500;
                        height = 500;
                        title = "";
                        background = "white";
                        foreground = "black";
                        font = "courier";

                        for (int i = 4; i < argv.length; i++)
                        {
                            ind = TclIndex.get(tclInterp, argv[i], createSw, "sw", 0);

                            switch (ind)
                            {
                                case 0:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        title = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Title name expected");
                                    }
                                    break;
                                case 1:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        rows = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Number of rows expected");
                                    }
                                    break;
                                case 2:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        columns = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Number of columns expected");
                                    }
                                    break;
                                case 3:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        x0 = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Position in x direction expected");
                                    }
                                    break;
                                case 4:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        y0 = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Position in y direction expected");
                                    }
                                    break;
                                case 5:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        width = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Frame width expected");
                                    }
                                    break;
                                case 6:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        height = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Frame height expected");
                                    }
                                    break;
                                case 7:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        font = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Font name expected");
                                    }
                                    break;
                                case 8:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        background = argv[i].toString();

                                        if (!NslColor.isValidColor(background))
                                        {
                                            echo(new StringBuilder("").append("Invalid color: ").append(background).toString());
                                        }

                                    }
                                    else
                                    {
                                        echo("Background color expected");
                                    }
                                    break;
                                case 9:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        foreground = argv[i].toString();

                                        if (!NslColor.isValidColor(foreground))
                                        {
                                            echo(new StringBuilder("").append("Invalid color: ").append(foreground).toString());
                                        }

                                    }
                                    else
                                    {
                                        echo("Foregroung name expected");
                                    }
                                    break;
                                case 10:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        freq = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Update frequency expected");
                                    }
                                    break;
                            }
                        }


                        if (title.length() == 0)
                        {
                            title = name;
                        }

                        if (nslj.src.nsls.Executive.system.frameExist(name))
                        {
                            echo("Frame " + name + " was previously used");
                        }

                        interp.createCommand(name, new nslj.src.nsls.NslFrameCommand());

                        NslDisplaySystem ds = new NslDisplaySystem(nslj.src.nsls.Executive.system,
                                name, title, rows, columns, x0, y0, width, height,
                                font, background, foreground);

                        nslj.src.nsls.Executive.system.addDisplaySystem(ds);
                        //System.out.println("Setting info "+name);
                        ds.show();
                        nslj.src.nsls.NslCommandData nd = new nslj.src.nsls.NslCommandData(ds);
                        interp.setAssocData(name, nd);

                        NslDisplaySystem.top += 30;
                        NslDisplaySystem.left += 30;

                    }
                    else
                    {
                        String title, frameName, varName, canvasName;
                        String curveColor, curveStyle, xlabel, ylabel;
                        double wymax, wymin, wxmin, wxmax;
                        int freq, graph, position, option;
                        boolean varDefined = false;

                        title = "";
                        frameName = "";
                        varName = "";
                        graph = 0;
                        position = 3;
                        xlabel = "";
                        ylabel = "";
                        wymin = -100.0;
                        wymax = 100.0;
                        wxmin = 0.0;
                        wxmax = nslj.src.nsls.Executive.system.getEndTime();
                        freq = 1;
                        curveColor = "Black";
                        option = 0;

                        for (int i = 4; i < argv.length; i++)
                        {
                            int ind = TclIndex.get(tclInterp, argv[i], createSwCanvas, "sw", 0);

                            switch (ind)
                            {
                                case 0:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        title = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Title name expected");
                                    }
                                    break;
                                case 1:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        varName = argv[i].toString();
                                        varDefined = true;
                                    }
                                    else
                                    {
                                        echo("Variable name expected");
                                    }
                                    break;
                                case 2:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        graph = TclIndex.get(tclInterp, argv[i], graphSw, "sw", 0);
                                    }
                                    else
                                    {
                                        echo("Graph type expected");
                                    }
                                    break;
                                case 3:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        position = TclIndex.get(tclInterp, argv[i], positionSw, "sw", 0);
                                    }
                                    else
                                    {
                                        echo("Position place expected");
                                    }
                                    break;
                                case 4:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        xlabel = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("X axis label expected");
                                    }
                                    break;
                                case 5:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        ylabel = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Y axis label expected");
                                    }
                                    break;
                                case 6:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        wymin = TclDouble.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Minimum Y axis value expected");
                                    }
                                    break;
                                case 7:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        wymax = TclDouble.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Maximum Y axis value expected");
                                    }
                                    break;
                                case 8:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        wxmin = TclDouble.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Minimum x axis value expected");
                                    }
                                    break;
                                case 9:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        wxmax = TclDouble.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Maximum X axis value expected");
                                    }
                                    break;
                                case 10:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        freq = TclInteger.get(tclInterp, argv[i]);
                                    }
                                    else
                                    {
                                        echo("Update frequency expected");
                                    }
                                    break;
                                case 11:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        curveColor = argv[i].toString();

                                        if (!NslColor.isValidColor(curveColor))
                                        {
                                            echo("Invalid color: " + curveColor);
                                        }

                                    }
                                    else
                                    {
                                        echo("Curve color expected");
                                    }
                                    break;

                                case 13:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        option = TclIndex.get(tclInterp, argv[i], optionSw, "sw", 0);
                                    }
                                    else
                                    {
                                        echo("Option expected");
                                    }
                                    break;
                            }
                        }

                        if (!varDefined)
                        {
                            echo("Canvas variable was not specified");
                        }

                        canvasName = getCanvasName(name);
                        frameName = getFrameName(name);

                        if (canvasName == null || frameName == null)
                        {
                            echo("Syntax error in canvas name: " + name);
                        }
                        else
                        {
                            NslFrame f = nslj.src.nsls.Executive.system.getFrame(frameName);
                            if (f != null)
                            {
                                if (!f.getPanel().canvasExist(canvasName))
                                {
                                    NslCanvas c = f.addCanvas(canvasName, varName,
                                            wymin, wymax, graphSw[graph]);

                                    interp.createCommand(name, new nslj.src.nsls.NslCanvasCommand());

                                    nslj.src.nsls.NslCommandData nd = new nslj.src.nsls.NslCommandData(c);
                                    interp.setAssocData(name, (AssocData) nd);

                                    //c.set_curve_color(NslColor.getColor(curveColor));
                                }
                            }
                            else
                            {
                                echo("Frame " + frameName + " was not found");
                            }
                        }
                    }

                    break;


                case 25:
                case 26:
                    /*
                       * the following code implements the exit/quit command
                       */
                    System.exit(0);
                case 27:
                    /*
                       * the following code implements the initModule command
                       */
                    nslj.src.nsls.Executive.interpreter.execute("initModule");
                    break;
                case 28:
                    /*
                       * the following code implements the endModule command
                       */
                    nslj.src.nsls.Executive.interpreter.execute("endModule");
                    break;
                case 29:
                    /*
                       * the following code implements the call command
                       */
                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl call\" moduleName methodName ?parameters?");
                    }

                    String objectName = nslj.src.nsls.Executive.system.getNslsObject(argv[2].toString());

                    if (objectName.length() == 0)
                    {
                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl model, module or class");
                    }
                    else
                    {
                        StringBuilder params = new StringBuilder("");

                        for (int i = 4; i < argv.length; i++)
                        {
                            params.append(argv[i]);
                            params.append(' ');
                        }

                        interp.eval(objectName + ' ' + argv[3] + ' ' + params.toString());
                    }


                    break;
                case 30:
                    /*
                       * the following code implements the getObject command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl getObject\" moduleName");
                    }
                    tclInterp.setResult(nslj.src.nsls.Executive.system.getNslsObject(argv[2].toString()));
                    break;

                case 31:
                    /*
                       * the following code implements the initRunEpoch command
                       */
                    exec("InitRunEpoch");
                    break;

                case 32:
                    /*
                       * the following code implements the endRunEpoch command
                       */
                    exec("EndRunEpoch");
                    break;

                case 33:
                    /*
                       * the following code implements the initTrainEpoch command
                       */
                    exec("InitTrainEpoch");
                    break;

                case 34:
                    /*
                       * the following code implements the endTrainEpoch command
                       */
                    exec("EndTrainEpoch");
                    break;

                case 35:
                    /*
                       * the following code implements the  breakModules command
                       */
                    exec("BreakModules");
                    break;

                case 36:
                    /*
                       * the following code implements the  breakCycles command
                       */
                    exec("BreakCycles");
                    break;

                case 37:
                    /*
                       * the following code implements the  breakEpochs command
                       */
                    exec("BreakEpochs");
                    break;

                case 38:
                    /*
                       * the following code implements the contModule command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("ContinueModule");
                    }
                    else
                    {
                        exec("ContinueModule", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 39:
                    /*
                       * the following code implements the contCycle command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("ContinueCycle");
                    }
                    else
                    {
                        exec("ContinueCycle", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 40:
                    /*
                       * the following code implements the contEpoch command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("ContinueEpoch");
                    }
                    else
                    {
                        exec("ContinueEpoch", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 41:
                    /*
                       * the following code implements the contModule command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("StepModule");
                    }
                    else
                    {
                        exec("StepModule", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 42:
                    /*
                       * the following code implements the contCycle command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("StepCycle");
                    }
                    else
                    {
                        exec("StepCycle", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 43:
                    /*
                       * the following code implements the contEpoch command
                       */
                    if (!nslj.src.nsls.Executive.initMade)
                    {
                        echo("You are not in run mode");
                    }
                    if (argv.length < 3)
                    {
                        exec("StepEpoch");
                    }
                    else
                    {
                        exec("StepEpoch", Integer.toString(TclInteger.get(tclInterp, argv[2])));
                    }
                    break;

                case 44:
                    /*
                       * the following code implements the trainAndRunAll command
                       */
                    exec("TrainAndRunAll");
                    break;

                case 45:
                    /*
                       * the following code implements the trans command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl trans\" value");
                    }

                    NslData var = resolve_var(argv[2].toString());

                    if (var != null || (var = createVar(argv[2])) != null)
                    {

                        NslDouble2 temp = null;
                        if (var instanceof NslNumeric1)
                        {

                            temp = new NslDouble2(NslTrans.eval(((NslNumeric1) var).getdouble1()));

                        }
                        else if (var instanceof NslNumeric2)
                        {

                            temp = new NslDouble2(NslTrans.eval(((NslNumeric2) var).getdouble2()));
                        }

                        tclInterp.setResult(temp.toString());


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 46:
                    /*
                       * the following code implements the product command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl prod\" variable1 variable2");
                    }

                    NslData var11 = resolve_var(argv[2].toString());

                    NslData var21;
                    if (var11 != null || (var11 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            //NslString temp = null;
                            if (var11 instanceof NslNumeric1 && var21 instanceof NslNumeric2)
                            {

                                NslDouble1 temp = new NslDouble1(NslProduct.eval(((NslNumeric1) var11).getdouble1(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var11 instanceof NslNumeric2 && var21 instanceof NslNumeric1)
                            {

                                NslDouble2 temp = new NslDouble2(NslProduct.eval(((NslNumeric2) var11).getdouble2(), ((NslNumeric1) var21).getdouble1()));

                                tclInterp.setResult(temp.toString());

                            }
                            else if (var11 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble2 temp = new NslDouble2(NslProduct.eval(((NslNumeric2) var11).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());
                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 47:
                    /*
                       * the following code implements the dotProduct command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl dotProduct\" variable1 variable2");
                    }

                    NslData var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        NslData var2 = resolve_var(argv[3].toString());

                        if (var2 != null || (var2 = createVar(argv[3])) != null)
                        {

                            //NslString temp = null;
                            if (var1 instanceof NslNumeric1 && var2 instanceof NslNumeric1)
                            {

                                NslDouble0 temp = new NslDouble0(NslDotProd.eval(((NslNumeric1) var1).getdouble1(), ((NslNumeric1) var2).getdouble1()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 48:
                    /*
                       * the following code implements the add command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl add\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            if (var1 instanceof NslNumeric0 && var21 instanceof NslNumeric0)
                            {

                                NslDouble0 temp = new NslDouble0(NslAdd.eval(((NslNumeric0) var1).getdouble(), ((NslNumeric0) var21).getdouble()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric1 && var21 instanceof NslNumeric1)
                            {

                                NslDouble1 temp = new NslDouble1(NslAdd.eval(((NslNumeric1) var1).getdouble1(), ((NslNumeric1) var21).getdouble1()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble2 temp = new NslDouble2(NslAdd.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric3 && var21 instanceof NslNumeric3)
                            {

                                NslDouble3 temp = new NslDouble3(NslAdd.eval(((NslNumeric3) var1).getdouble3(), ((NslNumeric3) var21).getdouble3()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric4 && var21 instanceof NslNumeric4)
                            {

                                NslDouble4 temp = new NslDouble4(NslAdd.eval(((NslNumeric4) var1).getdouble4(), ((NslNumeric4) var21).getdouble4()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 49:
                    /*
                       * the following code implements the sub command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl sub\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {
                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {
                            if (var1 instanceof NslNumeric0 && var21 instanceof NslNumeric0)
                            {
                                NslDouble0 temp = new NslDouble0(NslSub.eval(((NslNumeric0) var1).getdouble(), ((NslNumeric0) var21).getdouble()));
                                tclInterp.setResult(temp.toString());
                            }
                            else if (var1 instanceof NslNumeric1 && var21 instanceof NslNumeric1)
                            {
                                NslDouble1 temp = new NslDouble1(NslSub.eval(((NslNumeric1) var1).getdouble1(), ((NslNumeric1) var21).getdouble1()));
                                tclInterp.setResult(temp.toString());
                            }
                            else if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {
                                NslDouble2 temp = new NslDouble2(NslSub.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric3 && var21 instanceof NslNumeric3)
                            {

                                NslDouble3 temp = new NslDouble3(NslSub.eval(((NslNumeric3) var1).getdouble3(), ((NslNumeric3) var21).getdouble3()));
                                tclInterp.setResult(temp.toString());
                            }
                            else if (var1 instanceof NslNumeric4 && var21 instanceof NslNumeric4)
                            {

                                NslDouble4 temp = new NslDouble4(NslSub.eval(((NslNumeric4) var1).getdouble4(), ((NslNumeric4) var21).getdouble4()));
                                tclInterp.setResult(temp.toString());
                            }
                        }
                        else
                        {
                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }
                    }
                    else
                    {
                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 50:
                    /*
                       * the following code implements the elemMult command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl elemMult\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            if (var1 instanceof NslNumeric0 && var21 instanceof NslNumeric0)
                            {

                                NslDouble0 temp = new NslDouble0(NslElemMult.eval(((NslNumeric0) var1).getdouble(), ((NslNumeric0) var21).getdouble()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric1 && var21 instanceof NslNumeric1)
                            {

                                NslDouble1 temp = new NslDouble1(NslElemMult.eval(((NslNumeric1) var1).getdouble1(), ((NslNumeric1) var21).getdouble1()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble2 temp = new NslDouble2(NslElemMult.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric3 && var21 instanceof NslNumeric3)
                            {

                                NslDouble3 temp = new NslDouble3(NslElemMult.eval(((NslNumeric3) var1).getdouble3(), ((NslNumeric3) var21).getdouble3()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric4 && var21 instanceof NslNumeric4)
                            {

                                NslDouble4 temp = new NslDouble4(NslElemMult.eval(((NslNumeric4) var1).getdouble4(), ((NslNumeric4) var21).getdouble4()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;
                case 51:
                    /*
                       * the following code implements the elemDiv command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl elemDiv\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            if (var1 instanceof NslNumeric0 && var21 instanceof NslNumeric0)
                            {

                                NslDouble0 temp = new NslDouble0(NslElemDiv.eval(((NslNumeric0) var1).getdouble(), ((NslNumeric0) var21).getdouble()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric1 && var21 instanceof NslNumeric1)
                            {

                                NslDouble1 temp = new NslDouble1(NslElemDiv.eval(((NslNumeric1) var1).getdouble1(), ((NslNumeric1) var21).getdouble1()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble2 temp = new NslDouble2(NslElemDiv.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric3 && var21 instanceof NslNumeric3)
                            {

                                NslDouble3 temp = new NslDouble3(NslElemDiv.eval(((NslNumeric3) var1).getdouble3(), ((NslNumeric3) var21).getdouble3()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric4 && var21 instanceof NslNumeric4)
                            {

                                NslDouble4 temp = new NslDouble4(NslElemDiv.eval(((NslNumeric4) var1).getdouble4(), ((NslNumeric4) var21).getdouble4()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 52:
                    /*
                       * the following code implements the conv command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl conv\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble2 temp = new NslDouble2(NslConv.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;
                case 53:
                    /*
                       * the following code implements the convZero command
                       */

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl convZero\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble2 temp = new NslDouble2(NslConvZero.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 54:
                    /*
                       * the following code implements the abs command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl abs\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.abs.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.abs.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.abs.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.abs.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.abs.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 55:
                    /*
                       * the following code implements the arcCos command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl arcCos\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.acos.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.acos.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.acos.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.acos.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.acos.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 56:
                    /*
                       * the following code implements the arcSin command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl arcSin\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.asin.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.asin.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.asin.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.asin.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.asin.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 57:
                    /*
                       * the following code implements the arcTan command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl arcTan\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.atan.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.atan.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.atan.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.atan.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.atan.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 58:
                    /*
                       * the following code implements the cos command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl cos\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.cos.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.cos.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.cos.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.cos.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.cos.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 59:
                    // the following code implements the distance command

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl distance\" variable1 variable2");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        var21 = resolve_var(argv[3].toString());

                        if (var21 != null || (var21 = createVar(argv[3])) != null)
                        {

                            if (var1 instanceof NslNumeric0 && var21 instanceof NslNumeric0)
                            {

                                NslDouble0 temp = new NslDouble0(NslOperator.distance.eval(((NslNumeric0) var1).getdouble(), ((NslNumeric0) var21).getdouble()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric1 && var21 instanceof NslNumeric1)
                            {

                                NslDouble0 temp = new NslDouble0(NslOperator.distance.eval(((NslNumeric1) var1).getdouble1(), ((NslNumeric1) var21).getdouble1()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric2 && var21 instanceof NslNumeric2)
                            {

                                NslDouble1 temp = new NslDouble1(NslOperator.distance.eval(((NslNumeric2) var1).getdouble2(), ((NslNumeric2) var21).getdouble2()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric3 && var21 instanceof NslNumeric3)
                            {

                                NslDouble2 temp = new NslDouble2(NslOperator.distance.eval(((NslNumeric3) var1).getdouble3(), ((NslNumeric3) var21).getdouble3()));
                                tclInterp.setResult(temp.toString());

                            }
                            else if (var1 instanceof NslNumeric4 && var21 instanceof NslNumeric4)
                            {

                                NslDouble3 temp = new NslDouble3(NslOperator.distance.eval(((NslNumeric4) var1).getdouble4(), ((NslNumeric4) var21).getdouble4()));
                                tclInterp.setResult(temp.toString());

                            }


                        }
                        else
                        {

                            echo("can't read \""
                                    + argv[3].toString()
                                    + "\": no such nsl variable");
                        }

                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 66:
                    // the following code implements the random command
                    if(argv.length>2 && argv.length<4)
                    {
                        echo("wrong # args: should be \"nsl random\" lower upper");
                    }
                    double lower=0.0, upper=1.0;
                    if(argv.length>2)
                    {
                        var1 = resolve_var(argv[2].toString());

                        if (var1 != null || (var1 = createVar(argv[2])) != null)
                        {

                            var21 = resolve_var(argv[3].toString());

                            if (var21 != null || (var21 = createVar(argv[3])) != null)
                            {

                                if (var1 instanceof NslNumeric0 && var21 instanceof NslNumeric0)
                                {
                                    lower=((NslNumeric0)var1).getdouble();
                                    upper=((NslNumeric0)var21).getdouble();
                                }
                            }
                        }
                    }
                    NslDouble0 tempRand = new NslDouble0(NslRandom.eval(lower, upper));
                    tclInterp.setResult(tempRand.toString());
                    break;

                case 67:
                    /*
                       * the following code implements the rint command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl rint\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.rint.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.rint.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.rint.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.rint.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.rint.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                case 68:
                    /*
                       * the following code implements the sin command
                       */

                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl sin\" value");
                    }

                    var1 = resolve_var(argv[2].toString());

                    if (var1 != null || (var1 = createVar(argv[2])) != null)
                    {

                        if (var1 instanceof NslNumeric0)
                        {

                            NslDouble0 temp = new NslDouble0(NslOperator.sin.eval(((NslNumeric0) var1).getdouble()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric1)
                        {

                            NslDouble1 temp = new NslDouble1(NslOperator.sin.eval(((NslNumeric1) var1).getdouble1()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric2)
                        {

                            NslDouble2 temp = new NslDouble2(NslOperator.sin.eval(((NslNumeric2) var1).getdouble2()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric3)
                        {

                            NslDouble3 temp = new NslDouble3(NslOperator.sin.eval(((NslNumeric3) var1).getdouble3()));
                            tclInterp.setResult(temp.toString());

                        }
                        else if (var1 instanceof NslNumeric4)
                        {

                            NslDouble4 temp = new NslDouble4(NslOperator.sin.eval(((NslNumeric4) var1).getdouble4()));
                            tclInterp.setResult(temp.toString());

                        }


                    }
                    else
                    {

                        echo("can't read \""
                                + argv[2].toString()
                                + "\": no such nsl variable");
                    }

                    break;

                // save
                case 89:
                    // the following code implements the save command

                    if (argv.length < 4)
                    {
                        echo("wrong # args: should be \"nsl save\" widget_type name ?opts?");
                    }

                    type = TclIndex.get(tclInterp, argv[2], saveOpts, "option", 0);
                    name = argv[3].toString();

                    if (!name.startsWith(".nsl."))
                    {
                        echo("Bad frame name: should be .nsl.name");
                    }

                    if (type == 0)
                    {

                        int ind;
                        String filename=name+".png";
                        String format="png";

                        for (int i = 4; i < argv.length; i++)
                        {
                            ind = TclIndex.get(tclInterp, argv[i], saveSw, "sw", 0);

                            switch (ind)
                            {
                                case 0:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        filename = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Filename expected");
                                    }
                                    break;
                                case 1:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        format = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Format expected");
                                    }
                                    break;
                            }
                        }


                        if (nslj.src.nsls.Executive.system.frameExist(name))
                        {
                            NslFrame frame=nslj.src.nsls.Executive.system.getFrame(name);
                            frame.save(filename, format);
                        }
                    }
                    else
                    {
                        int ind;
                        String filename=name+".png";
                        String format="png";
                        String frameName, canvasName;

                        for (int i = 4; i < argv.length; i++)
                        {
                            ind = TclIndex.get(tclInterp, argv[i], saveSw, "sw", 0);

                            switch (ind)
                            {
                                case 0:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        filename = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Filename expected");
                                    }
                                    break;
                                case 1:
                                    if (i + 1 < argv.length)
                                    {
                                        i++;
                                        format = argv[i].toString();
                                    }
                                    else
                                    {
                                        echo("Format expected");
                                    }
                                    break;
                            }
                        }

                        canvasName = getCanvasName(name);
                        frameName = getFrameName(name);

                        if (canvasName == null || frameName == null)
                        {
                            echo("Syntax error in canvas name: " + name);
                        }
                        else
                        {
                            NslFrame f = nslj.src.nsls.Executive.system.getFrame(frameName);
                            if (f != null)
                            {
                                if (f.getPanel().canvasExist(canvasName))
                                {
                                    NslCanvas c = f.getCanvas(canvasName);

                                    c.save(filename, format);
                                }
                            }
                            else
                            {
                                echo("Frame " + frameName + " was not found");
                            }
                        }
                    }
                    break;
                case 90:
                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl export\" name ?opts?");
                    }

                    name = argv[2].toString();

                    if (!name.startsWith(".nsl."))
                    {
                        echo("Bad frame name: should be .nsl.name");
                    }

                    int ind;
                    String filename=name+".mat";
                    String format="mat";
                    String frameName, canvasName;

                    for (int i = 3; i < argv.length; i++)
                    {
                        ind = TclIndex.get(tclInterp, argv[i], exportSw, "sw", 0);

                        switch (ind)
                        {
                            case 0:
                                if (i + 1 < argv.length)
                                {
                                    i++;
                                    filename = argv[i].toString();
                                }
                                else
                                {
                                    echo("Filename expected");
                                }
                                break;
                            case 1:
                                if (i + 1 < argv.length)
                                {
                                    i++;
                                    format = argv[i].toString();
                                }
                                else
                                {
                                    echo("Format expected");
                                }
                                break;
                        }
                    }

                    canvasName = getCanvasName(name);
                    frameName = getFrameName(name);

                    if (canvasName == null || frameName == null)
                    {
                        echo("Syntax error in canvas name: " + name);
                    }
                    else
                    {
                        NslFrame f = nslj.src.nsls.Executive.system.getFrame(frameName);
                        if (f != null)
                        {
                            if (f.getPanel().canvasExist(canvasName))
                            {
                                NslCanvas c = f.getCanvas(canvasName);

                                if(format.equals("mat"))
                                    NslOutFile.outToMatlab(c, filename);
                                else if (format.equals("gnu"))
                                    NslOutFile.outToGnuplot(c, filename);
                            }
                        }
                        else
                        {
                            echo("Frame " + frameName + " was not found");
                        }
                    }
                    break;

                case 91:
                    if (argv.length < 3)
                    {
                        echo("wrong # args: should be \"nsl protocol\" name");
                    }

                    name = argv[2].toString();
                    nslj.src.nsls.Executive.system.nslSetProtocol(name);
                    break;
            }

            try
            {
                Thread.sleep(100);
                while((NslExecutive.system.scheduler.schedulerState=='R' || NslExecutive.system.scheduler.schedulerState=='T') &&
                    (!NslExecutive.system.scheduler.epochSuspended || !NslExecutive.system.scheduler.cycleSuspended || !NslExecutive.system.scheduler.moduleSuspended) &&
                    (NslExecutive.system.getCurrentTime()<NslExecutive.system.scheduler.contEndTime || NslExecutive.system.getCurrentEpoch()<NslExecutive.system.scheduler.contNumEpochs))
                    Thread.sleep(100);
            }
            catch(Exception e)
            {}
        }
    }

    private void echo(String s)
            throws TclException
    {

        tclInterp.setResult(s);
        throw new TclException(TCL.ERROR);
    }

    public NslNumeric0 createNumeric0(TclObject list) throws TclException
    {
        NslDouble0 temp = new NslDouble0();
        temp.set(TclDouble.get(tclInterp, list));
        return temp;
    }

    public NslNumeric1 createNumeric1(int dim, TclObject list) throws TclException
    {
        NslDouble1 temp = new NslDouble1(dim);
        for (int i = 0; i < dim; i++)
        {
            TclObject element = TclList.index(tclInterp, list, i);
            temp.set(i, TclDouble.get(tclInterp, element));
        }
        return temp;
    }

    public NslNumeric2 createNumeric2(int[] dim, TclObject list) throws TclException
    {
        NslDouble2 temp = new NslDouble2(dim[0], dim[1]);
        for (int i = 0; i < dim[0]; i++)
        {
            TclObject row = TclList.index(tclInterp, list, i);
            for (int j = 0; j < dim[1]; j++)
            {
                TclObject element = TclList.index(tclInterp, row, j);
                temp.set(i, j, TclDouble.get(tclInterp, element));
            }
        }
        return temp;
    }

    public NslNumeric3 createNumeric3(int[] dim, TclObject list) throws TclException
    {
        NslDouble3 temp = new NslDouble3(dim[0], dim[1], dim[2]);
        for (int i = 0; i < dim[0]; i++)
        {
            TclObject depth = TclList.index(tclInterp, list, i);
            for (int j = 0; j < dim[1]; j++)
            {
                TclObject row = TclList.index(tclInterp, depth, j);
                for (int k = 0; k < dim[2]; k++)
                {
                    TclObject element = TclList.index(tclInterp, row, k);
                    temp.set(i, j, k, TclDouble.get(tclInterp, element));
                }
            }

        }
        return temp;
    }

    public NslNumeric4 createNumeric4(int[] dim, TclObject list) throws TclException
    {
        NslDouble4 temp = new NslDouble4(dim[0], dim[1], dim[2], dim[3]);
        for (int i = 0; i < dim[0]; i++)
        {
            TclObject time = TclList.index(tclInterp, list, i);
            for (int j = 0; j < dim[1]; j++)
            {
                TclObject depth = TclList.index(tclInterp, time, j);
                for (int k = 0; k < dim[2]; k++)
                {
                    TclObject row = TclList.index(tclInterp, depth, k);
                    for (int l = 0; l < dim[3]; l++)
                    {
                        TclObject element = TclList.index(tclInterp, row, l);
                        temp.set(i, j, k, l, TclDouble.get(tclInterp, element));
                    }

                }
            }

        }

        return temp;
    }

    public NslData createVar(TclObject list) throws TclException
    {
        int i = 0, size;
        int[] dim = new int[5];
        TclObject temp = list;
        String last;

        do
        {
            dim[i++] = TclList.getLength(tclInterp, temp);
            //System.out.println("-->"+temp.toString());
            last = temp.toString();
            temp = TclList.index(tclInterp, temp, 0);
        }
        while (i < 5 && hasBrackets(last));
        size = i - 1;
        if (size == 0 && dim[0] == 1)
        {
            size = i - 1;
        }
        else
        {
            size = i;
        }

        //System.out.println("It is a "+size+" dimension variable");
        //for (int j=0; j<size; j++) {
        //    System.out.print(dim[j]+" ,");
        //}
        //System.out.println();


        switch (size)
        {
            case 0:
                return createNumeric0(list);
            case 1:
                return createNumeric1(dim[0], list);
            case 2:
                return createNumeric2(dim, list);
            case 3:
                return createNumeric3(dim, list);
            case 4:
                return createNumeric4(dim, list);
        }

        return null;
    }

    static public NslData resolve_var(String s)
    {
        return nslj.src.nsls.Executive.system.nslGetDataVar(s, 'R');
        /*NslNumeric num;
      NslModule child,m;
      boolean done;
      String t,r;

      m = Executive.system.nslGetModelRef();
      if (m==null) {
          // System.out.println("******  Top module not found  ******");
                 return null;
             } else {
                 // System.out.println("*** Top Module:" + m.nslGetName());
          done=false;

          while (!done) {
          r=root(s);
          t=tail(s);

          // System.err.println("r="+r+"    t="+t);

          if (r.equals(t)) {
              num=m.getNslNumericVar(r);

              //if (num==null)
              //   System.out.println("**** Variable not found  ****");

              return num;

          } else {
              child=m.nslGetModuleRef(r);

              if (child==null) {
              // System.out.println("**** Child not found  ****");
              return null;
              }

              m=child;
              s=tail(s);
          }
          }
      }

      return null;*/
    }

    static private String getVariableIndex(String variable)
            throws NoSuchMethodException
    {

        int o = openParenthesis(variable);
        int c = closeParenthesis(variable);

        if (o != -1 && c != -1 && o + 1 <= c - 1)
        {
            return variable.substring(o + 1, c - 1);
        }
        else
        {
            throw new NoSuchMethodException();
        }

    }

    private static String getFrameName(String name)
    {

        int i = lastdot(name);

        if (i > 5)
        {
            return name.substring(0, i);
        }
        else
        {
            return null;
        }
    }

    private static String getCanvasName(String name)
    {
        int i = lastdot(name);

        if (i > 5)
        {
            int l = name.length();
            return name.substring(i + 1, l);
        }
        else
        {
            return null;
        }
    }

    static private String getVariableName(String variable)
            throws NoSuchMethodException
    {

        int o = openParenthesis(variable);

        if (o > 0)
        {
            return variable.substring(0, o);
        }
        else
        {
            throw new NoSuchMethodException();
        }
    }

    /*
     * This is to solve the hierarchy - chetan
     */

    /*static private String root(String s)
    {
        int i;

        //System.out.println("root:"+s);
        i = lastdot(s);
        if (i != -1)
        {
            return s.substring(0, lastdot(s));
        }
        else
        {
            return s;
        }
    }

    static private String tail(String s)
    {
        //System.out.println("tail:"+s);
        return (s.substring(lastdot(s) + 1, s.length()));
    }*/

    static private boolean hasBrackets(String variable)
    {
        int o = openBrackets(variable);

        return o != -1;
    }


    static private boolean hasParenthesis(String variable)
    {
        int o = openParenthesis(variable);

        return o != -1;
    }

    static private boolean hasSystem(String variable)
    {
        return variable.startsWith("system.");
    }

    static private String nslGetSystemVariable(String variable)
    {

        int pos = 7; // system. <- it has 7 characters

        String firstCharacter = Character.toString(variable.charAt(pos));
        firstCharacter = firstCharacter.toUpperCase();
        return firstCharacter + variable.substring(pos + 1);
    }

    private Vector getIndices(String indices)
            throws NoSuchMethodException, TclException
    {

        Vector<Integer> result = new Vector<Integer>(0);
        StringBuffer strbuf = new StringBuffer("");
        char state = 's'; // 's' = Same Numbre, 'n' = New Number

        int l = indices.length();

        char element;

        for (int i = 0; i < l; i++)
        {
            element = indices.charAt(i);
            switch (state)
            {
                case 's':
                    if (element == ',')
                    {
                        state = 'n';
                    }
                    else
                    {
                        strbuf.append(element);
                    }
                    break;
                case 'n':
                    int index = TclInteger.get(tclInterp,
                            TclString.newInstance(strbuf.toString()));
                    result.add(index);
                    strbuf = new StringBuffer("");
                    strbuf.append(element);
                    state = 's';
                    break;
            }
        }

        if (state == 's')
        {
            int index = TclInteger.get(tclInterp,
                    TclString.newInstance(strbuf.toString()));
            result.add(index);
        }
        else
        {
            throw new NoSuchMethodException();
        }

        return result;
    }

    static private int openBrackets(String s)
    {
        int i, l;
        l = s.length();
        for (i = 0; i < l; i++)
        {
            if (s.charAt(i) == '{')
            {
                return i;
            }
        }

        return -1;
    }

    static private int openParenthesis(String s)
    {
        int i, l;
        l = s.length();
        for (i = 0; i < l; i++)
        {
            if (s.charAt(i) == '(')
            {
                return i;
            }
        }

        return -1;
    }


    static private int closeParenthesis(String s)
    {
        int l = s.length();

        if (s.charAt(l - 1) == ')')
        {
            return l;
        }
        else
        {
            return -1;
        }
    }

    static private int lastdot(String s)
    {
        int i, l;
        l = s.length();

        for (i = l - 1; i >= 0; i--)
        {
            if (s.charAt(i) == '.')
            {
                return i;
            }
        }

        return -1;
    }
}

