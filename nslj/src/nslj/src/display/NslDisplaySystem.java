/* SCCS  %W%---%G%--%U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

/**
 @author Nikunj Mehta
 */

package nslj.src.display;

import nslj.src.system.NslDoubleSync;
import nslj.src.system.NslSystem;

public class NslDisplaySystem extends Thread
{
    private NslDoubleSync displayMonitor;
    NslSystem system;
    public NslFrame frame;
    public boolean mutex;
    public static int top = 0, left = 500;

    public NslDisplaySystem(String name, NslSystem system)
    {
        this.system = system;
        frame = new NslFrame(name);
        frame.setBounds(left, top, 500, 500);
        left += 30;
        top += 30;
        frame.setFontName("Times");
        frame.setBackgroundColor("white");
        frame.setForegroundColor("black");
        NslFrame.nslSetSystem(system);
        displayMonitor = new NslDoubleSync();
    }

    public NslDisplaySystem(NslSystem system, String n, String title,
                            int rows, int columns, int x0, int y0, int width, int height,
                            String font, String background, String foreground)
    {

        this.system = system;
        frame = new NslFrame(null);
        frame.frameName = n;
        frame.setBounds(x0, y0, width, height);
        frame.setTitle(title);
        frame.setRows(rows);
        frame.setColumns(columns);
        frame.setFontName(font);
        frame.setBackgroundColor(background);
        frame.setForegroundColor(foreground);
        NslFrame.nslSetSystem(system);
        displayMonitor = new NslDoubleSync();
    }

    public synchronized void run()
    {
        mutex = true;
        double time;

        while(true)
        {
            waitTheScheduler();

            time = system.getCurTime();

            frame.collectTrial(time);

            system.notifySchedulerAck();
        }
    }

    public void show()
    {
        frame.setVisible(true);
    }

    public void hide()
    {
        frame.setVisible(false);
    }

    public void waitTheScheduler()
    {
        displayMonitor.nslRecv();
    }

    public void notifyDisplay()
    {
        displayMonitor.nslSend();
    }

    public void initialize()
    {
        frame.refresh();
    }

    public void initializeEpoch()
    {
        frame.refreshEpoch();
    }

    public void endEpoch(int epoch)
    {
        frame.collectEpoch(epoch);
        frame.endEpoch(epoch);
    }
}
