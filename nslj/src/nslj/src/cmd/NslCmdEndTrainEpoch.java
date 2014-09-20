/* SCCS  @(#)NslCmdEndTrainEpoch.java	1.2---09/01/99--00:14:44 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//
// NslCmdEndTrainEpoch.java
//
//////////////////////////////////////////////////////////////////////

/**
 * Execute endRun of all modules
 */

package nslj.src.cmd;

import nslj.src.system.NslScheduler;

import java.util.StringTokenizer;

public class NslCmdEndTrainEpoch extends NslCmd
{

    /**
     * Setup class name and help engine
     */

    public NslCmdEndTrainEpoch()
    {
        _name = "endTrainEpoch";
        _simple_help_string = "endTrainEpoch";
    }

    public void printHelp()
    {
        System.out.println("Nsl command : endTrainEpoch");
        System.out.println("usage: endTrainEpoch");
        System.out.println("The endTrainEpoch method of all modules will be called");
    }

    /**
     * Executes endTrainEpoch in the current active model
     * including its child modules
     */

    public void execute()
    {
        NslScheduler scheduler = system.getScheduler();
        scheduler.endTrainEpoch();
    }

    public void execute(StringTokenizer st)
    {
        execute();
    }

}
