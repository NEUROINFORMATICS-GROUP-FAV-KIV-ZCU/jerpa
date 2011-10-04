package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.tier.StorageException;

/**
 * Action class for deletion of selected files' local copies.
 * 
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction {

	private static final long serialVersionUID = -1107239494943191652L;
	private final EDEDBController controller;
	private static final Logger log = Logger.getLogger(ActionDeleteSelected.class);

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
	@Override
	public void actionPerformed(ActionEvent e) {

		List<DataRowModel> selected = controller.getSelectedFiles();

		if (!selected.isEmpty()) {
			int choice = JOptionPane.showConfirmDialog(null, "You're about to delete " + selected.size() + " files. Proceed?", "Delete prompt",
			        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

			controller.unselectAllFiles();

			if (choice == JOptionPane.NO_OPTION)
				return;
		}

		try {
			for (DataRowModel file : selected)
				controller.getStorage().removeFile(file.getFileInfo().getFileId());
			controller.update();
		}
		catch (StorageException exception) {
			log.error(exception.getMessage(), exception);
		}
	}
}
