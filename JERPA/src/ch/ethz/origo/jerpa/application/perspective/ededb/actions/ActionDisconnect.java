package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.setUserLoggedIn(false);
    }
}
