package nslj.src.display;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.spi.*;
import javax.imageio.stream.*;
import javax.swing.*;

public class ImageSaveDialog extends JFileChooser
{
    private JComboBox imageFormatCB;
    private Component component;
    private static String lastPath=null;
    private static int lastFormat=-1;

    public ImageSaveDialog(Component component)
    {
        super(new File(System.getProperty("user.dir")));
        this.component = component;
    }

    static private class FormatDescriptor
    {
        public String suffix;
        public ImageWriter imageWriter;
        public String description;

        public FormatDescriptor(String suffix, ImageWriter imageWriter, String description)
        {
            this.suffix = suffix;
            this.imageWriter = imageWriter;
            this.description = description;
        }

        public String toString()
        {
            return description;
        }
    }

    static public Vector getWriterFormats(boolean addEps)
    {
        Vector formats = new Vector();
        Hashtable seen = new Hashtable();

        String names[] = ImageIO.getWriterFormatNames();
        for (int i=0; i<names.length; ++i)
        {
            String name = names[i];
            Iterator writers = ImageIO.getImageWritersByFormatName(name);
            while (writers.hasNext())
            {
                ImageWriter iw = (ImageWriter)writers.next();
                ImageWriterSpi iws = iw.getOriginatingProvider();
                String suffixes[] = iws.getFileSuffixes();
                for (int j=0; j<suffixes.length; ++j)
                {
                    String suffix = suffixes[j];
                    suffix = suffix.toLowerCase();
                    if (!seen.containsKey( suffix ))
                    {
                        seen.put( suffix, suffix );
                        String description = name+" (*."+suffix+")";
                        FormatDescriptor fd = new FormatDescriptor(suffix, iw, description);
                        formats.addElement( fd );
                    }
                }
            }
        }
        if(addEps)
            formats.addElement(new FormatDescriptor("eps", null, "eps (*.eps)"));

        return formats;
    }

    private void augmentSaveDialog(BufferedImage image, boolean addEps)
    {
        File dir=null;
        if(lastPath!=null)
        {
            dir=new File(lastPath);
            if(!dir.exists())
                dir=null;
        }
        if(dir!=null)
            setCurrentDirectory(dir);
        String suffix="";
        Vector formats = getWriterFormats(addEps);
        if(getSelectedFile()!=null && getSelectedFile().getName()!=null)
        {
            suffix=getSelectedFile().getName().substring(getSelectedFile().getName().indexOf(".")+1);
        }
        imageFormatCB = new JComboBox( formats );
        boolean found=false;
        for(int i=0; i<imageFormatCB.getItemCount(); i++)
        {
            if(((FormatDescriptor)imageFormatCB.getItemAt(i)).suffix.equals(suffix))
            {
                imageFormatCB.setSelectedIndex(i);
                found=true;
                break;
            }
        }
        if(!found && lastFormat>=0 && imageFormatCB.getItemCount()>lastFormat)
            imageFormatCB.setSelectedIndex(lastFormat);
        imageFormatCB.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e )
                    {
                        FormatDescriptor fd = (FormatDescriptor)imageFormatCB.getSelectedItem();
                        String suffix = fd.suffix;
                        updateFileSuffix( suffix );
                    }
                } );

        Component north = getComponent( 0 );
        Component center = getComponent( 1 );
        Component south = getComponent( 2 );

        remove( north );
        remove( center );
        remove( south );

        JPanel jp = new JPanel();
        jp.setLayout( new BorderLayout( 10, 10 ) );
        jp.add( north, BorderLayout.NORTH );
        jp.add( center, BorderLayout.CENTER );
        jp.add( south, BorderLayout.SOUTH );

        JLabel formatLabel = new JLabel( "Select Format:" );

        JPanel formatRow = new JPanel();
        formatRow.setLayout( new BorderLayout( 10, 10 ) );
        formatRow.add( formatLabel, BorderLayout.WEST );
        formatRow.add( imageFormatCB, BorderLayout.CENTER );

        ImagePanel ip = new ImagePanel();
        ip.setImage( image );

        JPanel augmentation = new JPanel();
        augmentation.setLayout( new BorderLayout(10, 10 ) );
        augmentation.add( ip, BorderLayout.CENTER );
        augmentation.add( formatRow, BorderLayout.SOUTH );

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( augmentation );
        Dimension fillerDimension = new Dimension(100, 10 );
        add( new Box.Filler( fillerDimension, fillerDimension, fillerDimension ) );
        add( jp );

        setSize( 450, 550 );
        setLocation( 30, 30 );
    }

    public void save( BufferedImage image, boolean addEps )
    {
        augmentSaveDialog( image, addEps );

        int ret = showSaveDialog( component );
        if (ret == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = getSelectedFile();
                lastPath=file.getParent();
                lastFormat=imageFormatCB.getSelectedIndex();
                if(!file.getName().toLowerCase().endsWith("eps"))
                {
                    // Get the currently-selected item in
                    // the combo box
                    FormatDescriptor fd = (FormatDescriptor)imageFormatCB.getSelectedItem();

                    writeImageFile(image, file, fd.imageWriter);
                }
            }
            catch( IOException ie )
            {
                ie.printStackTrace();
            }
        }
    }

    public static void save(BufferedImage image, String filename, String format)
    {
        File file=new File(filename);
        Vector formats=getWriterFormats(true);
        for(int i=0; i<formats.size(); i++)
        {
            FormatDescriptor fd=(FormatDescriptor)formats.get(i);
            if(fd.suffix.equals(format))
            {
                try
                {
                    writeImageFile(image, file, fd.imageWriter);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static void writeImageFile(BufferedImage image, File file, ImageWriter imageWriter) throws IOException
    {

        // Get an output stream for the file
        FileOutputStream fout = new FileOutputStream(file);

        // Turn it into an ImageOutputStreamn
        ImageOutputStream ios = ImageIO.createImageOutputStream(fout);

        // Plug this stream into the ImageWriter
        imageWriter.setOutput( ios );

        // Write the image
        imageWriter.write( image );

        // Dont forget to close the file!
        fout.close();
    }

    private void updateFileSuffix(String suffix )
    {
        File file = getSelectedFile();
        if (file == null)
            return;
        String filename = file.getPath();
        int lp = filename.lastIndexOf( "." );
        if (lp==-1)
            return;
        filename = filename.substring( 0, lp ) + "." + suffix;
        setSelectedFile( new File( filename ) );
    }
}