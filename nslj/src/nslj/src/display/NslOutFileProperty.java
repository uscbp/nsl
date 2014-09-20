/* SCCS  @(#)NslOutFileProperty.java	1.8---09/01/99--00:15:46 */

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
import java.io.File;

public class NslOutFileProperty extends JDialog implements ActionListener
{
    // Current canvas
    private NslCanvas currentCanvas;

    // UI widgets
    private JLabel filenameLabel;
    private JButton selectFileButton;
    private JTextField exportFileField;
    private JComboBox fileFormatComboBox;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    // Last filename
    private static String lastFile = null;

    /**
     * Constructor
     *
     * @param parent - parent frame
     */
    public NslOutFileProperty(NslFrame parent)
    {
        super(parent, "Output File Properties", true);

        // Get current canvas
        currentCanvas = ((NslFrame) getParent()).getCurrentCanvas();

        // Initialize export file field to last filename
        if (lastFile != null)
            exportFileField.setText(lastFile);

        // Create UI
        setSize(400, 180);
        add(mainPanel);

        // Listen to button pushes
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        selectFileButton.addActionListener(this);
    }

    /**
     * Button pressed
     *
     * @param evt - ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        // Export data
        if (evt.getSource().equals(okButton))
        {
            // Close UI
            dispose();

            // No export file specified
            if (exportFileField.getText().length() == 0)
            {
                System.out.println("NslOutFileProperty:Error: file_name is null");
            }
            else
            {
                // Get export filename
                String fileName = exportFileField.getText();
                lastFile = fileName;

                // Export to Matlab format
                if (fileFormatComboBox.getSelectedItem().equals("Matlab"))
                {
                    NslOutFile.outToMatlab(currentCanvas, fileName);
                }
                // Export to Gnuplot format
                else if (fileFormatComboBox.getSelectedItem().equals("Gnuplot"))
                {
                    NslOutFile.outToGnuplot(currentCanvas, fileName);
                }
            }
        }
        // Select export file
        else if (evt.getSource().equals(selectFileButton))
        {
            // Create file chooser
            JFileChooser fc = new JFileChooser(exportFileField.getText());
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                // Update export file
                exportFileField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
        // Close UI
        else
        {
            dispose();

        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        filenameLabel = new JLabel();
        filenameLabel.setText("Export File:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(filenameLabel, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer3, gbc);
        selectFileButton = new JButton();
        selectFileButton.setText("Select File");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(selectFileButton, gbc);
        exportFileField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0E-4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(exportFileField, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("File Format:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label1, gbc);
        fileFormatComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Matlab");
        defaultComboBoxModel1.addElement("Gnuplot");
        fileFormatComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fileFormatComboBox, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer4, gbc);
        okButton = new JButton();
        okButton.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(okButton, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cancelButton, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer5, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return mainPanel;
    }
}
