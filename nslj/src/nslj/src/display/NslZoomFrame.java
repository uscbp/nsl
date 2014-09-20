/* SCCS  @(#)NslZoomFrame.java	1.12---09/01/99--00:15:50 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NslZoomFrame extends JFrame implements ActionListener
{
    NslCanvas canvas;
    
    public NslZoomFrame(NslCanvas ndc)
    {
        JPanel p = new JPanel();
        p.setBackground(Color.lightGray);
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton b = (JButton) p.add(new JButton("Zoom in"));
        b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                canvas.zoomIn();
            }
        });
        b = (JButton) p.add(new JButton("Zoom out"));
        b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                canvas.zoomOut();
            }
        });
        add("North", p);

        canvas = ndc;
        canvas.setCanvasType("Zoom");
        setTitle(canvas.nslGetName());
        canvas.addMouseListener(new ZoomMouseAdapter());
        canvas.addMouseMotionListener(new ZoomMouseAdapter());


        JMenuBar mbar = new JMenuBar();
        JMenu m = new JMenu("File");
        JMenuItem i=new JMenuItem("Export Data");
        i.addActionListener(this);
        m.add(i);
        i=new JMenuItem("Print");
        i.addActionListener(this);
        m.add(i);
        i=new JMenuItem("Close");
        i.addActionListener(this);
        m.add(i);
        mbar.add(m);

        setJMenuBar(mbar);

        JPanel drawPanel = new JPanel();

        GridLayout gb = new GridLayout();
        drawPanel.setLayout(gb);
        drawPanel.add(canvas);
        add("Center", drawPanel);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                dispose();
            }
        });
    }

    private class ZoomMouseAdapter extends MouseAdapter implements MouseMotionListener
    {
        public void mousePressed(MouseEvent evt)
        {
            canvas.mousePressed(evt);
        }

        public void mouseReleased(MouseEvent evt)
        {
            canvas.mouseReleased(evt);
        }

        public void mouseDragged(MouseEvent evt)
        {
            canvas.mouseDragged(evt);
        }

        public void mouseMoved(MouseEvent evt)
        {
        }
    }

    public void actionPerformed(ActionEvent evt)
    {
        String action = evt.getActionCommand();
        if (action.equals("Close"))
        {
            dispose();
        }
        else if (action.equals("Print"))
        {
            canvas.Print();
        }
        else if (action.equals("Export Data"))
        {
            //System.out.println("NslZoomFrame:Exporting...");
            //NslOutFileProperty ndofp = new NslOutFileProperty(NslCanvas.Frame);
            NslOutFileProperty ndofp = new NslOutFileProperty(canvas.nslDisplayFrame);
            ndofp.setVisible(true);
            //System.out.println("NslZoomFrame:Finished saving...");
        }
    }
}
