//--------------------------------------
// $Log: NslGrayscaleCanvas.java,v $
//--------------------------------------

package nslj.src.display;

import nslj.src.system.NslSystem;
import nslj.src.main.NslMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

public class NslGrayscaleCanvas extends NslCanvas
{
    private float[][] shadeMap;
    protected JToolTip tooltip=null;

    public NslGrayscaleCanvas()
    {
        super();
    }

    public NslGrayscaleCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        super(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, pre.temporalMode, pre.getMin(), pre.getMax());
    }

    public NslGrayscaleCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
    }

    public NslGrayscaleCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
    }

    public NslGrayscaleCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin,
                              double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
    }

    public NslGrayscaleCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                              double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode, dmin, dmax);
    }

    public void init(NslVariableInfo varInfo)
    {
        super.init(varInfo);
        shadeMap=new float[x_dimension][y_dimension];
    }

    public void setDrawSize()
    {
        drawY = "Normal".equals(canvas_type) ? 30 : 10;
        drawY += adjustY;
        drawX = showLegend ? 10 : 40;
        drawW = canvasWidth - 60;
        drawH = canvasHeight - 50 - adjustY;
        if(variables!=null)
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

    public void paintData(Graphics2D bufferGraphics)
    {
        bufferGraphics.setColor(Color.black);

        int last_data_pos = getVariable().last_data_pos;

        //before or during
        if ((NslSystem.init_run_char == 'B') || (NslSystem.init_run_char == 'D'))
        {
            last_data_pos = 0;
        }

        int draw_time = last_data_pos;

        for (int i = 0; i < x_dimension; i++)
        {
            for (int j = 0; j < y_dimension; j++)
            {
                drawVariable(bufferGraphics, variables.get(0), i, j, draw_time);
            }            
        }
        if(showLegend && (reset || !NslMain.doubleBuffering))
            drawLegend(bufferGraphics);
    }

    protected void drawLegend(Graphics2D bufferGraphics)
    {
        int noYMarks = 40;
        double yMarkValueDy = (max - min) / (noYMarks > 0 ? noYMarks : 1);
        double yBase=drawY+(x_dimension)*dy+1;
        double xBase=drawX+(y_dimension)*dx+5;
        double yMultiplier = ((double)drawH/(double)noYMarks);
        double yMarkSize = canvasWidth / 40 < 1 ? 1 : 3;
        for (double y = min; y < max; y += yMarkValueDy)
        {
            int colorLevel=getShade(y);
            bufferGraphics.setColor(new Color(255-colorLevel, 255-colorLevel, 255-colorLevel));
            bufferGraphics.fill(new Rectangle2D.Double(xBase, yBase-((y-min)/yMarkValueDy * yMultiplier),
                    yMarkSize, yMultiplier));
        }
        int noYLabels=drawH/40;
        double yLabelDy = (max - min) / (noYLabels > 0 ? noYLabels : 1);
        FontMetrics fm = bufferGraphics.getFontMetrics();
        yMultiplier = ((double)drawH/(double)noYLabels);
        bufferGraphics.setColor(NslColor.getColor("BLACK"));
        yBase+=fm.getHeight()/2;
        for (double y=min; y<max; y+=yLabelDy)
        {
            String f = df.format(y);
            float labelYPosition = (float)(yBase-((y-min)/yLabelDy * yMultiplier)+fm.getHeight()/2);
            bufferGraphics.drawString(f, (float)(xBase+yMarkSize), labelYPosition);
        }
        String f = df.format(max);
        float labelYPosition = (float)(yBase-((max-min)/yLabelDy * yMultiplier)+fm.getHeight()/2);
        bufferGraphics.drawString(f, (float)(xBase+yMarkSize), labelYPosition);
    }

    protected void drawVariable(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        int colorLevel=getShade(var.data[x][y][draw_time]);
        if(colorLevel!=shadeMap[x][y] || reset)
        {
            bufferGraphics.setColor(new Color(255-colorLevel, 255-colorLevel, 255-colorLevel));
            drawRectangle(bufferGraphics, x, y);
            shadeMap[x][y]=colorLevel;
        }
    }

    protected void drawRectangle(Graphics2D bufferGraphics, int x, int y)
    {
        bufferGraphics.fill(new Rectangle2D.Double(drawX + y * dx + 1, drawY + x * dy + 1, dx - 1, dy - 1));
    }

    protected int getShade(double val)
    {
        int colorLevel=(int)(((val- min)/(max - min))*255.0);
        if(colorLevel>255)
            colorLevel=255;
        else if(colorLevel<0)
            colorLevel=0;
        return colorLevel;
    }

    public void mouseMoved(MouseEvent e)
    {
        // Figure out which element mouse is pointing to
        Point mouseLocation=e.getPoint();
        int y=(int)((mouseLocation.getX()-1-drawX)/dx);
        int x=(int)((mouseLocation.getY()-1-drawY)/dy);

        if(x>=0 && x<x_dimension && y>=0 && y<y_dimension)
        {
            //Popup tooltip with value of element
            setToolTipText(Float.toString(variables.get(0).data[x][y][getVariable().last_data_pos]));
            tooltip=createToolTip();
        }
    }
}
