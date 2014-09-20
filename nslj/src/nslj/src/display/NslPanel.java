/* SCCS  %W%---%G%--%U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.main.NslMain;

import javax.swing.*;
import javax.media.j3d.Canvas3D;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Vector;


public class NslPanel extends JPanel
{
    // Constants
    private static final int initialColumns = 1;

    // Variables
    private Vector<NslCanvas> canvasList;
    private Vector<NslNumericEditor> variableList;
    private GridLayout gl;
    private NslFrame frame;

    private int indexOfCurrentCanvas;

    // Constructors
    NslPanel(NslFrame frame)
    {
        super();
        this.frame = frame;
        indexOfCurrentCanvas = -1;
        canvasList = new Vector<NslCanvas>(8);
        variableList = new Vector<NslNumericEditor>(8);
        gl = new GridLayout(0, initialColumns);
        setLayout(gl);
    }

    // Methods
    public boolean setColumns(int num)
    {
        if (getComponentCount() < num)
        {
            return false;
        }

        gl.setColumns(num);
        validate();
        doLayout();
        return true;
    }

    public boolean setRows(int num)
    {
        if (getComponentCount() < 1)
        {
            return false;
        }
        gl.setRows(num);
        validate();
        doLayout();
        return true;
    }

    public void addVariable(String variableName, NslVariableInfo variableInfo)
    {
        NslNumericEditor n = new NslNumericEditor(frame, variableName, variableInfo);
        variableList.addElement(n);

        add(n);
        validate();
    }

    public boolean canvasExist(String n)
    {
        Enumeration E = canvasList.elements();
        NslCanvas c;

        while (E.hasMoreElements())
        {
            c = (NslCanvas) E.nextElement();
            if (c != null && c.getCanvasName().equals(n))
            {
                return true;
            }
        }

        return false;
    }

    public NslCanvas getCanvas(String n)
    {
        Enumeration E = canvasList.elements();
        NslCanvas c;

        while (E.hasMoreElements())
        {
            c = (NslCanvas) E.nextElement();
            if (c != null && c.getCanvasName().equals(n))
            {
                return c;
            }
        }

        return null;
    }

    public void addCanvas(NslCanvas canvas)
    {
        canvas.mouseAdapter = new CanvasMouseAdapter();
        canvas.addMouseListener(canvas.mouseAdapter);
        canvas.addMouseMotionListener(canvas.mouseAdapter);

        boolean found=false;
        for(int i=0; i<canvasList.size(); i++)
        {
            if(canvasList.get(i).getCanvasName().equals(canvas.getCanvasName()))
            {
                for(int j=0; j<getComponentCount(); j++)
                {
                    if(getComponent(j).equals(canvasList.get(i)))
                    {
                        remove(canvasList.get(i));
                        add(canvas, j);
                        break;
                    }
                }
                canvasList.setElementAt(canvas, i);
                found=true;
                break;
            }
        }
        if(!found)
        {
            canvasList.addElement(canvas);

            add(canvas);


            int plotCount = getComponentCount();

            if (plotCount > 0)
            {
                int colCount = (plotCount > 2) ? 3 : plotCount;
                setColumns(colCount);
            }
            else
            {
                setColumns(1);
            }
        }
    }

    public NslCanvas addCanvas(String canvasName, NslVariableInfo variableInfo, String plotType)
            throws CanvasCreationException
    {
        NslCanvas canvas;
        try
        {
            String typeName;

            typeName = "nslj.src.display.Nsl" + plotType + "Canvas";
            Class canvasType = Class.forName(typeName);

            // todo: do we really need paramTypes?
            Class paramTypes[] = new Class[3];
            paramTypes[0] = Class.forName("nslj.src.display.NslFrame");
            paramTypes[1] = Class.forName("java.lang.String");
            paramTypes[2] = Class.forName("nslj.src.display.NslVariableInfo");

            Constructor canvasConstructor = canvasType.getConstructor(paramTypes);

            Object params[] = new Object[3];
            params[0] = frame;
            params[1] = canvasName;
            params[2] = variableInfo;

            canvas = (NslCanvas) canvasConstructor.newInstance(params);
            canvas.setMinMax(0,1);

            String fn = frame.getFontName() == null ? "Times" : frame.getFontName();
            String bg = frame.getBackgroundColor() == null ? "white" : frame.getBackgroundColor();
            String fg = frame.getForegroundColor() == null ? "black" : frame.getForegroundColor();

            canvas.setFont(new Font(fn, Font.PLAIN, 12));
            canvas.setBackground(NslColor.getColor(bg));
            canvas.setForeground(NslColor.getColor(fg));

            addCanvas(canvas);
        }
        catch (InvocationTargetException e)
        {
            System.err.println("NslPanel: [Error] Could not create plot for " + canvasName);
            System.err.println("NslPanel: [Posibilities] not enough memory, tmax is 0, or newInstance statement malformed.");
            e.printStackTrace();
            throw new CanvasCreationException();
        }
        catch (Exception e)
        {
            System.err.println("NslPanel: [Error] Could not create plot for " + canvasName);
            e.printStackTrace();
                throw new CanvasCreationException();
        }
        return canvas;
    }

    public NslCanvas addCanvas(String canvasName, NslVariableInfo variableInfo, String plotType, double min,
                               double max) throws CanvasCreationException
    {
        NslCanvas canvas=addCanvas(canvasName, variableInfo, plotType);
        canvas.setMinMax(min, max);
        return canvas;
    }

    public NslCanvas addUserCanvas(String canvasName,
                                   NslVariableInfo variableInfo,
                                   String libPath, String plotType, double min, double max)
            throws CanvasCreationException
    {
        NslCanvas c;
        try
        {
            c = addUserCanvas(canvasName, variableInfo, libPath, plotType);
            c.setMinMax(min, max);
        }
        catch (Exception e)
        {
            throw new CanvasCreationException();
        }
        return c;
    }                                                                                                                        

    public void addUserPanel(Panel panel)
    {
        add(panel);
        int plotCount = getComponentCount();
        if (plotCount > 0)
        {
            int colCount = (plotCount > 2) ? 3 : plotCount;
            setColumns(colCount);
        }
        else
        {
            setColumns(1);
        }
        validate();// the layout
    }

    public NslCanvas addUserCanvas(String canvasName,
                                   NslVariableInfo variableInfo,
                                   String libPath, String plotType)
            throws CanvasCreationException
    {
        NslCanvas c;
        try
        {
            String typeName;
            typeName = "Nsl" + plotType + "Canvas";
            Class canvasType;
            if (NslMain.modelPath == null)
            {
                canvasType = Class.forName(typeName);
            }
            else
            {
                URL pathURL = (new File(NslMain.modelPath)).toURI().toURL();
                URLClassLoader loader = URLClassLoader.newInstance(new URL[]{pathURL});
                canvasType = loader.loadClass(libPath.replace(File.separator, ".").replace("/", ".") + '.' + typeName);
            }
            Class paramTypes[] = new Class[4];
            paramTypes[0] = Class.forName("java.lang.String");
            paramTypes[1] = Class.forName("nslj.src.lang.NslModule");
            paramTypes[2] = Class.forName("nslj.src.display.NslFrame");
            paramTypes[3] = Class.forName("nslj.src.display.NslVariableInfo");
            Constructor canvasConstructor = canvasType.getConstructor(paramTypes);
            Object params[] = new Object[4];
            params[0] = canvasName;
            params[1] = variableInfo != null ?
                    variableInfo.getNslVar().nslGetParentModule() :
                    null;
            params[2] = frame;
            params[3] = variableInfo;

            String fn = frame.getFontName() == null ? "Times" : frame.getFontName();
            String bg = frame.getBackgroundColor() == null ? "white" : frame.getBackgroundColor();
            String fg = frame.getForegroundColor() == null ? "black" : frame.getForegroundColor();

            c = (NslCanvas) canvasConstructor.newInstance(params);
            c.setFont(new Font(fn, Font.PLAIN, 12));
            c.setBackground(NslColor.getColor(bg));
            c.setForeground(NslColor.getColor(fg));
            addCanvas(c);
        }
        catch (InvocationTargetException e)
        {
            System.err.println("Error: Canvas " + plotType + " was not found");
            e.printStackTrace();
            throw new CanvasCreationException();
        }
        catch (Exception e)
        {
            System.err.println("Something weired happened!");
            e.printStackTrace();
            throw new CanvasCreationException();
        }
        return c;
    }

    public void removeCanvas() throws NoCanvasSelectedException
    {
        if (indexOfCurrentCanvas == -1)
        {
            System.out.println("NslPanel: Error: not canvas selected.");
            throw new NoCanvasSelectedException();
        }
        remove(canvasList.elementAt(indexOfCurrentCanvas));
        canvasList.removeElementAt(indexOfCurrentCanvas);
        indexOfCurrentCanvas = -1;
        frame.plotSelectionChanged(null,true);
        GridLayout l = (GridLayout) getLayout();
        int compCount = getComponentCount();
        if (compCount > 0 && compCount < l.getColumns())
        {
            l.setColumns(compCount);
        }
        validate(); // the layout
        repaint();
    }

    /*public void print(Graphics g)
    {
        for (int i = 0; i < canvasList.size(); i++)
        {
            canvasList.elementAt()
            canvasList.elementAt(i).printAll(g);
        }
    }*/

    public void setCanvasProperties() throws NoCanvasSelectedException
    {
        try
        {
            String canvasType =
                    canvasList.elementAt(indexOfCurrentCanvas).getClass().getName() + "Property";
            Class canvasClass = Class.forName(canvasType);
            Class typeList[] = new Class[1];
            typeList[0] = frame.getClass();
            Constructor propertyConstructor =
                    canvasClass.getConstructor(typeList);
            Object param[] = new Object [1];
            param[0] = frame;
            Dialog dp = (Dialog) propertyConstructor.newInstance(param);
            dp.setVisible(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new NoCanvasSelectedException();
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
        }

    }

    public void changeCanvas(String newType) throws NoCanvasSelectedException
    {
        if (indexOfCurrentCanvas == -1)
        {
            System.out.println("NslPanel:Warning: No Canvas Selected.");
            throw new NoCanvasSelectedException();
        }
        NslCanvas c = canvasList.elementAt(indexOfCurrentCanvas);
        NslCanvas tmp = c.copy(newType);
        if (tmp == null)
        {
            System.out.println("NslPanel:Error: Could not create canvas type: " + newType);
            return;
        }
        canvasList.setElementAt(tmp, indexOfCurrentCanvas);

        tmp.setBackground(Color.cyan);
        remove(c);
        add(tmp, indexOfCurrentCanvas);
        tmp.repaint();
        tmp.mouseAdapter = new CanvasMouseAdapter();
        tmp.addMouseListener(tmp.mouseAdapter);
        tmp.addMouseMotionListener(tmp.mouseAdapter);
    }

    private class CanvasMouseAdapter extends MouseAdapter
    {

        public void mousePressed(MouseEvent evt)
        {

            switch (evt.getClickCount())
            {
                case 1:
                {
                    // Repaint the new active canvas and previous canvas
                    NslCanvas canvas;
                    if (indexOfCurrentCanvas != -1)
                    { // unselect old canvas
                        canvas = canvasList.elementAt(indexOfCurrentCanvas);
                        if (canvas == evt.getSource())
                        {
                            String bg = frame.getBackgroundColor();
                            if (bg == null)
                            {
                                bg = "white";
                            }
                            canvas.setBackground(NslColor.getColor(bg));
                            indexOfCurrentCanvas = -1;
                            frame.plotSelectionChanged(null,true); //99.5.11 aa
                            canvas.update();
                            return;
                        }
                        canvas.setBackground(Color.white);
                        canvas.update();
                    }
                    indexOfCurrentCanvas = canvasList.indexOf(evt.getSource());

                    if(indexOfCurrentCanvas==-1 && evt.getSource() instanceof Canvas3D)
                        indexOfCurrentCanvas=canvasList.indexOf(((Canvas3D)evt.getSource()).getParent());
                    if (indexOfCurrentCanvas == -1)
                    {  //
                        System.out.println("NslPanel:current canvas is -1");
                        return;
                    }
                    else
                    {   // if another canvas is selected change plot type and highlight
                        canvas = canvasList.elementAt(indexOfCurrentCanvas);
                        canvas.setBackground(Color.cyan);
                        frame.plotSelectionChanged(canvas,true);
                        canvas.update();
                    }
                }
                break;

                case 2:
                {
                    try
                    {
                        zoomCanvas();
                    }
                    catch (Exception e)
                    {
                        System.err.println("NslPanel:Exception:" + e.toString());
                    }
                }
                break;
            }
        }//end mouse pressed

        public void mouseReleased(MouseEvent e)
        {
            if(e.getSource() instanceof Nsl3dCanvas)
            {
                ((Nsl3dCanvas)e.getSource()).mouseReleased(e);
            }
        }

        public void mouseDragged(MouseEvent e)
        {
            if(e.getSource() instanceof Nsl3dCanvas)
            {
                ((Nsl3dCanvas)e.getSource()).mouseDragged(e);
            }
        }

        public void mouseMoved(MouseEvent e)
        {
            if(e.getSource() instanceof NslThermalCanvas)
            {
                ((NslThermalCanvas)e.getSource()).mouseMoved(e);
            }
            else if(e.getSource() instanceof NslGrayscaleCanvas)
            {
                ((NslGrayscaleCanvas)e.getSource()).mouseMoved(e);
            }
        }
    } //end adapter

    public void zoomCanvas()
    {
        if (indexOfCurrentCanvas > -1)
        {
            NslCanvas c = canvasList.elementAt(indexOfCurrentCanvas);
            String canvasName = c.getClass().getName();
            NslCanvas tmp;
            if (canvasName.equals("nslj.src.display.NslTemporalCanvas"))
                tmp = c.copy("Temporal");
            else if (canvasName.equals("nslj.src.display.NslAreaCanvas"))
                tmp = c.copy("Area");
            else if (canvasName.equals("nslj.src.display.NslGrayscaleCanvas"))
                tmp = c.copy("Grayscale");
            else if (canvasName.equals("nslj.src.display.NslThermalCanvas"))
                tmp = c.copy("Thermal");
            else if (canvasName.equals("nslj.src.display.NslHistogramCanvas"))
                tmp = c.copy("Histogram");
            else if (canvasName.equals("nslj.src.display.NslRasterCanvas"))
                tmp = c.copy("Raster");
            else if (canvasName.equals("nslj.src.display.NslSpatialCanvas"))
                tmp = c.copy("Spatial");
            else
            {
                System.err.println("NslPanel:Error: Only know how to zoom Temporal, Histogram, Raster, Spatial, Area, Grayscale, or Thermal Level graphs.");
                return;
            }
            tmp.doubleBuffering=true;
            c.setDrawSize();

            NslZoomFrame zoomFrame = new NslZoomFrame(tmp);

            zoomFrame.setSize(450, 450);
            zoomFrame.setVisible(true);
        }
    }

    public void init()
    {
        if (canvasList == null || variableList == null)
        {
            return;
        }
        for (int i = 0; i < canvasList.size(); i++)
        {
            canvasList.elementAt(i).initCanvas();
        }

        for (int i = 0; i < variableList.size(); i++)
        {
            variableList.elementAt(i).initCanvas();
        }

    }

    public void initEpoch()
    {
        if (canvasList == null || variableList == null)
        {
            return;
        }

        for (int i = 0; i < canvasList.size(); i++)
        {
            canvasList.elementAt(i).initEpochCanvas();
        }

        for (int i = 0; i < variableList.size(); i++)
        {
            variableList.elementAt(i).initEpochCanvas();
        }
    }

    public void endEpoch(int epoch)
    {
        for (int i = 0; i < canvasList.size(); i++)
        {
            canvasList.elementAt(i).endEpochCanvas(epoch);
        }

        for (int i = 0; i < variableList.size(); i++)
        {
            variableList.elementAt(i).endEpochCanvas(epoch);
        }
    }

    public void collectTrial()
    {
        if (canvasList == null || variableList == null)
        {
            return;
        }
        for (int i = 0; i < canvasList.size(); i++)
        {
            canvasList.elementAt(i).collectTrial();
        }

        for (int i = 0; i < variableList.size(); i++)
        {
            variableList.elementAt(i).collect();
        }

    }

    public void collectEpoch()
    {
        if (canvasList == null || variableList == null)
        {
            return;
        }
        for (int i = 0; i < canvasList.size(); i++)
        {
            canvasList.elementAt(i).collectEpoch();
        }

        for (int i = 0; i < variableList.size(); i++)
        {
            variableList.elementAt(i).collect();
        }

    }

    public NslCanvas getCurrentCanvas() throws NoCanvasSelectedException
    {
        try
        {
            return canvasList.elementAt(indexOfCurrentCanvas);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new NoCanvasSelectedException();
        }
    }

    public Vector<NslCanvas> getCanvasList()
    {
        return canvasList;
    }

    public int getNumColumns()
    {
        return gl.getColumns();
    }
}
