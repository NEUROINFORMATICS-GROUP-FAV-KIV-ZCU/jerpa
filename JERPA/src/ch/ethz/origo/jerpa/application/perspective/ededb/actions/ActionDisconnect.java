package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action class for disconnecting from EEG/ERP Database.
 *
 * @author Petr Miko
 */
public class ActionDisconnect extends AbstractAction {

    private Controller controller;

    /**
     * Constructor.
     *
     * @param controller EDEDB Controller
     */
    public ActionDisconnect(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.setUserLoggedIn(false);
    }
}
