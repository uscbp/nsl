/* SCCS  @(#)NslAreaCanvas.java	1.5---05/15/99--12:25:35 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslAreaCanvas.java,v $
//
// Revision 1.7  1998/01/30 20:02:25  erhan
// ver 5
//
// Revision 1.4  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import nslj.src.system.NslSystem;

import java.awt.*;

public class NslAreaCanvas extends NslCanvas
{
    public NslAreaCanvas()
    {
        super();
        paintMode=1;
    }

    public NslAreaCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        super(nslDisplayFrame, pre.canvas_name, pre.getVariable().info, pre.temporalMode, pre.getMin(), pre.getMax());
        paintMode=1;
    }

    public NslAreaCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        paintMode=1;
    }

    public NslAreaCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
        paintMode=1;
    }

    public NslAreaCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        paintMode=1;
    }

    public NslAreaCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                         double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode, dmin, dmax);
        paintMode=1;
    }

    protected synchronized void paintCanvas(Graphics2D g)
    {
        reset=true;
        super.paintCanvas(g);
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
    }

    protected void drawVariable(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        setColor(bufferGraphics, var, x, y, draw_time);

        drawRectangle(bufferGraphics, var, x, y, draw_time);
    }

    protected void drawRectangle(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        int data_x_size;
        int data_y_size;
        if (var.data[x][y][draw_time] >= max)
        {
            data_x_size = dx;
            data_y_size = dy;
        }
        else if (var.data[x][y][draw_time] <= min)
        {
            data_x_size = dx;
            data_y_size = dy;
        }
        else
        {
            double scale;
            if (var.data[x][y][draw_time] >= 0)
            {
                scale = var.data[x][y][draw_time] / max;
            }
            else
            {
                scale = var.data[x][y][draw_time] / min;
            }

            data_x_size = (int) (dx * scale);
            data_y_size = (int) (dy * scale);
        }

        if (var.data[x][y][draw_time] == 0)
        {
            bufferGraphics.fill(new Rectangle(drawX + y * dx + (dx >> 1), drawY + x * dy + (dy >> 1), 1, 1));
        }
        else if (var.data[x][y][draw_time] > 0)
        {
            bufferGraphics.fill(new Rectangle(drawX + y * dx + ((dx - data_x_size) >> 1), drawY + x * dy + ((dy - data_y_size) >> 1),
                    data_x_size, data_y_size));
        }
        else
        {
            bufferGraphics.draw(new Rectangle(drawX + y * dx + ((dx - data_x_size) >> 1) + 1,
                    drawY + x * dy + ((dy - data_y_size) >> 1) + 1,
                    data_x_size - 2, data_y_size - 2));
        }
    }

    protected void setColor(Graphics2D bufferGraphics, NslVariable var, int x, int y, int draw_time)
    {
        Color boxColor = var.info.getColor();
        bufferGraphics.setColor(boxColor);
    }
}


