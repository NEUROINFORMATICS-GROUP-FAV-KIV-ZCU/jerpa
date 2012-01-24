package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Action class for deletion of selected files' local copies.
 *
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction {

    private static final long serialVersionUID = -1107239494943191652L;
    private final EDEDBController controller;
    private static final Logger log = Logger.getLogger(ActionDeleteSelected.class);
    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();

    /**
     * Constructor method for action delete selected files.
     *
     * @param controller
     */
    public ActionDeleteSelected(EDEDBController controller) {

        this.controller = controller;
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
    }

    /**
     * Method invoked by performed action
     *
     * @param e action
     */
    public void actionPerformed(ActionEvent e) {

        List<DataRowModel> selected = controller.getSelectedFiles();

        if (!selected.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(null, "You're about to delete " + selected.size() + " files. Proceed?", "Delete prompt",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            controller.unselectAllFiles();

            if (choice == JOptionPane.NO_OPTION)
                return;
        }

        for (DataRowModel file : selected)
            dataFileDao.removeFile(file.getDataFile());
        controller.update();
    }
}
