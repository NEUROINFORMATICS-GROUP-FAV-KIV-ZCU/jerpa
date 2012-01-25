package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action class for disconnecting from EEG/ERP Database.
 * 
 * @author Petr Miko
 */
public class ActionDisconnect extends AbstractAction {

	private static final long serialVersionUID = 3207033415853318897L;
	private final EDEDBController controller;

	/**
	 * Constructor.
	 * 
	 * @param controller EDEDB EDEDBController
	 */
	public ActionDisconnect(EDEDBController controller) {
		this.controller = controller;

		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
	}

	/**
	 * Method invoked by performed action
	 * 
	 * @param e action
	 */
	public void actionPerformed(ActionEvent e) {
		controller.setUserLoggedIn(false);
	}
}
