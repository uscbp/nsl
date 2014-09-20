/* SCCS  @(#)NslPlotInput.java	1.14---09/20/99--19:23:04 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslPlotInput.java,v $
// Revision 1.5  1997/11/06 03:15:11  erhan
// nsl3.0.b
//
// Revision 1.4  1997/05/09 22:30:27  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import nslj.src.lang.NslData;
import nslj.src.lang.NslModule;
import nslj.src.system.NslSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class NslPlotInput extends NslInput
{
    public static NslSystem system;
    private String plotType = "Area";
    private String[] var_selected_name;
    private int XY_var;
    Tree mTree;

    public NslPlotInput(NslFrame parent)
    {
        super(parent, "Select Plot");

        var_selected_name = new String[2];
        XY_var = 0;

        // variable name
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(6, 2));
        p1.add(new JLabel("Select the variable either by"));
        p1.add(new JLabel("typing or tree selection."));
        p1.add(new JLabel("Expanded variable name:"));
        p1.add(new JTextField("", 8));
        p1.add(new JLabel("  Select Graph type:"));
        JComboBox graphChoice = new JComboBox();

        plotType = "Area";

        graphChoice.addItem("Area");    //default
        graphChoice.addItem("Bar");
        graphChoice.addItem("Dot");
        graphChoice.addItem("Grayscale");
        graphChoice.addItem("Histogram");
        graphChoice.addItem("Spatial");
        graphChoice.addItem("Temporal");
        graphChoice.addItem("Thermal");
        graphChoice.addItem("NumericEditor");    //default
        graphChoice.addItem("InputImage");

        p1.add(graphChoice);
        graphChoice.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent evt)
            {
                plotType = evt.getItem().toString();
            }
        });
        //98/9/22 aa: added instructions
        p1.add(new JLabel("  Tree selection below."));
        p1.add(new JLabel("  "));
        p1.add(new JLabel("  Click + to find name."));
        p1.add(new JLabel("  "));

        add("North", p1);

        // creat tree structure
        mTextArea = new JTextArea();

        mTree = new Tree(this);
        mTree.setLayout(new GridLayout(0, 1));
        TreeNode lSuper;
        NslModule topModule = system.nslGetModelRef();
        lSuper = new NslTreeNode(topModule.nslGetName(), this);
        lSuper.setModule(topModule);
        mTree.addTreeNode(lSuper);
        mTree.treeBuilder(topModule, lSuper);
        mTree.computeDisplayTree();
        JScrollPane lPanel = new JScrollPane();
        lPanel.setViewportView(mTree);
        lPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        lPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add("Center", lPanel);

        JPanel p2 = new JPanel();
        JButton b = new JButton("Close Window");
        p2.add(b);
        b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                dispose();
            }
        });

        b = new JButton("Next");
        p2.add(b);
        b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                String variableName = mTree.variableSelect;
                if (variableName == null)
                {
                    System.out.println("#Warning: select a leaf variable before hitting next.");
                }
                else
                    ((NslFrame) getParent()).addVariable(variableName, plotType);
            }
        });
        add("South", p2);
        setSize(340, 520);
    }
    // Find the submodules recursively until leaf module reached


    public String xvariable()
    {
        return var_selected_name[0];
    }

    public String yvariable()
    {
        return var_selected_name[1];
    }

    public int inputvariableindex()
    {
        return (XY_var - 1);
    }
}//end class
