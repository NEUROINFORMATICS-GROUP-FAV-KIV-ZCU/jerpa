package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.xml.ws.WebServiceException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ConnectException;

/**
 * Logic part of Login Dialog, necessary for logging into EEG/ERP Base.
 *
 * @author Petr Miko
 */
public final class LoginDialogLogic extends LoginDialog implements ActionListener {

    private static LoginDialogLogic instance;

    private Logger log = Logger.getLogger(LoginDialogLogic.class);

    private EDEDBController controller;
    private EDEDClient service;
    private LoginThread loginThread;

    /**
     * Constructor.
     *
     * @param controller EDEDB Controller instance
     * @param service    EDEDClient from EDEDClient.jar
     */
    private LoginDialogLogic(EDEDBController controller, EDEDClient service) {
        super();

        this.controller = controller;
        this.service = service;

        KeyStroke login = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke close = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        this.getRootPane().registerKeyboardAction(this, "ok", login, JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.getRootPane().registerKeyboardAction(this, "cancel", close, JComponent.WHEN_IN_FOCUSED_WINDOW);

        setButtonActions();
        initTextFields();
    }

    private void initTextFields() {
        usernameField.setText(EDEDBProperties.getConfigKey("ededb.username"));
        passwordField.setText(EDEDBProperties.getConfigKey("ededb.password"));
        endpointField.setText(EDEDBProperties.getConfigKey("ededb.endpoint"));

        boolean isEndpointEmpty = endpointField.getText().trim().isEmpty();
        optionsButton.setSelected(isEndpointEmpty);
        morePane.setVisible(isEndpointEmpty);
    }

    private void setButtonActions() {
        okButton.addActionListener(this);
        okButton.setActionCommand("ok");

        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("cancel");

        optionsButton.addActionListener(this);
        optionsButton.setActionCommand("options");
    }

    public void actionPerformed(ActionEvent event) {

        if ("ok".equals(event.getActionCommand())) {
            if (!endpointField.getText().isEmpty() && !usernameField.getText().isEmpty()
                    && passwordField.getPassword().length > 0) {
                loginThread = new LoginThread();
                loginThread.start();
            } else {
                JOptionPane.showMessageDialog(new JFrame(),
                        resource.getString("logindialog.ededb.errors.inputs.text"),
                        resource.getString("logindialog.ededb.errors.inputs.desc"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if ("cancel".equals(event.getActionCommand())) {
            if (loginThread == null)
                this.dispose();
        } else if ("options".equals(event.getActionCommand())) {
            morePane.setVisible(optionsButton.isSelected());
            this.pack();
        }

    }

    /**
     * Setter of activities and GUI elements in accordance to login in progress state.
     *
     * @param inProgress login in progress state
     */
    private void setLogInProgress(final boolean inProgress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.setEnabled(!inProgress);
                cancelButton.setEnabled(!inProgress);
                Working.setActivity(inProgress, "working.ededb.connecting");
                progress.setVisible(inProgress);
                if (inProgress) {
                    LoginDialogLogic.this.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                } else {
                    controller.setUserLoggedIn(true);
                    LoginDialogLogic.this.getRootPane().setCursor(Cursor.getDefaultCursor());
                    LoginDialogLogic.this.dispose();
                }
            }
        });
    }

    /**
     * Singleton getter of Login Dialog instance.
     *
     * @param service    web service instance
     * @param controller EDEDB Controller instance
     */
    public static void showLoginDialog(EDEDClient service, EDEDBController controller) {
        if (instance == null) {
            instance = new LoginDialogLogic(controller, service);
        } else if (!instance.isShowing()) {
            instance.setVisible(true);
        }
    }


    /**
     * Nested thread class used for login purposes.
     */
    private class LoginThread extends Thread {
        public void run() {
            try {
                setLogInProgress(true);

                String endpoint = endpointField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                service.userLogIn(username, password, endpoint);

                EDEDBProperties.setConfigKey("ededb.endpoint", endpoint);
                EDEDBProperties.setConfigKey("ededb.username", username);
                EDEDBProperties.setConfigKey("ededb.password", password);
            } catch (WebServiceException ex) {

                if (ex.getCause() instanceof IOException) {
                    JUIGLErrorInfoUtils.showErrorDialog(
                            resource.getString("logindialog.ededb.errors.credentials.desc"),
                            resource.getString("logindialog.ededb.errors.credentials.text"),
                            ex);
                } else if (ex.getCause() instanceof ConnectException) {
                    JUIGLErrorInfoUtils.showErrorDialog(
                            resource.getString("logindialog.ededb.errors.connection.desc"),
                            resource.getString("logindialog.ededb.errors.connection.text"),
                            ex);
                } else {
                    JUIGLErrorInfoUtils.showErrorDialog(ex.getMessage(), ex.getLocalizedMessage(), ex);
                }
            } catch (ConnectException ex) {
                JUIGLErrorInfoUtils.showErrorDialog(
                        resource.getString("logindialog.ededb.errors.connection.desc"),
                        resource.getString("logindialog.ededb.errors.connection.text"),
                        ex);
            } finally {
                setLogInProgress(false);
            }
        }
    }
}
