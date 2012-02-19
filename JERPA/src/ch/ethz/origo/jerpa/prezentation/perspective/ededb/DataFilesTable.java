package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataCellRenderer;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowSorter;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Petr Miko
 *         <p/>
 *         Table for Data Files view.
 */
public class DataFilesTable extends JTable implements MouseListener {

    private DataTableModel tableModel;

    public DataFilesTable(DataTableModel tableModel) {
        super(tableModel);
        this.tableModel = tableModel;
        this.setDefaultRenderer(Object.class, new DataCellRenderer());
        this.setRowSorter(new DataRowSorter(tableModel));
        this.setFillsViewportHeight(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
        if (2 == e.getClickCount()) {

            int selectedRow = this.getSelectedRow();
            int modelId = this.convertRowIndexToModel(selectedRow);
            final DataFile selectedFile = tableModel.getDataFileAtIndex(modelId);
            HibernateUtil.rebind(selectedFile);

            new BlobViewer(selectedFile.getFileContent(), selectedFile.getFilename(), selectedFile.getFileLength(), selectedFile.getMimetype());
        }
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
