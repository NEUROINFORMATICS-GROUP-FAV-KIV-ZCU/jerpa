package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataCellRenderer;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.generated.SOAPException_Exception;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 * Class for displaying experiments and its data in online mode.
 *
 * @author Petr Miko
 */
public class OnlineTables extends JSplitPane implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private ExpTableModel expModel;
    private DataTableModel dataModel;
    private JXTable expTable;
    private JXTable dataTable;
    private EDEDBController controller;
    private EDEDClient session;
    private String tableValueYes;
    private String tableValueNo;
    private String expInfoText;
    private String expInfoDesc;
    private String errorConnectionText;
    private String errorConnectionDesc;
    private String errorRangeDesc;

    /**
     * Constructor creating basic JSplitPane interface.
     * @param controller EDEDB EDEDBController class
     * @param session EDEDSession session
     */
    public OnlineTables(EDEDBController controller, EDEDClient session) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initTexts();

        this.controller = controller;
        this.session = session;

        this.setResizeWeight(.5d);

        this.setLeftComponent(createExpTable());
        this.setRightComponent(createDataTable());

        this.setDividerLocation(300 + this.getInsets().left);

    }

    /**
     * Method creating Experiment JXTable within a JScrollPane.
     * @return Experiment view table
     */
    private Container createExpTable() {
        expModel = new ExpTableModel();
        expTable = new JXTable(expModel);

        expTable.setAutoCreateRowSorter(true);
        expTable.setFillsViewportHeight(true);
        expTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        expTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel selectionModel = expTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && expModel.getRowCount() > 0) {
                    updateDataTable(expTable.getSelectedRow());
                }
            }
        });

        return new JScrollPane(expTable);
    }

    /**
     * Method creating Data JXTable within a JScrollPane.
     * @return Experiment's data view table
     */
    private Container createDataTable() {
        dataModel = new DataTableModel();
        dataTable = new JXTable(dataModel);
        dataTable.setDefaultRenderer(Object.class, new DataCellRenderer(dataModel));

        dataTable.setAutoCreateRowSorter(true);
        dataTable.setFillsViewportHeight(true);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(dataTable);
    }

    /**
     * Method clearing all data from experiment view table
     */
    public void clearExpTable() {
        expModel.clear();
    }

    /**
     * Method clearing all data from data view table
     */
    public void clearDataTable() {
        dataModel.clear();
    }

    /**
     * Method for filling experiment view table with information about experiments. Information depends on Owner/Subject button selection.
     */
    public void updateExpTable() {

        Thread updateExpThread = new Thread(new Runnable() {

            public void run() {
                if (session.isConnected()) {
                    java.util.List<ExperimentInfo> availableExperiments;

                    availableExperiments = session.getService().getExperiments(controller.getRights());
                    Working.setActivity(false, "working.ededb.update.exptable");

                    if (availableExperiments != null) {
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                availableExperiments.size() + " " + expInfoText,
                                expInfoDesc,
                                JOptionPane.INFORMATION_MESSAGE);

                        clearExpTable();

                        for (ExperimentInfo availableExperiment : availableExperiments) {
                            expModel.addRow(availableExperiment);
                        }

                        clearDataTable();
                    } else {
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                errorConnectionText,
                                errorConnectionDesc,
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    clearExpTable();
                    clearDataTable();
                }

                repaint();
            }
        });

        Working.setActivity(true, "working.ededb.update.exptable");
        updateExpThread.start();

    }

    /**
     * Method filing data view table with experiment's files information. Shown information depends on selected experiment in experiment view table.
     * @param row selected experiment in experiment view table
     */
    public void updateDataTable(final int row) {

        Thread updateDataThread = new Thread(new Runnable() {

            public void run() {
                java.util.List<DataFileInfo> dataFileInfos = null;
                if (row >= 0 && row <= expModel.getRowCount()) {
                    try {
                        int expId = Integer.parseInt(expTable.getValueAt(row, ExpTableModel.ID_COLUMN).toString());
                        dataFileInfos = session.getService().getExperimentFiles(expId);
                    } catch (SOAPException_Exception e) {

                        JUIGLErrorInfoUtils.showErrorDialog(
                                e.getMessage(),
                                resource.getString("soapexception.ededb.text"),
                                e);
                    } catch (Exception e) {

                        JUIGLErrorInfoUtils.showErrorDialog(
                                errorRangeDesc,
                                e.getMessage(),
                                e);
                    }

                    clearDataTable();

                    for (DataFileInfo info : dataFileInfos) {
                        String downloadPath = controller.getDownloadPath()
                                + File.separator + session.getUsername()
                                + File.separator + info.getExperimentId()
                                + " - " + info.getScenarioName();

                        dataModel.addRow(info, controller.isAlreadyDownloaded(info), downloadPath);
                    }
                }
                repaint();
                Working.setActivity(false, "working.ededb.update.datatable");
            }
        });

        Working.setActivity(true, "working.ededb.update.datatable");
        updateDataThread.start();
    }

    /**
     * Method updating file status within local machine
     */
    public void checkLocalCopies() {
        for (DataRowModel row : dataModel.getData()) {
            if (row.getDownloaded() != DataRowModel.DOWNLOADING) {
                row.setDownloaded(controller.isAlreadyDownloaded(row.getFileInfo()));
            }
        }
    }

    /**
     * Returns selected files in data view table.
     * @return list DataRowModel (files info) of users selection in data view table
     */
    public List<DataRowModel> getSelectedFiles() {
        List<DataRowModel> data = dataModel.getData();
        ArrayList<DataRowModel> selectedFiles = new ArrayList<DataRowModel>();

        for (DataRowModel row : data) {
            if (row.isSelected()) {
                selectedFiles.add(row);
            }
        }
        return selectedFiles;
    }

    /**
     * Setter of localization resource budle path
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initTexts();
            }
        });

    }

    /**
     * Init/update text method. Vital for localization.
     */
    private void initTexts() {
        tableValueYes = resource.getString("table.ededb.datatable.state.yes");
        tableValueNo = resource.getString("table.ededb.datatable.state.no");

        expInfoText = resource.getString("tables.ededb.exp.info.text");
        expInfoDesc = resource.getString("tables.ededb.exp.info.desc");
        errorConnectionText = resource.getString("tables.ededb.exp.connection.text");
        errorConnectionDesc = resource.getString("tables.ededb.exp.connection.desc");
        errorRangeDesc = resource.getString("tables.ededb.data.exception.desc");
    }

    /**
     * Method for returning row contents.
     * @return rows of data table
     */
    public List<DataRowModel> getRows() {
        return dataModel.getData();
    }
}
