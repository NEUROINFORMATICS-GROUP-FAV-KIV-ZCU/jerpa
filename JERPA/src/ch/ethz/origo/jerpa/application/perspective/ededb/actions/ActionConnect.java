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

    private EDEDBController controller;

    public ActionConnect(EDEDBController controller) {
        this.controller = controller;
        
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.setUserLoggedIn(true);
    }
}
