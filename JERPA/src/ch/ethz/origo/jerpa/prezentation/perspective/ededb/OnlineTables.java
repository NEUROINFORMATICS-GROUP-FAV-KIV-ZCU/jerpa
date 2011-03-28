package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
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
import org.jdesktop.swingx.JXTable;

/**
 * @author Petr Miko
 */
public class OnlineTables extends JSplitPane implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private ExpTableModel expModel;
    private DataTableModel dataModel;
    private Controller controller;
    private EDEDSession session;
    private String tableValueYes;
    private String tableValueNo;
    private String expInfoText;
    private String expInfoDesc;
    private String errorConnectionText;
    private String errorConnectionDesc;
    private String errorRangeDesc;

    public OnlineTables(Controller controller, EDEDSession session) {
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

    private Container createExpTable() {
        expModel = new ExpTableModel();
        final JXTable expTable = new JXTable(expModel);

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

    private Container createDataTable() {
        dataModel = new DataTableModel();
        JXTable dataTable = new JXTable(dataModel);

        dataTable.setAutoCreateRowSorter(true);
        dataTable.setFillsViewportHeight(true);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(dataTable);
    }

    public void clearExpTable() {
        expModel.clear();
    }

    public void clearDataTable() {
        dataModel.clear();
    }

    public void updateExpTable() {

        Thread updateExpThread = new Thread(new Runnable() {

            public void run() {
                if (session.isConnected()) {
                    java.util.List<ExperimentInfo> availableExperiments;

                    if (controller.getRights() == Rights.SUBJECT) {
                        availableExperiments = session.getService().getExperiments(Rights.SUBJECT);
                    } else {
                        availableExperiments = session.getService().getExperiments(Rights.OWNER);
                    }

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
                Working.hide();
            }
        });

        Working.show();
        updateExpThread.start();

    }

    public void updateDataTable(final int row) {

        Thread updateDataThread = new Thread(new Runnable() {

            public void run() {
                java.util.List<DataFileInfo> dataFileInfos = null;
                if (row >= 0 && row <= dataModel.getRowCount()) {
                    try {
                        int expId = Integer.parseInt(expModel.getValueAt(row, ExpTableModel.ID_COLUMN).toString());
                        dataFileInfos = session.getService().getExperimentFiles(expId);
                    } catch (Exception e) {

                        JUIGLErrorInfoUtils.showErrorDialog(
                                errorRangeDesc,
                                e.getMessage(),
                                e);
                    }

                    clearDataTable();

                    assert dataFileInfos != null;
                    for (DataFileInfo info : dataFileInfos) {
                        String downloadPath = controller.getDownloadPath()
                                + File.separator + session.getUsername()
                                + File.separator + info.getExperimentId()
                                + " - " + info.getScenarioName();

                        dataModel.addRow(info, controller.isAlreadyDownloaded(info), downloadPath);
                    }
                }
                repaint();
                Working.hide();
            }
        });

        Working.show();
        updateDataThread.start();
    }

    public void checkLocalCopies() {
        for (DataRowModel row : dataModel.getData()) {
            if(row.getDownloaded() != DataRowModel.DOWNLOADING)
                row.setDownloaded(controller.isAlreadyDownloaded(row.getFileInfo()));
        }
    }

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

    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initTexts();
            }
        });

    }

    private void initTexts() {
        tableValueYes = resource.getString("table.ededb.datatable.state.yes");
        tableValueNo = resource.getString("table.ededb.datatable.state.no");

        expInfoText = resource.getString("tables.ededb.exp.info.text");
        expInfoDesc = resource.getString("tables.ededb.exp.info.desc");
        errorConnectionText = resource.getString("tables.ededb.exp.connection.text");
        errorConnectionDesc = resource.getString("tables.ededb.exp.connection.desc");
        errorRangeDesc = resource.getString("tables.ededb.data.exception.desc");
    }
}
