/*
 *Copyright(c) 1997 USC Brain Project. email nsl@java.usc.edu
 */

/**
 @author Nikunj Mehta
 */

package nslj.src.display;

import javax.swing.*;
import java.awt.event.ItemListener;

public class NslRadioMenu extends JMenu
{
    ButtonGroup group;
    public NslRadioMenu(String name, String labels[])
    {
        super(name);
        group=new ButtonGroup();
        for (String label1 : labels)
        {
            JRadioButtonMenuItem button=new JRadioButtonMenuItem(label1, false);
            group.add(button);
            super.add(button);
        }
    }

    public synchronized void addItemListener(ItemListener l)
    {
        int numItems = getItemCount();
        for (int i = 0; i < numItems; i++)
        {
            getItem(i).addItemListener(l);
        }
    }

    public synchronized void removeItemListener(ItemListener l)
    {
        int numItems = getItemCount();
        for (int i = 0; i < numItems; i++)
        {
            getItem(i).removeItemListener(l);
        }
    }
}
