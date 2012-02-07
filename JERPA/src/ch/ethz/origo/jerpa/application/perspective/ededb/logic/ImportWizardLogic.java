package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ImportFilesTableModel;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.ImportWizard;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Logic part of Import Wizard of EDEDB.
 *
 * @author Petr Miko
 */
public class ImportWizardLogic extends ImportWizard implements ActionListener {

    private EDEDBController controller;

    /**
     * Constructor.
     * Adds actions to all gui components.
     *
     * @param controller
     */
    public ImportWizardLogic(EDEDBController controller) {
        super();

        this.controller = controller;

        existingRadio.addActionListener(this);
        newRadio.addActionListener(this);
        existingRadio.setActionCommand("experiment");
        newRadio.setActionCommand("experiment");

        experimentsCombo.addActionListener(this);
        experimentsCombo.setActionCommand("expSelected");

        addWeather.addActionListener(this);
        addWeather.setActionCommand("addWeather");

        addScenario.addActionListener(this);
        addScenario.setActionCommand("addScenario");

        addGroup.addActionListener(this);
        addGroup.setActionCommand("addGroup");

        addFile.addActionListener(this);
        addFile.setActionCommand("addFile");

        removeFile.addActionListener(this);
        removeFile.setActionCommand("removeFile");

        confirmButton.addActionListener(this);
        confirmButton.setActionCommand("ok");

        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("cancel");

        switchView();
    }

    public void actionPerformed(ActionEvent e) {

        if ("experiment".equals(e.getActionCommand())) {
            switchView();
        } else if ("expSelected".equals(e.getActionCommand())) {
            expSelected();
        } else if ("addGroup".equals(e.getActionCommand())) {
            //TODO add new research group dialog
        } else if ("addScenario".equals(e.getActionCommand())) {
            //TODO add new scenario dialog
        } else if ("addWeather".equals(e.getActionCommand())) {
            //TODO add new type of weather dialog
        } else if ("addFile".equals(e.getActionCommand())) {
            addFile();
        } else if ("removeFile".equals(e.getActionCommand())) {
            removeFile();
        } else if ("ok".equals(e.getActionCommand())) {
            confirm();
        } else if ("cancel".equals(e.getActionCommand())) {
            closeWizard();
        }
    }

    /**
     * Method for saving changes to experiment and storing them into db.
     */
    private void confirm() {

        Thread saveThread = new Thread(new Runnable() {
            public void run() {
                Working.setActivity(true, "working.ededb.import");
                confirmButton.setEnabled(false);
                cancelButton.setEnabled(false);

                progressBar.setVisible(true);

                if (existingRadio.isSelected()) {
                    ImportFilesTableModel model = (ImportFilesTableModel) importTable.getModel();
                    List<File> files = model.getFiles();

                    Experiment exp = (Experiment) experimentsCombo.getSelectedItem();
                    Set<String> fileNames = new HashSet<String>();
                    for (DataFile file : exp.getDataFiles()) {
                        HibernateUtil.rebind(file);
                        fileNames.add(file.getFilename());
                    }


                    for (File file : files) {
                        if (!fileNames.contains(file.getName())) {
                            dataFileDao.createDataFile(exp, file);
                        } else {

                            int choice = JOptionPane.showConfirmDialog(null, resource.getString("importWizard.ededb.addFile.overwrite1") + file.getName() + resource.getString("importWizard.ededb.addFile.overwrite2"),
                                    resource.getString("importWizard.ededb.addFile.overwritePrompt"),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                            if (choice == JOptionPane.YES_OPTION) {
                                for (DataFile dataFile : exp.getDataFiles()) {
                                    HibernateUtil.rebind(dataFile);

                                    if (file.getName().equals(dataFile.getFilename())) {
                                        dataFileDao.overwriteDataFile(dataFile, file);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {
                    //TODO store new experiment
                }

                controller.update();
                progressBar.setVisible(false);
                Working.setActivity(false, "working.ededb.import");
                closeWizard();
            }
        });

        saveThread.start();
    }

    /**
     * Method for closing Import Wizard.
     */
    private void closeWizard() {
        this.dispose();
    }

    /**
     * Sets gui components values in accordance to selected experiment from roll menu.
     */
    private void expSelected() {

        Experiment exp = (Experiment) experimentsCombo.getSelectedItem();

        expOwnerCombo.setSelectedItem(HibernateUtil.rebind(exp.getOwner()));
        expSubjectCombo.setSelectedItem(HibernateUtil.rebind(exp.getSubject()));
        weatherCombo.setSelectedItem(HibernateUtil.rebind(exp.getWeather()));
        scenarioCombo.setSelectedItem(HibernateUtil.rebind(exp.getScenario()));
        groupCombo.setSelectedItem(HibernateUtil.rebind(exp.getResearchGroup()));

        expStartTimeField.setValue(exp.getStartTime());
        expEndTimeField.setValue(exp.getEndTime());
        expTemperatureField.setValue(exp.getTemperature());
        weatherNoteArea.setText(exp.getWeathernote());

        super.repaint();

    }

    /**
     * Sets components active/inactive in accordance to selected radio button.
     */
    private void switchView() {
        boolean existing = existingRadio.isSelected();

        experimentsCombo.setEnabled(existing);
        expOwnerCombo.setEnabled(!existing);
        expSubjectCombo.setEnabled(!existing);
        weatherCombo.setEnabled(!existing);
        scenarioCombo.setEnabled(!existing);
        groupCombo.setEnabled(!existing);

        expStartTimeField.setEnabled(!existing);
        expEndTimeField.setEnabled(!existing);
        expTemperatureField.setEnabled(!existing);
        weatherNoteArea.setEnabled(!existing);

        addWeather.setEnabled(!existing);
        addScenario.setEnabled(!existing);
        addGroup.setEnabled(!existing);

        if (!existing) {
            expOwnerCombo.setSelectedIndex(0);
            expSubjectCombo.setSelectedIndex(0);
            weatherCombo.setSelectedIndex(0);
            groupCombo.setSelectedIndex(0);

            expEndTimeField.setValue(null);
            expStartTimeField.setValue(null);
            expTemperatureField.setValue(null);
            weatherNoteArea.setText(null);
        }

        super.repaint();
    }

    /**
     * Method for adding files into file list.
     */
    private void addFile() {
        JFileChooser fileChooser = new JFileChooser("/");
        fileChooser.setMultiSelectionEnabled(true);

        int retValue = fileChooser.showDialog(this, resource.getString("importWizard.ededb.addFile.add"));
        if (retValue == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                importTable.add(file);
            }

            importTable.revalidate();
            importTable.repaint();
        }
        this.repaint();
    }

    /**
     * Method for removing selected file from file list.
     */
    private void removeFile() {

        int[] selectedRows = importTable.getSelectedRows();

        if (selectedRows.length > 0) {
            for (Integer selectedRow : selectedRows) {
                int selectedModelRow = importTable.convertRowIndexToModel(selectedRow);

                if (selectedModelRow > 0) {
                    importTable.remove(selectedModelRow);
                }
            }
            importTable.revalidate();
            importTable.repaint();
        }

        this.repaint();
    }
}
