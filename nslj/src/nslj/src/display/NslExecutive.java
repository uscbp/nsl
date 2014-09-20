/* SCCS  @(#)NslExecutive.java	1.34---09/02/99--21:33:10 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.cmd.NslThreadCommands;
import nslj.src.lang.*;
import nslj.src.main.NslMain;
import nslj.src.system.NslSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;


/**
 * NSL Executive window
 *
 * @author Nikunj Mehta
 */

public class NslExecutive extends JFrame implements ActionListener, ClipboardOwner
{
    //Frame myFrame;
    Vector<Vector> menuOptions;

    static final public int trainIndex = 5;
    static final public int runIndex = 12;

    JMenu protocol;
    public Vector<Protocol> protocols;
    //Clipboard clipboard;

    public static NslSystem system;
    public nslj.src.nsls.NslShell shell;

    public NslExecutive(NslSystem s)
    {

        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
                if (!NslSystem.applet)
                {
                    if (NslMain.standAlone)
                    {
                        System.exit(0);
                    }
                    else
                    {
                        if(shell!=null)
                        {
                            shell.setVisible(false);
                            shell = null;
                        }
                        NslMain.close();
                    }
                }
            }
        });

        system = s;
        s.setExecutive(this);
        setTitle("NSL Executive");
        setBounds(0, 0, 500, 250);
        JMenuBar mbar = new JMenuBar();
        JMenu m, md, sm;
        //myFrame = this;
        NslThreadCommands.setExecutive(this);

        menuOptions = new Vector<Vector>(10);
        protocols = new Vector<Protocol>(1);

        NslSystemActionAdapter systemListener = new NslSystemActionAdapter(this);
        NslEditActionAdapter editListener = new NslEditActionAdapter();

        // System
        m = createMenu("System", new String[]{"Nsls file..."}, false,systemListener);
        sm = createMenu("Set", new String[]{"NumTrainEpochs", "TrainEndTime", "TrainDelta",
                "NumRunEpochs", "RunEndTime", "RunDelta",
                "DiffApproxMethod", "DiffDelta", "DiffTimeConstant"/*, "Logging"*/},
                false,systemListener);
        m.add(sm);
        JMenuItem exit=new JMenuItem("Exit");
        exit.addActionListener(systemListener);
        m.add(exit);
        mbar.add(m);


        // Edit
        m = createMenu("Edit", new String[]{"Copy", "Paste", "Clear"}, true,editListener);
        mbar.add(m);

        // Protocol
        protocol = createMenu("Protocol", new String[]{"Manual", "Separator"}, true, new NslProtocolActionListener());
        mbar.add(protocol);

        // Simulation
        m = createMenu("Simulation", new String[]{"InitSys", "InitModule", "TrainAndRunAll", "EndModule", "EndSys"}, true, this);
        mbar.add(m);

        // Simulation menu commands run in the following sequence
        // "Initialize", "Run", "Break","Continue", "Step"
        // the sequence of commands affects a lot of the code!!!
        // To change the simulation menu, change code for enabling menuitems

        m = createMenu("Train",
                new String[]{"InitTrainEpoch", "InitTrain", "SimTrain", "EndTrain", "EndTrainEpoch", "Separator",
                        "Train", "DoTrainEpochTimes", "Separator", "Break", "BreakModules", "BreakCycles", "BreakEpochs", "Continue", "ContinueModule", "ContinueCycle", "ContinueEpoch"},
                true, this);

        sm = createMenu("StepModule",
                new String[]{"1", "2", "3", "4", "5", "10", "20", "30", "40", "50"},
                true, new NslStepModuleActionListener());
        Vector<JMenu> menuItems = new Vector<JMenu>(1);
        menuItems.addElement(sm);
        menuOptions.addElement(menuItems);
        m.add(sm);

        sm = createMenu("StepCycle",
                new String[]{"1", "2", "3", "4", "5", "10", "20", "30", "40", "50"},
                true,new NslStepCycleActionListener());
        menuItems = new Vector<JMenu>(1);
        menuItems.addElement(sm);
        menuOptions.addElement(menuItems);
        m.add(sm);

        sm = createMenu("StepEpoch",
                new String[]{"1", "2", "3", "4", "5", "10", "20", "30", "40", "50"},
                true,new NslStepEpochActionListener());
        menuItems = new Vector<JMenu>(1);
        menuItems.addElement(sm);
        menuOptions.addElement(menuItems);
        m.add(sm);

        mbar.add(m);

        m = createMenu("Run",
                new String[]{"InitRunEpoch", "InitRun", "SimRun", "EndRun", "EndRunEpoch", "Separator",
                        "Run", "DoRunEpochTimes", "Separator", "Break", "BreakModules", "BreakCycles", "BreakEpochs", "Continue", "ContinueModule", "ContinueCycle", "ContinueEpoch"},
                true, this);
        sm = createMenu("StepModule",
                new String[]{"1", "2", "3", "4", "5", "10", "20", "30", "40", "50"},
                true, new NslStepModuleActionListener());
        menuItems = new Vector<JMenu>(1);
        menuItems.addElement(sm);
        menuOptions.addElement(menuItems);
        m.add(sm);
        sm = createMenu("StepCycle",
                new String[]{"1", "2", "3", "4", "5", "10", "20", "30", "40", "50"},
                true, new NslStepCycleActionListener());
        menuItems = new Vector<JMenu>(1);
        menuItems.addElement(sm);
        menuOptions.addElement(menuItems);
        m.add(sm);
        sm = createMenu("StepEpoch",
                new String[]{"1", "2", "3", "4", "5", "10", "20", "30", "40", "50"},
                true, new NslStepEpochActionListener());
        menuItems = new Vector<JMenu>(1);
        menuItems.addElement(sm);
        menuOptions.addElement(menuItems);
        m.add(sm);

        mbar.add(m);

        // Display - may add input displays later
        md = createMenu("Display", new String[]{"New Display Frame"}, false, new NslActionAdapter(this));
        mbar.add(md);

        // Add link to bring up HTML Help page
        m = createMenu("Help", new String[]{"How To", "Command Help", "Setup"}, false, this);
        mbar.add(m);
        //mbar.setHelpMenu(m);

        this.setJMenuBar(mbar);

        disable(trainIndex, 2); // simRun
        disable(trainIndex, 3); // endRun
        disable(trainIndex, 4); // endRunEpoch
        disable(trainIndex, 7); // break
        disable(trainIndex, 8); // breakModule
        disable(trainIndex, 9); // breakCycle
        disable(trainIndex, 10); // breakEpoch
        disable(trainIndex, 11); // continue
        disable(trainIndex, 12); // continueModule
        disable(trainIndex, 13); // continueCycle
        disable(trainIndex, 14); // continueEpoch
        disable(trainIndex + 2, 0); // stepModule
        disable(trainIndex + 4, 0); // stepCycle
        disable(trainIndex + 6, 0); // stepEpoch

        disable(runIndex, 2); // simRun
        disable(runIndex, 3); // endRun
        disable(runIndex, 4); // endRunEpoch
        disable(runIndex, 7); // break
        disable(runIndex, 8); // breakModule
        disable(runIndex, 9); // breakCycle
        disable(runIndex, 10); // breakEpoch
        disable(runIndex, 11); // continue
        disable(runIndex, 12); // continueModule
        disable(runIndex, 13); // continueCycle
        disable(runIndex, 14); // continueEpoch
        disable(runIndex + 2, 0); // stepModule
        disable(runIndex + 4, 0); // stepCycle
        disable(runIndex + 6, 0); // stepEpoch

        setLayout(new GridLayout(1, 1));
        JScrollPane shellPane=new JScrollPane(shell = new nslj.src.nsls.NslShell());
        shellPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        shellPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(shellPane);

        disable(2, 1); // Copy
    }

    public void enable(int m, int s)
    {
        ((JMenuItem) ((menuOptions.elementAt(m))).elementAt(s)).setEnabled(true);
    }

    public void disable(int m, int s)
    {
        ((JMenuItem) ((menuOptions.elementAt(m))).elementAt(s)).setEnabled(false);
    }

    private static class Protocol
    {
        public String name;
        public NslModule module;
        public boolean methodFound = true;

        Protocol(String n, NslModule m)
        {
            name = n;
            module = m;
        }
    }

    public boolean protocolInList(String name)
    {
        Enumeration<Protocol> e = protocols.elements();

        Protocol p;
        String protocolName;

        while (e.hasMoreElements())
        {
            p = e.nextElement();
            protocolName = p.name;

            if (name.equals(protocolName))
            {
                return true;
            }
        }

        return false;
    }

    public void addProtocol(String name, NslModule module)
    {
        addProtocol(name, module, true);
    }

    public void addProtocol(String name, NslModule module, boolean addToMenu)
    {
        if (!protocolInList(name))
        {
            if(addToMenu)
            {
                JMenuItem m = new JMenuItem(name);
                m.addActionListener(new NslProtocolActionListener());
                protocol.add(m);
            }
            protocols.addElement(new Protocol(name, module));
            // We have to add it to the enable vector
        }
    }

    public void addProtocol(String name, String label, NslModule module)
    {
        addProtocol(name, label, module, true);
    }

    public void addProtocol(String name, String label, NslModule module, boolean addToMenu)
    {
        if (!protocolInList(name))
        {
            if(addToMenu)
            {
                JMenuItem m = new JMenuItem(label);
                m.setActionCommand(name);
                m.addActionListener(new NslProtocolActionListener());
                protocol.add(m);
            }
            protocols.addElement(new Protocol(name, module));
            // We have to add it to the enable vector
        }
    }

    private JMenu createMenu(String name, String[] items, boolean tearOff, ActionListener listener)
    {
        JLongMenu m = new JLongMenu(name, tearOff);

        Vector<JMenuItem> menuItems = new Vector<JMenuItem>(3);

        for (String item : items)
        {
            if (item.equals("Separator"))
            {
                m.addSeparator();
            }
            else
            {
                JMenuItem mi = new JMenuItem(item);
                mi.addActionListener(listener);
                m.add(mi);
                menuItems.addElement(mi);
            }
        }

        menuOptions.addElement(menuItems);
        
        return m;
    }

    private static class NslDotNslsFilter extends javax.swing.filechooser.FileFilter
    {
        public String getDescription()
        {
            return "Nsl script files";
        }

        public boolean accept(File file)
        {
            return file.isDirectory() || file.getName().endsWith(".nsls");
        }
    }

    public void execProtocol(String command)
    {
        Enumeration<Protocol> e = protocols.elements();

        Protocol p;
        while (e.hasMoreElements())
        {
            p = e.nextElement();
            if (p.name.equals(command))
            {
                try
                {
                    invokeProtocolMethod(new StringBuilder("").append(command).append("Protocol").toString(), p.module);
                    break;
                }
                catch (Exception ex)
                {
                    if (p.methodFound)
                    {
                        System.out.println(new StringBuilder("").append("Warning: Couldn't find method ").append(command).append("Protocol() in module ").append(p.module.nslGetFullName()).toString());
                    }
                    p.methodFound = false;
                }
            }
        }
    }

    private static void invokeProtocolMethod(String methodName, NslModule module)
    {
        try
        {
            Method m = module.getClass().getMethod(methodName);
            if (m != null)
            {
                m.invoke(module);
            }
        }
        catch (Exception e)
        {
        }

        for (int i = 0; i < module.child_c; i++)
        {
            invokeProtocolMethod(methodName, (NslModule) module.nslGetModuleChildrenVector().get(i));
        }
    }

    private static class NslProtocolActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();
            if (command.equals("Manual"))
            {
                command = "manual";
            }
            system.nslSetProtocol(command);
        }
    }

    private class NslEditActionAdapter implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();
            if (command.equals("Paste"))
            {
                if (!NslSystem.applet)
                {
                    nslj.src.nsls.Executive.readingFile = true;
                    Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

                    if (contents != null)
                    {
                        try
                        {
                            shell.paste(contents.getTransferData(DataFlavor.stringFlavor).toString());
                        }
                        catch (Exception e)
                        {
                        }
                    }
                    nslj.src.nsls.Executive.readingFile = false;
                }
            }
            else if (command.equals("Copy"))
            {
                if (!NslSystem.applet)
                {
                    String string = shell.getSelectedText();

                    StringSelection contents = new StringSelection(string);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, NslExecutive.this);
                }
                enable(2, 1);
            }
            else if (command.equals("Clear"))
            {
                nslj.src.nsls.Executive.readingFile = true;
                if (!NslSystem.applet)
                    shell.clear();
                nslj.src.nsls.Executive.readingFile = false;
            }
        }
    }

    public void lostOwnership(Clipboard clip, Transferable transferable)
    {
    }

    private class NslSystemActionAdapter implements ActionListener
    {
        JFrame frame;

        public NslSystemActionAdapter(JFrame frame)
        {
            this.frame = frame;
        }

        public void actionPerformed(ActionEvent evt)
        {
            if(evt.getActionCommand().equals("Exit"))
            {
                if (NslMain.standAlone)
                {
                    System.exit(0);
                }
                else
                {
                    setVisible(false);
                    shell.setVisible(false);
                    shell = null;
                    NslMain.close();
                }
            }
            else if(evt.getActionCommand().equals("Nsls file..."))
            {
                JFileChooser dialog = new JFileChooser(NslMain.modelPath);
                dialog.setDialogTitle("Source nsls file");
                dialog.setFileFilter(new NslDotNslsFilter());
                int returnVal = dialog.showOpenDialog(frame);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    String fileName = dialog.getSelectedFile().toString();
                    NslThreadCommands cmdExec = new NslThreadCommands("Source", fileName, system.getInterpreter());
                    cmdExec.start();
                }
            }
            else
            {
                new NslSetVariable(frame, evt.getActionCommand());
            }
        }
    }

    private class NslActionAdapter implements ActionListener
    {
        NslExecutive frame;

        public NslActionAdapter(NslExecutive f)
        {
            this.frame=f;
        }

        public void actionPerformed(ActionEvent evt)
        {
            if (evt.getActionCommand().equals("New Display Frame"))
            {
                if (!protocols.isEmpty())
                {
                    String leftStrs[] = new String[protocols.size()];
                    String rightStrs[] = new String[1];

                    rightStrs[0] = "manual";

                    Enumeration<Protocol> e = protocols.elements();

                    Protocol p;
                    int i = 0;
                    while (e.hasMoreElements())
                    {
                        p = e.nextElement();
                        leftStrs[i] = p.name;
                        i++;
                    }

                    NslFrameProtocols dialog = new NslFrameProtocols(frame, leftStrs, rightStrs);
                    dialog.setVisible(true);
                }
                else
                {
                    NslDummyModule m = new NslDummyModule();
                    NslDisplaySystem ds=m.nslCreateDisplaySystem(m.nslGetName());
                    ds.frame.setVisible(true);
                }
            }
        }
    }

    public static void createModuleWithProtocols(Object[] strs)
    {
        NslModule m = new NslDummyModule();
        m.nslRemoveFromLocalProtocols("manual");
        for (Object str : strs)
        {
            System.err.println(new StringBuilder("").append("Adding: ").append(str).toString());
            m.nslAddProtocolRecursiveUp(str.toString());
        }
        NslDisplaySystem ds=m.nslCreateDisplaySystem(m.nslGetName());
        ds.frame.setVisible(true);
    }

    private static class NslStepModuleActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();

            NslThreadCommands cmdExec = new NslThreadCommands("StepModule", command, system.getInterpreter());

            cmdExec.start();
        }
    }

    private static class NslStepCycleActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();

            NslThreadCommands cmdExec = new NslThreadCommands("StepCycle", command, system.getInterpreter());

            cmdExec.start();
        }
    }

    private static class NslStepEpochActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();

            NslThreadCommands cmdExec = new NslThreadCommands("StepEpoch", command, system.getInterpreter());

            cmdExec.start();
        }
    }

    public void actionPerformed(ActionEvent evt)
    {
        String command = evt.getActionCommand();

        NslThreadCommands cmdExec = new NslThreadCommands(command, system.getInterpreter());

        cmdExec.start();
    }
}

