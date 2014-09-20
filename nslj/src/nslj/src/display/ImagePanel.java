package nslj.src.display;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ImagePanel extends JPanel
{
    private BufferedImage image;

    public ImagePanel()
    {
        setOpaque( false );
    }

    public ImagePanel( BufferedImage image )
    {
        setOpaque( false );
        this.image = image;
    }

    public void setImage( BufferedImage image )
    {
        this.image = image;
        repaint();
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public Dimension getMinimumSize()
    {
        return new Dimension( 450, 200 );
    }

    public Dimension getMaximumSize()
    {
        return getMinimumSize();
    }

    public Dimension getPreferredSize()
    {
        return getMinimumSize();
    }

    public void paint( Graphics g )
    {
        super.paint( g );
    }

    public void paintComponent( Graphics g )
    {
        if (image != null)
        {
            int height=image.getHeight();
            int width=image.getWidth();
            if(image.getWidth()>getWidth() || image.getHeight()>getHeight())
            {
                double scale=Math.min((double)getWidth()/(double)image.getWidth(),(double)getHeight()/(double)image.getHeight());
                height*=scale;
                width*=scale;
            }
            int x=(getWidth()-width)/2;
            int y=(getHeight()-height)/2;
            g.drawImage( image, x, y, width, height, null );
        }
    }
}