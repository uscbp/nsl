/* SCCS  @(#)NslOutCanvas.java	1.10---09/01/99--00:15:51 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import nslj.src.lang.NslHierarchy;
import nslj.src.lang.NslModule;
import nslj.src.nsls.Executive;
import nslj.src.system.NslSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;

public class NslOutCanvas extends NslCanvas
{
    protected NslSystem system;
    protected NslModule parentModule;
    private Graphics2D bufferGraphics;
    private boolean canvasIsInitialized;

    public NslOutCanvas(String full_name, NslHierarchy parent)
    {
        super(null, full_name, null);

        system = Executive.system;
        parentModule = (NslModule) parent;

        nslCreateCanvas();
    }

    public NslOutCanvas(String full_name, NslHierarchy parent, NslFrame f,
                        NslVariableInfo varInfo)
    {
        super(f, full_name, varInfo);

        system = nslj.src.nsls.Executive.system;
        parentModule = (NslModule) parent;

        nslCreateCanvas();
    }

    public NslOutCanvas(String full_name,
                        NslVariableInfo varInfo)
    {
        super(null, full_name, varInfo);

        system = nslj.src.nsls.Executive.system;

        nslCreateCanvas();
    }

    public NslOutCanvas(NslFrame frame, String name, NslVariableInfo vi)
    {
        super(frame, name, vi);

        system = Executive.system;
    }

    public void makeInst(String full_name, NslHierarchy parent)
    {
    }

    public NslHierarchy nslGetParentModule()
    {
        return parentModule;
    }

    public void nslSetColor(NslColor color)
    {
        bufferGraphics.setColor(color);
    }

    public void nslSetColor(String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
    }

    public void nslDrawLine(double x, double y, double w, double h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Line2D.Double(x, y, w, h));
    }

    public void nslDrawLine(float x, float y, float w, float h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Line2D.Float(x, y, w, h));
    }

    public void nslDrawLine(int x, int y, int w, int h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Line2D.Double(x, y, w, h));
    }

    public void nslDrawLine(int x, int y, int w, int h)
    {
        bufferGraphics.draw(new Line2D.Double(x, y, w, h));
    }

    public void nslDrawLine(double x, double y, double w, double h)
    {
        bufferGraphics.draw(new Line2D.Double(x, y, w, h));
    }

    public void nslDrawLine(float x, float y, float w, float h)
    {
        bufferGraphics.draw(new Line2D.Float(x, y, w, h));
    }

    public void nslDrawRect(int x, int y, int w, int h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.draw(new Rectangle(x, y, w, h));
    }

    public void nslDrawRect(double x, double y, double w, double h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.draw(new Rectangle2D.Double(x, y, w, h));
    }

    public void nslDrawRect(float x, float y, float w, float h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.draw(new Rectangle2D.Float(x, y, w, h));
    }

    public void nslDrawRect(int x, int y, int w, int h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Rectangle(x, y, w, h));
    }

    public void nslDrawRect(double x, double y, double w, double h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Rectangle.Double(x, y, w, h));
    }

    public void nslDrawRect(float x, float y, float w, float h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Rectangle.Float(x, y, w, h));
    }

    public void nslDrawRect(int x, int y, int w, int h)
    {
        bufferGraphics.draw(new Rectangle(x, y, w, h));
    }

    public void nslDrawRect(double x, double y, double w, double h)
    {
        bufferGraphics.draw(new Rectangle.Double(x, y, w, h));
    }

    public void nslDrawRect(float x, float y, float w, float h)
    {
        bufferGraphics.draw(new Rectangle.Float(x, y, w, h));
    }

    public void nslFillRect(int x, int y, int w, int h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fill(new Rectangle(x, y, w, h));
    }

    public void nslFillRect(double x, double y, double w, double h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fill(new Rectangle.Double(x, y, w, h));
    }

    public void nslFillRect(float x, float y, float w, float h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fill(new Rectangle.Float(x, y, w, h));
    }

    public void nslFillRect(int x, int y, int w, int h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.fill(new Rectangle(x, y, w, h));
    }

    public void nslFillRect(double x, double y, double w, double h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.fill(new Rectangle.Double(x, y, w, h));
    }

    public void nslFillRect(float x, float y, float w, float h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.fill(new Rectangle.Float(x, y, w, h));
    }

    public void nslFillRect(int x, int y, int w, int h)
    {
        bufferGraphics.fill(new Rectangle(x, y, w, h));
    }

    public void nslFillRect(double x, double y, double w, double h)
    {
        bufferGraphics.fill(new Rectangle.Double(x, y, w, h));
    }

    public void nslFillRect(float x, float y, float w, float h)
    {
        bufferGraphics.fill(new Rectangle.Float(x, y, w, h));
    }

    public void nslDrawOval(int x, int y, int w, int h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.draw(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslDrawOval(double x, double y, double w, double h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.draw(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslDrawOval(float x, float y, float w, float h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.draw(new Ellipse2D.Float(x, y, w, h));
    }

    public void nslDrawOval(int x, int y, int w, int h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslDrawOval(double x, double y, double w, double h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslDrawOval(float x, float y, float w, float h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.draw(new Ellipse2D.Float(x, y, w, h));
    }

    public void nslDrawOval(int x, int y, int w, int h)
    {
        bufferGraphics.draw(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslDrawOval(double x, double y, double w, double h)
    {
        bufferGraphics.draw(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslDrawOval(float x, float y, float w, float h)
    {
        bufferGraphics.draw(new Ellipse2D.Float(x, y, w, h));
    }

    public void nslFillOval(int x, int y, int w, int h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fill(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslFillOval(double x, double y, double w, double h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fill(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslFillOval(float x, float y, float w, float h, NslColor color)
    {
        bufferGraphics.setColor(color);
        bufferGraphics.fill(new Ellipse2D.Float(x, y, w, h));
    }

    public void nslFillOval(int x, int y, int w, int h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.fill(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslFillOval(double x, double y, double w, double h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.fill(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslFillOval(float x, float y, float w, float h, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.fill(new Ellipse2D.Float(x, y, w, h));
    }

    public void nslFillOval(int x, int y, int w, int h)
    {
        bufferGraphics.fill(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslFillOval(double x, double y, double w, double h)
    {
        bufferGraphics.fill(new Ellipse2D.Double(x, y, w, h));
    }

    public void nslFillOval(float x, float y, float w, float h)
    {
        bufferGraphics.fill(new Ellipse2D.Float(x, y, w, h));
    }

    public void nslDrawString(String s, int x, int y, String color)
    {
        bufferGraphics.setColor(NslColor.getColor(color));
        bufferGraphics.drawString(s, x, y);
    }

    public void nslDrawString(String s, int x, int y)
    {
        bufferGraphics.drawString(s, x, y);
    }

    public int nslGetWidth()
    {
        return getWidth();
    }

    public int nslGetHeight()
    {
        return getHeight();
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        this.bufferGraphics=bufferGraphics;
        if (canvasIsInitialized)
        {
            nslRefresh();
        }
    }

    public void nslUpdateDisplay()
    {
        paintComponent(getGraphics());
    }

    public void nslClearDisplay()
    {
        updateDimensions();
        if(bufferGraphics!=null)
        {
            bufferGraphics.clearRect(0, 0, canvasWidth, canvasHeight);
            paintCanvas(bufferGraphics);
        }
    }

    public BufferedImage nslCreateImageBuffer(int width, int height)
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
    }

    public void nslDrawImageBuffer(BufferedImage image, int left, int top)
    {
        bufferGraphics.drawImage(image, left, top, this);
    }

    public void nslDrawImageBuffer(BufferedImage image, Graphics2D graph, int left, int top)
    {
        graph.drawImage(image, left, top, this);
    }

    public void collectTrial()
    {
        if(temporalMode==TEMPORAL_MODE_TRIAL)
        {
            if (NslSystem.init_run_char == 'A')
            {
                nslCollectTrial();
            }
        }
        recordTrial();
    }

    public void collectEpoch()
    {
        if(temporalMode==TEMPORAL_MODE_EPOCH)
        {
            if (NslSystem.init_run_char == 'A')
            {
                nslCollectEpoch();
            }
        }
        recordEpoch(system.getCurrentEpoch());
    }

    public void initEpochCanvas()
    {
        nslInitEpochCanvas();
        canvasIsInitialized = true;
    }

    public void initCanvas()
    {
        nslInitCanvas();
        canvasIsInitialized = true;
    }

    public void nslCollectTrial()
    {
    }

    public void nslCollectEpoch()
    {
    }

    public void nslRefresh()
    {
    }

    public void nslCreateCanvas()
    {
    }

    public void nslInitCanvas()
    {
    }

    public void nslInitEpochCanvas()
    {
    }

    public void processEvent(AWTEvent event)
    {
    }

    public void callFromConstructorBottom()
    {
    }

    public void callFromConstructorTop(Object[] nslArgs)
    {
    }
}
