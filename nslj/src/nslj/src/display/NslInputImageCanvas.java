/* SCCS  %W%---%G%--%U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.lang.NslNumeric1;
import nslj.src.lang.NslNumeric2;
import nslj.src.system.NslSystem;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NslInputImageCanvas extends NslCanvas
{
    private int data_x_size, data_y_size, x_dimension, y_dimension;
    private int last_data_pos, draw_time;
    private Color boxColor = Color.black;

    public NslInputImageCanvas()
    {
        super();
        paintMode=1;
    }

    public NslInputImageCanvas(NslCanvas pre)
    {
        super();
        setMinMax(pre.getMin(), pre.getMax());
        paintMode=1;
    }

    public NslInputImageCanvas(NslFrame nslDisplayFrame, String full_name,
                               NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        paintMode=1;

        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                int mx = e.getX(), my = e.getY();
                float posX = (float) (my - drawY - 1) / (float) dy;
                float posY = (float) (mx - drawX - 1) / (float) dx;

                int x = variables.get(0).info.getDimension(0);
                int y = variables.get(0).info.getDimension(1);

                if (posX <= x && posX >= 0 && posY <= y && posY >= 0)
                {
                    int i = (int) posX, j = (int) posY;

                    if (variables.get(0).data[i][j][draw_time] == min)
                    {
                        variables.get(0).data[i][j][draw_time] = (float) max;
                        boxColor = variables.get(0).info.getColor();
                    }
                    else
                    {
                        variables.get(0).data[i][j][draw_time] = (float) min;
                        boxColor = getBackground();

                    }

                    Graphics2D bufferGraphics = (Graphics2D)getGraphics();
                    bufferGraphics.setColor(boxColor);
                    bufferGraphics.fill(new Rectangle2D.Double(drawX + j * dx + 1, drawY + i * dy + 1, dx - 1, dy - 1));
                    //g.dispose();

                    switch (variables.get(0).info.getCountDimensions())
                    {
                        case 1:
                            ((NslNumeric1) variables.get(0).info.getNslVar())._set(i, variables.get(0).data[i][j][draw_time]);
                            break;
                        case 2:
                            ((NslNumeric2) variables.get(0).info.getNslVar())._set(i, j, variables.get(0).data[i][j][draw_time]);
                            break;
                    }

                }
            }
        });
    }

    public NslInputImageCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        this(nslDisplayFrame, full_name, data_info);
        this.temporalMode=temporalMode;
    }

    public NslInputImageCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        paintMode=1;
    }

    public NslInputImageCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                               double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, temporalMode, dmin, dmax);
        paintMode=1;
    }

    /*public void update()
    {
        super.update();
        draw_time = 0;
    }*/

    protected synchronized void paintCanvas(Graphics2D g)
    {
        reset=true;
        super.paintCanvas(g);
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        if (mouseAdapter != null)
        {
            removeMouseListener(mouseAdapter);
            mouseAdapter = null;
        }

        x_dimension = variables.get(0).info.getDimension(0);
        y_dimension = variables.get(0).info.getDimension(1);

        last_data_pos = variables.get(0).last_data_pos;

        if ((NslSystem.init_run_char == 'B') ||
                (NslSystem.init_run_char == 'D'))
        { //before or during
            //NslSystem.init_run_char = 'B';
            last_data_pos = 0;
        }

        draw_time = last_data_pos;

        for (int i = 0; i < x_dimension; i++)
        {
            for (int j = 0; j < y_dimension; j++)
            {
                Color color = getBackground();
                bufferGraphics.setColor(color);

                bufferGraphics.fill(new Rectangle2D.Double(drawX + j * dx + 1, drawY + i * dy + 1, dx - 1, dy - 1));

                if (variables.get(0).data[i][j][draw_time] >= max)
                {
                    data_x_size = dx;
                    data_y_size = dy;
                }
                else if (variables.get(0).data[i][j][draw_time] <= min)
                {
                    data_x_size = dx; //(int)min;
                    data_y_size = dy; //(int)min;
                }
                else
                {
                    double scale;
                    if (variables.get(0).data[i][j][draw_time] >= 0)
                    {
                        scale = variables.get(0).data[i][j][draw_time] / max;
                    }
                    else
                    {
                        scale = variables.get(0).data[i][j][draw_time] / min;
                    }

                    data_x_size = (int) (dx * scale);
                    data_y_size = (int) (dy * scale);
                }

                boxColor = variables.get(0).info.getColor();
                bufferGraphics.setColor(boxColor);

                if (variables.get(0).data[i][j][draw_time] <= 0)
                {
                    bufferGraphics.fill(new Rectangle2D.Double(drawX + j * dx + (dx >> 1),
                            drawY + i * dy + (dy >> 1),
                            1, 1));
                }
                else if (variables.get(0).data[i][j][draw_time] > 0)
                {
                    bufferGraphics.fill(new Rectangle2D.Double(drawX + j * dx + ((dx - data_x_size) >> 1),
                            drawY + i * dy + ((dy - data_y_size) >> 1),
                            data_x_size, data_y_size));
                }
                //bufferGraphics.setPaintMode();
            }
        }
    }
}


