package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataCellRenderer;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowSorter;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Petr Miko
 *         <p/>
 *         Table for Data Files view.
 */
public class DataFilesTable extends JTable implements MouseListener {

    public DataFilesTable(DataTableModel tableModel) {
        super(tableModel);

        this.setDefaultRenderer(Object.class, new DataCellRenderer());
        this.setRowSorter(new DataRowSorter(tableModel));
        this.setFillsViewportHeight(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
            if(2 == e.getClickCount()){
               //visualize experiment information
            }
    }

    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
