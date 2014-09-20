/* SCCS  @(#)NslSpatialCanvasProperty.java	1.7---09/01/99--00:15:48 */

// Copyright: Copyright (c) 1997 University of Southern California Brain Project.
// Copyright: This software may be freely copied provided the toplevel
// Copyright: COPYRIGHT file is included with each such copy.
// Copyright: Email nsl@java.usc.edu.

//--------------------------------------
// $Log: NslSpatialCanvasProperty.java,v $
// Revision 1.1  1997/11/06 03:18:59  erhan
// NSL3.0.b
//
// Revision 1.3  1997/05/09 22:30:23  danjie
// add some comments and Log
//
//--------------------------------------


package nslj.src.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

class NslSpatialCanvasProperty extends JDialog implements ActionListener
{
    private NslSpatialCanvas currentCanvas;
    protected Vector<JComboBox> line_color;
    protected Vector<JComboBox> line_style;
    protected Vector<JButton> removeButtons;
    private JPanel mainPanel;
    private JLabel yminLabel;
    private JTextField ymin;
    private JLabel ymaxLabel;
    private JTextField ymax;
    private JCheckBox showLegendCheckbox;
    private JLabel temporalModeLabel;
    private JRadioButton temporal_epoch;
    private JRadioButton temporal_trial;
    private JCheckBox recordData;
    private JComboBox dataFormat;
    private JComboBox dataTemporalMode;
    private JCheckBox recordFrames;
    private JComboBox framesFormat;
    private JComboBox framesTemporalMode;
    private JCheckBox recordVideo;
    private JComboBox videoTemporalMode;
    private JLabel fpsLabel;
    private JTextField fpsField;
    private JLabel recordDirLabel;
    private JButton selectDirButton;
    private JTextField recordDirField;
    private JButton okButton;
    private JButton cancelButton;
    private JComboBox modeComboBox;
    private JCheckBox autoscaleCheckBox;
    private JPanel linePanel;

    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        if (arg.equals("Ok"))
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

        if (temporal_trial.isSelected() && currentCanvas.getTemporalMode() != NslCanvas.TEMPORAL_MODE_TRIAL)
            currentCanvas.setTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (temporal_epoch.isSelected() && currentCanvas.getTemporalMode() != NslCanvas.TEMPORAL_MODE_EPOCH)
            currentCanvas.setTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);

        currentCanvas.setShowLegend(showLegendCheckbox.isSelected());

        if (modeComboBox.getSelectedItem().equals("Color"))
            currentCanvas.setMode(NslSpatialCanvas.MODE_COLOR);
        else if (modeComboBox.getSelectedItem().equals("Grayscale"))
            currentCanvas.setMode(NslSpatialCanvas.MODE_GREY);
        else
            currentCanvas.setMode(NslSpatialCanvas.MODE_BW);

        // change all other properties here
        currentCanvas.update();
    }

    public NslSpatialCanvasProperty(NslFrame parent)
    {
        this(parent, "Spatial Plot Properties");
    }

    public NslSpatialCanvasProperty(NslFrame parent, String name)
    {
        super(parent, name, true);
        setupLayout();
    }

    private void setupLayout()
    {
        currentCanvas = (NslSpatialCanvas) ((NslFrame) getParent()).getCurrentCanvas();

        ymin.setText(Double.toString(currentCanvas.getMin()));
        ymax.setText(Double.toString(currentCanvas.getMax()));

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

        recordDirField.setText(currentCanvas.getRecordDir());

        temporal_epoch.setSelected(currentCanvas.temporalMode == NslCanvas.TEMPORAL_MODE_EPOCH);
        temporal_trial.setSelected(currentCanvas.temporalMode == NslCanvas.TEMPORAL_MODE_TRIAL);

        showLegendCheckbox.setSelected(currentCanvas.showLegend);

        if (currentCanvas.getMode() == NslSpatialCanvas.MODE_COLOR)
            modeComboBox.setSelectedItem("Color");
        else if (currentCanvas.getMode() == NslSpatialCanvas.MODE_GREY)
            modeComboBox.setSelectedItem("Grayscale");
        else
            modeComboBox.setSelectedItem("B/W");

        selectDirButton.addActionListener(this);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setSize(500, 500);
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
        mainPanel.setMinimumSize(new Dimension(363, 279));
        mainPanel.setPreferredSize(new Dimension(363, 274));
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer2, gbc);
        ymaxLabel = new JLabel();
        ymaxLabel.setText("Ymax:");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(ymaxLabel, gbc);
        ymax = new JTextField();
        ymax.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(ymax, gbc);
        ymin = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(ymin, gbc);
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
        label1.setText("File Format");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        mainPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        mainPanel.add(label2, gbc);
        temporalModeLabel = new JLabel();
        temporalModeLabel.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporalModeLabel, gbc);
        temporal_epoch = new JRadioButton();
        temporal_epoch.setText("Epoch");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporal_epoch, gbc);
        temporal_trial = new JRadioButton();
        temporal_trial.setText("Trial");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(temporal_trial, gbc);
        recordData = new JCheckBox();
        recordData.setText("Record Data");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordData, gbc);
        dataFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Matlab");
        defaultComboBoxModel1.addElement("Gnuplot");
        dataFormat.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
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
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dataTemporalMode, gbc);
        recordFrames = new JCheckBox();
        recordFrames.setText("Record Frames");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordFrames, gbc);
        framesFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("EPS");
        defaultComboBoxModel3.addElement("JPG");
        defaultComboBoxModel3.addElement("PNG");
        framesFormat.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
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
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(framesTemporalMode, gbc);
        recordVideo = new JCheckBox();
        recordVideo.setText("Record Video");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordVideo, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("MOV");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
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
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(videoTemporalMode, gbc);
        fpsLabel = new JLabel();
        fpsLabel.setText("Frames per Second:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fpsLabel, gbc);
        fpsField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fpsField, gbc);
        recordDirLabel = new JLabel();
        recordDirLabel.setText("Record Directory:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordDirLabel, gbc);
        selectDirButton = new JButton();
        selectDirButton.setText("Select Directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(selectDirButton, gbc);
        recordDirField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(recordDirField, gbc);
        okButton = new JButton();
        okButton.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(okButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer3, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cancelButton, gbc);
        modeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("Color");
        defaultComboBoxModel6.addElement("Grayscale");
        defaultComboBoxModel6.addElement("B/W");
        modeComboBox.setModel(defaultComboBoxModel6);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(modeComboBox, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Shading Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label4, gbc);
        showLegendCheckbox = new JCheckBox();
        showLegendCheckbox.setText("Show Legend");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(showLegendCheckbox, gbc);
        autoscaleCheckBox = new JCheckBox();
        autoscaleCheckBox.setText("Autoscale");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(autoscaleCheckBox, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 13;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane1, gbc);
        linePanel = new JPanel();
        linePanel.setLayout(new GridBagLayout());
        scrollPane1.setViewportView(linePanel);
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
