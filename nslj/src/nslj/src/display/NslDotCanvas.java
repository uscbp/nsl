/* SCCS  @(#)NslDotCanvas.java	1.6---05/15/99--12:25:36 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.system.NslSystem;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class NslDotCanvas extends NslCanvas
{
    private double scale = 1;

    public NslDotCanvas()
    {
        super();
        paintMode=-1;
    }

    public NslDotCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        super(nslDisplayFrame, pre.canvas_name, pre.getVariable().info, pre.temporalMode, pre.getMin(), pre.getMax());
        paintMode=-1;
    }

    public NslDotCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        paintMode=-1;
    }

    public NslDotCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
        paintMode=-1;
    }

    public NslDotCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        paintMode=-1;
    }

    public NslDotCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                        double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode, dmin, dmax);
        paintMode=-1;
    }

    public void init(NslVariableInfo varInfo)
    {
        super.init(varInfo);
    }

    private void MYdrawPoint(Graphics2D g, double x0, double y0)
    {
        g.draw(new Ellipse2D.Double(((x0 - min) * scale + 0.5), ((canvasHeight - (y0 - min) * scale) + 0.5)
                , 3, 3));
    }

    protected synchronized void paintCanvas(Graphics2D g)
    {
        reset=true;
        super.paintCanvas(g);
    }
    
    public void paintData(Graphics2D bufferGraphics)
    {
        bufferGraphics.setColor(Color.black);


        int x_dimension = variables.get(0).info.getDimension(0);
        y_dimension = variables.get(0).info.getDimension(1);
        scale = (Math.min(canvasWidth,canvasHeight) - 4) / (Math.max(x_dimension,y_dimension));
        int last_data_pos = variables.get(0).last_data_pos;

        if ((NslSystem.init_run_char == 'B') || (NslSystem.init_run_char == 'D'))
        { //before or during
            last_data_pos = 0;
        }
        int draw_time = last_data_pos;

        /*if (canvasWidth < canvasHeight)
        {
            scale = (canvasWidth - 4) / (max - min);
        }
        else
        {
            scale = (canvasHeight - 4) / (max - min);
        }*/
        
        /*for (int i = 0; i < x_dimension; i++)
        {
            MYdrawPoint(bufferGraphics, variables.get(0).data[i][0][draw_time],
                        variables.get(0).data[i][1][draw_time]);
        }*/
        for (int i = 0; i < x_dimension; i++)
        {
            for (int j = 0; j < y_dimension; j++)
            {
                drawVariable(bufferGraphics, variables.get(0), i, j, draw_time);
            }
        }

        //bufferGraphics.drawString("Layer: " + super.canvas_name, 5, 12);
    }

    protected void drawVariable(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        setColor(bufferGraphics, var, x, y, draw_time);

        drawEllipse(bufferGraphics, var, x, y, draw_time);
    }

    protected void setColor(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        Color boxColor = var.info.getColor();
        bufferGraphics.setColor(boxColor);
    }

    protected void drawEllipse(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        if(var.data[x][y][draw_time]>0)
            bufferGraphics.draw(new Ellipse2D.Double((x*scale + 0.5), ((canvasHeight-y*scale) + 0.5) , 3, 3));
    }
}
