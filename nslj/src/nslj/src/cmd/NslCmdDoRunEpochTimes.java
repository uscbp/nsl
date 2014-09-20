/* SCCS @(#)NslCmdDoRunEpochTimes.java	1.2 --- 09/02/99 --21:34:47 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//
// NslCmdDoRunEpochTimes.java
//
//////////////////////////////////////////////////////////////////////

/**
 * Run all current model
 */

package nslj.src.cmd;

import nslj.src.system.NslScheduler;

import java.util.StringTokenizer;

public class NslCmdDoRunEpochTimes extends NslCmd
{

    /**
     * Setup class name and help engine
     */

    public NslCmdDoRunEpochTimes()
    {
        _name = "doRunEpochTimes";
        _simple_help_string = "doRunEpochTimes [endEpoch]";
    }

    /**
     * Print complex help on standard out
     */

    public void printHelp()
    {
        System.out.println("Nsl command : doRunEpochTimesl");
        System.out.println("usage: doRunEpochTimes [endEpoch]");
        System.out.println("All modules would be run all until endEpoch");
    }

    /**
     * Start to run the model in the current context until
     * simulation end time is reached. It will recursively
     * call the child modules to run.
     */

    public void execute()
    {
        NslScheduler scheduler = system.getScheduler();
        scheduler.runAll();
    }

    public void execute(StringTokenizer st)
    {
        if (st.hasMoreTokens())
        {
            String str = st.nextToken();
            try
            {
                execute(Integer.valueOf(str));
                return;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Run All cmd: Invalid argument: " + str);
                return;
            }
        }
        execute();
    }

    static void execute(int endEpoch)
    {
        NslScheduler scheduler = system.getScheduler();
        scheduler.runAll(endEpoch);
    }
}
//////////////////////////////////////////////////////////////////////




