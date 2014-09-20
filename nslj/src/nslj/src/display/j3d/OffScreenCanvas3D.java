package nslj.src.display.j3d;

import javax.media.j3d.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A Canvas3D for offscreen printing
 */
public class OffScreenCanvas3D extends Canvas3D
{
    // Drawing raster
    Raster drawRaster;

    // Whether or not currently printing
    boolean printing = false;

    /**
     * OffScreenCanvas3D constructor
     * @param gconfig - graphics configuration to use
     * @param drawRaster - drawing raster
     */
    public OffScreenCanvas3D(GraphicsConfiguration gconfig, Raster drawRaster)
    {

        super(gconfig, true);
        this.drawRaster = drawRaster;
    }

    /**
     * Print canvas
     */
    public void print()
    {
        // Set printing flag to true
        printing = true;

        // Create new image
        BufferedImage bImage = new BufferedImage(drawRaster.getImage().getWidth(), drawRaster.getImage().getHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);

        // Create image buffer
        ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
        buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

        // Set offscreen buffer
        setOffScreenBuffer(buffer);

        // Render to buffer
        renderOffScreenBuffer();
    }

    /**
     * Called automatically after rendering
     */
    public void postSwap()
    {
        // If currently printing
        if (printing)
        {
            // Swap buffer
            super.postSwap();

            // Draw buffer to the raster
            drawOffScreenBuffer();

            // Done printing
            printing = false;
        }
    }

    /**
     * Draw the offscreen buffer to the drawing raster
     */
    protected void drawOffScreenBuffer()
    {
        // Get buffer image
        BufferedImage bImage = this.getOffScreenBuffer().getImage();
        ImageComponent2D newImageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);

        // Update raster
        drawRaster.setImage(newImageComponent);
    }

    /**
     * Get drawing raster
     * @return drawRaster
     */
    public Raster getDrawRaster()
    {
        return drawRaster;
    }

    /**
     * Whether or not currently printing
     * @return printing
     */
    public boolean isPrinting()
    {
        return printing;
    }
}
