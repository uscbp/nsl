/* SCCS  @(#)NslTemporalCanvasProperty.java	1.7---09/01/99--00:15:48 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslTemporalCanvasProperty.java,v $
// Revision 1.3  1997/05/09 22:30:26  danjie
// add some comments and Log
//
//--------------------------------------

package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class NslTemporalCanvasProperty extends JDialog implements ActionListener
{
    protected NslTemporalCanvas currentCanvas;
    protected JTextField ymax, ymin;
    protected Vector<JComboBox> line_color;
    protected Vector<JComboBox> line_style;
    protected Vector<JButton> removeButtons;
    protected JRadioButton temporal_trial, temporal_epoch;
    protected JCheckBox recordData;
    protected JComboBox dataFormat;
    protected JComboBox dataTemporalMode;
    protected JCheckBox recordFrames;
    protected JComboBox framesFormat;
    protected JComboBox framesTemporalMode;
    protected JCheckBox recordVideo;
    protected JComboBox videoTemporalMode;
    protected JLabel fpsLabel;
    protected JTextField fpsField;
    protected JButton selectDirButton;
    protected JLabel recordDirLabel;
    protected JTextField recordDirField;
    protected JCheckBox showAxisLabels;
    protected JButton okButton, cancelButton;
    protected JLabel yminLabel, ymaxLabel;
    protected JLabel temporalModelLabel;
    protected JPanel linePanel;
    protected JPanel mainPanel;
    private JCheckBox showLegend;
    private JCheckBox autoscaleCheckBox;
    
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getSource().equals(okButton))
        {
            dispose();
            updateCanvas();
        }
        else if (removeButtons.contains(evt.getSource()))
        {
            currentCanvas.variables.remove(removeButtons.indexOf(evt.getSource()));
            linePanel.removeAll();
            setupLayout();
            updateCanvas();
            linePanel.validate();
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
        else if (evt.getSource().equals(cancelButton))
        {
            dispose();
        }
    }

    protected void updateCanvas()
    {
        currentCanvas.setMinMax(Float.valueOf(ymin.getText().trim()), Float.valueOf(ymax.getText().trim()));
        currentCanvas.setAutoScale(autoscaleCheckBox.isSelected());
        for (int i = 0; i < currentCanvas.variables.size(); i++)
        {
            currentCanvas.variables.get(i).info.setColor(NslColor.colorTypes[line_color.get(i).getSelectedIndex()]);
            currentCanvas.variables.get(i).info.setStyle(line_style.get(i).getSelectedIndex());
        }
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
        else if (framesFormat.getSelectedItem().equals("PNG"))
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

        currentCanvas.setShowAxisLabels(showAxisLabels.isSelected());
        currentCanvas.setShowLegend(showLegend.isSelected());
        if (temporal_trial.isSelected() && currentCanvas.getTemporalMode() != NslCanvas.TEMPORAL_MODE_TRIAL)
            currentCanvas.setTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (temporal_epoch.isSelected() && currentCanvas.getTemporalMode() != NslCanvas.TEMPORAL_MODE_EPOCH)
            currentCanvas.setTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);
        // change all other properties here
        currentCanvas.update();
    }

    public NslTemporalCanvasProperty(NslFrame parent, String name)
    {
        super(parent, name, true);

        setupLayout();
    }

    public NslTemporalCanvasProperty(NslFrame parent)
    {
        this(parent, "Temporal Plot Properties");
    }

    protected void setupLayout()
    {
        currentCanvas = (NslTemporalCanvas) ((NslFrame) getParent()).getCurrentCanvas();

        double y_min = currentCanvas.getMin();
        double y_max = currentCanvas.getMax();
        ymin.setText(Double.toString(y_min));
        ymax.setText(Double.toString(y_max));

        autoscaleCheckBox.setSelected(currentCanvas.isAutoScale());

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
        selectDirButton.addActionListener(this);

        showAxisLabels.setSelected(currentCanvas.showAxisLabels);
        showLegend.setSelected(currentCanvas.showLegend);

        temporal_epoch.setSelected(currentCanvas.temporalMode == NslCanvas.TEMPORAL_MODE_EPOCH);
        temporal_trial.setSelected(currentCanvas.temporalMode == NslCanvas.TEMPORAL_MODE_TRIAL);

        linePanel.setLayout(new GridLayout(4 * currentCanvas.variables.size(), 1));
        line_color = new Vector<JComboBox>();
        line_style = new Vector<JComboBox>();
        removeButtons = new Vector<JButton>();
        for (int i = 0; i < currentCanvas.variables.size(); i++)
        {
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(1, 2));
            p.add(new JLabel(currentCanvas.variables.get(i).info.name));
            JButton remove = new JButton("Remove");
            remove.addActionListener(this);
            if (currentCanvas.variables.size() == 1)
                remove.setEnabled(false);
            removeButtons.add(remove);
            p.add(remove);
            linePanel.add(p);

            JPanel p2 = new JPanel();
            p2.setLayout(new GridLayout(1, 2));
            p2.add(new JLabel("Line Style:"));
            JComboBox box = new JComboBox();
            box.addItem("Solid");
            box.addItem("Invisible");
            box.addItem("Dotted");
            box.addItem("Dashdot");
            box.addItem("Dashed");
            box.setSelectedIndex(currentCanvas.variables.get(i).info.getStyle());
            line_style.add(box);
            p2.add(box);
            linePanel.add(p2);

            JPanel p3 = new JPanel();
            p3.setLayout(new GridLayout(1, 2));
            p3.add(new JLabel("Line Color:"));
            box = new JComboBox(NslColor.colorNames);
            box.setSelectedIndex(NslColor.getIndexByColor(currentCanvas.variables.get(i).info.getColor()));
            line_color.add(box);
            p3.add(box);
            linePanel.add(p3);

            linePanel.add(new JSeparator(JSeparator.HORIZONTAL));
        }
        setSize(500, 500);
        add(mainPanel);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(yminLabel, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 13;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane1, gbc);
        linePanel = new JPanel();
        linePanel.setLayout(new GridBagLayout());
        scrollPane1.setViewportView(linePanel);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer2, gbc);
        ymin = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(ymin, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer3, gbc);
        okButton = new JButton();
        okButton.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(okButton, gbc);
        recordData = new JCheckBox();
        recordData.setText("Record Data");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordData, gbc);
        recordFrames = new JCheckBox();
        recordFrames.setText("Record Frames");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordFrames, gbc);
        framesFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("EPS");
        defaultComboBoxModel1.addElement("JPG");
        defaultComboBoxModel1.addElement("PNG");
        framesFormat.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(framesFormat, gbc);
        framesTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Trial");
        defaultComboBoxModel2.addElement("Epoch");
        defaultComboBoxModel2.addElement("Simulation");
        framesTemporalMode.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(framesTemporalMode, gbc);
        dataFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Matlab");
        defaultComboBoxModel3.addElement("Gnuplot");
        dataFormat.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dataFormat, gbc);
        dataTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("Trial");
        defaultComboBoxModel4.addElement("Epoch");
        defaultComboBoxModel4.addElement("Simulation");
        dataTemporalMode.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dataTemporalMode, gbc);
        recordVideo = new JCheckBox();
        recordVideo.setText("Record Video");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordVideo, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer4, gbc);
        selectDirButton = new JButton();
        selectDirButton.setText("Select Directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(selectDirButton, gbc);
        recordDirLabel = new JLabel();
        recordDirLabel.setText("Record Directory:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordDirLabel, gbc);
        recordDirField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 10;
        gbc.weightx = 0.02;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(recordDirField, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cancelButton, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer5, gbc);
        ymaxLabel = new JLabel();
        ymaxLabel.setText("Ymax:");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(ymaxLabel, gbc);
        ymax = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 0;
        gbc.weightx = 0.05;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(ymax, gbc);
        fpsField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fpsField, gbc);
        fpsLabel = new JLabel();
        fpsLabel.setText("Frames per Second:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fpsLabel, gbc);
        videoTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Trial");
        defaultComboBoxModel5.addElement("Epoch");
        defaultComboBoxModel5.addElement("Simulation");
        videoTemporalMode.setModel(defaultComboBoxModel5);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(videoTemporalMode, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("MOV");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("File Format");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 0, 0);
        mainPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
        label3.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        mainPanel.add(label3, gbc);
        showAxisLabels = new JCheckBox();
        showAxisLabels.setText("Show Labels");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(showAxisLabels, gbc);
        showLegend = new JCheckBox();
        showLegend.setText("Show Legend");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(showLegend, gbc);
        temporalModelLabel = new JLabel();
        temporalModelLabel.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporalModelLabel, gbc);
        temporal_epoch = new JRadioButton();
        temporal_epoch.setText("Epoch");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporal_epoch, gbc);
        temporal_trial = new JRadioButton();
        temporal_trial.setText("Trial");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporal_trial, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer6, gbc);
        autoscaleCheckBox = new JCheckBox();
        autoscaleCheckBox.setText("Autoscale");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
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


