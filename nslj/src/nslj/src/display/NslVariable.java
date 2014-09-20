/* SCCS  %W%---%G%--%U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslVariable.java,v $
// Revision 1.8  1998/01/30 20:02:29  erhan
// ver 5
//
// Revision 1.4  1997/05/09 22:30:26  danjie
// add some comments and Log
//
//--------------------------------------


package nslj.src.display;

import nslj.src.lang.*;
import nslj.src.system.NslSystem;


/**
 * The Nsl Variable
 * Maintain data and properties which are needed for displaying the variable
 * in various canvases
 *
 * @author Danjie Pan
 * @version 1.0
 */

public class NslVariable
{
    // Variables
    public NslVariableInfo info;

    // Other
    public float  [][][] data;    //temporally set to public for testing

    public static NslSystem system;

    public int last_data_pos;
    public int start = 0;
    public int absolute_last_data_pos;

    static public int time_interval = 5000; //temporally defined

    public int totalSize;

    public NslVariable(NslCanvas nslDisplayCanvas, NslVariableInfo vi)
    {
        info = vi;
        if(nslDisplayCanvas!=null)
            init(nslDisplayCanvas.maxDataPos);

        collect();
    }

    public void init(int maxDataPos)
    {
        if (info.isHistoryOn())
        {
            totalSize = maxDataPos + 2;
        }
        else
        {
            totalSize = 1;
        }

        data = new float[info.getDimension(0)][info.getDimension(1)][totalSize];

        last_data_pos = 0; //-1;
        start = 0;
        absolute_last_data_pos = 0;//-1;
    }

    public void set_tmin(double ti)
    {
    }

    public void set_tmax(double ta)
    {
    }

    public void set_ymin(double ymin)
    {
    }

    public void set_ymax(double ymax)
    {
    }

    public void set_area_ymin(float ymin)
    {
    }

    public void set_area_ymax(float ymax)
    {
    }

    public void set_last_data_pos(int ldp)
    {
        last_data_pos = ldp;
    }

    public int get_last_data_position()
    {
        return last_data_pos;
    }

    public int get_absolute_last_data_position()
    {
        return absolute_last_data_pos;
    }

    public void collectTrial()
    {
        if (NslSystem.init_run_char=='B' || NslSystem.init_run_char=='D' || system.getFinishedCycles()==0)
        {
            last_data_pos = absolute_last_data_pos = 0;
        }
        else
        {
            last_data_pos = absolute_last_data_pos = system.getFinishedCycles() % totalSize;
        }
        collect();
    }

    public void collectEpoch()
    {
        if (NslSystem.init_run_char=='B' || NslSystem.init_run_char=='D')
        {
            last_data_pos = absolute_last_data_pos = 0;
        }
        else
        {
            last_data_pos = absolute_last_data_pos = system.getFinishedEpochs()+1;
        }
        collect();
    }

    private void collect()
    {
        // Now for the case where we are just running and not at end
        int xDim = info.getDimension(0);
        int yDim = info.getDimension(1);

        if (info.getCountDimensions() == 0)
        {
            data[0][0][last_data_pos]=((NslNumeric0)info.getNslVar()).getfloat();
        }
        else if (info.getCountDimensions() == 1)
        {
            for (int i = 0; i < xDim; i++)
            {
                data[i][0][last_data_pos] = ((NslNumeric1) info.getNslVar()).getfloat(i);
            }
        }
        else if (info.getCountDimensions() == 2)
        {
            for (int i = 0; i < xDim; i++)
            {
                for (int j = 0; j < yDim; j++)
                {
                    data[i][j][last_data_pos] = ((NslNumeric2)info.getNslVar()).getfloat(i, j);
                }
            }
        }
        else if (info.getCountDimensions() ==3)
        {
            int zDim=info.getDimension(2);
            if(info.choiceDimensions!=null && info.choiceDimensions.contains("X"))
            {
                for(int i=0; i<xDim; i++)
                {
                    if(info.choiceDimensions.contains("Y"))
                    {
                        for(int j=0; j<yDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric3)info.getNslVar()).getfloat(i, j, info.slicingDims[0]);
                        }
                    }
                    else if(info.choiceDimensions.contains("Z"))
                    {
                        for(int j=0; j<zDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric3)info.getNslVar()).getfloat(i, info.slicingDims[0], j);
                        }
                    }
                    else
                        data[i][0][last_data_pos] = ((NslNumeric3)info.getNslVar()).getfloat(i, info.slicingDims[0], info.slicingDims[1]);
                }
            }
            else if(info.choiceDimensions!=null && info.choiceDimensions.contains("Y"))
            {
                for(int i=0; i<yDim; i++)
                {
                    if(info.choiceDimensions.contains("Z"))
                    {
                        for(int j=0; j<zDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric3)info.getNslVar()).getfloat(info.slicingDims[0], i, j);
                        }
                    }
                    else
                        data[i][0][last_data_pos] = ((NslNumeric3)info.getNslVar()).getfloat(info.slicingDims[0], i, info.slicingDims[1]);
                }
            }
            else if(info.choiceDimensions!=null && info.choiceDimensions.contains("Z"))
            {
                for(int i=0; i<zDim; i++)
                {
                    data[i][0][last_data_pos] = ((NslNumeric3)info.getNslVar()).getfloat(info.slicingDims[0], info.slicingDims[1], i);
                }
            }
        }
        else if(info.getCountDimensions()==4)
        {
            int zDim=info.getDimension(2);
            int zzDim=info.getDimension(3);
            if(info.choiceDimensions!=null && info.choiceDimensions.contains("W"))
            {
                for(int i=0; i<xDim; i++)
                {
                    if(info.choiceDimensions.contains("X"))
                    {
                        for(int j=0; j<xDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric4)info.getNslVar()).getfloat(i, j, info.slicingDims[0], info.slicingDims[1]);
                        }
                    }
                    else if(info.choiceDimensions.contains("Y"))
                    {
                        for(int j=0; j<zDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric4)info.getNslVar()).getfloat(i, info.slicingDims[0], j, info.slicingDims[1]);
                        }
                    }
                    else if(info.choiceDimensions.contains("Z"))
                    {
                        for(int j=0; j<zzDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric4)info.getNslVar()).getfloat(i, info.slicingDims[0], info.slicingDims[1], j);
                        }
                    }
                }
            }
            if(info.choiceDimensions!=null && info.choiceDimensions.contains("X"))
            {
                for(int i=0; i<yDim; i++)
                {
                    if(info.choiceDimensions.contains("Y"))
                    {
                        for(int j=0; j<zDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric4)info.getNslVar()).getfloat(info.slicingDims[0], i, j, info.slicingDims[1]);
                        }
                    }
                    else if(info.choiceDimensions.contains("Z"))
                    {
                        for(int j=0; j<zzDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric4)info.getNslVar()).getfloat(info.slicingDims[0], i, info.slicingDims[1], j);
                        }
                    }
                }
            }
            else if(info.choiceDimensions!=null && info.choiceDimensions.contains("Y"))
            {
                for(int i=0; i<zDim; i++)
                {
                    if(info.choiceDimensions.contains("Z"))
                    {
                        for(int j=0; j<zzDim; j++)
                        {
                            data[i][j][last_data_pos] = ((NslNumeric4)info.getNslVar()).getfloat(info.slicingDims[0], info.slicingDims[1], i, j);
                        }
                    }
                }
            }
        }
    } // end collect

    public double getElement(int x, int y)
    {
        return ((NslNumeric2)info.getNslVar()).getdouble(x, y);
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }
}