package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Petr Miko
 */
public class ActionConnect extends AbstractAction {

    private LoginDialog loginDialog;

    public ActionConnect(LoginDialog loginDialog) {
        this.loginDialog = loginDialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loginDialog.setVisible(true);
    }
}
