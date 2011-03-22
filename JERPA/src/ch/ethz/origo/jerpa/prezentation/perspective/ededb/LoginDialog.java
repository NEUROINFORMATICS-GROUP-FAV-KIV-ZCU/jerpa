package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.net.ConnectException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.xml.ws.WebServiceException;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

/**
 * @author Petr Miko
 */
public class LoginDialog implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private Controller controller;
    private EDEDSession session;
    private JFormattedTextField usernameField;
    private JPasswordField passwordField;

    private JDialog dialog;
    private JTextArea info;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton okButton, cancelButton;

    private String inputsErrorText;
    private String inputsErrorDesc;
    private String credentialsErrorText;
    private String credentialsErrorDesc;
    private String connectionErrorText;
    private String connectionErrorDesc;

    public LoginDialog(Controller controller, EDEDSession session) {

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.session = session;
    }

    public void createDialog() {
        dialog = new JDialog();

        JXPanel canvas = new JXPanel();
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.PAGE_AXIS));
        JXPanel labelPane = new JXPanel(new GridLayout(0, 1));
        JXPanel fieldPane = new JXPanel(new GridLayout(0, 1));
        JXPanel buttonPane = new JXPanel(new FlowLayout());

        updateErrorTexts();

        info = new JTextArea(resource.getString("logindialog.ededb.caution"));
        info.setEditable(false);
        info.setFocusable(false);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info.setBackground(canvas.getBackground());
        info.setForeground(canvas.getForeground());

        final JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setVisible(false);

        usernameField = new JFormattedTextField();
        passwordField = new JPasswordField();

        usernameLabel = new JXLabel(resource.getString("logindialog.ededb.username"));
        passwordLabel = new JXLabel(resource.getString("logindialog.ededb.password"));
        usernameLabel.setLabelFor(usernameField);
        passwordLabel.setLabelFor(passwordField);

        usernameField.setColumns(10);
        passwordField.setColumns(10);

        usernameField.setText(session.getUsername());
        passwordField.setText(session.getPassword());

        labelPane.add(usernameLabel);
        labelPane.add(passwordLabel);
        fieldPane.add(usernameField);
        fieldPane.add(passwordField);

        okButton = new JButton(resource.getString("logindialog.ededb.buttons.ok"));
        cancelButton = new JButton(resource.getString("logindialog.ededb.buttons.cancel"));

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final String tempUsername;
                final String tempPassword;

                if (usernameField.getText().length() > 0 && passwordField.getPassword().length > 0) {

                    tempUsername = usernameField.getText();
                    tempPassword = new String(passwordField.getPassword());

                    Thread loginThread = new Thread(new Runnable() {

                        public void run() {
                            try {
                                session.userLogIn(tempUsername, tempPassword);
                                controller.update();
                                dialog.dispose();
                            } catch (WebServiceException ex) {

                                if (ex.getCause().getClass() == IOException.class) {
                                    JUIGLErrorInfoUtils.showErrorDialog(
                                            credentialsErrorDesc,
                                            credentialsErrorText,
                                            ex);
                                } else if (ex.getCause().getClass() == ConnectException.class) {
                                    JUIGLErrorInfoUtils.showErrorDialog(
                                            connectionErrorDesc,
                                            connectionErrorText,
                                            ex);
                                }
                            } catch (ConnectException ex) {
                                JUIGLErrorInfoUtils.showErrorDialog(
                                        connectionErrorDesc,
                                        connectionErrorText,
                                        ex);
                            }

                            okButton.setEnabled(true);
                            cancelButton.setEnabled(true);

                            progress.setVisible(false);
                        }
                    });

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            okButton.setEnabled(false);
                            cancelButton.setEnabled(false);
                            progress.setVisible(true);
                        }
                    });

                    loginThread.start();

                } else {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            inputsErrorText,
                            inputsErrorDesc,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        usernameField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                int kc = ke.getKeyCode();
                if (kc == KeyEvent.VK_ENTER) {
                    okButton.doClick();
                } else if (kc == KeyEvent.VK_ESCAPE) {
                    cancelButton.doClick();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                int kc = ke.getKeyCode();
                if (kc == KeyEvent.VK_ENTER) {
                    okButton.doClick();
                } else if (kc == KeyEvent.VK_ESCAPE) {
                    cancelButton.doClick();
                }
            }
        });

        buttonPane.add(okButton);
        buttonPane.add(cancelButton);

        JXPanel center = new JXPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));
        center.add(labelPane);
        center.add(fieldPane);

        canvas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        canvas.add(info);
        canvas.add(center);
        canvas.add(buttonPane);
        canvas.add(progress);

        dialog.setTitle(resource.getString("logindialog.ededb.title"));
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        dialog.add(canvas);
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null);
    }

    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                dialog.setTitle(resource.getString("logindialog.ededb.title"));
                info.setText(resource.getString("logindialog.ededb.caution"));
                usernameLabel.setText(resource.getString("logindialog.ededb.username"));
                passwordLabel.setText(resource.getString("logindialog.ededb.password"));
                okButton.setText(resource.getString("logindialog.ededb.buttons.ok"));
                cancelButton.setText(resource.getString("logindialog.ededb.buttons.cancel"));
                updateErrorTexts();
            }
        });

    }

    private void updateErrorTexts() {
        inputsErrorText = resource.getString("logindialog.ededb.errors.inputs.text");
        inputsErrorDesc = resource.getString("logindialog.ededb.errors.inputs.desc");
        credentialsErrorText = resource.getString("logindialog.ededb.errors.credentials.text");
        credentialsErrorDesc = resource.getString("logindialog.ededb.errors.credentials.desc");
        connectionErrorText = resource.getString("logindialog.ededb.errors.connection.text");
        connectionErrorDesc = resource.getString("logindialog.ededb.errors.connection.desc");
    }
}
