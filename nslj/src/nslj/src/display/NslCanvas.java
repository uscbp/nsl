/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslCanvas.java,v $
// Revision 1.5  1997/11/06 03:15:01  erhan
// nsl3.0.b
//
// Revision 1.4  1997/05/09 22:30:24  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import nslj.src.lang.NslData;
import nslj.src.lang.NslHierarchy;
import nslj.src.main.NslMain;
import nslj.src.display.video.QuickTimeOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Stack;
import java.util.Vector;
import java.text.DecimalFormat;

import org.jibble.epsgraphics.EpsGraphics2D;

/**
 * The Nsl Canvas
 * <p/>
 * Implement the general functions of nslj  canvases
 *
 * @author Danjie Pan
 * @version 1.0
 */


public abstract class NslCanvas extends JPanel
{
    protected static DecimalFormat df=new DecimalFormat("0.00");
    public static int TEMPORAL_MODE_TRIAL=0;
    public static int TEMPORAL_MODE_EPOCH=1;
    public static int TEMPORAL_MODE_SIMULATION=2;

    // Variables
    public boolean antiAlias=false;
    public String canvas_name;
    public String windowName;

    public NslFrame nslDisplayFrame = null;
    public MouseAdapter mouseAdapter;

    protected Vector<NslVariable> variables;

    protected int drawX, drawY, drawW, drawH, dx, dy, adjustY=0; 
    protected int x_dimension, y_dimension;

    protected String canvas_type;

    // time min and time max
    public double tmin = 0.0;
    public double tmax = 25;
    public int minDataPos =0;
    public int maxDataPos =25;

    protected double min = -100, max = 100;

    protected String name;
    private int mouseX0, mouseY0, mouseXcurrent, mouseYcurrent;
    private int mouseX1, mouseY1;
    private Stack<double[]> zoomStack;

    protected boolean showLegend =true;

    public static int RECORD_DATA_FORMAT_MAT=0;
    public static int RECORD_DATA_FORMAT_GNU=1;
    public static int RECORD_FRAMES_FORMAT_PNG=0;
    public static int RECORD_FRAMES_FORMAT_JPG=1;
    public static int RECORD_FRAMES_FORMAT_EPS=1;

    protected boolean recordData=false;
    protected boolean recordFrames=false;
    protected boolean recordVideo=false;
    protected int recordDataTemporalMode=TEMPORAL_MODE_TRIAL;
    protected int recordFramesTemporalMode=TEMPORAL_MODE_TRIAL;
    protected int recordVideoTemporalMode=TEMPORAL_MODE_TRIAL;
    protected int recordDataFormat=RECORD_DATA_FORMAT_MAT;
    protected int recordFramesFormat=RECORD_FRAMES_FORMAT_PNG;
    protected double recordVideoFramesPerSec=33.0;
    protected String recordDir=System.getProperty("user.dir")+"/data/";
    // Video output stream
    protected QuickTimeOutputStream recordVideoOut = null;

    protected boolean recordCanvas = false;
    protected String fileFormat = "Matlab";

    protected boolean drawRect = true;

    protected boolean doubleBuffering=true;
    protected int paintMode=-1;

    protected int canvasWidth;
    protected int canvasHeight;
    protected int canvasX;
    protected int canvasY;

    protected boolean reset=true;
    protected int temporalMode=TEMPORAL_MODE_TRIAL;
    //Whether or not to automatically scale
    protected boolean autoScale=false;


    // Constructors

    public NslCanvas()
    {
        super(true);
        variables=new Vector<NslVariable>();
    }

    public NslCanvas(NslFrame nslDisplayFrame)
    {
        super(true);
        this.nslDisplayFrame = nslDisplayFrame;
        variables=new Vector<NslVariable>();
    }

    public NslCanvas(NslFrame nslDisplayFrame, double min, double max)
    {
        super(true);
        this.nslDisplayFrame = nslDisplayFrame;
        variables=new Vector<NslVariable>();
        setMinMax(min, max);
    }

    public NslCanvas(NslFrame nslDisplayFrame, int temporalMode, double min, double max)
    {
        super(true);
        this.temporalMode=temporalMode;
        this.nslDisplayFrame = nslDisplayFrame;
        variables=new Vector<NslVariable>();
        setMinMax(min, max);
    }

    public NslCanvas(NslFrame frame, String fullName, NslVariableInfo varInfo)
    {
        super(true);
        canvas_type = "Normal";
        canvas_name = fullName;
        if(fullName!=null)
            name = canvas_name.substring(canvas_name.indexOf('.', 1) + 1);
        else
            name = "";

        nslDisplayFrame = frame;

        mouseX0 = 0;
        mouseY0 = 0;
        mouseX1 = 0;
        mouseY1 = 0;

        init(varInfo);
    } // end constructor

    public NslCanvas(NslFrame frame, String fullName, NslVariableInfo varInfo, int temporalMode)
    {
        this(frame, fullName, varInfo);
        this.temporalMode=temporalMode;
    }

    public NslCanvas(NslFrame frame, String fullName, NslVariableInfo varInfo, double min, double max)
    {
        this(frame, fullName, varInfo);
        setMinMax(min, max);
    }

    public NslCanvas(NslFrame frame, String fullName, NslVariableInfo varInfo, int temporalMode, double min, double max)
    {
        this(frame, fullName, varInfo, min, max);
        this.temporalMode=temporalMode;
    }

    /**
     * Executed when the frame a canvas belongs to is closed. Should be overidden by subclasses
     */
    public void onClose()
    {

    }

    public void addVariable(NslVariableInfo varInfo)
    {
        variables.add(new NslVariable(this, varInfo));
    }

    public void addVariable(NslData var)
    {
        NslVariableInfo varInfo=NslFrame.getVarInfo(var);
        addVariable(varInfo);
    }

    public void addVariable(NslVariableInfo varInfo, Color col)
    {
        varInfo.setColor(col);
        variables.add(new NslVariable(this, varInfo));
    }

    public void addVariable(NslData var, Color col)
    {
        NslVariableInfo varInfo=NslFrame.getVarInfo(var);
        addVariable(varInfo, col);
    }

    public void addVariable(NslVariableInfo varInfo, Color col, int style)
    {
        varInfo.setColor(col);
        varInfo.setStyle(style);
        variables.add(new NslVariable(this, varInfo));
    }
    
    public void addVariable(NslData var, Color col, int style)
    {
        NslVariableInfo varInfo=NslFrame.getVarInfo(var);
        addVariable(varInfo, col, style);
    }

    // Methods

    public String nslGetName()
    {
        return name;
    }

    // Event handeler

    public void mousePressed(MouseEvent evt)
    {
        if ("Zoom".equals(canvas_type))
        {
            Graphics g = getGraphics();

            drawRect=false;

            update(g);

            mouseX0 = evt.getX();
            mouseY0 = evt.getY();

            mouseXcurrent = evt.getX();
            mouseYcurrent = evt.getY();
        }
    }

    public void mouseDragged(MouseEvent evt)
    {

        if ("Zoom".equals(canvas_type))
        {
            drawRect=true;
            Graphics g = getGraphics();
            g.setXORMode(getBackground());

            // for flexible area selection - nikunj
            int xLeft = (mouseX0 < mouseXcurrent) ? mouseX0 : mouseXcurrent;
            int xRight = (mouseX0 > mouseXcurrent) ? mouseX0 : mouseXcurrent;
            int yTop = (mouseY0 < mouseYcurrent) ? mouseY0 : mouseYcurrent;
            int yBottom = (mouseY0 > mouseYcurrent) ? mouseY0 : mouseYcurrent;
            g.drawRect(xLeft, yTop, (xRight - xLeft), (yBottom - yTop));

            mouseXcurrent = evt.getX();
            mouseYcurrent = evt.getY();

            // for flexible area selection - nikunj
            g.setColor(Color.black);
            xLeft = (mouseX0 < mouseXcurrent) ? mouseX0 : mouseXcurrent;
            xRight = (mouseX0 > mouseXcurrent) ? mouseX0 : mouseXcurrent;
            yTop = (mouseY0 < mouseYcurrent) ? mouseY0 : mouseYcurrent;
            yBottom = (mouseY0 > mouseYcurrent) ? mouseY0 : mouseYcurrent;
            g.drawRect(xLeft, yTop, (xRight - xLeft), (yBottom - yTop));

            g.dispose();

        }
    }

    public void mouseReleased(MouseEvent evt)
    {
        if ("Zoom".equals(canvas_type))
        {
            mouseX1 = evt.getX();
            mouseY1 = evt.getY();

            // for flexible area selection - nikunj

            int xLeft = (mouseX0 < mouseX1) ? mouseX0 : mouseX1;
            int xRight = (mouseX0 > mouseX1) ? mouseX0 : mouseX1;
            int yTop = (mouseY0 < mouseY1) ? mouseY0 : mouseY1;
            int yBottom = (mouseY0 > mouseY1) ? mouseY0 : mouseY1;

            mouseX0 = xLeft;
            mouseX1 = xRight;
            mouseY0 = yTop;
            mouseY1 = yBottom;
        }
    }

    public void setMinMax(double dmin, double dmax)
    {
        min = dmin;
        max = dmax;
    }

    public void setVarColor(Color c)
    {
        if(variables!=null && !variables.isEmpty())
            variables.get(0).info.setColor(c);
    }

    public Color getVarColor()
    {
        if(variables!=null && !variables.isEmpty())
            return variables.get(0).info.getColor();
        return null;
    }

    public void initCanvas()
    {
        // must be implemented in each canvas type
        if(temporalMode==TEMPORAL_MODE_TRIAL)
            reset=true;
    }

    public void initEpochCanvas()
    {
        // must be implemented in each canvas type
        if(temporalMode==TEMPORAL_MODE_EPOCH)
            reset=true;
    }

    public void endEpochCanvas(int epoch)
    {
        recordEpoch(epoch);
        if(recordVideo && recordVideoOut!=null && (recordVideoTemporalMode==TEMPORAL_MODE_TRIAL ||
                (recordVideoTemporalMode==TEMPORAL_MODE_SIMULATION && epoch==NslExecutive.system.getEpochs())))
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

    protected void recordTrial()
    {
        String filename = recordDir+nslGetName()+"_epoch_"+NslExecutive.system.getCurrentEpoch()+"_cycle_"+NslExecutive.system.getCurrentCycle();
        if(recordData && (recordDataTemporalMode==TEMPORAL_MODE_TRIAL || recordDataTemporalMode==TEMPORAL_MODE_SIMULATION))
            recordData(filename);
        if(recordFrames && (recordFramesTemporalMode==TEMPORAL_MODE_TRIAL || recordFramesTemporalMode==TEMPORAL_MODE_SIMULATION))
            recordFrame(filename);
        if(recordVideo && (recordVideoTemporalMode==TEMPORAL_MODE_TRIAL || recordVideoTemporalMode==TEMPORAL_MODE_SIMULATION))
        {
            try
            {
                if(recordVideoOut==null)
                {
                    if(recordVideoTemporalMode==TEMPORAL_MODE_TRIAL)
                        initVideoOut(recordDir+nslGetName()+"_epoch_"+NslExecutive.system.getCurrentEpoch()+".mov");
                    else
                        initVideoOut(recordDir+nslGetName()+".mov");
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

    protected void recordData(String filename)
    {
        if(recordDataFormat==RECORD_DATA_FORMAT_MAT)
            NslOutFile.outToMatlab(this, filename+".mat");
        else if (recordDataFormat==RECORD_DATA_FORMAT_GNU)
            NslOutFile.outToGnuplot(this, filename+".gnu");
    }

    protected void recordEpoch(int epoch)
    {
        String filename = recordDir+nslGetName() + "_epoch_"+epoch;
        if(recordData && recordDataTemporalMode==TEMPORAL_MODE_EPOCH)
            recordData(filename);
        if(recordFrames && recordFramesTemporalMode==TEMPORAL_MODE_EPOCH)
            recordFrame(filename);
        if(recordVideo && recordVideoTemporalMode==TEMPORAL_MODE_EPOCH)
        {
            try
            {
                if(recordVideoOut==null)
                    initVideoOut(recordDir+nslGetName()+".mov");
                recordVideoOut.writeFrame(getImage(),1);
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
        if(recordFramesFormat==RECORD_FRAMES_FORMAT_EPS)
            save(filename+".eps","eps");
        else if(recordFramesFormat==RECORD_FRAMES_FORMAT_PNG)
            save(filename+".png","png");
        else if(recordFramesFormat==RECORD_FRAMES_FORMAT_JPG)
            save(filename+".jpg","jpg");
    }

    protected void initVideoOut(String movieFilename) throws IOException
    {
        try
        {
            File qtFile = new File(movieFilename);
            QuickTimeOutputStream.VideoFormat f= QuickTimeOutputStream.VideoFormat.PNG;
            recordVideoOut = new QuickTimeOutputStream(qtFile, f);
            recordVideoOut.setTimeScale((int)recordVideoFramesPerSec);
            recordVideoOut.setVideoDimension(getVideoFrameWidth(), getVideoFrameHeight());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    protected int getVideoFrameWidth()
    {
        return getWidth();
    }

    protected int getVideoFrameHeight()
    {
        return getHeight();
    }

    public void init(NslVariableInfo varInfo)
    {
        variables=new Vector<NslVariable>();
        if(varInfo!=null)
        {
            varInfo.setHistory(false);            
            variables.add(new NslVariable(this, varInfo));
        }
        if (NslHierarchy.system!=null)
        {
            if(temporalMode==TEMPORAL_MODE_TRIAL)
            {
                tmax = NslHierarchy.system.getEndTime();
                minDataPos = (int)(tmin / NslHierarchy.system.getDelta());
                maxDataPos = (int)(tmax / NslHierarchy.system.getDelta());
            }
            else if(temporalMode==TEMPORAL_MODE_EPOCH)
            {
                tmax = Math.max(NslHierarchy.system.getNumRunEpochs(),NslHierarchy.system.getNumTrainEpochs());
                minDataPos = (int)(tmin);
                maxDataPos = (int)(tmax);
            }
        }
        for(int i=0; i<variables.size(); i++)
            variables.get(i).init(maxDataPos);
        updateDimensions();
    }

    public void update()
    {
        update(getGraphics());
    }
    
    public void update(Graphics g)
    {
        paintComponent(g);
    }

    public synchronized void paintComponent(Graphics g)
    {
        reset=true;
        boolean buffering=doubleBuffering;
        if(!nslDisplayFrame.printing)
            doubleBuffering=true;
        paintCanvas((Graphics2D)g);
        doubleBuffering=buffering;
    }

    /**
     * Should be implemented by each canvas
     */
    protected synchronized void paintCanvas(Graphics2D g)
    {
        BufferedImage bufferImage=null;
        Graphics2D bufferGraphics;
        if(reset && doubleBuffering && getWidth()>0 && getHeight()>0)
        {
            bufferImage=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(),getHeight());
            bufferGraphics=bufferImage.createGraphics();
        }
        else
            bufferGraphics=g;

        updateDimensions();

        if (canvasWidth<=0 || canvasHeight<=0)
            return;
        if(canvas_type.equals("Zoom"))
            reset=true;

        if(reset)
        {
            Color color = getBackground();
            bufferGraphics.setColor(color);
            bufferGraphics.fill(new Rectangle2D.Double(0,0,canvasWidth, canvasHeight));

            // draw title here
            bufferGraphics.setColor(Color.black);

            if ("Normal".equals(canvas_type))
            {
                bufferGraphics.drawString(name, 10, 20);
            }

        	adjustY = 0;
            if(variables.size()>1 && showLegend)
            {
                int labelX = 20, labelY = 20, stepY = 15, lineWidth=20, maxStrWidth=0;
                Color store = bufferGraphics.getColor();
                for(int i=0; i<variables.size(); i++)
                {
                    NslVariable var = variables.get(i);
                    if(var.info.name!=null && var.info.style!=NslVariableInfo.LINE_STYLE_INVISIBLE)
                    {
                        labelY += stepY;
                        adjustY += stepY;
                        bufferGraphics.setColor(var.info.color);
                        bufferGraphics.drawString(var.info.name, labelX, labelY);
                        int strWidth=bufferGraphics.getFontMetrics().stringWidth(var.info.name);
                        if(strWidth>=maxStrWidth)
                            maxStrWidth=strWidth;
                    }
                }
                int lineX=labelX+maxStrWidth+5;
                labelY = 20;
                int lineY=labelY-(int)(.3*bufferGraphics.getFontMetrics().getHeight());
                for(int i=0; i<variables.size(); i++)
                {
                    NslVariable var = variables.get(i);
                    if(var.info.name!=null && var.info.style!=NslVariableInfo.LINE_STYLE_INVISIBLE)
                    {
                        labelY += stepY;
                        lineY += stepY;
                        bufferGraphics.setColor(var.info.color);
                        bufferGraphics.drawLine(lineX, lineY, lineX+lineWidth, lineY);
                    }
                }
                bufferGraphics.setColor(store);
            }
            
            if ("Normal".equals(canvas_type))
            {
                setDrawSize();
            }
            else if ("Zoom".equals(canvas_type))
            {
                double[] lastVCDimen = zoomStack.peek();

                double x0 = lastVCDimen[0];
                double y0 = lastVCDimen[1];
                double dw = lastVCDimen[2];
                double dh = lastVCDimen[3];
                double mx0 = lastVCDimen[4];
                double my0 = lastVCDimen[5];
                double mdw = lastVCDimen[6];
                double mdh = lastVCDimen[7];

                mouseX0 = (int) (mx0 * (double) canvasWidth);
                mouseY0 = (int) (my0 * (double) canvasHeight);
                mouseX1 = (int) ((mx0 + mdw) * (double) canvasWidth);
                mouseY1 = (int) ((my0 + mdh) * (double) canvasHeight);

                bufferGraphics.setColor(getBackground());
                bufferGraphics.fill(new Rectangle2D.Double(canvasX, canvasY, canvasWidth, canvasHeight));

                if(drawRect)
                {
                    bufferGraphics.setColor(Color.black);

                    bufferGraphics.draw(new Rectangle2D.Double(mouseX0, mouseY0, (mouseX1 - mouseX0), (mouseY1 - mouseY0)));
                }

                zoomDrawSize(x0, y0, dw, dh);
            }

            if (paintMode == 99)
            {
                drawRectangle(bufferGraphics);
            }
            else if (paintMode > 0)
            {
                drawBox(bufferGraphics);
            }
        }

        paintData(bufferGraphics);
        
        if(reset)
        {
            if(doubleBuffering)
                g.drawImage(bufferImage,null,0,0);
            reset=false;
        }
    }

    protected abstract void paintData(Graphics2D bufferGraphics);

    protected void updateDimensions()
    {
        canvasHeight=getHeight();
        canvasWidth=getWidth();
        canvasX=getX();
        canvasY=getY();
        if(getVariable()!=null)
        {
            x_dimension = getVariable().info.getDimension(0);
            y_dimension = getVariable().info.getDimension(1);
        }
    }

    public void drawRectangle(Graphics2D bufferGraphics)
    {
        // draw box
        bufferGraphics.setColor(Color.black);
        bufferGraphics.draw(new Rectangle2D.Double(drawX, drawY, dx * variables.get(0).info.getDimension(1),
                dy * variables.get(0).info.getDimension(0)));
    }

    public void drawBox(Graphics2D bufferGraphics)
    {
        // draw box
        bufferGraphics.setColor(Color.black);
        bufferGraphics.draw(new Rectangle2D.Double(drawX, drawY,
									                dx * variables.get(0).info.getDimension(1),
									                dy * variables.get(0).info.getDimension(0)));
        
        for (int i = 0; i < variables.get(0).info.getDimension(0) - 1; i++)
        {
            bufferGraphics.draw(new Line2D.Double(drawX, drawY + dy * (i + 1),
			                    drawX + dx * variables.get(0).info.getDimension(1),
			                    drawY + dy * (i + 1)));
        }

        for (int i = 0; i < variables.get(0).info.getDimension(1) - 1; i++)
        {
            bufferGraphics.draw(new Line2D.Double(drawX + dx * (i + 1), drawY,
			                    drawX + dx * (i + 1),
			                    drawY + dy * variables.get(0).info.getDimension(0)));
        }
    }

    public NslVariable getVariable()
    {
        if(variables!=null && variables.size()>0)
            return variables.get(0);
        else
            return null;
    }

    public void Print()
    {
        PrintJob pjob = getToolkit().getPrintJob(nslDisplayFrame, "Current Canvas", null);
        if (pjob != null)
        {
            Graphics pg = pjob.getGraphics();
            if (pg != null)
            {
                printAll(pg);
                pg.dispose(); // flush page
            }
            pjob.end();
        }
    }

    /**
     * Function to make a copy of canvas which is needed
     * when switch one type of canvas to another
     * @param graph_type_name - name of the type of graph to copy to
     * @return - new canvas
     */
    public NslCanvas copy(String graph_type_name)
    {
        NslCanvas ndc;
        boolean copyHistory=variables.get(0).info.isHistoryOn();
        try
        {
            String frameType = "nslj.src.display.NslFrame";
            String canvasType = "nslj.src.display.Nsl" + graph_type_name + "Canvas";

            Class frameClass = Class.forName(frameType);
            Class canvasClass = Class.forName(canvasType);

            Class typeList[] = new Class[2];
            typeList[0] = frameClass;
            typeList[1] = canvasClass.getSuperclass();
            while(!typeList[1].equals(NslCanvas.class) && !typeList[1].equals(Object.class))
                typeList[1]=typeList[1].getSuperclass();

            java.lang.reflect.Constructor copyConstructor = canvasClass.getConstructor(typeList);
            if (nslDisplayFrame == null)
            {
                System.err.println("NslCanvas: [Error] NslFrame is null");
            }

            Object[] param = new Object[2];
            param[0] = nslDisplayFrame;
            param[1] = this;
            ndc = (NslCanvas) copyConstructor.newInstance(param);
        }
        catch (Exception e)
        {
            System.err.println("NslCanvas: [Error] don't know this type " + graph_type_name);
            return null;
        }
        for(int i=1; i<variables.size(); i++)
            ndc.addVariable(variables.get(i).info);
        ndc.canvas_name = canvas_name;
        ndc.canvas_type = canvas_type;
        ndc.name = this.name;
        ndc.x_dimension = x_dimension;
        ndc.y_dimension = y_dimension;
        ndc.drawX = drawX;
        ndc.drawY = drawY;
        ndc.drawW = drawW;
        ndc.drawH = drawH;
        ndc.dx = dx;
        ndc.dy = dy;
        ndc.tmin = tmin;
        ndc.tmax = tmax;
        ndc.minDataPos = minDataPos;
        ndc.maxDataPos = maxDataPos;
        ndc.showLegend=showLegend;
        ndc.setBounds(getX(), getY(), getWidth(), getHeight());
        ndc.setBackground(Color.white);
        for(int i=0; i<variables.size(); i++)
        {
            ndc.variables.get(i).data=variables.get(i).data;
            ndc.variables.get(i).last_data_pos=variables.get(i).last_data_pos;
            ndc.variables.get(i).absolute_last_data_pos=variables.get(i).absolute_last_data_pos;
            ndc.variables.get(i).totalSize=variables.get(i).totalSize;
        }

        return ndc;
    }

    public void setBackground(String b)
    {
    }

    public void setDrawSize()
    {
        drawY = "Normal".equals(canvas_type) ? 30 : 10;
        drawY += adjustY;
        drawX = 40;
        drawW = canvasWidth - 60;
        drawH = canvasHeight - 50 - adjustY;
        if(variables!=null && variables.size()>0)
        {
            dx = drawW / variables.get(0).info.getDimension(1);
            dy = drawH / variables.get(0).info.getDimension(0);
        }
        else
        {
            dx=1;
            dy=1;
        }
    }

    /**
     * implement zoom in function
     */
    public void zoomIn()
    {
        updateDimensions();

        // Normalize mouse position using current zoom canvas size
        double mx0 = (double) mouseX0 / canvasWidth;
        double my0 = (double) mouseY0 / canvasHeight;
        double mx1 = (double) mouseX1 / canvasWidth;
        double my1 = (double) mouseY1 / canvasHeight;
        double mdw = mx1 - mx0;
        double mdh = my1 - my0;

        // check for invalid zoom area - zero width or count
        if (mdw == 0 || mdh == 0)
        {
            System.err.println("NslCanvas: [Error] Invalid zoom area");
            return;
        }
        double ratio;

        double ratioX = 0.9 / (mx1 - mx0);
        double ratioY = 0.9 / (my1 - my0);

        if (ratioX <= ratioY)
        {
            ratio = ratioX;
        }
        else
        {
            ratio = ratioY;
        }
        double centerX = (mx1 + mx0) / 2.0;
        double centerY = (my1 + my0) / 2.0;

        // Calculate  new virtual canvas position and size using new ratio

        double[] preVCDimen = zoomStack.pop();

        double x0_pre = preVCDimen[0];
        double y0_pre = preVCDimen[1];
        double dw_pre = preVCDimen[2];
        double dh_pre = preVCDimen[3];

        double x0_curr = (x0_pre - centerX) * ratio + 0.5;
        double y0_curr = (y0_pre - centerY) * ratio + 0.5;
        double dw_curr = dw_pre * ratio;
        double dh_curr = dh_pre * ratio;

        // Calculate zoom box position and size
        double mx0_curr = (mx0 - centerX) * ratio + 0.5;
        double my0_curr = (my0 - centerY) * ratio + 0.5;
        double mdw_curr = mdw * ratio;
        double mdh_curr = mdh * ratio;

        // Save current virtual canvas parameters and zoom box parameters in zoomStack
        double[] currVCDimen = new double[8];
        currVCDimen[0] = x0_curr;
        currVCDimen[1] = y0_curr;
        currVCDimen[2] = dw_curr;
        currVCDimen[3] = dh_curr;
        currVCDimen[4] = mx0_curr;
        currVCDimen[5] = my0_curr;
        currVCDimen[6] = mdw_curr;
        currVCDimen[7] = mdh_curr;

        preVCDimen[4] = mx0;
        preVCDimen[5] = my0;
        preVCDimen[6] = mdw;
        preVCDimen[7] = mdh;

        zoomStack.push(preVCDimen);

        zoomStack.push(currVCDimen);

        zoomDrawSize(x0_curr, y0_curr, dw_curr, dh_curr);

        // redraw canvas and zoom box
        update();
    }


    /**
     * Implement zoom out function
     */
    public void zoomOut()
    {
        if (zoomStack.size() > 1)
        {

            double[] lastVCDimen = zoomStack.pop(); //(double[])zoomStack.peek();

            double x0 = lastVCDimen[0];
            double y0 = lastVCDimen[1];
            double dw = lastVCDimen[2];
            double dh = lastVCDimen[3];

            zoomDrawSize(x0, y0, dw, dh);
            update();
        }
    }

    /**
     * To calculate the parameters for paint() accoding to virtual canvas parameters
     * and actual zoom canvas size.
     * @param x0 - virtual canvas x
     * @param y0 - virtual canvas y
     * @param dw - virtual canvas width
     * @param dh - virtual canvas height
     */
    public void zoomDrawSize(double x0, double y0, double dw, double dh)
    {
        updateDimensions();

        drawX = (int) ((x0 + dw / 20.0) * (double) canvasWidth);
        drawY = (int) ((y0 + dh / 10.0) * (double) canvasHeight);
        drawW = (int) ((dw - 2.0 * dw / 20.0) * (double) canvasWidth);
        drawH = (int) ((dh - 3.0 * dh / 20.0) * (double) canvasHeight);

        dx = (int) ((dw - 2.0 * dw / 20.0) *
                (double) canvasWidth / (double) variables.get(0).info.getDimension(1));
        dy = (int) ((dh - 3.0 * dh / 20.0) *
                (double) canvasHeight / (double) variables.get(0).info.getDimension(0));
    }

    public void zoomDrawSize()
    {
        double[] lastVCDimen = zoomStack.peek();

        double x0 = lastVCDimen[0];
        double y0 = lastVCDimen[1];
        double dw = lastVCDimen[2];
        double dh = lastVCDimen[3];

        zoomDrawSize(x0, y0, dw, dh);
    }

    // Change the canvas type and create the zoomStack accordingly

    public void setCanvasType(String ct)
    {
        canvas_type = ct;

        if ("Zoom".equals(canvas_type))
        {
            zoomStack = new Stack<double[]>();
            double[] initArray = new double[8];

            initArray[0] = 0.0;   // virtual canvas left-up corner x0(0)
            initArray[1] = 0.0;   // virtual canvas left-up corner y0(0)
            initArray[2] = 1.0;   // virtual canvas width (normalized)
            initArray[3] = 1.0;   // virtual canvas height (normalized)
            initArray[4] = 0.0;   // zoom box mx0(0)
            initArray[5] = 0.0;   // zoom box my0(0)
            initArray[6] = 1.0;   // zoom box width (normalized)
            initArray[7] = 1.0;   // zoom box height (normalized)

            zoomStack.push(initArray);
        }
    }

    public void collectTrial()
    {
        if(temporalMode==TEMPORAL_MODE_TRIAL)
        {
            if(variables!=null)
            {
                for(int i=0; i<variables.size(); i++)
                {
                    variables.get(i).collectTrial();
                    if(autoScale)
                    {
                        min=Double.POSITIVE_INFINITY;
                        max=Double.NEGATIVE_INFINITY;
                        for(int j=0; j<x_dimension; j++)
                        {
                            for(int k=0; k<y_dimension; k++)
                            {
                                if(variables.get(i).data[j][k][variables.get(i).last_data_pos]<min)
                                    min=variables.get(i).data[j][k][variables.get(i).last_data_pos];
                                if(variables.get(i).data[j][k][variables.get(i).last_data_pos]>max)
                                    max=variables.get(i).data[j][k][variables.get(i).last_data_pos];
                            }
                        }
                    }
                }
            }
            paintCanvas((Graphics2D)getGraphics());

            recordTrial();
        }
    }

    public void collectEpoch()
    {
        if(this.temporalMode==TEMPORAL_MODE_EPOCH)
        {
            if(variables!=null)
            {
                for(int i=0; i<variables.size(); i++)
                {
                    variables.get(i).collectEpoch();
                    if(autoScale)
                    {
                        min=Double.POSITIVE_INFINITY;
                        max=Double.NEGATIVE_INFINITY;
                        for(int j=0; j<x_dimension; j++)
                        {
                            for(int k=0; k<y_dimension; k++)
                            {
                                if(variables.get(i).data[j][k][variables.get(i).last_data_pos]<min)
                                    min=variables.get(i).data[j][k][variables.get(i).last_data_pos];
                                if(variables.get(i).data[j][k][variables.get(i).last_data_pos]>max)
                                    max=variables.get(i).data[j][k][variables.get(i).last_data_pos];
                            }
                        }
                    }
                }
            }
            paintCanvas((Graphics2D)getGraphics());
        }
    }

    public void setSize(Dimension d)
    {
        super.setSize(d);
    }

    public void setWindowName(String n)
    {
        windowName = n;
    }

    public String getWindowName()
    {
        return windowName;
    }

    public double getMax()
    {
        return max;
    }

    public double getMin()
    {
        return min;
    }

    public boolean getRecordCanvas()
    {
        return recordCanvas;
    }

    public void setRecordCanvas(boolean recordCanvas)
    {
        this.recordCanvas = recordCanvas;
    }

    public String getFileFormat()
    {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat)
    {
        this.fileFormat = fileFormat;
    }

    public int getTemporalMode()
    {
        return temporalMode;
    }

    public void setTemporalMode(int mode)
    {
        this.temporalMode=mode;
    }

    public boolean isAutoScale()
    {
        return autoScale;
    }

    public void setAutoScale(boolean autoScale)
    {
        this.autoScale = autoScale;
    }

    public void save()
    {
        boolean buffering=NslMain.doubleBuffering;
        NslMain.doubleBuffering=false;
        boolean canvasBuffering=doubleBuffering;
        doubleBuffering=false;
        BufferedImage img=getImage();
        ImageSaveDialog sd=new ImageSaveDialog(nslDisplayFrame);
        sd.setDialogTitle("Select Canvas Capture File");
        sd.save(img, true);
        NslMain.doubleBuffering=buffering;
        doubleBuffering=canvasBuffering;
        if(sd.getSelectedFile()!=null && sd.getSelectedFile().getName().toLowerCase().endsWith("eps"))
        {
            writeEps(sd.getSelectedFile().getAbsolutePath());
        }        
    }

    public void save(String filename, String format)
    {
        // Set doublebuffering to false and save current setting
        boolean buffering=NslMain.doubleBuffering;
        NslMain.doubleBuffering=false;
        // Turn off doublebuffering on each canvas, but save their setting to turn it back on when done
        boolean canvasBuffering=doubleBuffering;
        doubleBuffering=false;
        BufferedImage img=getImage();
        // Restore double-buffering settings
        NslMain.doubleBuffering=buffering;
        doubleBuffering=canvasBuffering;
        if(format.equals("eps"))
        {
            writeEps(filename);
        }
        else
        {
            ImageSaveDialog.save(img, filename, format);
        }
    }

    private void writeEps(String filename)
    {
        boolean buffering;
        boolean canvasBuffering;
        try
        {
            EpsGraphics2D eps = new EpsGraphics2D();
            reset=true;
            buffering= NslMain.doubleBuffering;
            NslMain.doubleBuffering=false;
            canvasBuffering=doubleBuffering;
            doubleBuffering=false;
            Color oldColor = getBackground();
            setBackground(Color.white);
            paintCanvas(eps);
            setBackground(oldColor);
            NslMain.doubleBuffering=buffering;
            doubleBuffering=canvasBuffering;
            eps.close();
            BufferedWriter writer=new BufferedWriter(new FileWriter(filename));
            writer.write(eps.toString());
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {}
    }

    /**
     * Get screenshot of canvas
     * @return - image
     */
    protected BufferedImage getImage()
    {
        BufferedImage img=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
        Color oldColor = getBackground();
        setBackground(Color.white);
        printAll(img.getGraphics());
        setBackground(oldColor);
        return img;
    }


    public boolean isShowLegend()
    {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend)
    {
        this.showLegend = showLegend;
    }

    public String getCanvasName()
    {
        return canvas_name;
    }

    public boolean getRecordData()
    {
        return recordData;
    }

    public void setRecordData(boolean recordData)
    {
        this.recordData = recordData;
    }

    public boolean getRecordFrames()
    {
        return recordFrames;
    }

    public void setRecordFrames(boolean recordFrames)
    {
        this.recordFrames = recordFrames;
    }

    public boolean getRecordVideo()
    {
        return recordVideo;
    }

    public void setRecordVideo(boolean recordVideo)
    {
        this.recordVideo = recordVideo;
    }

    public int getRecordDataTemporalMode()
    {
        return recordDataTemporalMode;
    }

    public void setRecordDataTemporalMode(int recordTemporalMode)
    {
        this.recordDataTemporalMode = recordTemporalMode;
    }

    public int getRecordFramesTemporalMode()
    {
        return recordFramesTemporalMode;
    }

    public void setRecordFramesTemporalMode(int recordTemporalMode)
    {
        this.recordFramesTemporalMode = recordTemporalMode;
    }

    public int getRecordVideoTemporalMode()
    {
        return recordVideoTemporalMode;
    }

    public void setRecordVideoTemporalMode(int recordTemporalMode)
    {
        this.recordVideoTemporalMode = recordTemporalMode;
    }

    public int getRecordDataFormat()
    {
        return recordDataFormat;
    }

    public void setRecordDataFormat(int recordDataFormat)
    {
        this.recordDataFormat = recordDataFormat;
    }

    public int getRecordFramesFormat()
    {
        return recordFramesFormat;
    }

    public void setRecordFramesFormat(int recordFramesFormat)
    {
        this.recordFramesFormat = recordFramesFormat;
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

    public double getRecordVideoFramesPerSec()
    {
        return recordVideoFramesPerSec;
    }

    public void setRecordVideoFramesPerSec(double recordVideoFramesPerSec)
    {
        this.recordVideoFramesPerSec = recordVideoFramesPerSec;
    }
}
