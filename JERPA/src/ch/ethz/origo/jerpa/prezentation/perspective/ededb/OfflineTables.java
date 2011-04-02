package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.UserTableModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTable;

/**
 * Class for viewing already downloaded experiment files.
 * @author Petr Miko
 */
public class OfflineTables extends JSplitPane {

    private UserTableModel userModel;
    private ExpTableModel expModel;
    private DataTableModel dataModel;
    private EDEDBController controller;
    private String username;
    private String folder;

    /**
     * Constructor method creating JSplitPane with tables.
     * @param controller
     */
    public OfflineTables(EDEDBController controller) {
        super();

        this.controller = controller;

        this.setResizeWeight(.5d);

        JSplitPane leftSide = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSide.setResizeWeight(.5d);
        leftSide.setTopComponent(createUserTable());
        leftSide.setBottomComponent(createExpTable());

        this.setLeftComponent(leftSide);
        this.setRightComponent(createDataTable());

        updateUserTable();

        this.setDividerLocation(300 + this.getInsets().left);
    }

    /**
     * Table of users with local copies.
     * @return JScrollPane with JXTable in it
     */
    private Container createUserTable() {
        userModel = new UserTableModel();
        final JXTable userTable = new JXTable(userModel);

        userTable.setAutoCreateRowSorter(true);
        userTable.setFillsViewportHeight(true);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel selectionModel = userTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && userTable.getSelectedRows().length > 0) {
                    username = "" + userModel.getValueAt(userTable.getSelectedRow(), 0);
                    updateExpTable();
                }
            }
        });

        return new JScrollPane(userTable);
    }

    /**
     * Table of users experiments with local copies.
     * @return JScrollPane with JXTable in it
     */
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

                    folder = expModel.getValueAt(expTable.getSelectedRow(), 0)
                            + " - " + expModel.getValueAt(expTable.getSelectedRow(), 1);
                    updateDataTable();
                }
            }
        });

        return new JScrollPane(expTable);
    }

    /**
     * Table of local data files.
     * @return JScrollPane with JXTable in it
     */
    private Container createDataTable() {
        dataModel = new DataTableModel();
        JXTable dataTable = new JXTable(dataModel);

        dataTable.setAutoCreateRowSorter(true);
        dataTable.setFillsViewportHeight(true);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        dataTable.getColumn(DataTableModel.MIME_COLUMN).setMaxWidth(0);
        dataTable.getColumn(DataTableModel.DOWNLOADED_COLUMN).setMaxWidth(0);

        return new JScrollPane(dataTable);
    }

    /**
     * Method clearing user view table of all data.
     */
    public void clearUserTable() {
        while (userModel.getRowCount() > 0) {
            userModel.removeRow(0);
        }
    }

    /**
     * Method clearing experiment view table of all data.
     */
    public void clearExpTable() {
        expModel.clear();
    }

    /**
     * Method clearing data view table of all data.
     */
    public void clearDataTable() {
        dataModel.clear();
    }

    /**
     * Loading all usernames with local copies on machine.
     */
    public void updateUserTable() {
        if (controller.getDownloadPath() != null) {

            Thread updateUserThread = new Thread(new Runnable() {

                public void run() {

                    File downloadFolder = new File(controller.getDownloadPath());
                    File[] experiments = downloadFolder.listFiles();

                    clearUserTable();

                    if (experiments != null) {
                        for (File experiment : experiments) {
                            Object[] temp = {experiment.getName()};

                            userModel.addRow(temp);
                            userModel.fireTableDataChanged();
                        }
                    }
                    repaint();
                }
            });

            updateUserThread.start();
        }
    }

    /**
     * Loading all users experiments which has local copies. User (=row in user table) muset be selected first.
     */
    public void updateExpTable() {

        Thread updateExpThread = new Thread(new Runnable() {

            public void run() {

                File downloadFolder = new File(controller.getDownloadPath()
                        + File.separator + username);
                File[] experiments = downloadFolder.listFiles();

                clearExpTable();

                if (experiments != null) {
                    for (File experiment : experiments) {
                        String[] name = experiment.getName().split(" - ");
                        ExperimentInfo data = new ExperimentInfo();
                        data.setExperimentId(Integer.parseInt(name[0]));
                        data.setScenarioName(name[1]);
                        expModel.addRow(data);
                        dataModel.clear();
                    }
                }
                repaint();
                Working.setVisible(false);
            }
        });

        Working.setVisible(true);
        updateExpThread.start();

    }

    /**
     * Loading local data files' names.
     */
    public void updateDataTable() {

        Thread updateDataThread = new Thread(new Runnable() {

            public void run() {

                File downloadFolder = new File(controller.getDownloadPath()
                        + File.separator + username
                        + File.separator + folder);
                File[] files = downloadFolder.listFiles();

                clearDataTable();

                if (files != null) {
                    for (File file : files) {
                        DataFileInfo info = new DataFileInfo();
                        info.setFilename(file.getName());
                        info.setLength(file.length());
                        info.setScenarioName(folder);

                        dataModel.addRow(info, DataRowModel.HAS_LOCAL_COPY,
                                downloadFolder.getAbsolutePath());
                    }
                }

                repaint();
                Working.setVisible(false);
            }
        });

        updateDataThread.start();
        if (!controller.isOnlineTab()) {
            Working.setVisible(true);
        }
    }

    /**
     * File update method.
     */
    public void checkLocalCopies() {
        updateUserTable();
        updateExpTable();
        updateDataTable();
    }

    /**
     * Getter of information about selected rows in data table.
     * @return List of DataRowModel information
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
}
