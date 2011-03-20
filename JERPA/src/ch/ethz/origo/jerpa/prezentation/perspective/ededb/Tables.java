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
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.jdesktop.swingx.JXTable;

/**
 * @author Petr Miko
 */
public class Tables extends JSplitPane implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private ExpTableModel expModel;
    private DataTableModel dataModel;
    private Controller controller;
    private EDEDSession session;

    public Tables(Controller controller, EDEDSession session) {
        super();

        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

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
                updateDataTable(expTable.getSelectedRow());
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

        if (session.isConnected()) {
            java.util.List<ExperimentInfo> availableExperiments;

            if (controller.getRights() == Rights.SUBJECT) {
                availableExperiments = session.getService().getAvailableExperimentsWithRights(Rights.SUBJECT);
            } else {
                availableExperiments = session.getService().getAvailableExperimentsWithRights(Rights.OWNER);
            }

            if (availableExperiments != null) {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        availableExperiments.size() + " " + resource.getString("tables.ededb.exp.info.text"),
                        resource.getString("tables.ededb.exp.info.desc"),
                        JOptionPane.INFORMATION_MESSAGE);
                expModel.clear();

                for (ExperimentInfo availableExperiment : availableExperiments) {
                    expModel.insertRow(availableExperiment);
                }

                dataModel.clear();
            } else {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        resource.getString("tables.ededb.exp.connection.text"),
                        resource.getString("tables.ededb.exp.connection.desc"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            clearExpTable();
            clearDataTable();
        }
    }

    public void updateDataTable(int row) {
        java.util.List<DataFileInfo> dataFileInfos = null;
        if (row >= 0 && row <= dataModel.getRowCount()) {
            try {
                int expId = Integer.parseInt(expModel.getValueAt(row, ExpTableModel.ID_COLUMN).toString());
                dataFileInfos = session.getService().getExperimentDataFilesWhereExpId(expId);
            } catch (Exception e) {

                JUIGLErrorInfoUtils.showErrorDialog(
                        resource.getString("tables.ededb.data.exception.desc"),
                        e.getMessage(),
                        e);
            }

            dataModel.clear();

            assert dataFileInfos != null;
            for (DataFileInfo info : dataFileInfos) {
                boolean downloaded = controller.isAlreadyDownloaded(info);
                
                if(downloaded)
                    dataModel.addRow(info, resource.getString("table.ededb.datatable.state.yes"));
                else
                    dataModel.addRow(info, resource.getString("table.ededb.datatable.state.no"));
            }
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
        //not implemented
    }

    public void updateText() throws JUIGLELangException {
        //not implemented
    }
}
