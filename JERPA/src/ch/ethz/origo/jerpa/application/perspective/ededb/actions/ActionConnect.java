package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Connect action class "calling" LoginDialog.
 * 
 * @author Petr Miko
 */
public class ActionConnect extends AbstractAction {

	private static final long serialVersionUID = 6355605804293660348L;
	private final EDEDBController controller;

	/**
	 * Constructor method for action connect to EEG/ERP Database
	 * 
	 * @param controller
	 */
	public ActionConnect(EDEDBController controller) {
		this.controller = controller;

		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
	}

	/**
	 * Method invoked by performed action.
	 * 
	 * @param e performed action
	 */
	public void actionPerformed(ActionEvent e) {
		controller.setUserLoggedIn(true);
	}
}
