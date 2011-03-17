package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.tables.ExpTableModel;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.swingx.JXTable;

/**
 * @author Petr Miko
 */
public class Tables extends JSplitPane {

    private ExpTableModel expModel;
    private DataTableModel dataModel;

    private EDEDSession session;

    public Tables(EDEDSession session) {
        super();

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
                //updateDataTable(expTable.getSelectedRow());
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

    public List<DataRowModel> getFileDataToDownload() {
        List<DataRowModel> data = dataModel.getData();
        ArrayList<DataRowModel> selectedFiles = new ArrayList<DataRowModel>();

        for (DataRowModel row : data) {
            if (row.isToDownload())
                selectedFiles.add(row);
        }

        return selectedFiles;
    }

}
