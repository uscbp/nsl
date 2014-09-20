/* SCCS  @(#)NslSetVariable.java	1.5---05/21/99--17:42:37 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslSetVariable.java,v $
// Revision 1.1  1997/11/06 03:19:15  erhan
// NSL3.0.b
//
// Revision 1.3  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import jacl.tcl.lang.TclException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NslSetVariable extends JDialog implements ActionListener
{
    private JTextField txt;
    String name;

    public NslSetVariable(Frame parent, String name)
    {
        super(parent, "Set Variable " + name, true);
        this.name = name;

        setLayout(new GridLayout(2, 1));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        panel.add(new JLabel(name + ':'));
        // We have to set the text field with the current value
        String value = "";
        try
        {
            nslj.src.nsls.Executive.interp.eval("nsl call system get" + name);
            value = nslj.src.nsls.Executive.interp.getResult().toString();
        }
        catch (TclException e)
        {
        }

        panel.add(txt = new JTextField(value, 8));

        add(panel);

        JButton b;

        JPanel okbutton = new JPanel();
        okbutton.add(b = new JButton("OK"));
        b.addActionListener(this);

        add(okbutton);
        setSize(250, 100);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        if (arg.equals("OK"))
        {
            try
            {
                nslj.src.nsls.Executive.interp.eval("nsl call system set" + name + ' ' + txt.getText().trim());
            }
            catch (TclException e)
            {
            }
            dispose();
        }
    }
}


