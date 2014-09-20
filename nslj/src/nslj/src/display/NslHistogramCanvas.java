/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.main.NslMain;
import nslj.src.system.NslSystem;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class NslHistogramCanvas extends NslTemporalCanvas
{
    public static int MODE_MIN =0, MODE_MEAN =1, MODE_MAX =2, MODE_SUM=3;
    protected int mode=MODE_SUM;
    protected int bin_size = 1;
    protected Bin[][][][] currentBins;

    public NslHistogramCanvas()
    {
        super();
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, double min, double max)
    {
        super(nslDisplayFrame, min, max);
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, int temporalMode, double min, double max)
    {
        super(nslDisplayFrame, temporalMode, min, max);
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        this(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, pre.temporalMode, pre.getMin(), pre.getMax());
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double min,
                              double max)
    {
        this(nslDisplayFrame, full_name, data_info);
        setMinMax(min, max);
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                              double min, double max)
    {
        this(nslDisplayFrame, full_name, data_info, temporalMode);
        setMinMax(min, max);
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
        init(data_info);
    }

    public NslHistogramCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        this(nslDisplayFrame, full_name, data_info);
        this.temporalMode=temporalMode;
    }

    public void setMode(int mode)
    {
        this.mode=mode;
    }

    public int getMode()
    {
        return mode;
    }

    public void initCanvas()
    {
        if(NslFrame.system.getCurrentTime()>0)
            reset=true;
        else if(NslFrame.system.getCurrentEpoch()==1)
            currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
    }


    public void init(NslVariableInfo varInfo)
    {
        super.init(varInfo);
        if(bin_size>0)
            currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        int last_data_pos = variables.get(0).last_data_pos;

        if(reset || !NslMain.doubleBuffering)
            drawTicksAndLabels(bufferGraphics);

        bufferGraphics.setColor(Color.black);

        if(reset || !NslMain.doubleBuffering)
        {
            double limit=NslFrame.system.getEndTime()/NslFrame.system.getDelta();
            if(NslFrame.system.getFinishedEpochs()==0)
                limit=last_data_pos;
            for (int j = 0; j < limit; j++)
            {
                for(int i=0; i<variables.size(); i++)
                    addPoint(bufferGraphics, i, j);
                drawBins(bufferGraphics, j, j/bin_size);
            }
        }
        else
        {
            for(int i=0; i<variables.size(); i++)
                addPoint(bufferGraphics, i, last_data_pos);
            drawBins(bufferGraphics, last_data_pos, last_data_pos/bin_size);
        }
    }

    protected void drawBins(Graphics2D bufferGraphics, int time, int binIdx)
    {
        for(int x=0; x<x_dimension; x++)
        {
            for(int y=0; y<y_dimension; y++)
            {
                Vector<Integer> drawnIdx=new Vector<Integer>();
                // For this x,y coord, sort data from each variable at this time step, draw in order of increasing height
                while(drawnIdx.size()<variables.size())
                {
                    double maxHeight=Double.POSITIVE_INFINITY;
                    int maxIdx=-1;
                    for(int i=0; i<variables.size(); i++)
                    {
                        if(variables.get(i).info.style== NslVariableInfo.LINE_STYLE_INVISIBLE ||
                                binIdx>=currentBins[i][x][y].length || currentBins[i][x][y][binIdx]==null)
                            drawnIdx.add(i);
                        else
                        {
                            double height = calculateBinHeight(binIdx, x, y, i);
                            if(height<maxHeight && !drawnIdx.contains(i))
                            {
                                maxIdx=i;
                                maxHeight=height;
                            }
                        }
                    }
                    if(maxIdx>-1)
                    {
                        drawBin(bufferGraphics, maxIdx, binIdx, x, y, maxHeight);
                        drawnIdx.add(maxIdx);
                    }
                }
            }
        }
    }

    protected double calculateBinHeight(int binIdx, int x, int y, int varIdx)
    {
        double yBase=drawY+(x+1)*dy;
        double count=0.0;
        if(mode==MODE_MEAN || mode==MODE_SUM)
        {
            for(int j=0; j<bin_size; j++)
                count+=currentBins[varIdx][x][y][binIdx].count[j];
            if(mode==MODE_MEAN)
                count/=bin_size;
        }
        else if(mode==MODE_MAX)
        {
            count=Double.NEGATIVE_INFINITY;
            for(int j=0; j<bin_size; j++)
            {
                if(currentBins[varIdx][x][y][binIdx].count[j]>count)
                    count=currentBins[varIdx][x][y][binIdx].count[j];
            }
        }
        else if(mode==MODE_MIN)
        {
            count=Double.POSITIVE_INFINITY;
            for(int j=0; j<bin_size; j++)
            {
                if(currentBins[varIdx][x][y][binIdx].count[j]<count)
                    count=currentBins[varIdx][x][y][binIdx].count[j];
            }
        }
        double height=count/(NslFrame.system.getDelta()*bin_size)/NslFrame.system.getEpochs();
        height=yBase-(height*yMultiplier)+y*dy;
        return height;
    }

    protected synchronized void addPoint(Graphics2D bufferGraphics, int var, int time)
    {
        // figure out which bin to put the data in
        int binIdx=time/bin_size;
        // bin size (in percentage of the total number of time steps)
        double binSize=(double)bin_size/(double)maxDataPos;
        for(int x=0; x<x_dimension; x++)
        {
            double yBase=drawY+(x+1)*dy;
            for(int y=0; y<y_dimension; y++)
            {
                if(binIdx<currentBins[var][x][y].length)
                {
                    // create new bin if none exists here
                    if(currentBins[var][x][y][binIdx]==null)
                    {
                        currentBins[var][x][y][binIdx]=new Bin(bin_size);
                    }
                    // set bin position and width
                    currentBins[var][x][y][binIdx].x=(int)(drawX + dx*((double)binIdx*binSize));
                    currentBins[var][x][y][binIdx].y=(int)(yBase+(double)y*dy);

                    currentBins[var][x][y][binIdx].width=Math.max(1.0,(dx*binSize));

                    // update bin count
                    if(time==variables.get(var).last_data_pos)
                        currentBins[var][x][y][binIdx].count[time%bin_size] += variables.get(var).data[x][y][time];
                }
            }
        }
    }

    protected void drawBin(Graphics2D bufferGraphics, int var, int binIdx, int x, int y, double height)
    {
        //Draw bin
        bufferGraphics.setColor(variables.get(var).info.getColor());

        bufferGraphics.fill(new Rectangle2D.Double(currentBins[var][x][y][binIdx].x, height,
                                                   currentBins[var][x][y][binIdx].width,
                                                   currentBins[var][x][y][binIdx].y-height));
    }

    public void setBinSize(int size)
    {
        if(size!=bin_size)
        {
            bin_size=size;
            currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
            paintComponent(getGraphics());
        }
    }

    public int getBinSize()
    {
        return bin_size;
    }

    public void addVariable(NslVariableInfo varInfo)
    {
        super.addVariable(varInfo);
        currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
    }

    public void addVariable(NslVariableInfo varInfo, Color col)
    {
        super.addVariable(varInfo, col);
        currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
    }

    public void addVariable(NslVariableInfo varInfo, Color col, int style)
    {
        super.addVariable(varInfo, col, style);
        currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
    }

    public NslCanvas copy(String graph_type_name)
    {
        NslCanvas ndc=super.copy(graph_type_name);
        if("Raster".equals(graph_type_name) || "Histogram".equals(graph_type_name))
        {
            ((NslHistogramCanvas)ndc).bin_size = this.bin_size;
            ((NslHistogramCanvas)ndc).currentBins=new Bin[variables.size()][x_dimension][y_dimension][maxDataPos/bin_size];
            for(int i=0; i<variables.size(); i++)
            {
                for(int j=0; j<x_dimension; j++)
                {
                    for(int k=0; k<y_dimension; k++)
                    {
                        System.arraycopy(currentBins[i][j][k], 0, ((NslHistogramCanvas) ndc).currentBins[i][j][k], 0,
                                         maxDataPos / bin_size);
                    }
                }
            }
        }
        return ndc;
    }

    static class Bin
    {
        int x, y;
        double[] count;
        double width;

        public Bin(int size)
        {
            x=Integer.MAX_VALUE;
            y=0;
            count =new double[size];
            width=0;
        }
    }
}
