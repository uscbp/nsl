/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.


package nslj.src.display;

import nslj.src.lang.NslData;
import nslj.src.system.NslSystem;
import nslj.src.main.NslMain;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Vector;
import java.text.DecimalFormat;

public class NslTemporalCanvas extends NslCanvas
{
    protected int x_dimension;  // get the info from NslVariable
    protected int y_dimension;

    protected double yMultiplier;

    static protected int xMarkPixelDistance = 40;
    static protected int yMarkPixelDistance = 40;

    protected double[][][] lastY, lastX;
    protected boolean[][][] strokeDrawn;

    protected boolean showAxisLabels=true;

    protected double quanta;

    public NslTemporalCanvas()
    {
        super();
        paintMode=1;
        doubleBuffering=false;
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, double min, double max)
    {
        this(nslDisplayFrame, TEMPORAL_MODE_TRIAL, min, max);
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, int temporalMode, double min, double max)
    {
        super(nslDisplayFrame, temporalMode, min, max);
        this.paintMode=1;
        this.doubleBuffering=false;
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        this(nslDisplayFrame, pre, pre.temporalMode);
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, NslCanvas pre, int temporalMode)
    {
        super(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, temporalMode, pre.getMin(), pre.getMax());
        this.paintMode=1;
        this.doubleBuffering=false;
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin,
                             double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        this.paintMode=1;
        this.doubleBuffering=false;
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                             double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode, dmin, dmax);
        this.paintMode=1;
        this.doubleBuffering=false;
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        this.paintMode=1;
        this.doubleBuffering=false;
    }

    public NslTemporalCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
        this.paintMode=1;
        this.doubleBuffering=false;
    }

    public void init(NslVariableInfo varInfo)
    {
        variables=new Vector<NslVariable>();
        if(varInfo!=null)
        {
            varInfo.setHistory(true);
            NslVariable var = new NslVariable(this, varInfo); 
            variables.add(var);
            x_dimension = varInfo.getDimension(0);
            y_dimension = varInfo.getDimension(1);
        }
        else
        {
            x_dimension=0;
            y_dimension=0;
        }
        quanta=1;
        if (NslFrame.system!=null)
        {
            updateDataBounds();
        }

        lastX = new double[1][x_dimension][y_dimension];
        lastY = new double[1][x_dimension][y_dimension];
        strokeDrawn = new boolean[1][x_dimension][y_dimension];
        for(int i=0; i<x_dimension; i++)
        {
            for(int j=0; j<y_dimension; j++)
            {
                lastX[0][i][j]=Double.NaN;
                lastY[0][i][j]=Double.NaN;
                strokeDrawn[0][i][j]=false;
            }
        }
        if(max>1.0)
            df=new DecimalFormat("0.0");
    }

    public void initEpochCanvas()
    {
        super.initEpochCanvas();
        if(autoScale)
        {
            min=Double.POSITIVE_INFINITY;
            max=Double.NEGATIVE_INFINITY;
        }
    }

    public void addVariable(NslData var)
    {
        NslVariableInfo varInfo=NslFrame.getVarInfo(var);
        addVariable(varInfo);
    }

    public void addVariable(NslData var, Color col)
    {
        NslVariableInfo varInfo=NslFrame.getVarInfo(var);
        addVariable(varInfo, col);
    }

    public void addVariable(NslData var, Color col, int style)
    {
        NslVariableInfo varInfo=NslFrame.getVarInfo(var);
        addVariable(varInfo, col, style);
    }

    public void addVariable(NslVariableInfo varInfo)
    {
        varInfo.setHistory(true);
        super.addVariable(varInfo);
        resetLastDrawnCoordinates();
    }

    public void addVariable(NslVariableInfo varInfo, Color col)
    {
        varInfo.setHistory(true);
        super.addVariable(varInfo, col);
        resetLastDrawnCoordinates();
    }

    public void addVariable(NslVariableInfo varInfo, Color col, int style)
    {
        varInfo.setHistory(true);
        super.addVariable(varInfo, col, style);
        resetLastDrawnCoordinates();
    }

    protected void updateDataBounds()
    {
        if(this.temporalMode==TEMPORAL_MODE_TRIAL)
        {
            tmax = NslFrame.system.getEndTime();
            quanta = NslFrame.system.getDelta();
            minDataPos = (int)(tmin / quanta);
            maxDataPos = (int)(tmax / quanta);
        }
        else if(this.temporalMode==TEMPORAL_MODE_EPOCH)
        {
            tmax = Math.max(NslFrame.system.getNumRunEpochs(),NslFrame.system.getNumTrainEpochs());
            quanta = 1;
            minDataPos = (int)(tmin / quanta);
            maxDataPos = (int)(tmax / quanta);
        }
        for(int i=0; i<variables.size(); i++)
            variables.get(i).init(maxDataPos);
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        int last_data_pos = variables.get(0).last_data_pos;

        if(reset || !NslMain.doubleBuffering)
            drawTicksAndLabels(bufferGraphics);

        bufferGraphics.setColor(Color.black);
        for(int i=0; i<variables.size(); i++)
        {
            if(variables.get(i).info.style!=NslVariableInfo.LINE_STYLE_INVISIBLE)
            {
                if(reset || !NslMain.doubleBuffering)
                {
                    for(int x=0; x<x_dimension; x++)
                    {
                        for(int y=0; y<y_dimension; y++)
                        {
                            lastX[i][x][y]=Double.NaN;
                            lastY[i][x][y]=Double.NaN;
                            strokeDrawn[i][x][y]=false;
                        }
                    }
                    for (int j = 0; j <= last_data_pos; j++)
                    {
                        addPoint(bufferGraphics, i, j);
                    }
                }
                else
                {
                    addPoint(bufferGraphics, i, last_data_pos);
                }
            }
        }
    }

    private void resetLastDrawnCoordinates()
    {
        lastX = new double[variables.size()][x_dimension][y_dimension];
        lastY = new double[variables.size()][x_dimension][y_dimension];
        strokeDrawn = new boolean[variables.size()][x_dimension][y_dimension];
        for(int x=0; x<variables.size(); x++)
        {
            for(int i=0; i<x_dimension; i++)
            {
                for(int j=0; j<y_dimension; j++)
                {
                    lastX[x][i][j]=Double.NaN;
                    lastY[x][i][j]=Double.NaN;
                    strokeDrawn[x][i][j]=false;
                }
            }
        }
    }

    protected void drawTicksAndLabels(Graphics2D bufferGraphics)
    {
        bufferGraphics.setColor(Color.black);

        // parameters for determining marks and labels on the display
        double xMarkSize = canvasHeight / 60 < 1 ? 1 : 3;
        double yMarkSize = canvasWidth / 40 < 1 ? 1 : 3;
        int noXMarks=dx/xMarkPixelDistance;
        double xMarkDistance=maxDataPos*quanta/(noXMarks > 0 ? noXMarks : 1);
        int noYMarks = dy/yMarkPixelDistance;
        double yMarkValueDy = (max - min) / (noYMarks > 0 ? noYMarks : 1);
        yMultiplier = dy/(max - min);

        if ("Zoom".equals(canvas_type) || showAxisLabels)
            bufferGraphics.setFont(new Font("Courier", Font.PLAIN, 12));

        for (int xIdx = 0; xIdx < x_dimension; xIdx++)
        {
            double yBase=drawY+(xIdx+1)*dy;

            for (int yIdx = 0; yIdx < y_dimension; yIdx++)
            {                
                double xBase=drawX+yIdx*dx;

                // Draw x marks
                drawXMarks(bufferGraphics, xMarkSize, xMarkDistance, xIdx, xBase, yBase);

                // Draw y marks
                drawYMarks(bufferGraphics, yMarkSize, yMarkValueDy, yIdx, xBase, yBase);
            }
        }
    }

    private void drawYMarks(Graphics2D bufferGraphics, double yMarkSize, double yMarkValueDy, int yIdx,
                            double xBase, double yBase)
    {
        boolean showLabel=false;      // label alternate y marks
        for (double y = min; y < max; y += yMarkValueDy, showLabel = !showLabel)
        {
            bufferGraphics.draw(new Line2D.Double(xBase-yMarkSize, yBase-((y-min) * yMultiplier),
                    xBase+yMarkSize, yBase-((y-min) * yMultiplier)));

            if (("Zoom".equals(canvas_type) || (showAxisLabels && yIdx==0)) && showLabel)
            {
                String f = df.format(y);
                FontMetrics fm = bufferGraphics.getFontMetrics();
                float labelYPosition = (float)(yBase-((y-min) * yMultiplier)+fm.getHeight()/2);
                bufferGraphics.drawString(f, (float)(xBase-yMarkSize-fm.stringWidth(f)), labelYPosition);
            }
        }
    }

    private void drawXMarks(Graphics2D bufferGraphics, double xMarkSize, double xMarkDistance, int xIdx,
                            double xBase, double yBase)
    {
        boolean showLabel = false;      // label alternate x marks
        for (double x = 0.0; x < maxDataPos * quanta; x += xMarkDistance, showLabel = !showLabel)
        {
            bufferGraphics.draw(new Line2D.Double((dx * x / (maxDataPos * quanta) + xBase), yBase - xMarkSize,
                    (dx * x / (maxDataPos * quanta) + xBase), yBase + xMarkSize));

            if (("Zoom".equals(canvas_type) || (showAxisLabels && xIdx==(x_dimension-1))) && x>0.0 && showLabel)
            {
                String f=df.format(x);
                FontMetrics fm = bufferGraphics.getFontMetrics();
                float labelXPosition = (float)(dx * x / (maxDataPos * quanta) + xBase) - (fm.stringWidth(f) >> 1);
                bufferGraphics.setColor(Color.black);
                bufferGraphics.drawString(f, labelXPosition, (float)(yBase + fm.getHeight()));
            }
        }
    }

    protected synchronized void addPoint(Graphics2D bufferGraphics, int varIdx, int time)
    {
        // calculate bases
        for (int k = 0; k < y_dimension; k++)
        {
            double xBase=drawX+k*dx;
            double x1 = xBase + (dx * (double) ((time) % maxDataPos) / maxDataPos);
            for (int l = 0; l < x_dimension; l++)
            {
                double yBase=drawY+(l+1)*dy;
                bufferGraphics.setColor(variables.get(varIdx).info.getColor());

                double y1 = yBase - ((variables.get(varIdx).data[l][k][time]-min) * yMultiplier);
                if (variables.get(varIdx).info.style!=NslVariableInfo.LINE_STYLE_DASH ||
                        !strokeDrawn[varIdx][l][k])
                {
                    if(time>0)
                    {
                        if(x1>=lastX[varIdx][l][k]+.01)
                        {
                            setStroke(bufferGraphics, variables.get(varIdx).info.style);
                            bufferGraphics.draw(new Line2D.Double(lastX[varIdx][l][k], lastY[varIdx][l][k], x1, y1));
                            lastX[varIdx][l][k] = x1;
                            lastY[varIdx][l][k] = y1;
                            strokeDrawn[varIdx][l][k]=true;
                        }
                        else
                        {
                            if(y1>lastY[varIdx][l][k])
                                lastY[varIdx][l][k]=y1;
                        }
                    }
                    else
                    {
                        lastX[varIdx][l][k]=xBase + (dx * (double) (time % maxDataPos) / maxDataPos);
                        lastY[varIdx][l][k]=y1;
                    }
                }
                else
                {
                    lastX[varIdx][l][k] = x1;
                    lastY[varIdx][l][k] = y1;
                    strokeDrawn[varIdx][l][k]=false;
                }
            }
        }
    }

    protected void setStroke(Graphics g, int style)
    {
        if (style == NslVariableInfo.LINE_STYLE_SOLID)
        {
            ((Graphics2D) g).setStroke(new BasicStroke());
        }
        else if (style == NslVariableInfo.LINE_STYLE_DASH)
        {
            ((Graphics2D) g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[]{.25f, .25f}, 0.0f));
        }
        else if (style == NslVariableInfo.LINE_STYLE_DASH_DOT)
        {
            ((Graphics2D) g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[]{.21f, .09f, .03f, .09f}, 0.0f));
        }
        else if (style == NslVariableInfo.LINE_STYLE_DOTTED)
        {
            ((Graphics2D) g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[]{.14f, .03f}, 0.0f));
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
            for(int i=0; i<variables.size(); i++)
            {
                variables.get(i).collectEpoch();
                int draw_time = variables.get(i).last_data_pos;

                if (draw_time >= 0 && draw_time <= maxDataPos)
                {
                    addPoint((Graphics2D)getGraphics(), i, draw_time);
                }
            }
            paintCanvas((Graphics2D)getGraphics());
        }
    }

    public int getMaxDataPos()
    {
        return maxDataPos;
    }

    public void setMaxDataPos(int maxDataPos)
    {
        this.maxDataPos = maxDataPos;
    }

    public void setTemporalMode(int mode)
    {
        super.setTemporalMode(mode);
        updateDataBounds();
    }
    
    public boolean isShowAxisLabels()
    {
        return showAxisLabels;
    }

    public void setShowAxisLabels(boolean showAxisLabels)
    {
        this.showAxisLabels = showAxisLabels;
    }

    public NslCanvas copy(String graph_type_name)
    {
        NslCanvas ndc=super.copy(graph_type_name);
        if("Raster".equals(graph_type_name) || "Histogram".equals(graph_type_name) || "Temporal".equals(graph_type_name))
        {
            ((NslTemporalCanvas)ndc).quanta=quanta;
            ((NslTemporalCanvas)ndc).tmax=tmax;
        }
        return ndc;
    }
}
