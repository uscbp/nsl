/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.


package nslj.src.display;

import nslj.src.lang.*;
import nslj.src.main.NslMain;
import nslj.src.system.NslInterpreter;
import nslj.src.system.NslSystem;
import nslj.src.display.video.QuickTimeOutputStream;
import org.jibble.epsgraphics.EpsGraphics2D;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.text.DecimalFormat;

/**
 * The Nsl Frame
 *
 * @author Danjie Pan
 * @version 1.0
 */

public class NslFrame extends JFrame implements ActionListener, ItemListener
{
    Color color;
    NslVariableInput variable_input_window = null;
    NslPlotInput plot_input_window = null;
    protected static DecimalFormat df=new DecimalFormat("0.000");
    public static NslSystem system;
    public static NslInterpreter interpreter;
    public String frameName;
    private String fontName, fg, bg;
    private NslPanel drawPanel;
    private JPanel pStatus;
    private NslRadioMenu typeMenu;
    private JMenuItem comi; //canvas options
    private JMenuItem cpmi; //canvas print
    private JMenuItem cadv; //canvas add var
    private JMenuItem zmi; //zoom
    private JMenuItem emi; //export
    private JMenuItem smi; // save

    public Color gridColor = Color.black;
    protected boolean updateCanvas=true;

    protected boolean recordFrames=false;
    protected boolean recordVideo=false;
    protected int recordFramesTemporalMode=NslCanvas.TEMPORAL_MODE_TRIAL;
    protected int recordVideoTemporalMode=NslCanvas.TEMPORAL_MODE_TRIAL;
    protected int recordFramesFormat=NslCanvas.RECORD_FRAMES_FORMAT_PNG;
    protected double recordVideoFramesPerSec=33.0;
    protected String recordDir=System.getProperty("user.dir")+"/data/";
    // Video output stream
    protected QuickTimeOutputStream recordVideoOut = null;

    public boolean printing=false;

    public NslFrame(String name)
    {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        frameName=name;
        if (frameName.length() == 0)
        {
            setTitle(".nsl.NslOutFrame");
        }
        else
        {
            frameName = ".nsl." + frameName;
            setTitle(frameName);
        }

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                NslFrame frame=(NslFrame) e.getWindow();
                for(int i=0; i<frame.getCanvasList().size(); i++)
                {
                    frame.getCanvasList().get(i).onClose();
                }
                system.remove((NslFrame) e.getWindow());
                dispose();
            }
        });  //keep for (new WindowAdapter()

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        
        JMenuBar mbar = new JMenuBar();

        // Frame
        DisplayFrameActionAdapter ds=new DisplayFrameActionAdapter(this);
        JMenu m = createMenu("Frame", new String[]{"New Canvas", "Remove Canvas"},ds);
        m.addSeparator(); //---------------
        NslRadioMenu columnsmenu = new NslRadioMenu("Columns", new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        columnsmenu.addItemListener(new ColumnAdapter());
        m.add(columnsmenu);
        JMenuItem fomi = new JMenuItem("Frame Options");
        fomi.addActionListener(ds);
        m.add(fomi);
        JMenuItem fsmi = new JMenuItem("Frame Save");
        fsmi.addActionListener(ds);
        m.add(fsmi);
        JMenuItem fpmi = new JMenuItem("Frame Print");
        fpmi.addActionListener(ds);
        m.add(fpmi);
        m.addSeparator(); //---------------
        JMenuItem close=new JMenuItem("Close");
        close.addActionListener(ds);
        m.add(close);
        mbar.add(m);
        //------------------------
        // Canvas
        // all of these menu options need to be disabled until a canvas is selected
        m = new JMenu("Canvas");
        DisplayCanvasActionAdapter dc=new DisplayCanvasActionAdapter(this);
        zmi = new JMenuItem("Zoom");
        zmi.setEnabled(false);
        zmi.addActionListener(dc);
        m.add(zmi);
        cadv = new JMenuItem("Add Variable");
        cadv.setEnabled(false);
        cadv.addActionListener(dc);
        m.add(cadv);
        initTypeMenu(null);
        m.add(typeMenu);
        comi = new JMenuItem("Canvas Options");
        comi.setEnabled(false);
        comi.addActionListener(dc);
        m.add(comi);
        smi = new JMenuItem("Canvas Save");
        smi.setEnabled(false);
        smi.addActionListener(dc);
        m.add(smi);
        cpmi = new JMenuItem("Canvas Print");
        cpmi.setEnabled(false);
        cpmi.addActionListener(dc);
        m.add(cpmi);
        emi = new JMenuItem("Export Data");
        emi.setEnabled(false);
        emi.addActionListener(dc);
        m.add(emi);
        mbar.add(m);
        //------------------------
        // Help
        m = new JMenu("Help");
        mbar.add(m);
        //mbar.setHelpMenu(m);

        //finally put up the menu bar
        this.setJMenuBar(mbar);

        pStatus = new JPanel();
        pStatus.setBackground(Color.lightGray);
        pStatus.setLayout(new FlowLayout(FlowLayout.LEFT));
        pStatus.add(new JLabel("End Cycle time: "));
        // This label would be used to display the simulation time.
        pStatus.add(new JLabel("0.0 "));
        pStatus.add(new JLabel("Finished Cycles:"));
        pStatus.add(new JLabel("0 "));
        pStatus.add(new JLabel("Finished Epochs:"));
        pStatus.add(new JLabel("0 "));
        add("South", pStatus);

        drawPanel = new NslPanel(this);

        add("Center", drawPanel);
    } // end constructor

    private void initTypeMenu(NslCanvas selectedCanvas)
    {
        if (selectedCanvas == null)
        {
            if(typeMenu!=null)
                typeMenu.removeItemListener(this);
            typeMenu = new NslRadioMenu("Change Type", new String[]{"Area", "Bar", "Dot", "Grayscale", "Histogram", "Raster", "Spatial", "Temporal", "Thermal"});
            typeMenu.addItemListener(this);
            typeMenu.setEnabled(false);
        }
        else
        {
            String type = selectedCanvas.getClass().getName().substring(selectedCanvas.getClass().getName().lastIndexOf(".Nsl")+4);
            int nameEnd = type.length() - 6; // for Canvas
            type=type.substring(0,nameEnd);

            if (selectedCanvas.getVariable()!=null && selectedCanvas.getVariable().info.getCountDimensions() < 2)
            {
                for (int i = 0; i < typeMenu.getItemCount(); i++)
                {
                    String label = typeMenu.getItem(i).getText();
                    if (label.equals("Dot"))
                    {
                        typeMenu.remove(i);
                        break;
                    }
                }
            }
            else
            {
                if (typeMenu.getItem(2).getText().equals("Area"))
                {
                    typeMenu.removeItemListener(this);
                    typeMenu.insert(new JRadioButtonMenuItem("Dot"), 2);
                    typeMenu.addItemListener(this);
                }
            }
            typeMenu.setEnabled(true);
            updateCanvasSelection(type);
        }
    }

    private void updateCanvasSelection(String type)
    {
        updateCanvas=false;
        for(int i=0; i<typeMenu.getItemCount(); i++)
        {
            typeMenu.getItem(i).setSelected(typeMenu.getItem(i).getText().equals(type));
        }
        updateCanvas=true;
    }

    private static JMenu createMenu(String name, String[] items, ActionListener listener)
    {
        JMenu m = new JMenu(name);
        for (String item : items)
        {
            JMenuItem i=new JMenuItem(item);
            i.addActionListener(listener);
            m.add(i);
        }

        return m;
    }

    public static NslFrame getFrame(ActionEvent evt)
    {
        MenuContainer menu = (MenuContainer) (evt.getSource());

        NslFrame frame;

        while (!(menu instanceof JMenuBar) && !(menu instanceof JPopupMenu))
        {
            if(((JComponent) menu).getParent()!=null)
                menu = ((JComponent) menu).getParent();
            else
                break;
        }
        frame = (NslFrame) (((JComponent) menu).getParent());
        return frame;
    }

    public void actionPerformed(ActionEvent evt)
    { // for double click servicing
        try
        {
            drawPanel.zoomCanvas();
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
        }
    }

    public void itemStateChanged(ItemEvent evt)
    {
        if(updateCanvas)
        {
            JRadioButtonMenuItem item = (JRadioButtonMenuItem) evt.getItemSelectable();
            String type = item.getText();
            try
            {
                drawPanel.changeCanvas(type);
            }
            catch (Exception e)
            {
                System.out.println("#Warning: NslFrame: " + e.toString());
            }
            plotSelectionChanged(getCurrentCanvas(),false);
        }
    }

    public boolean contains3dCanvas()
    {
        boolean contains3dCanvas=false;
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            if(drawPanel.getCanvasList().get(i) instanceof Nsl3dCanvas)
            {
                contains3dCanvas=true;
                break;
            }
        }
        return contains3dCanvas;
    }

    public void plotSelectionChanged(NslCanvas canvas, boolean initTypeMenu)
    {
        if (canvas == null)
        {
            comi.setEnabled(false); //canvas options
            cpmi.setEnabled(false); //canvas print
            zmi.setEnabled(false); //zoom
            emi.setEnabled(false); //export
            smi.setEnabled(false);
            cadv.setEnabled(false); // add var
        }
        else
        {
            comi.setEnabled(true); //canvas options
            cpmi.setEnabled(true); //canvas print
            zmi.setEnabled(true); //zoom
            emi.setEnabled(true); //export
            smi.setEnabled(true);
            if(canvas instanceof NslTemporalCanvas || canvas instanceof NslSpatialCanvas)
                cadv.setEnabled(true);
            else
                cadv.setEnabled(false); // add var
        }
        if(initTypeMenu)
            initTypeMenu(canvas);
    }

    /**
     * Get screenshot of canvas
     * @return - image
     */
    protected BufferedImage getImage()
    {
        int numCols=this.drawPanel.getNumColumns();
        int numRows=this.getCanvasList().size()/numCols;

        BufferedImage img=new BufferedImage(numCols*getCanvasList().get(0).getWidth(),
                                            numRows*getCanvasList().get(0).getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        int row=0;
        int col=0;
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            int canvasHeight=drawPanel.getCanvasList().get(i).getHeight();
            int canvasWidth=drawPanel.getCanvasList().get(i).getWidth();
            img.getGraphics().drawImage(drawPanel.getCanvasList().get(i).getImage(), col*canvasWidth,
                                        row*canvasHeight, canvasWidth, canvasHeight, null);
            col++;
            if(col==numCols)
            {
                col=0;
                row++;
            }
        }
        return img;
    }

    public void save()
    {
        /**
         * All canvases must have doublebuffering set to false for this to work. The printAll method (called below)
         * calles the paintComponent method of each canvas. This method is also used to refresh the canvas and thus
         * sets doublebuffering to true. The printing flag prevents this.
         */
        printing=true;
        // Set doublebuffering to false and save current setting
        boolean buffering=NslMain.doubleBuffering;
        NslMain.doubleBuffering=false;
        // Turn off doublebuffering on each canvas, but save their setting to turn it back on when done
        boolean[] canvasBuffering=new boolean[drawPanel.getCanvasList().size()];
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            canvasBuffering[i]=drawPanel.getCanvasList().get(i).doubleBuffering;
            drawPanel.getCanvasList().get(i).doubleBuffering=false;
        }
        BufferedImage img=getImage();
        // Restore double-buffering settings
        NslMain.doubleBuffering=buffering;
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            drawPanel.getCanvasList().get(i).doubleBuffering=canvasBuffering[i];
        }
        printing=false;
        ImageSaveDialog sd=new ImageSaveDialog(this);
        sd.setDialogTitle("Select Frame Capture File");
        sd.save(img, !contains3dCanvas());
        if(sd.getSelectedFile()!=null && sd.getSelectedFile().getName().toLowerCase().endsWith("eps"))
        {
            try
            {
                writeEps(sd.getSelectedFile().getAbsolutePath());
            }
            catch(IOException e)
            {}
        }
    }

    public void save(String filename, String format)
    {
        printing=true;
        // Set doublebuffering to false and save current setting
        boolean buffering=NslMain.doubleBuffering;
        NslMain.doubleBuffering=false;
        // Turn off doublebuffering on each canvas, but save their setting to turn it back on when done
        boolean[] canvasBuffering=new boolean[drawPanel.getCanvasList().size()];
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            canvasBuffering[i]=drawPanel.getCanvasList().get(i).doubleBuffering;
            drawPanel.getCanvasList().get(i).doubleBuffering=false;
        }
        BufferedImage img=getImage();
        // Restore double-buffering settings
        NslMain.doubleBuffering=buffering;
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            drawPanel.getCanvasList().get(i).doubleBuffering=canvasBuffering[i];
        }
        printing=false;
        if(format.equals("eps"))
        {
            try
            {
                writeEps(filename);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            ImageSaveDialog.save(img, filename, format);
        }
    }

    private void writeEps(String filename)
            throws IOException
    {
        boolean buffering;
        boolean[] canvasBuffering;
        EpsGraphics2D eps = new EpsGraphics2D();
        printing=true;
        // Set doublebuffering to false and save current setting
        buffering= NslMain.doubleBuffering;
        NslMain.doubleBuffering=false;
        // Turn off doublebuffering on each canvas, but save their setting to turn it back on when done
        canvasBuffering=new boolean[drawPanel.getCanvasList().size()];
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            canvasBuffering[i]=drawPanel.getCanvasList().get(i).doubleBuffering;
            drawPanel.getCanvasList().get(i).doubleBuffering=false;
        }
        printAll(eps);
        // Restore double-buffering settings
        NslMain.doubleBuffering=buffering;
        for(int i=0; i<drawPanel.getCanvasList().size(); i++)
        {
            drawPanel.getCanvasList().get(i).doubleBuffering=canvasBuffering[i];
        }
        printing=false;
        eps.close();
        BufferedWriter writer=new BufferedWriter(new FileWriter(filename));
        writer.write(eps.toString());
        writer.flush();
        writer.close();
    }

    private class DisplayFrameActionAdapter implements ActionListener
    {
        NslFrame frame;

        DisplayFrameActionAdapter(NslFrame f)
        {
            frame = f;
        }

        public void actionPerformed(ActionEvent evt)
        {
            if (evt.getActionCommand().equals("Close"))
            {
                system.remove(getFrame(evt));
                dispose();
            }
            else if (evt.getActionCommand().equals("New Canvas"))
            {
                plot_input_window = new NslPlotInput(frame);
                plot_input_window.setVisible(true);
            }
            else if (evt.getActionCommand().equals("Remove Canvas"))
            {
                try
                {
                    drawPanel.removeCanvas();
                }
                catch (NoCanvasSelectedException e)
                {
                    System.err.println(e.toString());
                }
            }
            else if (evt.getActionCommand().equals("Frame Options"))
            {
                NslFrameProperty fp = new NslFrameProperty(frame);
                fp.setVisible(true);
            }
            else if ("Frame Save".equals(evt.getActionCommand()))
            {
                frame.save();
            }
            else if ("Frame Print".equals(evt.getActionCommand()))
            {
                Toolkit kit = getToolkit();
                try
                {

                    PrintJob pjob = kit.getPrintJob(frame, frame.frameName, null);
                    if (pjob != null)
                    {
                        Graphics pg = pjob.getGraphics();
                        if (pg != null)
                        {
                            frame.printAll(pg);
                            pg.dispose(); // flush page
                        }
                        pjob.end();
                    }
                }
                catch(Exception e)
                {}
            }
        }
    }

    private class ColumnAdapter implements ItemListener
    {
        public void itemStateChanged(ItemEvent evt)
        {
            try
            {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem) evt.getItemSelectable();
                String label = item.getText();
                int newColumns = Integer.parseInt(label);
                if (!drawPanel.setColumns(newColumns))
                {
                    item.setSelected(false);
                }
            }
            catch (Exception e)
            {
                System.err.println("NslFrame:Exception: Invalid number format:" + e.toString());
            }
        }
    }

    private class DisplayCanvasActionAdapter implements ActionListener
    {
        NslFrame frame;

        DisplayCanvasActionAdapter(NslFrame f)
        {
            frame = f;
        }

        public void actionPerformed(ActionEvent evt)
        {
            try
            {
                if (evt.getActionCommand().equals("Canvas Options"))
                {
                    drawPanel.setCanvasProperties();
                }
                else if (evt.getActionCommand().equals("Export Data"))
                {
                    NslOutFileProperty ndofp = new NslOutFileProperty(frame);
                    ndofp.setVisible(true);
                }
                else if (evt.getActionCommand().equals("Zoom"))
                {
                    try
                    {
                        drawPanel.zoomCanvas();
                    }
                    catch (Exception e)
                    {
                        System.err.println("NslFrame:Exception: " + e.toString());
                    }
                }
                else if (evt.getActionCommand().equals("Canvas Save"))
                {
                    try
                    {
                        NslCanvas c = drawPanel.getCurrentCanvas();
                        c.save();
                    }
                    catch (Exception e)
                    {
                        System.err.println("Error saving the frame " + frameName);
                    }
                }
                else if (evt.getActionCommand().equals("Canvas Print"))
                {
                    NslCanvas c = drawPanel.getCurrentCanvas();
                    PrintJob pjob = getToolkit().getPrintJob(frame, c.canvas_name, null);
                    if (pjob != null)
                    {
                        Graphics pg = pjob.getGraphics();

                        if (pg != null)
                        {
                            c.printAll(pg);
                            pg.dispose(); // flush page
                        }
                        pjob.end();
                    }
                }
                else if (evt.getActionCommand().equals("Add Variable"))
                {
                    variable_input_window = new NslVariableInput(frame);
                    variable_input_window.setVisible(true);
                }
            }
            catch (NoCanvasSelectedException e)
            {  //end try
                System.out.println("NslFrame:Exception: No Canvas Selected");
                System.out.println("NslFrame:Exception: " + e.toString());
            }  // end catch
        }
    }

    public static void nslSetSystem(NslSystem sys)
    {
        system = sys;
        interpreter = sys.getInterpreter();
        NslVariable.system = sys;
        NslPlotInput.system = sys;
        NslVariableInput.system = sys;
    }

    public void collectTrial(double time)
    {
        // For displaying status
        updateStatus(time);

        drawPanel.collectTrial();

        recordTrial();
    }

    protected void recordTrial()
    {
        String filename = recordDir+frameName.substring(1)+"_epoch_"+NslExecutive.system.getCurrentEpoch()+"_cycle_"+NslExecutive.system.getCurrentCycle();
        if(recordFrames && (recordFramesTemporalMode==NslCanvas.TEMPORAL_MODE_TRIAL || recordFramesTemporalMode==NslCanvas.TEMPORAL_MODE_SIMULATION))
            recordFrame(filename);
        if(recordVideo && (recordVideoTemporalMode==NslCanvas.TEMPORAL_MODE_TRIAL || recordVideoTemporalMode==NslCanvas.TEMPORAL_MODE_SIMULATION))
        {
            try
            {
                if(recordVideoOut==null)
                {
                    if(recordVideoTemporalMode== NslCanvas.TEMPORAL_MODE_TRIAL)
                        initVideoOut(recordDir+frameName.substring(1)+"_epoch_"+NslExecutive.system.getCurrentEpoch()+".mov");
                    else
                        initVideoOut(recordDir+frameName.substring(1)+".mov");
                }
                double frameInterval=1.0/recordVideoFramesPerSec;
                int cyclesPerFrameInterval=(int)(frameInterval/NslExecutive.system.getDelta());
                if(recordVideoFramesPerSec>1/NslExecutive.system.getDelta() || NslExecutive.system.getCurrentCycle()%(cyclesPerFrameInterval)==0)
                    recordVideoOut.writeFrame(getImage(),1);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void recordEpoch(int epoch)
    {
        String filename = recordDir+this.frameName.substring(1) + "_epoch_"+epoch;
        if(recordFrames && recordFramesTemporalMode==NslCanvas.TEMPORAL_MODE_EPOCH)
            recordFrame(filename);
        if(recordVideo && (recordVideoTemporalMode==NslCanvas.TEMPORAL_MODE_EPOCH || recordVideoTemporalMode==NslCanvas.TEMPORAL_MODE_SIMULATION))
        {
            try
            {
                if(recordVideoTemporalMode==NslCanvas.TEMPORAL_MODE_EPOCH)
                {
                    if(recordVideoOut==null)
                        initVideoOut(recordDir+this.frameName.substring(1)+".mov");
                    recordVideoOut.writeFrame(getImage(),1);
                }
                if(epoch== NslExecutive.system.getEpochs())
                {
                    recordVideoOut.close();
                    recordVideoOut=null;
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void recordFrame(String filename)
    {
        if(recordFramesFormat==NslCanvas.RECORD_FRAMES_FORMAT_EPS)
            save(filename+".eps","eps");
        else if(recordFramesFormat==NslCanvas.RECORD_FRAMES_FORMAT_PNG)
            save(filename+".png","png");
        else if(recordFramesFormat==NslCanvas.RECORD_FRAMES_FORMAT_JPG)
            save(filename+".jpg","jpg");
    }

    protected void initVideoOut(String movieFilename) throws IOException
    {
        try
        {
            File qtFile = new File(movieFilename);
            BufferedImage img=getImage();
            recordVideoOut = new QuickTimeOutputStream(qtFile, QuickTimeOutputStream.VideoFormat.PNG);
            recordVideoOut.setTimeScale((int)recordVideoFramesPerSec);
            recordVideoOut.setVideoDimension(img.getWidth(), img.getHeight());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void collectEpoch(int epoch)
    {
        updateStatus(system.getCurrentTime());
        drawPanel.collectEpoch();
    }

    public void updateStatus(double time)
    {
        ((JLabel) pStatus.getComponent(1)).setText(df.format(time) + ' ');

        ((JLabel) pStatus.getComponent(3)).setText(String.valueOf(system.getFinishedCycles()) + ' ');
        ((JLabel) pStatus.getComponent(5)).setText(String.valueOf(system.getFinishedEpochs()) + ' ');
    }

    public void refresh()
    {
        double curtime = system.getCurTime();
        drawPanel.init();
        collectTrial(curtime);
    }

    public void refreshEpoch()
    {
        int epoch = system.getCurrentEpoch();
        drawPanel.initEpoch();
        collectEpoch(epoch);
    }

    public void endEpoch(int epoch)
    {
        drawPanel.endEpoch(epoch);
        recordEpoch(epoch);
        if(recordVideo && recordVideoTemporalMode==NslCanvas.TEMPORAL_MODE_TRIAL && recordVideoOut!=null)
        {
            try
            {
                recordVideoOut.close();
                recordVideoOut=null;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public NslCanvas getCurrentCanvas()
    {
        try
        {
            return drawPanel.getCurrentCanvas();
        }
        catch (NoCanvasSelectedException e)
        {
            System.out.println("NslFrame:Exception: " + e.toString());
            return null;
        }
    }

    public Vector<NslCanvas> getCanvasList()
    {
        return drawPanel.getCanvasList();
    }

    public NslCanvas getCanvas(String name)
    {
    	Vector<NslCanvas> cs = getCanvasList();
    	for(int i=0; i<cs.size(); i++)
    	{
    		NslCanvas canvas = cs.get(i);
    		if(canvas.canvas_name.equals(name))
    			return canvas;
    	}
    	
    	return null;
    }
    
    public void addDisplayCanvas(String name, NslVariableInfo varInfo, String plotType)
    {
        try
        {
            drawPanel.addCanvas(name, varInfo, plotType, 0, 1);
            drawPanel.repaint();
        }
        catch (Exception e)
        {
            System.out.println("NslFrame:Exception: unknown plot:" + e.toString());
        }
    }

    public void addVariable(String varName, String plotType)
    {
        try
        {
            NslVariableInfo varInfo = (NslVariableInfo)getVarInfo(varName).clone();
            queryPlotWizardA(varName, varInfo, plotType);
        }
        catch(CloneNotSupportedException e)
        {
        }
    }//end addVariable

    /**
     * Get variable reference number and other properties from  Nsl System
     *
     * @param varName The variable's full name
     * @return A vector which contains variable's reference number and dimensions
     */
    public static NslVariableInfo getVarInfo(String varName)
    {
        NslData var_sel = system.nslGetDataVar(varName);
        return getVarInfo(var_sel);
    }

    public static NslVariableInfo getVarInfo(NslData var_sel)
    {
        NslVariableInfo info = null;

        if (var_sel != null)
        {
            if (var_sel instanceof NslNumeric4)
            {
                if (var_sel instanceof NslFloat4)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.FLOAT, 4);
                }
                else if (var_sel instanceof NslDouble4)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.DOUBLE, 4);
                }
                else if (var_sel instanceof NslInt4)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.INT, 4);
                }
            }
            else if (var_sel instanceof NslNumeric3)
            {
                if (var_sel instanceof NslFloat3)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.FLOAT, 3);
                }
                else if (var_sel instanceof NslDouble3)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.DOUBLE, 3);
                }
                else if (var_sel instanceof NslInt3)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.INT, 3);
                }
            }
            else if (var_sel instanceof NslNumeric2)
            {
                if (var_sel instanceof NslFloat2)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.FLOAT, 2);
                }
                else if (var_sel instanceof NslDouble2)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.DOUBLE, 2);
                }
                else if (var_sel instanceof NslInt2)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.INT, 2);
                }
            }
            else if (var_sel instanceof NslNumeric1)
            {
                if (var_sel instanceof NslFloat1)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.FLOAT, 1);
                }
                else if (var_sel instanceof NslDouble1)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.DOUBLE, 1);
                }
                else if (var_sel instanceof NslInt1)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.INT, 1);
                }
            }
            else if (var_sel instanceof NslNumeric0)
            {
                if (var_sel instanceof NslFloat0)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.FLOAT, 0);
                }
                else if (var_sel instanceof NslDouble0)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.DOUBLE, 0);
                }
                else if (var_sel instanceof NslInt0)
                {
                    info = new NslVariableInfo(var_sel, var_sel.nslGetName(), NslVariableInfo.INT, 0);
                }
            }
        }
        return info;
    }

    /**
     * Add a canvas into the panel given a NslVariable.
     *
     * @param var - variable to be displayed
     * @param minv - minimum value
     * @param maxv - maximum value
     * @param type - plot type
     * @return new created canvas.
     */
    public NslCanvas addCanvas(NslData var, double minv, double maxv, String type)
    {
        return addCanvas(var, var.nslGetName(), minv, maxv, type);
    }

    /**
     * Add a canvas into the panel given a NslVariable and a name.
     *
     * @param var - variable to be displayed
     * @param name - name of the variable
     * @param minv - minimum value
     * @param maxv - maximum value
     * @param type - plot type
     * @return new created canvas.
     */
    public NslCanvas addCanvas(NslData var, String name, double minv, double maxv, String type)
    {
        NslCanvas c = null;
        NslVariableInfo vi = getVarInfo(var);

        try
        {
            c = drawPanel.addCanvas(name, vi, type, minv, maxv);
        }
        catch (Exception e)
        {
            System.err.println("NslFrame: addCanvas Error: Plot could not be created. " + var.nslGetName());
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
        return c;
    }

    public NslCanvas addCanvas(NslVariableInfo vi, String name, double minv, double maxv, String type)
    {
        NslCanvas c=null;
        try
        {
            c = drawPanel.addCanvas(name, vi, type, minv, maxv);
        }
        catch (Exception e)
        {
            System.err.println("NslFrame: addCanvas Error: Plot could not be created. " + vi.nslGetName());
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
        return c;
    }

    public void queryPlotWizardA(String name, NslVariableInfo varInfo, String plotType)
    {
        if (varInfo == null)
        {
            System.err.println("NslFrame:Error: " + name + ": No variable information.");
            return;
        }

        int dimsize = varInfo.getCountDimensions();

        if (plotType.equals("Area") || plotType.equals("Bar") || plotType.equals("Spatial") ||
                plotType.equals("Temporal") || plotType.equals("Grayscale") || plotType.equals("Thermal") ||
                plotType.equals("Histogram") || plotType.equals("Raster"))
        {
            if (dimsize == 0 || dimsize == 1 || dimsize == 2)
            {
                addDisplayCanvas(name, varInfo, plotType);
            }
            else
            {
                NslDimInput dm = new NslDimInput(this, name, varInfo, plotType);
                dm.setVisible(true);
            }
        }
        else if (plotType.equals("Dot") && dimsize == 2)
        {
            // two-dimensional data only
            addDisplayCanvas(name, varInfo, plotType);
        }
        else if (plotType.equals("InputImage"))
        {
            addDisplayCanvas(name, varInfo, plotType);
        }
        else if (plotType.equals("NumericEditor"))
        {
            addVariableInfo(name, varInfo);
        }
        else
        {
            System.err.println("Error: bad plot type :" + plotType);
        }
    } //end queryDataWizardA

    public void addVariableInfo(NslNumeric var)
    {
        try
        {
            NslVariableInfo vi = getVarInfo(var);
            if (vi != null)
            {
                drawPanel.addVariable(var.nslGetName(), vi);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Variable info could not be created. " + var.nslGetName());
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
    }

    public void addVariableInfo(String name)
    {
        try
        {
            NslVariableInfo vi = getVarInfo(name);
            if (vi != null)
            {
                drawPanel.addVariable(name, vi);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Variable info could not be created. " + name);
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
    }

    public void addVariableInfo(String name, NslVariableInfo vi)
    {
        try
        {
            if (vi != null)
            {
                drawPanel.addVariable(name, vi);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Variable info could not be created. " + name);
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
    }

    // called from nslm code
    public void addCanvas(String name, String type)
    {
        addCanvas(name, 0, 100, type);
    }

    public NslCanvas addCanvas(String name, double minv, double maxv, String type)
    {
        NslCanvas c = null;
        NslVariableInfo vi = getVarInfo(name);
        try
        {
            c = drawPanel.addCanvas(name, vi, type, minv, maxv);
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Plot could not be created. " + name);
            System.err.println("Error: " + e.toString());

            e.printStackTrace();
        }
        return c;
    }  //end addPlotsccs

    public void addCanvas(String windowName, NslCanvas c)
    {
        drawPanel.addCanvas(c);
        c.setWindowName(windowName);
    }
    
    public NslCanvas addCanvas(String windowName, String name, double minv, double maxv, String type)
    {
        NslCanvas c = addCanvas(name, minv, maxv, type);
        c.setWindowName(windowName);
        return c;
    }  //end addPlotsccs

    public NslCanvas addCanvas(String windowName, NslNumeric var, double minv, double maxv, String type)
    {
        NslCanvas c = null;
        NslVariableInfo vi = getVarInfo(var);
        try
        {
            c = drawPanel.addCanvas(var.nslGetName(), vi, type, minv, maxv);
            c.setWindowName(windowName);
            return c;
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Plot could not be created. " + var.nslGetName());
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
        return c;
    }

    public NslCanvas addUserCanvas(NslNumeric var, double minv, double maxv, String libPath, String type)
    {
        String name="Independent";
        NslVariableInfo vi = null;
        if (var != null)
        {
            vi = getVarInfo(var);
            name = var.nslGetName();
        }
        try
        {
            return drawPanel.addUserCanvas(name, vi, libPath, type, minv, maxv);
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Plot could not be created. " + name);
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public NslCanvas addUserCanvas(String name, NslNumeric var, double minv, double maxv, String libPath, String type)
    {
        NslVariableInfo vi = null;
        if (var != null)
        {
            vi = getVarInfo(var);
            name = var.nslGetName();
        }
        try
        {
            return drawPanel.addUserCanvas(name, vi, libPath, type, minv, maxv);
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Plot could not be created. " + name);
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public NslCanvas addUserCanvas(String windowName, String name, NslNumeric var, double minv, double maxv, String libPath, String type)
    {
        NslCanvas c = null;
        NslVariableInfo vi = getVarInfo(var);
        try
        {
            c = drawPanel.addUserCanvas(name, vi, libPath, type, minv, maxv);
            c.setWindowName(windowName);
            return c;
        }
        catch (Exception e)
        {
            System.err.println("Error: NslFrame: Plot could not be created. " + var.nslGetName());
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
        return c;
    }

    public void addComponent(Component c)
    {
        drawPanel.add(c);
        int plotCount = drawPanel.getComponentCount();
        if (plotCount > 0)
        {
            int colCount = (plotCount > 2) ? 3 : plotCount;
            drawPanel.setColumns(colCount);
        }
        else
        {
            drawPanel.setColumns(1);
        }
        drawPanel.validate();
    }

    public void setColumns(int columns)
    {
        drawPanel.setColumns(columns);
    }

    public void setRows(int rows)
    {
        drawPanel.setRows(rows);
    }

    public void setFontName(String name)
    {
        fontName = name;
    }

    public String getFontName()
    {
        return fontName;
    }

    public void setBackgroundColor(String name)
    {
        bg = name;
    }

    public String getBackgroundColor()
    {
        return bg;
    }

    public void setForegroundColor(String name)
    {
        fg = name;
    }

    public String getForegroundColor()
    {
        return fg;
    }

    public NslPanel getPanel()
    {
        return drawPanel;
    }

    public static NslSystem getNslSystem()
    {
        return system;
    }

    public boolean isRecordFrames()
    {
        return recordFrames;
    }

    public void setRecordFrames(boolean recordFrames)
    {
        this.recordFrames = recordFrames;
    }

    public boolean isRecordVideo()
    {
        return recordVideo;
    }

    public void setRecordVideo(boolean recordVideo)
    {
        this.recordVideo = recordVideo;
    }

    public int getRecordFramesTemporalMode()
    {
        return recordFramesTemporalMode;
    }

    public void setRecordFramesTemporalMode(int recordFramesTemporalMode)
    {
        this.recordFramesTemporalMode = recordFramesTemporalMode;
    }

    public int getRecordVideoTemporalMode()
    {
        return recordVideoTemporalMode;
    }

    public void setRecordVideoTemporalMode(int recordVideoTemporalMode)
    {
        this.recordVideoTemporalMode = recordVideoTemporalMode;
    }

    public int getRecordFramesFormat()
    {
        return recordFramesFormat;
    }

    public void setRecordFramesFormat(int recordFramesFormat)
    {
        this.recordFramesFormat = recordFramesFormat;
    }

    public double getRecordVideoFramesPerSec()
    {
        return recordVideoFramesPerSec;
    }

    public void setRecordVideoFramesPerSec(double recordVideoFramesPerSec)
    {
        this.recordVideoFramesPerSec = recordVideoFramesPerSec;
    }

    public String getRecordDir()
    {
        return recordDir;
    }

    public void setRecordDir(String recordDir)
    {
        this.recordDir = recordDir;
        if(!this.recordDir.endsWith(File.separator))
            this.recordDir=this.recordDir+File.separator;
    }
}

class GifEpsFileFilter extends FileFilter
{
    public String getDescription()
    {
        return "Accepts .gif and .eps files";
    }

    public boolean accept(File f)
    {
        return (f.isDirectory() || f.getName().toLowerCase().endsWith(".gif") || f.getName().toLowerCase().endsWith(".eps"));
    }
}

class GifFileFilter extends FileFilter
{
    public String getDescription()
    {
        return "Accepts .gif files";
    }

    public boolean accept(File f)
    {
        return (f.isDirectory() || f.getName().toLowerCase().endsWith(".gif"));
    }
}

class GifJpegFileFilter extends FileFilter
{
    public String getDescription()
    {
        return "Accepts .gif, .jpg and .jpeg files";
    }

    public boolean accept(File f)
    {
        return (f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") ||
                f.getName().toLowerCase().endsWith(".jpeg") || f.getName().toLowerCase().endsWith(".gif"));
    }
}
