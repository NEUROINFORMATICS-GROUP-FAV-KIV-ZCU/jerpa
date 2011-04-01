package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Connect action class "calling" LoginDialog.
 *
 * @author Petr Miko
 */
public class ActionConnect extends AbstractAction {

    private Controller controller;

    public ActionConnect(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.setUserLoggedIn(true);
    }
}
