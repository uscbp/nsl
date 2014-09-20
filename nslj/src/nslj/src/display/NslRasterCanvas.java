package nslj.src.display;

import nslj.src.main.NslMain;
import nslj.src.system.NslSystem;

import java.awt.*;

public class NslRasterCanvas extends NslHistogramCanvas
{
    public NslRasterCanvas()
    {
        super();
        paintMode=-1;
    }

    public NslRasterCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        this(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, pre.temporalMode);
    }

    public NslRasterCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        paintMode=-1;
    }

    public NslRasterCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
        this.paintMode=-1;
    }

    public void initCanvas()
    {
        if(NslFrame.system.getCurrentTime()>0)
            reset=true;
    }

    public void init(NslVariableInfo varInfo)
    {
        super.init(varInfo);
        min=0;
        max=1;
        if(bin_size>0)
            currentBins=new Bin[variables.size()][NslFrame.system.getEpochs()][x_dimension][maxDataPos/bin_size];
    }

    public void setDrawSize()
    {
        drawY = "Normal".equals(canvas_type) ? 30 : 10;
        drawY += adjustY;
        drawX = 40;
        drawW = canvasWidth - 60;
        drawH = canvasHeight - 50 - adjustY;
        if(variables!=null)
        {
            dx = drawW / variables.get(0).info.getDimension(0);
            dy = drawH / NslFrame.system.getEpochs();
        }
        else
        {
            dx=1;
            dy=1;
        }
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        int last_data_pos = variables.get(0).last_data_pos;

        //before or during init
        if ((NslSystem.init_run_char == 'B') || (NslSystem.init_run_char == 'D'))
        {
            last_data_pos = 1;
            //draw_time = 0;
        }

        if(reset || !NslMain.doubleBuffering)
            drawTicksAndLabels(bufferGraphics);

        bufferGraphics.setColor(Color.black);

        if(reset || !NslMain.doubleBuffering)
        {
            for(int x=0; x<NslFrame.system.getFinishedEpochs()+1; x++)
            {
                double limit=NslFrame.system.getEndTime()/NslFrame.system.getDelta();
                if(x==NslFrame.system.getFinishedEpochs())
                    limit=last_data_pos;
                for (int j = 1; j < limit; j++)
                {
                    for(int i=0; i<variables.size(); i++)
                        addPoint(bufferGraphics, i, x, j);
                    drawBins(bufferGraphics, x, j, (j-1)/bin_size);
                }
            }
        }
        if(last_data_pos>1)
        {
            for(int i=0; i<variables.size(); i++)
                addPoint(bufferGraphics, i, NslFrame.system.getFinishedEpochs(), last_data_pos-1);
            drawBins(bufferGraphics, NslFrame.system.getFinishedEpochs(), last_data_pos-1, (last_data_pos-2)/bin_size);
        }                         //
    }

    protected void drawTicksAndLabels(Graphics2D bufferGraphics)
    {
        bufferGraphics.setColor(Color.black);

        // parameters for determining marks and labels on the display
        int noXMarks=dx/xMarkPixelDistance;
        double xMarkDistance=maxDataPos*quanta/(noXMarks > 0 ? noXMarks : 1);
        yMultiplier = dy/(max - min);

        if ("Zoom".equals(canvas_type) || showAxisLabels)
            bufferGraphics.setFont(new Font("Courier", Font.PLAIN, 12));

        for (int epoch = 0; epoch < NslFrame.system.getEpochs(); epoch++)
        {
            for (int xIdx = 0; xIdx < x_dimension; xIdx++)
            {
                // Draw x marks
                drawXMarks(bufferGraphics, xMarkDistance, xIdx, epoch);
            }
        }
    }

    private void drawXMarks(Graphics2D bufferGraphics, double xMarkDistance, int xIdx, int epoch)
    {
        double xBase=drawX+xIdx*dx;
        double yBase=drawY+(epoch+1)*dy;
        boolean showLabel = false;      // label alternate x marks
        for (double x = 0.0; x < maxDataPos * quanta; x += xMarkDistance, showLabel = !showLabel)
        {
            if (("Zoom".equals(canvas_type) || (showAxisLabels && epoch==(NslFrame.system.getEpochs()-1))) && x>0.0 &&
                    showLabel)
            {
                String f=df.format(x);
                FontMetrics fm = bufferGraphics.getFontMetrics();
                float labelXPosition = (float)(dx * x / (maxDataPos * quanta) + xBase) - (fm.stringWidth(f) >> 1);
                bufferGraphics.setColor(Color.black);
                bufferGraphics.drawString(f, labelXPosition, (float)(yBase + fm.getHeight()));
            }
        }
    }

    protected synchronized void addPoint(Graphics2D bufferGraphics, int var, int epoch, int time)
    {
        if(epoch<NslFrame.system.getEpochs())
        {
            // figure out which bin to put the data in
            int binIdx=(time-1)/bin_size;
            double yBase=drawY+(epoch+1)*dy;
            for(int y=0; y<x_dimension; y++)
            {
                // create new bin if none exists here
                if(currentBins[var][epoch][y][binIdx]==null)
                {
                    currentBins[var][epoch][y][binIdx]=new Bin(bin_size);
                }
                // set bin position and width
                currentBins[var][epoch][y][binIdx].x=(int)(drawX + dx*((double)binIdx*((double)bin_size/(double)maxDataPos)));
                currentBins[var][epoch][y][binIdx].y=(int)(yBase+y*dy);
                currentBins[var][epoch][y][binIdx].width=Math.max(1,(int)((drawX + dx*((double)(binIdx+1)*((double)bin_size/(double)maxDataPos)))-(drawX + dx*((double)binIdx*((double)bin_size/(double)maxDataPos)))));

                // update bin count
                if(epoch==NslFrame.system.getFinishedEpochs() && variables.get(var).data[y][0][time]>0)
                    currentBins[var][epoch][y][binIdx].count[(time-1)%bin_size]=1;
            }
        }
    }

    protected void drawBins(Graphics2D bufferGraphics, int epoch, int time, int binIdx)
    {
        for(int y=0; y<x_dimension; y++)
        {
            for(int i=0; i<variables.size(); i++)
            {
                if(epoch<NslFrame.system.getEpochs() &&
                        currentBins[i][epoch][y][binIdx]!=null &&
                        variables.get(i).info.style!= NslVariableInfo.LINE_STYLE_INVISIBLE)
                {
                    double height = calculateBinHeight(binIdx, epoch, y, i);
                    drawBin(bufferGraphics, i, binIdx, epoch, y, height);
                }
            }
        }
    }

    protected double calculateBinHeight(int binIdx, int x, int y, int varIdx)
    {
        double yBase=drawY+(x+1)*dy;
        double count=0.0;
        for(int j=0; j<bin_size; j++)
            count+=currentBins[varIdx][x][y][binIdx].count[j];
        return yBase-(count*yMultiplier)+y*dy;
    }

    public void setBinSize(int size)
    {
        if(size!=bin_size)
        {
            bin_size=size;
            currentBins=new Bin[variables.size()][NslFrame.system.getEpochs()][x_dimension][maxDataPos/bin_size];
            paintComponent(getGraphics());
        }
    }

    public void addVariable(NslVariableInfo varInfo)
    {
        super.addVariable(varInfo);
        currentBins=new Bin[variables.size()][NslFrame.system.getEpochs()][x_dimension][maxDataPos/bin_size];
    }

    public void addVariable(NslVariableInfo varInfo, Color col)
    {
        super.addVariable(varInfo, col);
        currentBins=new Bin[variables.size()][NslFrame.system.getEpochs()][x_dimension][maxDataPos/bin_size];
    }

    public void addVariable(NslVariableInfo varInfo, Color col, int style)
    {
        super.addVariable(varInfo, col, style);
        currentBins=new Bin[variables.size()][NslFrame.system.getEpochs()][x_dimension][maxDataPos/bin_size];
    }

    public NslCanvas copy(String graph_type_name)
    {
        NslCanvas ndc=super.copy(graph_type_name);
        if("Raster".equals(graph_type_name) || "Histogram".equals(graph_type_name))
        {
            ((NslHistogramCanvas)ndc).bin_size = this.bin_size;
            ((NslHistogramCanvas)ndc).currentBins=new Bin[variables.size()][NslFrame.system.getEpochs()][x_dimension][maxDataPos/bin_size];
            for(int i=0; i<variables.size(); i++)
            {
                for(int j=0; j<NslFrame.system.getFinishedEpochs(); j++)
                {
                    for(int k=0; k<x_dimension; k++)
                    {
                        System.arraycopy(currentBins[i][j][k], 0, ((NslHistogramCanvas) ndc).currentBins[i][j][k], 0,
                                         maxDataPos / bin_size);
                    }
                }
            }
        }
        return ndc;
    }
}
