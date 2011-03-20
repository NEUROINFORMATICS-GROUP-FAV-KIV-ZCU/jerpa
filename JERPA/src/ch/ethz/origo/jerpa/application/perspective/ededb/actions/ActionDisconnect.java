package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Petr Miko
 */
public class ActionDisconnect extends AbstractAction {

    private EDEDSession session;
    private Controller controller;

    public ActionDisconnect(Controller controller, EDEDSession session) {
        this.controller = controller;
        this.session = session;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        session.userLogout();
        controller.update();
    }
}
