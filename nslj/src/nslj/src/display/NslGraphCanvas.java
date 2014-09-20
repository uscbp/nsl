package nslj.src.display;

import nslj.src.main.NslMain;
import nslj.src.math.NslOperator;
import nslj.src.math.NslDistance;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Vector;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NslGraphCanvas extends NslCanvas
{
    private float fixedSaturation=1.0f;
    private float fixedBrightness=1.0f;
    private HashMap<String,GraphNode> nodes=new HashMap<String,GraphNode>();
    private HashMap<String,Vector<String>> edges=new HashMap<String,Vector<String>>();
    double scale;
    int maxCoord;

    public NslGraphCanvas()
    {
        super();
        paintMode=-1;
    }

    public NslGraphCanvas(NslFrame nslDisplayFrame, NslCanvas pre, int maxCoord)
    {
        this(nslDisplayFrame, pre.canvas_name, maxCoord, pre.getMin(), pre.getMax());
        paintMode=-1;
    }

    public NslGraphCanvas(NslFrame nslDisplayFrame, String full_name, int maxCoord, double dmin, double dmax)
    {
        super(nslDisplayFrame, full_name, null);
        setMinMax(dmin, dmax);
        this.maxCoord=maxCoord;
        paintMode=-1;
    }

    public void initCanvas()
    {
        super.initCanvas();
        variables=new Vector<NslVariable>();
        reset=true;
        setBackground(Color.white);
    }

    public void initEpochCanvas()
    {
        super.initEpochCanvas();
        reset=true;
    }

    public void setDrawSize()
    {
        drawY = "Normal".equals(canvas_type) ? 30 : 10;
        drawY += adjustY;
        drawX = showLegend ? 10 : 40;
        drawW = canvasWidth - 60;
        drawH = canvasHeight - 50 - adjustY;
        dx=drawW;
        dy=drawH;
        scale=Math.min(drawH,drawW)/maxCoord;
    }

    protected void updateDimensions()
    {
        canvasHeight=getHeight();
        canvasWidth=getWidth();
        canvasX=getX();
        canvasY=getY();
        x_dimension = 1;
        y_dimension = 1;
    }

    protected synchronized void paintCanvas(Graphics2D g)
    {
        super.paintCanvas(g);
    }

    public void paintData(Graphics2D bufferGraphics)
    {
        if(reset || !NslMain.doubleBuffering)
        {
            bufferGraphics.setColor(Color.BLACK);
            // Paint nodes
            Set<String> nodeIds=nodes.keySet();
            if(!nodeIds.isEmpty())
            {
                Iterator<String> nodeIdIter=nodeIds.iterator();
                while(nodeIdIter.hasNext())
                {
                    String nodeId=nodeIdIter.next();
                    GraphNode node=nodes.get(nodeId);
                    Vector<String> nodeEdges=edges.get(nodeId);
                    for(int i=0; i<nodeEdges.size(); i++)
                    {
                        GraphNode node2=nodes.get(nodeEdges.get(i));
                        drawEdge(bufferGraphics, node, node2);
                    }
                }
                nodeIdIter=nodeIds.iterator();
                while(nodeIdIter.hasNext())
                {
                    String nodeId=nodeIdIter.next();
                    GraphNode node=nodes.get(nodeId);
                    drawNode(bufferGraphics, node, scale*3);
                }
            }
            if(showLegend)
                drawLegend(bufferGraphics);
        }
    }

    private void drawEdge(Graphics2D bufferGraphics, GraphNode node, GraphNode node2)
    {
        int x=drawX+(int)(node.coords[0]*scale);
        int y=drawY+(int)(node.coords[1]*scale);
        int xx=drawX+(int)(node2.coords[0]*scale);
        int yy=drawY+(int)(node2.coords[1]*scale);
        double[] baseVec= new  double[]{1,0};
        double[] directionVec= new  double[]{xx-x,yy-y};
        double angle=(Math.PI+ Math.atan2(directionVec[1],directionVec[0])-Math.atan2(baseVec[1],baseVec[0]))%(Math.PI*2.0);
        if(angle<0)
			                angle=Math.PI*2.0+angle;
        double dist= NslDistance.eval(new int[]{x,y}, new int[]{xx,yy});
        dist=dist-(scale*3)/2;
        xx=(int)(x-dist*Math.cos(angle));
        yy=(int)(y-dist*Math.sin(angle));
        drawArrow(bufferGraphics, x, y, xx, yy);
    }

    protected void drawNode(Graphics2D bufferGraphics, GraphNode node, double diameter)
    {
        double centerX=drawX+node.coords[0]*scale;
        double centerY=drawY+node.coords[1]*scale;
        for(int i=0; i<node.colors.length; i++)
        {
            double startArc=i*(360/node.colors.length);
            double endArc=(360/node.colors.length);
            bufferGraphics.setColor(Color.getHSBColor((float)node.colors[i], fixedSaturation, fixedBrightness));
            bufferGraphics.fillArc((int)(centerX-diameter/2), (int)(centerY-diameter/2), (int)(diameter),
                                   (int)(diameter), (int)startArc, (int)endArc);
        }
        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.drawArc((int)(centerX-diameter/2), (int)(centerY-diameter/2), (int)(diameter), (int)(diameter),
                               0, 360);
        if(node.highlighted)
        {
            bufferGraphics.setColor(Color.RED);
            bufferGraphics.drawArc((int)(centerX-(diameter+10)/2), (int)(centerY-(diameter+10)/2), (int)(diameter+10),
                                   (int)(diameter+10), 0, 360);
        }
        bufferGraphics.setColor(Color.BLACK);
        FontMetrics f=getFontMetrics(getFont());
        int labelW=f.stringWidth(node.id);
        bufferGraphics.drawString(node.id, (int)(centerX-labelW/2), (int)(centerY+f.getHeight()/4));
    }

    protected void drawLegend(Graphics2D bufferGraphics)
    {
        int noYMarks = 40;
        double yMarkValueDy = (max - min) / (noYMarks > 0 ? noYMarks : 1);
        double yBase=drawY+dy+1;
        double xBase=drawX+dx+5;
        double yMultiplier = ((double)drawH/(double)noYMarks);
        double yMarkSize = canvasWidth / 40 < 1 ? 1 : 3;
        for (double y = min; y < max; y += yMarkValueDy)
        {
            bufferGraphics.setColor(Color.getHSBColor(getColor(y), fixedSaturation, fixedBrightness));
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

    public void addNode(String id, double[] coords, double[] colors, boolean highlighted)
    {
        GraphNode node=new GraphNode(id, coords, colors, highlighted);
        nodes.put(id, node);
        edges.put(id, new Vector<String>());
        reset=true;
    }

    public void updateNode(String id, double[] colors)
    {
        if(nodes.get(id)!=null)
        {
            nodes.get(id).colors=colors;
            reset=true;
        }
    }

    public void addEdge(String start, String finish)
    {
        if(nodes.get(start)!=null)
        {
            if(!edges.get(start).contains(finish))
            {
                edges.get(start).add(finish);
                reset=true;
            }
        }
    }

    public boolean isAdjacent(String start, String finish)
    {
        if(nodes.get(start)!=null)
            return edges.get(start).contains(finish);
        else
            return false;
    }

    public void highlightNode(String id)
    {
        if(nodes.get(id)!=null)
            nodes.get(id).highlighted=true;
    }

    public void unhighlightAll()
    {
        Set<String> nodeIds=nodes.keySet();
        if(!nodeIds.isEmpty())
        {
            Iterator<String> nodeIdIter=nodeIds.iterator();
            while(nodeIdIter.hasNext())
            {
                String nodeId=nodeIdIter.next();
                nodes.get(nodeId).highlighted=false;
            }
        }
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

    /**
     * Draws an arrow on the given Graphics2D context
     * @param g The Graphics2D context to draw on
     * @param x The x location of the "tail" of the arrow
     * @param y The y location of the "tail" of the arrow
     * @param xx The x location of the "head" of the arrow
     * @param yy The y location of the "head" of the arrow
     */
    private void drawArrow( Graphics2D g, int x, int y, int xx, int yy )
    {
        float arrowWidth = 10.0f ;
        float theta = 0.423f ;
        int[] xPoints = new int[ 3 ] ;
        int[] yPoints = new int[ 3 ] ;
        float[] vecLine = new float[ 2 ] ;
        float[] vecLeft = new float[ 2 ] ;
        float fLength;
        float th;
        float ta;
        float baseX, baseY ;

        xPoints[ 0 ] = xx ;
        yPoints[ 0 ] = yy ;

        // build the line vector
        vecLine[ 0 ] = (float)xPoints[ 0 ] - x ;
        vecLine[ 1 ] = (float)yPoints[ 0 ] - y ;

        // build the arrow base vector - normal to the line
        vecLeft[ 0 ] = -vecLine[ 1 ] ;
        vecLeft[ 1 ] = vecLine[ 0 ] ;

        // setup length parameters
        fLength = (float)Math.sqrt( vecLine[0] * vecLine[0] + vecLine[1] * vecLine[1] ) ;
        th = arrowWidth / ( 2.0f * fLength ) ;
        ta = arrowWidth / ( 2.0f * ( (float)Math.tan( theta ) / 2.0f ) * fLength ) ;

        // find the base of the arrow
        baseX = ( (float)xPoints[ 0 ] - ta * vecLine[0]);
        baseY = ( (float)yPoints[ 0 ] - ta * vecLine[1]);

        // build the points on the sides of the arrow
        xPoints[ 1 ] = (int)( baseX + th * vecLeft[0] );
        yPoints[ 1 ] = (int)( baseY + th * vecLeft[1] );
        xPoints[ 2 ] = (int)( baseX - th * vecLeft[0] );
        yPoints[ 2 ] = (int)( baseY - th * vecLeft[1] );

        g.drawLine( x, y, (int)baseX, (int)baseY ) ;
        g.fillPolygon( xPoints, yPoints, 3 ) ;
    }
}

class GraphNode
{
    public String id;
    public double[] coords;
    public double[] colors;
    public boolean highlighted;

    public GraphNode(String id, double[] coords, double[] colors, boolean highlighted)
    {
        this.id=id;
        this.coords=coords;
        this.colors=colors;
        this.highlighted=highlighted;
    }
}
