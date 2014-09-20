/* SCCS  %W% --- %G% -- %U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import javax.swing.*;
import java.awt.*;

public class NslUserPanel extends JPanel
{
    private String name;
    private GridBagConstraints gc;

    public NslUserPanel(String n)
    {
        super();

        this.name = n;

        gc = new GridBagConstraints();
        setLayout(new GridBagLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createTitledBorder(n));
    }

    public void addComponent(JComponent c)
    {
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.NONE;
        for(int j=0; j<getComponentCount(); j++)
        {
            if(getComponent(j).equals(c))
            {
                remove(getComponent(j));
                add(c, gc, j);
                return;
            }
        }
        add(c, gc);
    }

    public String nslGetName()
    {
        return name;
    }
}

