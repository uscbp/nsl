/* SCCS  @(#)NslStringCanvas.java	1.10---09/01/99--00:15:47 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.
package nslj.src.display;

import nslj.src.system.NslSystem;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


public class NslStringCanvas extends NslCanvas
{
    private double scale = 1;

    public NslStringCanvas()
    {
        super();
        paintMode=-1;
    }

    public NslStringCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        this(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, pre.temporalMode, pre.getMin(), pre.getMax());
        paintMode=-1;
    }

    public NslStringCanvas(NslFrame nslDisplayFrame, String full_name,
                           NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        paintMode=-1;
    }

    public NslStringCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode);
        paintMode=-1;
    }

    public NslStringCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin,
                           double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        paintMode=-1;
    }

    public NslStringCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                           double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode, dmin, dmax);
        paintMode=-1;
    }

    public void init(NslVariableInfo varInfo)
    {
        super.init(varInfo);
        if (canvasWidth < canvasHeight)
        {
            scale = canvasWidth / (max - min);
        }
        else
        {
            scale = canvasHeight / (max - min);
        }
    }

    private void MYdrawLine(Graphics2D bufferGraphics, float x0, float y0, float x1, float y1)
    {
        bufferGraphics.draw(new Line2D.Double(((x0 - min) * scale + 0.5), (canvasHeight - (y0 - min) * scale + 0.5),
                ((x1 - min) * scale + 0.5), (canvasHeight - (y1 - min) * scale + 0.5)));
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        if (canvas_type.equals("Normal"))
        {
            super.setDrawSize();
        }
        /*else
        {
            ;
        }*/
        /* if(canvas_type.equals("Zoom"))
        super.zoomDrawSize();*/

        bufferGraphics.setColor(Color.black);

        //NslVariable v = variable_list.elementAt(0);
        int x_dimension = variables.get(0).info.getDimension(0);
        y_dimension = variables.get(0).info.getDimension(1);

        int last_data_pos = variables.get(0).last_data_pos;

        if ((NslSystem.init_run_char == 'B') ||
                (NslSystem.init_run_char == 'D'))
        { //before or during
            //NslSystem.init_run_char = 'B';
            last_data_pos = 0;
        }

        int draw_time = last_data_pos;

        /* ERH: expecting x_dim as # point in the string y_dim as 2 */
        bufferGraphics.clearRect(0, 0, canvasWidth, canvasHeight);
        bufferGraphics.draw(new Rectangle2D.Double(1, 1, canvasWidth - 4, canvasHeight - 4));
        if (canvasWidth < canvasHeight)
        {
            scale = canvasWidth / (max - min);
        }
        else
        {
            scale = canvasHeight / (max - min);
        }
        for (int i = 1; i < x_dimension; i++)
        {
            MYdrawLine(bufferGraphics, variables.get(0).data[i - 1][0][draw_time],
                    variables.get(0).data[i - 1][1][draw_time],
                    variables.get(0).data[i][0][draw_time],
                    variables.get(0).data[i][1][draw_time]);
        }
        bufferGraphics.setColor(Color.black);
        bufferGraphics.drawString("Layer: " + canvas_name, 5, 12);
    }
}
