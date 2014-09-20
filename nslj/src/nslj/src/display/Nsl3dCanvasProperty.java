package nslj.src.display;

import nslj.src.display.video.QuickTimeOutputStream;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.*;

class Nsl3dCanvasProperty extends JDialog implements ActionListener, ItemListener
{
    private Nsl3dCanvas currentCanvas;
    private JPanel mainPanel;
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

    public void actionPerformed(ActionEvent evt)
    {
        String arg = evt.getActionCommand();

        if (arg.equals("Ok"))
        {
            dispose();
            updateCanvas();
        }
        else if (arg.equals("Select Directory"))
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
        currentCanvas.setRecordFrames(recordFrames.isSelected());
        if (framesFormat.getSelectedItem().equals("PNG"))
            currentCanvas.setRecordFramesFormat(NslCanvas.RECORD_FRAMES_FORMAT_PNG);
        else if (framesFormat.getSelectedItem().equals("EPS"))
            currentCanvas.setRecordFramesFormat(NslCanvas.RECORD_FRAMES_FORMAT_JPG);
        else
            currentCanvas.setRecordFramesFormat(NslCanvas.RECORD_FRAMES_FORMAT_JPG);
        if (framesTemporalMode.getSelectedItem().equals("Trial"))
            currentCanvas.setRecordFramesTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (framesTemporalMode.getSelectedItem().equals("Epoch"))
            currentCanvas.setRecordFramesTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);
        else if (framesTemporalMode.getSelectedItem().equals("Simulation"))
            currentCanvas.setRecordFramesTemporalMode(NslCanvas.TEMPORAL_MODE_SIMULATION);

        currentCanvas.setRecordVideo(recordVideo.isSelected());
        currentCanvas.setRecordVideoFramesPerSec(Double.parseDouble(fpsField.getText()));
        if (videoTemporalMode.getSelectedItem().equals("Trial"))
            currentCanvas.setRecordVideoTemporalMode(NslCanvas.TEMPORAL_MODE_TRIAL);
        else if (videoTemporalMode.getSelectedItem().equals("Epoch"))
            currentCanvas.setRecordVideoTemporalMode(NslCanvas.TEMPORAL_MODE_EPOCH);
        else if (videoTemporalMode.getSelectedItem().equals("Simulation"))
            currentCanvas.setRecordVideoTemporalMode(NslCanvas.TEMPORAL_MODE_SIMULATION);

        if (recordDirField.getText().length() > 0)
            currentCanvas.setRecordDir(recordDirField.getText());

        // change all other properties here
        currentCanvas.update();
    }

    public void itemStateChanged(ItemEvent evt)
    {
    }

    public Nsl3dCanvasProperty(NslFrame parent)
    {
        super(parent, "3d Plot Properties", true);

        setupLayout();
    }

    protected void setupLayout()
    {
        currentCanvas = (Nsl3dCanvas) ((NslFrame) getParent()).getCurrentCanvas();

        recordFrames.setSelected(currentCanvas.getRecordFrames());
        if (currentCanvas.getRecordFramesFormat() == NslCanvas.RECORD_FRAMES_FORMAT_PNG)
            framesFormat.setSelectedItem("PNG");
        else if (currentCanvas.getRecordFramesFormat() == NslCanvas.RECORD_FRAMES_FORMAT_EPS)
            framesFormat.setSelectedItem("EPS");
        else
            framesFormat.setSelectedItem("JPG");
        if (currentCanvas.getRecordFramesTemporalMode() == NslCanvas.TEMPORAL_MODE_EPOCH)
            framesTemporalMode.setSelectedItem("Epoch");
        else if (currentCanvas.getRecordFramesTemporalMode() == NslCanvas.TEMPORAL_MODE_TRIAL)
            framesTemporalMode.setSelectedItem("Trial");
        else if (currentCanvas.getRecordFramesTemporalMode() == NslCanvas.TEMPORAL_MODE_SIMULATION)
            framesTemporalMode.setSelectedItem("Simulation");

        recordVideo.setSelected(currentCanvas.getRecordVideo());
        if (currentCanvas.getRecordVideoTemporalMode() == NslCanvas.TEMPORAL_MODE_EPOCH)
            videoTemporalMode.setSelectedItem("Epoch");
        else if (currentCanvas.getRecordVideoTemporalMode() == NslCanvas.TEMPORAL_MODE_TRIAL)
            videoTemporalMode.setSelectedItem("Trial");
        else if (currentCanvas.getRecordVideoTemporalMode() == NslCanvas.TEMPORAL_MODE_SIMULATION)
            videoTemporalMode.setSelectedItem("Simulation");

        fpsField.setText(Double.toString(currentCanvas.getRecordVideoFramesPerSec()));

        recordDirField.setText(currentCanvas.getRecordDir());

        setSize(500, 200);
        add(mainPanel);

        selectDirButton.addActionListener(this);
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
        recordFrames = new JCheckBox();
        recordFrames.setText("Record Frames");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordFrames, gbc);
        framesFormat = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("EPS");
        defaultComboBoxModel1.addElement("JPG");
        defaultComboBoxModel1.addElement("PNG");
        framesFormat.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
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
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(framesTemporalMode, gbc);
        recordVideo = new JCheckBox();
        recordVideo.setText("Record Video");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordVideo, gbc);
        recordDirLabel = new JLabel();
        recordDirLabel.setText("Record Directory:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(recordDirLabel, gbc);
        selectDirButton = new JButton();
        selectDirButton.setText("Select Directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(selectDirButton, gbc);
        recordDirField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(recordDirField, gbc);
        okButton = new JButton();
        okButton.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(okButton, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(cancelButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(spacer2, gbc);
        fpsLabel = new JLabel();
        fpsLabel.setText("Frames per Second:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fpsLabel, gbc);
        fpsField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fpsField, gbc);
        videoTemporalMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Trial");
        defaultComboBoxModel3.addElement("Epoch");
        defaultComboBoxModel3.addElement("Simulation");
        videoTemporalMode.setModel(defaultComboBoxModel3);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(videoTemporalMode, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("MOV");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("File Format");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        mainPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
        label3.setText("Temporal Mode");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        mainPanel.add(label3, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(spacer3, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return mainPanel;
    }
}
