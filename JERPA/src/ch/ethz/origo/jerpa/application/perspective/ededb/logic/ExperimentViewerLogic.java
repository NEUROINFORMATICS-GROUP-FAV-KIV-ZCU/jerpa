package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.dao.DaoException;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.dao.ExperimentDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.ExperimentViewer;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import org.apache.log4j.Logger;

import javax.activation.DataSource;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * @author Petr Miko
 *         <p/>
 *         Logic part of EDEDB experiment browser.
 */
public class ExperimentViewerLogic extends ExperimentViewer implements Observer {
    private static final long serialVersionUID = 4318865850000265030L;
    private static final Logger log = Logger.getLogger(ExperimentViewer.class);

    private ExperimentDao experimentDao = ExperimentDao.getInstance();
    private DataFileDao dataFileDao = DataFileDao.getInstance();
    private List<Experiment> selectedExps;
    private EDEDBController controller;

    private long expVersion = 0;
    private long dataVersion = 0;

    /**
     * Constructor.
     *
     * @param controller EDEDB Controller instance
     */
    public ExperimentViewerLogic(EDEDBController controller) {
        super();
        this.controller = controller;

        initExperimentTable();
        initDataTable();
        updateExpTable();

    }

    /**
     * Init method for experiments table.
     */
    private void initExperimentTable() {
        selectedExps = new ArrayList<Experiment>();

        ListSelectionModel selectionModel = expTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && expTable.getSelectedRow() != -1) {

                    Working.setActivity(true, "working.ededb.update.datatable");
                    selectedExps.clear();
                    for (Integer i : expTable.getSelectedRows()) {
                        int selected = expModel.getExperimentAtIndex(expTable.convertRowIndexToModel(i)).getExperimentId();
                        try {
                            selectedExps.add(experimentDao.get(selected));
                        } catch (DaoException e1) {
                            log.error(e1.getMessage(), e1);
                        }
                    }
                    updateDataTable();
                    Working.setActivity(false, "working.ededb.update.datatable");
                }
            }
        });
    }

    /**
     * Init method for data files table.
     */
    private void initDataTable() {
        dataTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (dataTable.getSelectedColumn() != DataTableModel.ACTION_COLUMN && dataTable.getSelectedRow() != -1) {
                    dataModel.getData().get(dataTable.getSelectedRow())
                            .setSelected(!dataModel.getData().get(dataTable.getSelectedRow()).isSelected());
                }

                dataTable.revalidate();
                dataTable.repaint();
            }
        });

    }

    /**
     * Method for updating experiment table.
     */
    public void updateExpTable() {
        synchronized (ExpTableModel.class) {
            Working.setActivity(true, "working.ededb.update.exptable");
            List<Experiment> experiments = experimentDao.getAll();
            if (experiments.size() != expModel.getRowCount() || experimentDao.getLastRevision() != expVersion || DataSyncer.experimentsUpdated) {
                expVersion = experimentDao.getLastRevision();
                DataSyncer.experimentsUpdated = false;
                expModel.clear();
                for (Experiment exp : experiments) {
                    expModel.addRow(exp);
                }
                repaint();
            }
            Working.setActivity(false, "working.ededb.update.exptable");
        }
    }

    /**
     * Method filling data view table with experiment's files information. Shown
     * information depends on selected experiment in experiment view table.
     */
    public void updateDataTable() {

        synchronized (DataTableModel.class) {

            List<DataFile> dataFiles = dataFileDao.getAllFromExperiments(selectedExps);
            if (dataFiles.size() != dataModel.getRowCount() || dataVersion != dataFileDao.getLastRevision() || DataSyncer.dataUpdated) {
                DataSyncer.dataUpdated = false;
                dataVersion = dataFileDao.getLastRevision();
                dataModel.clear();
                for (DataFile file : dataFiles) {
                    FileState state = (controller.getDownloader().isDownloading(file) ? FileState.DOWNLOADING : dataFileDao.getFileState(file));
                    dataModel.addRow(file, state);
                }
                dataTable.repaint();
            }

            for (DataRowModel row : dataModel.getData()) {

                if (controller.getDownloader().isDownloading(row.getDataFile())) {
                    row.setState(FileState.DOWNLOADING);
                } else {
                    row.setState(dataFileDao.getFileState(row.getDataFile()));
                }
            }
            repaint();
        }

    }

    /**
     * Returns selected files in data view table.
     *
     * @return list DataRowModel (files info) of users selection in data view
     *         table
     */
    public List<DataRowModel> getSelectedFiles() {
        synchronized (ExperimentViewerLogic.class) {
            List<DataRowModel> data = dataModel.getData();
            List<DataRowModel> selectedFiles = new ArrayList<DataRowModel>();

            for (DataRowModel row : data) {
                if (row.isSelected()) {
                    selectedFiles.add(row);
                }
            }
            return selectedFiles;
        }
    }

    /**
     * Getter of first selected Experiment in experiment table.
     *
     * @return first selected experiment
     */
    public Experiment getSelectedExperiment() {
        synchronized (ExpTableModel.class) {
            {
                int selectedRow = expTable.getSelectedRow();

                if (selectedRow >= 0 && selectedRow < expModel.getRowCount()) {
                    int modelId = expTable.convertRowIndexToModel(selectedRow);
                    return expModel.getExperimentAtIndex(modelId);
                }

                return null;
            }
        }
    }

    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateExpTable();
                updateDataTable();
            }
        });
    }
}