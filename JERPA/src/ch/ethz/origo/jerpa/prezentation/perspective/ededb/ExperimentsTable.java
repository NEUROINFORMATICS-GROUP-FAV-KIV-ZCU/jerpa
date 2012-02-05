package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.dao.ExperimentDao;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import ch.ethz.origo.jerpa.data.tier.pojo.Scenario;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Petr Miko
 *         <p/>
 *         Table for experiments visualization.
 */
public class ExperimentsTable extends JTable implements MouseListener {

    private ExperimentDao experimentDao = DaoFactory.getExperimentDao();

    /**
     * Constructor.
     *
     * @param expTableModel Experiment table model
     */
    public ExperimentsTable(ExpTableModel expTableModel) {
        super(expTableModel);

        this.setAutoCreateRowSorter(true);
        this.setFillsViewportHeight(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
        if (2 == e.getClickCount()) {
            int selectedRow = this.getSelectedRow();
            int selectedFile = (Integer) this.getValueAt(selectedRow, 0);

            new ExperimentOverview(selectedFile);
        }
    }

    public void mousePressed(MouseEvent e) {
        //do nothing
    }

    public void mouseReleased(MouseEvent e) {
        //do nothing
    }

    public void mouseEntered(MouseEvent e) {
        //do nothing
    }

    public void mouseExited(MouseEvent e) {
        //do nothing
    }
}
