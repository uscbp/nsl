/* SCCS  @(#)NslSpatialCanvas.java	1.9---09/01/99--00:15:48 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslSpatialCanvas.java,v $
// Revision 1.1  1997/11/06 03:18:57  erhan
// NSL3.0.b
//
// Revision 1.4  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import nslj.src.main.NslMain;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

public class NslSpatialCanvas extends NslCanvas
{
    public static final int MODE_COLOR=0;
    public static final int MODE_GREY=1;
    public static final int MODE_BW=2;
    
    private int mode=MODE_COLOR;
    private float fixedSaturation=1.0f;
    private float fixedBrightness=1.0f;
    private int zScale=25;
    protected int[] xtemp;
    protected int[] ytemp;
    protected float[] ztemp;
    protected int[] polyX=new int[5];
    protected int[] polyY=new int[5];
    protected double[] pt_orig=new double[3];

    protected double yMultiplier;
    protected double yBase;
    static protected int xMarkPixelDistance = 40;
    static protected int yMarkPixelDistance = 40;

    public NslSpatialCanvas()
    {
        super();
        if(y_dimension==1)
            paintMode=99;
        else
            paintMode=-1;
    }

    public NslSpatialCanvas(NslFrame nslDisplayFrame, NslCanvas pre)
    {
        this(nslDisplayFrame, pre.canvas_name, pre.variables.get(0).info, pre.temporalMode, pre.getMin(), pre.getMax());
    }

    public NslSpatialCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info)
    {
        super(nslDisplayFrame, full_name, data_info);
        if(y_dimension==1)
            paintMode=99;
        else
            paintMode=-1;
    }

    public NslSpatialCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode)
    {
        this(nslDisplayFrame, full_name, data_info);
        this.temporalMode=temporalMode;
    }

    public NslSpatialCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, double dmin,
                            double dmax)
    {
        super(nslDisplayFrame, full_name, data_info, dmin, dmax);
        if(y_dimension==1)
            paintMode=99;
        else
            paintMode=-1;
    }

    public NslSpatialCanvas(NslFrame nslDisplayFrame, String full_name, NslVariableInfo data_info, int temporalMode,
                            double dmin, double dmax)
    {
        this(nslDisplayFrame, full_name, data_info, dmin, dmax);
        this.temporalMode=temporalMode;
    }
    
    public void init(NslVariableInfo varInfo)
    {
        super.init(varInfo);
        if(variables.get(0).info.getDimension(1)>1)
        {
            xtemp = new int[x_dimension * y_dimension];
            ytemp = new int[x_dimension * y_dimension];
            ztemp = new float[x_dimension * y_dimension];
        }
        if(max>1.0)
            df=new DecimalFormat("0.0");
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
            dx = drawW / variables.get(0).info.getDimension(1);
            dy = drawH / variables.get(0).info.getDimension(0);
            if(variables.get(0).info.getDimension(1)>1)
                drawX=10;
        }
        else
        {
            dx=1;
            dy=1;
        }
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int m)
    {
        mode=m;
    }

    protected synchronized void paintCanvas(Graphics2D g)
    {
        reset=true;
        super.paintCanvas(g);
    }
    
    public void paintData(Graphics2D bufferGraphics)
    {
        if(variables.get(0).info.getDimension(1)==1)
            drawVectorData(bufferGraphics);
        else
        {
            drawMatrixData(bufferGraphics);
            if(showLegend && mode!=MODE_BW && (reset || !NslMain.doubleBuffering))
                drawLegend(bufferGraphics);
        }
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
            if(mode==MODE_COLOR)
                bufferGraphics.setColor(Color.getHSBColor(getColor(y), fixedSaturation, fixedBrightness));
            else if(mode==MODE_GREY)
            {
                int colorLevel=getShade(y);
                bufferGraphics.setColor(new Color(255-colorLevel, 255-colorLevel, 255-colorLevel));
            }
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

    protected int getShade(double val)
    {
        int colorLevel=(int)(((val- min)/(max - min))*255.0);
        if(colorLevel>255)
            colorLevel=255;
        else if(colorLevel<0)
            colorLevel=0;
        return colorLevel;
    }

    protected float getColor(double val)
    {
        float degreesOfHue = 240.0f-((float)((val- min)/(max - min))*240.0f);
        if(degreesOfHue<0.0)
            degreesOfHue=0.0f;
        else if(degreesOfHue>360.0)
            degreesOfHue=360.0f;
        return degreesOfHue/360.0f;
    }

    private void drawMatrixData(Graphics2D bufferGraphics)
    {
        bufferGraphics.setColor(Color.black);

        //draw variable
        double maxX=0,maxY=0,minX=Double.POSITIVE_INFINITY,minY=Double.POSITIVE_INFINITY;

        for (int i = 0; i < x_dimension; i++)
        {
            for (int j = 0; j < y_dimension; j++)
            {
                pt_orig[0] = i;
                pt_orig[1] = j;
                pt_orig[2] = (zScale*(variables.get(0).data[i][j][variables.get(0).last_data_pos]- min))/(max - min);
                project(pt_orig);
                pt_orig[0] = drawX + pt_orig[0] * dx;
                pt_orig[1] = drawY + pt_orig[1] * dy;
                Rotatex(pt_orig, 3.14156 / 4.0);
                xtemp[i * x_dimension + j] = (int) pt_orig[0];
                ytemp[i * x_dimension + j] = (int) pt_orig[1];
                ztemp[i * x_dimension + j] = (float)((variables.get(0).data[i][j][variables.get(0).last_data_pos]- min)/(max - min));

                if(xtemp[i*x_dimension+j]<minX)
                    minX=xtemp[i*x_dimension+j];
                if(xtemp[i*x_dimension+j]>maxX)
                    maxX=xtemp[i*x_dimension+j];
                if(ytemp[i*x_dimension+j]<minY)
                    minY=ytemp[i*x_dimension+j];
                if(ytemp[i*x_dimension+j]>maxY)
                    maxY=ytemp[i*x_dimension+j];
            }
        }

        int xOffset=(int)((canvasWidth-(maxX-minX))/((showLegend && mode!=MODE_BW)? 6 : 2)-minX);
        int yOffset=(int)((canvasHeight-(maxY-minY))/2-minY);

        for (int i = 1; i < x_dimension; i++)
        {
            for (int j = 1; j < y_dimension; j++)
            {
                float meanZ=(ztemp[i*x_dimension+j]+ztemp[i*x_dimension+j-1]+ztemp[(i-1)*x_dimension+j-1]+
                       ztemp[(i-1)*x_dimension+j])/4;

                polyX[0]=xtemp[i * x_dimension + j]+xOffset;
                polyX[1]=xtemp[i * x_dimension + j - 1]+xOffset;
                polyX[2]=xtemp[(i - 1) * x_dimension + j - 1]+xOffset;
                polyX[3]=xtemp[(i - 1) * x_dimension + j]+xOffset;
                polyX[4]=xtemp[i * x_dimension + j]+xOffset;
                polyY[0]=ytemp[i * x_dimension + j]+yOffset;
                polyY[1]=ytemp[i * x_dimension + (j - 1)]+yOffset;
                polyY[2]=ytemp[(i - 1) * x_dimension + j - 1]+yOffset;
                polyY[3]=ytemp[(i - 1) * x_dimension + j]+yOffset;
                polyY[4]=ytemp[i * x_dimension + j]+yOffset;

                setColor(bufferGraphics, meanZ);
                bufferGraphics.fillPolygon(polyX,polyY,5);
                bufferGraphics.setColor(Color.black);
                bufferGraphics.drawPolygon(polyX,polyY,5);
            }
        }
        bufferGraphics.setColor(Color.black);
    }

    public void drawRectangle(Graphics2D bufferGraphics)
    {
        // draw box
        bufferGraphics.setColor(Color.black);
        bufferGraphics.draw(new Rectangle2D.Double(drawX, drawY, drawW, drawH));
    }

    private void drawVectorData(Graphics2D bufferGraphics)
    {
        // parameters for determining marks and labels on the display
        drawTicksAndLabels(bufferGraphics);

        for(int i=0; i<variables.size(); i++)
        {
            for (int j = 0; j < x_dimension; j++)
            {
                addPoint(bufferGraphics, i, j);
            }
        }
    }

    private void drawTicksAndLabels(Graphics2D bufferGraphics)
    {
        bufferGraphics.setColor(Color.black);

        // parameters for determining marks and labels on the display
        yBase = drawH + drawY;
        double xMarkSize = canvasHeight / 60 < 1 ? 1 : 3;
        int noXMarks = x_dimension;
        double xMarkDistance = (double)drawW / (noXMarks > 0 ? noXMarks : 1);

        if ("Zoom".equals(canvas_type) || (showLegend && mode!=MODE_BW))
        {
            bufferGraphics.setFont(new Font("Courier", Font.PLAIN, 12));
        }

        // draw x marks
        for (int x = 0; x < x_dimension; x++)
        {
            double xTickPosition=drawX+(x*xMarkDistance);
            bufferGraphics.draw(new Line2D.Double(xTickPosition, yBase-xMarkSize, xTickPosition, yBase + xMarkSize));

            if ("Zoom".equals(canvas_type) || (showLegend && mode!=MODE_BW))
            {
                FontMetrics fm = bufferGraphics.getFontMetrics();
                if(fm.stringWidth(""+x_dimension)<xMarkDistance || x==0 || x==(int)x_dimension/2 || x==x_dimension-1)
                {
                    String f=""+x;
                    float labelXPosition = (float)(xMarkDistance/2+drawX+x*xMarkDistance) - (fm.stringWidth(f) >> 1);
                    bufferGraphics.drawString(f, labelXPosition, (float)(yBase + xMarkSize + fm.getHeight()));
                }
            }
        }

        double yMarkSize = canvasWidth / 40 < 1 ? 1 : 3;
        int noYMarks = drawH / yMarkPixelDistance;
        yMultiplier = drawH/(max - min);
        double yMarkValueDy = (max - min) / (noYMarks > 0 ? noYMarks : 1);

        // draw y marks
        boolean showLabel=false;
        for (double y = min; y < max; y += yMarkValueDy, showLabel = !showLabel)
        {
            bufferGraphics.draw(new Line2D.Double(drawX - yMarkSize, yBase-((y-min) * yMultiplier), drawX + yMarkSize,
                    yBase-((y-min) * yMultiplier)));

            if (("Zoom".equals(canvas_type) || (showLegend && mode!=MODE_BW)) && showLabel)
            {
                String f = df.format(y);
                FontMetrics fm = bufferGraphics.getFontMetrics();
                float labelYPosition = (float)(yBase-((y-min) * yMultiplier)+fm.getHeight()/2);
                bufferGraphics.drawString(f, (float)(drawX-yMarkSize-fm.stringWidth(f)), labelYPosition);
            }
        }
    }

    protected void addPoint(Graphics2D g, int var, int x)
    {
        g.setColor(variables.get(var).info.getColor());

        if(x>0)
            g.draw(new Line2D.Double(drawX + x*((double)drawW/x_dimension),drawY+drawH-((variables.get(var).data[x-1][0][0]-min)*yMultiplier),
                    drawX + x*((double)drawW/x_dimension),drawY+drawH-((variables.get(var).data[x][0][0]-min)*yMultiplier)));

        g.draw(new Line2D.Double(drawX + x*((double)drawW/x_dimension), drawY+drawH-((variables.get(var).data[x][0][0]-min)*yMultiplier),
                drawX + (x+1)*((double)drawW/x_dimension), drawY+drawH-((variables.get(var).data[x][0][0]-min)*yMultiplier)));
    }

    private void setColor(Graphics2D bufferGraphics, float zValue)
    {
        switch(mode)
        {
            case MODE_COLOR:
                float degreesOfHue = 240.0f-(zValue*240.0f);
                if(degreesOfHue<0.0)
                    degreesOfHue=0.0f;
                else if(degreesOfHue>360.0)
                    degreesOfHue=360.0f;
                bufferGraphics.setColor(Color.getHSBColor(degreesOfHue/360.0f, fixedSaturation, fixedBrightness));
                break;
            case MODE_GREY:
                int colorLevel=(int)(((zValue- min)/(max - min))*255.0);
                if(colorLevel>255)
                    colorLevel=255;
                else if(colorLevel<0)
                    colorLevel=0;
                bufferGraphics.setColor(new Color(255-colorLevel, 255-colorLevel, 255-colorLevel));
                break;
            default:
                bufferGraphics.setColor(Color.white);
                break;
        }
    }

    public static void calculate_center(double[] center)
    {
        center[0]=4;
        center[1]=4;
        center[2]=-2;
        project(center);
    }

    // pt_orig is the 3d original point (z is the value of the variable
    // returns the x,y of the projection
    public static void project(double pt_orig[])
    {
        double k = (8.0 - (pt_orig[0] + pt_orig[1] + pt_orig[2])) / 3.0;

        for (int i = 0; i < 3; i++)
        {
            pt_orig[i] = pt_orig[i] + k;
        }
    }

    private static void Rotate2d(double []coord, double angle, int xof, int yof)
    {
        coord[0] = coord[0] * Math.cos(angle) - coord[1] * Math.sin(angle) + (xof * (1 - Math.cos(angle)) +
                                                                              yof * Math.sin(angle));
        coord[1] = coord[0] * Math.sin(angle) + coord[1] * Math.cos(angle) + (yof * (1 - Math.cos(angle)) -
                                                                              xof * Math.sin(angle));
    }

    private static void Rotatex(double []coord, double angle)
    {
        coord[1]=coord[1] * Math.cos(angle) - coord[2] * Math.sin(angle);
        coord[2]=coord[1] * Math.sin(angle) + coord[2] * Math.cos(angle);
    }

    private static void Rotatez(double []coord, double angle)
    {
        coord[0] = coord[0] * Math.cos(angle) - coord[1] * Math.sin(angle);
        coord[1] = coord[0] * Math.cos(angle) + coord[1] * Math.sin(angle);
    }
}
