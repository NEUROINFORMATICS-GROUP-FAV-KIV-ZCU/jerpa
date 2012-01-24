package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.dao.ExperimentDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Petr Miko
 *         <p/>
 *         Logic part of EDEDB experiment browser.
 */
public class ExperimentViewerLogic extends ExperimentViewer implements Observer, ILanguage {
    private final static Logger log = Logger.getLogger(ExperimentViewerLogic.class);
    private static final long serialVersionUID = 4318865850000265030L;
    private String resourceBundlePath;

    private ExperimentDao experimentDao = DaoFactory.getExperimentDao();
    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();
    private List<Experiment> selectedExps;

    /**
     * Constructor.
     */
    public ExperimentViewerLogic() {
        super();

        LanguageObservable.getInstance().attach(this);

        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

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
                        int selected = (Integer) expTable.getValueAt(i, 0);
                        selectedExps.add(experimentDao.get(selected));
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

        synchronized (expModel) {

            Working.setActivity(true, "working.ededb.update.exptable");
            expModel.clear();
            List<Experiment> experiments = experimentDao.getAll();

            for (Experiment exp : experiments) {
                expModel.addRow(exp);
            }
            repaint();
            Working.setActivity(false, "working.ededb.update.exptable");
        }
    }

    /**
     * Method clearing all data from experiment view table
     */
    public void clearExpTable() {
        synchronized (expModel) {
            expModel.clear();
        }
    }

    /**
     * Method clearing all data from data view table
     */
    public void clearDataTable() {
        synchronized (dataModel) {
            dataModel.clear();
        }
    }

    /**
     * Method filling data view table with experiment's files information. Shown
     * information depends on selected experiment in experiment view table.
     */
    public void updateDataTable() {

        synchronized (dataModel) {
            clearDataTable();

            List<DataFile> dataFiles = dataFileDao.getAllFromExperiments(selectedExps);

            for (DataFile file : dataFiles)
                dataModel.addRow(file, dataFileDao.getFileState(file));
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
        synchronized (dataModel) {
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
     * Method for returning row contents.
     *
     * @return rows of data table
     */
    public List<DataRowModel> getRows() {
        synchronized (dataModel) {
            return dataModel.getData();
        }
    }

    /**
     * Setter of localization resource bundle path
     *
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
    }

    /**
     * Getter of path to resource bundle.
     *
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource bundle key.
     *
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     *
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {

    }

    public void update(Observable o, Object arg) {
        updateExpTable();

        synchronized (dataModel) {
            for (DataRowModel row : dataModel.getData()) {
                if (row.getState() != FileState.DOWNLOADING) {
                    row.setState(dataFileDao.getFileState(row.getDataFile()));
                }
            }
        }

        this.repaint();
    }
}
