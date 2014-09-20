/* SCCS  @(#)NslErrorWrong.java	1.8---09/01/99--00:15:44 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslErrorWrong.java,v $
//
// Revision 1.1  1997/11/06 03:19:07  erhan
// NSL3.0.b
//
// Revision 1.3  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------


package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NslErrorWrong extends JDialog implements ActionListener
{

    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        if (arg.equals("Ok"))
        {
            dispose();
        }
    }

    public NslErrorWrong(Frame parent)
    {
        super(parent, "Error", true);

        setLayout(new GridLayout(2, 1));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Error: Invalid value entered for variable!"));

        add(panel);

        JButton b;

        JPanel okbutton = new JPanel();
        okbutton.add(b = new JButton("Ok"));
        b.addActionListener(this);

        add(okbutton);
        setSize(300, 85);
        setVisible(true);
    }
}


