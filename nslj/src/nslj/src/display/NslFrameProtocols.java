/* SCCS  %G%---%W%--%U% */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NslFrameProtocols extends JDialog implements ActionListener
{

    private DoubleList list;
    private JButton addButton = new JButton("Add Protocols");

    public NslFrameProtocols(NslExecutive executive, String[] leftStrs, String[] rightStrs)
    {
        super(executive, "NslFrame Protocols", true);

        JPanel controlPanel = new JPanel();
        controlPanel.add(addButton);

        setLayout(new BorderLayout());
        add(controlPanel, "North");
        add(list = new DoubleList(leftStrs, rightStrs), "Center");

        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                dispose();
                NslExecutive.createModuleWithProtocols(list.getRightSideItems());
            }
        });

        setSize(700, 300);

    }

    public void actionPerformed(ActionEvent event)
    {
        dispose();
    }


    class DoubleList extends JPanel
    {
        private DefaultListModel leftListModel, rightListModel;
        private JList left = new JList(), right = new JList();
        private JPanel controlPanel = new ControlPanel(this);

        public DoubleList(String[] leftStrs, String[] rightStrs)
        {
            GridBagLayout gbl = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();

            leftListModel=new DefaultListModel();
            for(int i=0;i<leftStrs.length;i++)
                leftListModel.addElement(leftStrs[i]);
            left=new JList(leftListModel);
            JScrollPane leftPanel=new JScrollPane(left);
            left.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            rightListModel=new DefaultListModel();
            for(int i=0;i<rightStrs.length;i++)
                rightListModel.addElement(rightStrs[i]);
            right=new JList(rightListModel);
            JScrollPane rightPanel=new JScrollPane(right);
            right.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            setLayout(gbl);

            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbl.setConstraints(leftPanel, gbc);

            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weightx = 0;
            gbc.weighty = 1.0;
            gbl.setConstraints(controlPanel, gbc);

            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbl.setConstraints(rightPanel, gbc);

            add(leftPanel);
            add(controlPanel);
            add(rightPanel);
        }

        public void moveLeftToRight()
        {
            Object[] leftSelected = left.getSelectedValues();

            for (int i = 0; i < leftSelected.length; ++i)
            {
                leftListModel.removeElement(leftSelected[i]);
                rightListModel.addElement(leftSelected[i]);
                right.setSelectedIndex(rightListModel.getSize()-1);
            }
        }

        public void moveRightToLeft()
        {
            Object[] rightSelected = right.getSelectedValues();

            for (int i = 0; i < rightSelected.length; ++i)
            {
                rightListModel.removeElement(rightSelected[i]);
                leftListModel.addElement(rightSelected[i]);
                left.setSelectedIndex(leftListModel.getSize()-1);
            }
        }

        public void moveAllRightToLeft()
        {
            int rightCnt = right.getModel().getSize();

            for (int i = 0; i < rightCnt; ++i)
            {
                leftListModel.addElement(rightListModel.getElementAt(i));
                if(right.isSelectedIndex(i))
                {
                    left.setSelectedIndex(i);
                }
            }
            rightListModel.clear();
        }

        public void moveAllLeftToRight()
        {
            int leftCnt = left.getModel().getSize();

            for (int i = 0; i < leftCnt; ++i)
            {
                rightListModel.addElement(leftListModel.getElementAt(i));
                if (left.isSelectedIndex(i))
                {
                    right.setSelectedIndex(i);
                }
            }
            left.removeAll();
        }

        public Object[] getRightSideItems()
        {
            return rightListModel.toArray();
        }

        public Object[] getRightSideSelectedItems()
        {
            return right.getSelectedValues();
        }

        public Object[] getLeftSideItems()
        {
            return leftListModel.toArray();
        }

        public Object[] getLeftSideSelectedItems()
        {
            return left.getSelectedValues();
        }

    }

    class ControlPanel extends JPanel
    {
        private DoubleList doubleList;
        private JButton leftToRight = new JButton(">");
        private JButton allLeftToRight = new JButton(">>");
        private JButton rightToLeft = new JButton("<");
        private JButton allRightToLeft = new JButton("<<");

        private Font buttonFont = new Font("TimeRoman",
                Font.BOLD, 14);

        public ControlPanel(DoubleList dbList)
        {
            this.doubleList = dbList;

            GridBagLayout gbl = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();

            setLayout(gbl);

            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbl.setConstraints(leftToRight, gbc);
            gbl.setConstraints(allLeftToRight, gbc);
            gbl.setConstraints(rightToLeft, gbc);
            gbl.setConstraints(allRightToLeft, gbc);

            add(leftToRight);
            add(allLeftToRight);
            add(rightToLeft);
            add(allRightToLeft);

            leftToRight.setFont(buttonFont);
            allLeftToRight.setFont(buttonFont);
            rightToLeft.setFont(buttonFont);
            allRightToLeft.setFont(buttonFont);

            leftToRight.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    doubleList.moveLeftToRight();
                }
            });

            allLeftToRight.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    doubleList.moveAllLeftToRight();
                }
            });

            rightToLeft.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    doubleList.moveRightToLeft();
                }
            });

            allRightToLeft.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    doubleList.moveAllRightToLeft();
                }
            });

        }

        public Insets getInsets()
        {
            return new Insets(4, 4, 4, 4);
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Dimension size = getSize();
            g.setColor(Color.black);
            g.drawRect(0, 0, size.width - 1, size.height - 1);
            g.setColor(Color.lightGray);
            g.fill3DRect(1, 1, size.width - 2, size.height - 2, true);
        }
    }
}
