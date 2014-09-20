/* SCCS  @(#)NslDimInput.java	1.9---09/01/99--00:15:43 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.


package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * NslDimInput is a dynamic window that popup when after
 * the NslPlotInput has appeared and the user selected "next".
 * Thus the items when the window is created should be current.
*/
public class NslDimInput extends JDialog implements ActionListener, ItemListener
{

    public static String dim_choice;
    private List dims = new List(4, true);
    private String[] di;

    private NslFrame nsl_display_frame1;
    private String var_sel_full_name1;
    private NslVariableInfo var_sel_info;
    private String plot_type_name1;
    //private boolean replace_canvas;

    public NslDimInput(NslFrame nsl_display_frame, String var_sel_full_name, NslVariableInfo var_sel_info,
                       String plot_type_name)
    {

        super(nsl_display_frame, "Select Dimensions", true);

        nsl_display_frame1 = nsl_display_frame;
        var_sel_full_name1 = var_sel_full_name;
        this.var_sel_info = var_sel_info;
        plot_type_name1 = plot_type_name;

        // 98/8/27 aa
        int num_dims = (var_sel_info.getCountDimensions());

        if ((num_dims == 0) || (num_dims == 1) || (num_dims == 2) || (num_dims > 4))
        {
            System.err.println("Error. NslDinInput: should not have gotten here in the code.");
            return;
        }
        else if (num_dims == 3)
        {
            dims.add("X");
            dims.add("Y");
            dims.add("Z");
        }
        else if (num_dims == 4)
        {
            dims.add("W");
            dims.add("X");
            dims.add("Y");
            dims.add("Z");
        }

        JPanel p = new JPanel();
        p.add(new JLabel("Select two dimensions you wish to display:"));
        p.add(dims);
        dims.addItemListener(this);

        add("Center", p);

        JButton b;
        JPanel p4 = new JPanel();
        p4.setLayout(new GridLayout(1, 5));
        p4.add(b = new JButton("< Back"));
        b.addActionListener(this);
        p4.add(new JLabel(""));
        p4.add(b = new JButton("Cancel"));
        b.addActionListener(this);
        p4.add(new JLabel(""));
        p4.add(b = new JButton("Next >"));
        b.addActionListener(this);
        add("South", p4);
        setSize(450, 180);
    }// end constructor

    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        if (arg.equals("Next >"))
        {
            if (di.length == 0)
            {
                System.out.println("Warning:NslDimInput 0 - select two dimensions.");
                dispose();
            }
            else if (di.length == 1)
            {
                System.out.println("Warning:NslDimInput 1 - select two dimentions.");
                dispose();
            }
            else
            {  // two dimensions selected
                System.out.println("Debug:DimInput" + di.length + ' ' + di[0]);
                if ((di[0].equals("W") && di[1].equals("X")) || (di[0].equals("X") && di[1].equals("W")))
                {
                    dim_choice = "WX";
                }
                if ((di[0].equals("W") && di[1].equals("Y")) || (di[0].equals("Y") && di[1].equals("W")))
                {
                    dim_choice = "WY";
                }
                if ((di[0].equals("W") && di[1].equals("Z")) || (di[0].equals("Z") && di[1].equals("W")))
                {
                    dim_choice = "WZ";
                }
                if ((di[0].equals("X") && di[1].equals("Y")) || (di[0].equals("Y") && di[1].equals("X")))
                {
                    dim_choice = "XY";
                }
                if ((di[0].equals("X") && di[1].equals("Z")) || (di[0].equals("Z") && di[1].equals("X")))
                {
                    dim_choice = "XZ";
                }
                if ((di[0].equals("Y") && di[1].equals("Z")) || (di[0].equals("Z") && di[1].equals("Y")))
                {
                    dim_choice = "YZ";
                }
                dispose();
                /* call dim values here : aa */
                NslDimValue dv =new NslDimValue(nsl_display_frame1, var_sel_full_name1, var_sel_info, plot_type_name1);
                dv.setVisible(true);
            }
        }
        else if (arg.equals("Cancel"))
        {
            dispose();
        }
        else if (arg.equals("< Back"))
        {
            /* The Variable Input Window should still be open
           so we should not have to bring it up again.
           */
            dispose();
        }
    }

    public void itemStateChanged(ItemEvent evt)
    {
        di = dims.getSelectedItems();
    }
}

