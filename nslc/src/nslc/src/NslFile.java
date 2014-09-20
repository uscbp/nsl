package nslc.src;/* SCCS  %W--- %G% -- %U% */

// Copyright: Copyright (c) 1997-2002 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

// Author: Salvador Marmol

//
// nslc.src.NslFile.java
//
//////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Vector;

public class NslFile
{

    private static Vector<NslFile> files = new Vector<NslFile>(5);

    private Vector<String> fileLines;
    private String fileName;

    public NslFile(String fileName)
    {
        this.fileName = fileName;
        fileLines = new Vector<String>(50);
        String line;
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((line = in.readLine()) != null)
            {
                fileLines.addElement(line);
            }
        }
        catch (Exception ex)
        {
            System.err.println("NSlFile [error]: An error occured while reading file " + fileName);
        }
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getLine(int line)
    {
        return fileLines.elementAt(line - 1);
    }

    public static void loadFile(String fileName)
    {
        NslFile file = new NslFile(fileName);
        files.addElement(file);
    }

    public static NslFile getFile(String fileName)
    {
        Enumeration e = files.elements();
        NslFile fileTmp;
        while (e.hasMoreElements())
        {
            fileTmp = (NslFile) e.nextElement();
            if (fileName.equals(fileTmp.fileName))
            {
                return fileTmp;
            }
        }
        return null;
    }

    public static String getLine(String fileName, int line)
    {
        Enumeration e = files.elements();
        NslFile fileTmp;
        while (e.hasMoreElements())
        {
            fileTmp = (NslFile) e.nextElement();
            if (fileName.equals(fileTmp.fileName))
            {
                return fileTmp.getLine(line);
            }
        }
        return null;
    }

}