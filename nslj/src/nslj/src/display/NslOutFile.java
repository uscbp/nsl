/*  SCCS - @(#)NslOutFile.java	1.6 - 09/01/99 - 00:15:46 */

/*
 *Copyright(c) 1997 USC Brain Project. email nsl@java.usc.edu
 */

package nslj.src.display;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLArray;

import java.io.*;
import java.util.ArrayList;

/**
 * The class to implement function of saving file
 * in various file formats
 */
public final class NslOutFile
{
    static public int time = 0;

    public static boolean readFromFile(String name, double [][][]values, int start, int time_offset)
    {
        // need to handle file exception here
        try
        {
            int mrows, ncols, total_time;

            FileInputStream fin = new FileInputStream(name);
            DataInputStream din = new DataInputStream(fin);

            din.readInt();
            mrows = din.readInt();
            ncols = din.readInt();
            din.readInt();

            for (total_time = start; total_time < start + time_offset; total_time++)
            {
                for (int i = 0; i < mrows; i++)
                {
                    for (int j = 0; j < ncols; j++)
                    {
                        values[i][j][total_time - start] = din.readDouble();
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Error: File Operation Error in NslOutFile.java");
        }
        return true;
    }

    public static boolean outFromVariable(NslVariable variable, double [][][]values, int start_time, int end_time,
                                          int time_offset)
    {
        if ((start_time >= 0) && (end_time >= 0) && (time_offset >= 0))
        {

            String variable_name = variable.info.nslGetName();

            Integer intval = time_offset * (1 + (start_time / time_offset));

            String name = variable_name + intval.toString();

            for (int i = time_offset * (start_time / time_offset); i < time_offset * (end_time / time_offset); i += time_offset)
            {
                readFromFile(name, values, i, time_offset);
            }
            return true;
        }
        return false;
    }

    public static boolean outVariable(NslVariable variable, int start_time, int end_time, int offset)
    {
        int type, mrows, ncols, imagf, total_time;

        String variable_name = variable.info.nslGetName();

        String name = variable_name + ((Integer)offset).toString();

        // need to handle file exception here
        try
        {
            FileOutputStream fout = new FileOutputStream(name);
            DataOutputStream dout = new DataOutputStream(fout);

            // Big Endian, double precision, numeric data
            type = 1000;

            // Get rows and columns
            mrows = variable.info.getDimension(0);
            ncols = variable.info.getDimension(1);

            // Only real data
            imagf = 0;

            dout.writeInt(type);
            dout.writeInt(mrows);
            dout.writeInt(ncols);
            dout.writeInt(imagf);

            for (total_time = start_time; total_time <= end_time; total_time++)
            {
                for (int i = 0; i < mrows; i++)
                {
                    for (int j = 0; j < ncols; j++)
                    {
                        dout.writeDouble(variable.data[i][j][total_time]);
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("File Operation Error in NslOutFile.java");
        }
        return true;
    }

    public static boolean outToVariable(NslVariable variable, int time_offset)
    {
        int type, mrows, ncols, imagf, total_time;

        String variable_name = variable.info.nslGetName();

        Integer intval = time;

        String name = variable_name + intval.toString();

        // need to handle file exception here
        try
        {
            FileOutputStream fout = new FileOutputStream(name);
            DataOutputStream dout = new DataOutputStream(fout);

            // Big Endian, double precision, numeric data
            type = 1000;

            // Get rows and columns
            mrows = variable.info.getDimension(0);
            ncols = variable.info.getDimension(1);

            // Only real data
            imagf = 0;

            dout.writeInt(type);
            dout.writeInt(mrows);
            dout.writeInt(ncols);
            dout.writeInt(imagf);

            for (total_time = 0; total_time < time_offset; total_time++)
            {
                for (int i = 0; i < mrows; i++)
                {
                    for (int j = 0; j < ncols; j++)
                    {
                        dout.writeDouble(variable.data[i][j][total_time]);
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("File Operation Error in NslOutFile.java");
        }
        return true;
    }

    public static boolean outToMatlab(NslCanvas ndc, String fileName)
    {
        NslVariable variable = ndc.getVariable();

        // need to handle file exception here
        try
        {
            MatFileWriter writer=new MatFileWriter();
            ArrayList<MLArray> vars=new ArrayList<MLArray>();
            for(int i=0; i<variable.info.getDimension(1); i++)
            {
                String varName=variable.info.nslGetName();
                if(variable.info.getDimension(1)>1)
                    varName+="_"+i;
                double[][] data=new double[variable.info.getDimension(0)][variable.get_last_data_position()];
                for(int j=0; j<variable.info.getDimension(0); j++)
                {
                    for(int k=0; k<variable.get_last_data_position(); k++)
                        data[j][k]=variable.data[j][i][k];
                }
                vars.add(new MLDouble(varName,data));
            }
            writer.write(fileName, vars);
        }
        catch (IOException e)
        {
            System.out.println("File Operation Error in NslOutFile.java");
        }
        return true;
    }

    public static boolean outToGnuplot(NslCanvas ndc, String fileName)
    {
        NslVariable variable = ndc.getVariable();

        // need to handle file exception here
        try
        {
            for (int i = 0; i < variable.info.getDimension(0); i++)
            {
                for (int j = 0; j < variable.info.getDimension(1); j++)
                {
                    FileOutputStream fout = new FileOutputStream(fileName + i + '_' + j);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fout));
                    for (int k = 0; k < variable.get_last_data_position() - 1; k++)
                    {
                        out.write((new Float(variable.data[i][j][k])).toString() + ' ');
                        out.write("\n");
                    }
                    out.close();
                    fout.close();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("File Operation Error in NslOutFile.java");
        }
        return true;
    }

    public static boolean outToPlotmtv(NslCanvas ndc, String fileName)
    {
        return true;
    }

    public static boolean outToPLplot(NslCanvas ndc, String fileName)
    {
        return true;
    }
}
