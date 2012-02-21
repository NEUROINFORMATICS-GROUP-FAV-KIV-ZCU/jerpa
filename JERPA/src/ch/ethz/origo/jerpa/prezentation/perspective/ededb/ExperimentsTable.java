package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Petr Miko
 *         <p/>
 *         Table for experiments visualization.
 */
public class ExperimentsTable extends JTable implements MouseListener {

    private ExpTableModel expTableModel;

    /**
     * Constructor.
     *
     * @param expTableModel Experiment table model
     */
    public ExperimentsTable(ExpTableModel expTableModel) {
        super(expTableModel);

        this.expTableModel = expTableModel;
        this.setAutoCreateRowSorter(true);
        this.setFillsViewportHeight(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
        if (2 == e.getClickCount()) {
            int selectedRow = this.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < expTableModel.getRowCount()) {
                int modelId = this.convertRowIndexToModel(selectedRow);
                int selectedFile = expTableModel.getExperimentAtIndex(modelId).getExperimentId();
                new ExperimentOverview(selectedFile);
            }
        }

    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
