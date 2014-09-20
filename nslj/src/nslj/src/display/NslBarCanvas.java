/* SCCS  @(#)NslBarCanvas.java	1.9---09/01/99--00:15:41 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslBarCanvas.java,v $
// Revision 1.1  1997/11/06 03:19:01  erhan
// NSL3.0.b
//
// Revision 1.4  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class NslBarCanvas extends NslCanvas
{
    public NslBarCanvas()
    {
        super();
        paintMode=1;
    }

    public NslBarCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        this(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, pre.getMin(), pre.getMax());
        paintMode=1;
    }

    public NslBarCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        paintMode=1;
    }

    public NslBarCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
        paintMode=1;
    }

    public NslBarCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        paintMode=1;
    }

    public NslBarCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
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

        //draw variable
        int x_dimension = variables.get(0).info.getDimension(0);
        int y_dimension = variables.get(0).info.getDimension(1);

        int draw_time = variables.get(0).last_data_pos;

        for (int i = 0; i < x_dimension; i++)
        {
            for (int j = 0; j < y_dimension; j++)
            {
                bufferGraphics.setColor(getBackground());
                bufferGraphics.fill(new Rectangle2D.Double(drawX + j * dx + 1, drawY + i * dy + 1, dx - 1, dy - 1));

                int x_pos = (int) (dy * (0 - min) / (max - min));

                int data_y_size;
                if (variables.get(0).data[i][j][draw_time] >= max)
                {
                    data_y_size = dy;
                }
                else if (variables.get(0).data[i][j][draw_time] <= min)
                {
                    data_y_size = 0;
                }
                else
                {
                    // actual value + 1 pixel to be able to see the zero...
                    data_y_size = 1 + (int) (dy * (variables.get(0).data[i][j][draw_time] - min) / (max - min));
                }

                Color boxColor = variables.get(0).info.getColor();
                bufferGraphics.setColor(boxColor);

                bufferGraphics.setXORMode(getBackground());  // For overlaying the multiple box

                if (variables.get(0).data[i][j][draw_time] >= 0.0)
                {
                    bufferGraphics.fill(new Rectangle2D.Double(drawX + j * dx,
                            drawY + (i + 1) * dy - data_y_size,
                            dx,
                            data_y_size - x_pos));
                }
                if (variables.get(0).data[i][j][draw_time] <= 0.0)
                {
                    bufferGraphics.draw(new Rectangle2D.Double(drawX + j * dx,
                            drawY + (i + 1) * dy - x_pos,
                            dx,
                            -1 * (data_y_size - x_pos)));
                }

                bufferGraphics.setPaintMode();
            }
        }
    }
}
