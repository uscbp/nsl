/* SCCS  @(#)NslAreaCanvasProperty.java	1.7---09/01/99--00:15:54 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslAreaCanvasProperty.java,v $
// Revision 1.3  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NslAreaCanvasProperty extends JDialog implements ActionListener
{
    protected NslAreaCanvas currentCanvas;
    protected JPanel mainPanel;
    protected JLabel yminLabel;
    protected JLabel ymaxLabel;
    protected JTextField ymax;
    protected JTextField ymin;
    protected JComboBox boxColor;
    protected JCheckBox recordData;
    protected JCheckBox recordFrames;
    protected JCheckBox recordVideo;
    protected JComboBox dataFormat;
    protected JComboBox framesFormat;
    protected JComboBox dataTemporalMode;
    protected JComboBox framesTemporalMode;
    protected JComboBox videoTemporalMode;
    protected JLabel fpsLabel;
    protected JTextField fpsField;
    protected JLabel recordDirLabel;
    protected JButton selectDirButton;
    protected JTextField recordDirField;
    protected JLabel temporalModeLabel;
    protected JRadioButton temporal_epoch;
    protected JRadioButton temporal_trial;
    protected JButton okButton;
    protected JButton cancelButton;
    private JCheckBox autoscaleCheckBox;

    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getSource().equals(okButton))
        {
            dispose();
            updateCanvas();
        }
        else if (evt.getSource().equals(selectDirButton))
        {
            JFileChooser fc = new JFileChooser(recordDirField.getText());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                recordDirField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
        else
        {
            dispose();
        }

    }

    private void updateCanvas()
    {
        float y_max = Float.valueOf(ymax.getText().trim());
        float y_min = Float.valueOf(ymin.getText().trim());

        currentCanvas.setMinMax(y_min, y_max);

        currentCanvas.setAutoScale(autoscaleCheckBox.isSelected());

        currentCanvas.setVarColor(NslColor.colorTypes[boxColor.getSelectedIndex()]);

        currentCanvas.setRecordData(recordData.isSelected());
        if (dataFormat.getSelectedItem().equals("Matlab"))
            currentCanvas.setRecordDataFormat(NslCanvas.RECORD_DATA_FORMAT_MAT);
        else if (dataFormat.getSelectedItem().equals("Gnuplot"))
            currentCanvas.setRecordDataFormat(NslCanvas.RECORD_DATA_FORMAT_GNU);
        if (dataTemporalMode.getSelectedItem().equals("Trial"))
            currentCanvas.setRecordDataTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (dataTemporalMode.getSelectedItem().equals("Epoch"))
            currentCanvas.setRecordDataTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);
        else if (dataTemporalMode.getSelectedItem().equals("Simulation"))
            currentCanvas.setRecordDataTemporalMode(NslCanvas.TEMPORAL_MODE_SIMULATION);

        currentCanvas.setRecordFrames(recordFrames.isSelected());
        if (framesFormat.getSelectedItem().equals("EPS"))
            currentCanvas.setRecordFramesFormat(NslCanvas.RECORD_FRAMES_FORMAT_EPS);
        else if (framesFormat.getSelectedItem().equals("JPG"))
            currentCanvas.setRecordFramesFormat(NslCanvas.RECORD_FRAMES_FORMAT_JPG);
        if (framesFormat.getSelectedItem().equals("PNG"))
            currentCanvas.setRecordFramesFormat(NslCanvas.RECORD_FRAMES_FORMAT_PNG);
        if (framesTemporalMode.getSelectedItem().equals("Trial"))
            currentCanvas.setRecordFramesTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (framesTemporalMode.getSelectedItem().equals("Epoch"))
            currentCanvas.setRecordFramesTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);
        else if (framesTemporalMode.getSelectedItem().equals("Simulation"))
            currentCanvas.setRecordFramesTemporalMode(NslCanvas.TEMPORAL_MODE_SIMULATION);

        currentCanvas.setRecordVideo(recordVideo.isSelected());
        if (videoTemporalMode.getSelectedItem().equals("Trial"))
            currentCanvas.setRecordVideoTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (videoTemporalMode.getSelectedItem().equals("Epoch"))
            currentCanvas.setRecordVideoTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);
        else if (videoTemporalMode.getSelectedItem().equals("Simulation"))
            currentCanvas.setRecordVideoTemporalMode(NslCanvas.TEMPORAL_MODE_SIMULATION);

        currentCanvas.setRecordVideoFramesPerSec(Double.parseDouble(fpsField.getText()));

        currentCanvas.setRecordDir(recordDirField.getText());

        if (temporal_trial.isSelected() && currentCanvas.getTemporalMode() != NslCanvas.TEMPORAL_MODE_TRIAL)
            currentCanvas.setTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (temporal_epoch.isSelected() && currentCanvas.getTemporalMode() != NslCanvas.TEMPORAL_MODE_EPOCH)
            currentCanvas.setTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);

        // change all other properties here
        currentCanvas.update();
    }

    public NslAreaCanvasProperty(NslFrame parent, String name)
    {
        super(parent, name, true);
        setupLayout();
    }

    public NslAreaCanvasProperty(NslFrame parent)
    {
        this(parent, "Area Plot Properties");
    }

    protected void setupLayout()
    {
        currentCanvas = (NslAreaCanvas) ((NslFrame) getParent()).getCurrentCanvas();

        ymin.setText(Double.toString(currentCanvas.getMin()));
        ymax.setText(Double.toString(currentCanvas.getMax()));

        autoscaleCheckBox.setSelected(currentCanvas.isAutoScale());

        for (int i = 0; i < NslColor.colorNames.length; i++)
            boxColor.addItem(NslColor.colorNames[i]);
        boxColor.setSelectedIndex(NslColor.getIndexByColor(currentCanvas.getVarColor()));

        recordData.setSelected(currentCanvas.recordData);
        if (NslCanvas.RECORD_DATA_FORMAT_MAT == currentCanvas.getRecordDataFormat())
            dataFormat.setSelectedItem("Matlab");
        else if (NslCanvas.RECORD_DATA_FORMAT_GNU == currentCanvas.getRecordDataFormat())
            dataFormat.setSelectedItem("Gnuplot");
        if (NslCanvas.TEMPORAL_MODE_TRIAL == currentCanvas.getRecordDataTemporalMode())
            dataTemporalMode.setSelectedItem("Trial");
        else if (NslCanvas.TEMPORAL_MODE_EPOCH == currentCanvas.getRecordDataTemporalMode())
            dataTemporalMode.setSelectedItem("Epoch");
        else if (NslCanvas.TEMPORAL_MODE_SIMULATION == currentCanvas.getRecordDataTemporalMode())
            dataTemporalMode.setSelectedItem("Simulation");

        recordFrames.setSelected(currentCanvas.recordFrames);
        if (NslCanvas.RECORD_FRAMES_FORMAT_EPS == currentCanvas.getRecordFramesFormat())
            framesFormat.setSelectedItem("EPS");
        else if (NslCanvas.RECORD_FRAMES_FORMAT_JPG == currentCanvas.getRecordFramesFormat())
            framesFormat.setSelectedItem("JPG");
        else if (NslCanvas.RECORD_FRAMES_FORMAT_PNG == currentCanvas.getRecordFramesFormat())
            framesFormat.setSelectedItem("PNG");
        if (NslCanvas.TEMPORAL_MODE_TRIAL == currentCanvas.getRecordFramesTemporalMode())
            framesTemporalMode.setSelectedItem("Trial");
        else if (NslCanvas.TEMPORAL_MODE_EPOCH == currentCanvas.getRecordFramesTemporalMode())
            framesTemporalMode.setSelectedItem("Epoch");
        else if (NslCanvas.TEMPORAL_MODE_SIMULATION == currentCanvas.getRecordFramesTemporalMode())
            framesTemporalMode.setSelectedItem("Simulation");

        recordVideo.setSelected(currentCanvas.recordVideo);
        if (NslCanvas.TEMPORAL_MODE_TRIAL == currentCanvas.getRecordVideoTemporalMode())
            videoTemporalMode.setSelectedItem("Trial");
        else if (NslCanvas.TEMPORAL_MODE_EPOCH == currentCanvas.getRecordVideoTemporalMode())
            videoTemporalMode.setSelectedItem("Epoch");
        else if (NslCanvas.TEMPORAL_MODE_SIMULATION == currentCanvas.getRecordVideoTemporalMode())
            videoTemporalMode.setSelectedItem("Simulation");

        fpsField.setText(Double.toString(currentCanvas.getRecordVideoFramesPerSec()));

        recordDirField.setText(currentCanvas.getRecordDir());

        temporal_epoch.setSelected(currentCanvas.temporalMode == NslCanvas.TEMPORAL_MODE_EPOCH);
        temporal_trial.setSelected(currentCanvas.temporalMode == NslCanvas.TEMPORAL_MODE_TRIAL);

        selectDirButton.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setSize(500, 300);
        add(mainPanel);
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
        yminLabel = new JLabel();
        yminLabel.setText("Ymin:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(yminLabel, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer2, gbc);
        ymin = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(ymin, gbc);
        ymaxLabel = new JLabel();
        ymaxLabel.setText("Ymax:");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(ymaxLabel, gbc);
        ymax = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(ymax, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer3, gbc);
        recordData = new JCheckBox();
        recordData.setText("Record Data");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordData, gbc);
        dataFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Matlab");
        defaultComboBoxModel1.addElement("Gnuplot");
        dataFormat.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dataFormat, gbc);
        dataTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Trial");
        defaultComboBoxModel2.addElement("Epoch");
        defaultComboBoxModel2.addElement("Simulation");
        dataTemporalMode.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dataTemporalMode, gbc);
        recordFrames = new JCheckBox();
        recordFrames.setText("Record Frames");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordFrames, gbc);
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
        label1.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        mainPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("File Format");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        mainPanel.add(label2, gbc);
        framesFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("EPS");
        defaultComboBoxModel3.addElement("JPG");
        defaultComboBoxModel3.addElement("PNG");
        framesFormat.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(framesFormat, gbc);
        framesTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("Trial");
        defaultComboBoxModel4.addElement("Epoch");
        defaultComboBoxModel4.addElement("Simulation");
        framesTemporalMode.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(framesTemporalMode, gbc);
        recordVideo = new JCheckBox();
        recordVideo.setText("Record Video");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordVideo, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("MOV");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label3, gbc);
        videoTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Trial");
        defaultComboBoxModel5.addElement("Epoch");
        defaultComboBoxModel5.addElement("Simulation");
        videoTemporalMode.setModel(defaultComboBoxModel5);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(videoTemporalMode, gbc);
        fpsLabel = new JLabel();
        fpsLabel.setText("Frames per Second:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fpsLabel, gbc);
        fpsField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fpsField, gbc);
        recordDirLabel = new JLabel();
        recordDirLabel.setText("Record Directory:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordDirLabel, gbc);
        selectDirButton = new JButton();
        selectDirButton.setText("Select Directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(selectDirButton, gbc);
        recordDirField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(recordDirField, gbc);
        okButton = new JButton();
        okButton.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(okButton, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cancelButton, gbc);
        temporalModeLabel = new JLabel();
        temporalModeLabel.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporalModeLabel, gbc);
        temporal_epoch = new JRadioButton();
        temporal_epoch.setText("Epoch");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporal_epoch, gbc);
        temporal_trial = new JRadioButton();
        temporal_trial.setText("Trial");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporal_trial, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer4, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Box Color:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label4, gbc);
        boxColor = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(boxColor, gbc);
        autoscaleCheckBox = new JCheckBox();
        autoscaleCheckBox.setText("Autoscale");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(autoscaleCheckBox, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(temporal_epoch);
        buttonGroup.add(temporal_trial);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return mainPanel;
    }
}

