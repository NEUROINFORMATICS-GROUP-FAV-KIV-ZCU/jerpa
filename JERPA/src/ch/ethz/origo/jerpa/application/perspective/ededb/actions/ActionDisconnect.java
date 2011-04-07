package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
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

    private EDEDBController controller;

    /**
     * Constructor.
     *
     * @param controller EDEDB EDEDBController
     */
    public ActionDisconnect(EDEDBController controller) {
        this.controller = controller;
        
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.setUserLoggedIn(false);
    }
}
