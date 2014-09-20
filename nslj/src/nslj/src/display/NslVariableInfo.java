/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.lang.NslData;

import java.awt.*;

public class NslVariableInfo
{
    public static int LINE_STYLE_SOLID=0, LINE_STYLE_INVISIBLE=1, LINE_STYLE_DOTTED=2, LINE_STYLE_DASH_DOT=3, LINE_STYLE_DASH=4;
    // Constants

    public static final int INT = 1,
            FLOAT = 2,
            DOUBLE = 3,
            BOOLEAN = 4,
            STRING = 5;

    // Variables

    protected NslData data;
    protected String name;

    protected int type;

    protected boolean history;
    //protected boolean overwrite;

    protected Color color = Color.black;
    protected int style=LINE_STYLE_SOLID;

    protected int countDims;
    protected int dimensions[];
    protected int slicingDims[];
    protected String choiceDimensions;

    // Constructors

    public NslVariableInfo(NslData data, String name, int countDimensions)
    {
        this(data, name, DOUBLE, countDimensions);
    }

    public NslVariableInfo(NslData data, String name, int type, int countDimensions)
    {
        this.data = data;
        this.name = name;
        this.type = type;
        countDims = countDimensions;

        history = false;
        //overwrite = false;

        dimensions = new int [4];
        slicingDims = new int [2];
        slicingDims[0] = slicingDims[1] = 1;

        switch (countDims)
        {
            case 0:
                dimensions[0] = 1;
            case 1:
                dimensions[1] = 1;
            case 2:
                dimensions[2] = 1;
            case 3:
                dimensions[3] = 1;
        }
        switch (countDims)
        {
            case 4:
                setDimension(0, data.getSize1());
                setDimension(1, data.getSize2());
                setDimension(2, data.getSize3());
                setDimension(3, data.getSize4());
                break;
            case 3:
                setDimension(0, data.getSize1());
                setDimension(1, data.getSize2());
                setDimension(2, data.getSize3());
                break;
            case 2:
                setDimension(0, data.getSize1());
                setDimension(1, data.getSize2());
                break;
            case 1:
                setDimension(0, data.getSize1());
                break;
        }
    }

    // Methods

    public Object clone() throws CloneNotSupportedException
    {
        NslVariableInfo vi = new NslVariableInfo(data, name, type, countDims);
        vi.history = history;
        //vi.overwrite = overwrite;
        vi.color = color;
        vi.style = style;
        vi.slicingDims[0] = slicingDims[0];
        vi.slicingDims[1] = slicingDims[1];
        vi.choiceDimensions = this.choiceDimensions;

        return vi;
    }

    public void setDimension(int i, int dim)
    {
        if (i < countDims)
        {
            dimensions[i] = dim;
        }
    }

    public void setHistory(boolean history)
    {
        this.history = history;
    }

    /*public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }*/

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setStyle(int s)
    {
        style = s;
    }

    public void setDimensionChoice(String dimChoice)
    {
        choiceDimensions = dimChoice;
    }

    public void setSlicingPoint(int index, int point)
    {
        try
        {
            slicingDims[index] = point;
        }
        catch (Exception e)
        {
            System.err.println("NslVariableInfo: [Error] Cannot set slicing point.");
        }
    }

    public int getDimension(int i)
    {
        if (i < countDims)
        {
            int idx=i;
            //return dimensions[i];
            if(countDims==3)
            {
                if(i==0)
                {
                    if(!choiceDimensions.contains("H"))
                        idx++;
                    if(!choiceDimensions.contains("I"))
                        idx++;
                }
                else if(i==1)
                {
                    if(!choiceDimensions.contains("I"))
                        idx++;
                }
            }
            else if(countDims==4)
            {
                if(i==0)
                {
                    if(!choiceDimensions.contains("H"))
                        idx++;
                    if(!choiceDimensions.contains("I"))
                        idx++;
                    if(!choiceDimensions.contains("J"))
                        idx++;
                }
                else if(i==1)
                {
                    if(!choiceDimensions.contains("I"))
                        idx++;
                    if(!choiceDimensions.contains("J"))
                        idx++;
                }
            }
            return dimensions[idx];
        }
        else
        {
            return 1;
        }
    }

    public boolean isHistoryOn()
    {
        return history;
    }

    /*public boolean isOverwriteOn()
    {
        return overwrite;
    }*/

    public NslData getNslVar()
    {
        return data;
    }

    public String nslGetName()
    {
        return name;
    }

    public int getCountDimensions()
    {
        return countDims;
    }

    public int getType()
    {
        return type;
    }

    public Color getColor()
    {
        return color;
    }

    public int getStyle()
    {
        return style;
    }

    public String toString()
    {
        return "NslVariableInfo: " + name + ' ' + countDims + ' ' + type;
    }

    public String getDimensionChoice()
    {
        return choiceDimensions;
    }

    public int getSlicingPoint(int index)
    {
        try
        {
            return slicingDims[index];
        }
        catch (Exception e)
        {
            System.err.println("NslVariableInfo: [Error] Cannot get slicing point.");
        }
        return 0;
    }
}
