/* SCCS  @(#)NslDimValue.java	1.9---09/01/99--00:15:43 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NslDimValue extends JDialog implements ActionListener
{
    int num_dims = 0;
    NslFrame nsl_display_frame;
    String var_sel_full_name;
    NslVariableInfo var_sel_info;
    String plot_type_name;
    JTextField dim1;
    JTextField dim2;
    //boolean replace_canvas;

    public NslDimValue(NslFrame nsl_display_frame, String var_sel_full_name, NslVariableInfo var_sel_info, 
                       String plot_type_name)
    {
        super(nsl_display_frame, "Enter Values", true);

        /* these variables are needed in the action method */
        this.nsl_display_frame = nsl_display_frame;
        this.var_sel_full_name = var_sel_full_name;
        this.var_sel_info = var_sel_info;
        this.plot_type_name = plot_type_name;

        num_dims = (var_sel_info.getCountDimensions());

        if ((num_dims == 0) || (num_dims == 1) || (num_dims == 2) || (num_dims >= 4))
        {
            System.err.println("Error: NslDimValue: should not have gotten here in the code.");
            return;
        }

        JPanel p = new JPanel();
        if (num_dims == 3)
        {
            if (NslDimInput.dim_choice.equals("XY"))
            {
                addEnterOneValue(p, "Z");
            }
            else if (NslDimInput.dim_choice.equals("XZ"))
            {
                addEnterOneValue(p, "Y");
            }
            else if (NslDimInput.dim_choice.equals("YZ"))
            {
                addEnterOneValue(p, "X");
            }
            else
            {
                System.err.println("Error: NslDimVal: bad input choice.");
                dispose();
                return;
            }
        } // end if (num_dims==3)
        else if (num_dims == 4)
        {
            if (NslDimInput.dim_choice.equals("WX"))
            {
                addEnterTwoValues(p, "Y", "Z");
            }
            else if (NslDimInput.dim_choice.equals("WY"))
            {
                addEnterTwoValues(p, "X", "Z");
            }
            else if (NslDimInput.dim_choice.equals("WZ"))
            {
                addEnterTwoValues(p, "X", "Y");
            }
            else if (NslDimInput.dim_choice.equals("XY"))
            {
                addEnterTwoValues(p, "W", "Z");
            }
            else if (NslDimInput.dim_choice.equals("XZ"))
            {
                addEnterTwoValues(p, "W", "Y");
            }
            else if (NslDimInput.dim_choice.equals("YZ"))
            {
                addEnterTwoValues(p, "W", "X");
            }
            else
            {
                System.err.println("Error: NslDimVal:bad input choice");
                dispose();
                return;
            }

        } // end if (num_dims==4)

        add("Center", p);

        JButton b;
        JPanel p4 = new JPanel();
        p4.setLayout(new GridLayout(1, 5));
        p4.add(b = new JButton("<Back"));
        b.addActionListener(this);
        p4.add(new JLabel(""));
        p4.add(b = new JButton("Cancel"));
        b.addActionListener(this);
        p4.add(new JLabel(""));
        p4.add(b = new JButton("Next>"));
        b.addActionListener(this);
        add("South", p4);
        setSize(450, 180);
    } //end constructor


    private void addEnterOneValue(JPanel p, String one)
    {
        p.add(new JLabel("Enter value for Dimension " + one));
        dim1 = new JTextField(8);
        p.add(dim1);
    }

    private void addEnterTwoValues(JPanel p, String one, String two)
    {
        p.add(new JLabel("Enter value for Dimension " + one));
        dim1 = new JTextField(8);
        p.add(dim1);
        p.add(new JLabel("Enter value for Dimension " + two));
        dim2 = new JTextField(8);
        p.add(dim2);
    }

/*----------------------------------------------------*/

    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        if (arg.equals("Next>"))
        {
            var_sel_info.choiceDimensions=NslDimInput.dim_choice;
            String canvasName=var_sel_full_name+"_"+var_sel_info.choiceDimensions+"-";
            if(dim1!=null)
            {
                var_sel_info.slicingDims[0]=Integer.parseInt(dim1.getText());
                canvasName=canvasName+dim1.getText();
            }
            if(dim2!=null)
            {
                var_sel_info.slicingDims[1]=Integer.parseInt(dim2.getText());
                canvasName=canvasName+","+dim2.getText();
            }

            nsl_display_frame.addDisplayCanvas(canvasName, var_sel_info, plot_type_name);
        }
        else if (arg.equals("Cancel"))
        {
            dispose();
        }
        else if (arg.equals("<Back"))
        {
            /* TODO - bring back NslDimInput Window */
            dispose();
        }

    }

} // end class
