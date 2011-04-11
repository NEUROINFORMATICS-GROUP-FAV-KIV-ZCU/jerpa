package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Class creating login dialog for setting up connection to EEG/ERP Database.
 *
 * @author Petr Miko
 */
public class LoginDialog implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private EDEDBController controller;
    private EDEDClient session;
    private JFormattedTextField usernameField;
    private JPasswordField passwordField;
    private JFormattedTextField endpointField;
    private JDialog dialog;
    private JTextArea info;
    private JXLabel usernameLabel;
    private JXLabel passwordLabel;
    private JXLabel endpointLabel;
    private JButton okButton, cancelButton;
    private String inputsErrorText;
    private String inputsErrorDesc;
    private String credentialsErrorText;
    private String credentialsErrorDesc;
    private String connectionErrorText;
    private String connectionErrorDesc;

    /**
     * Constructor.
     *
     * @param controller EDEDB EDEDBController
     * @param session EDEDSession from EDEDClient.jar
     */
    public LoginDialog(EDEDBController controller, EDEDClient session) {

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.session = session;

        createDialog();
    }

    /**
     * Creating LoginDialong JDialog with all its fields, labels and buttons (with actions).
     */
    private void createDialog() {
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

        endpointField = new JFormattedTextField();
        usernameField = new JFormattedTextField();
        passwordField = new JPasswordField();

        endpointLabel = new JXLabel(resource.getString("logindialog.ededb.endpoint"));
        usernameLabel = new JXLabel(resource.getString("logindialog.ededb.username"));
        passwordLabel = new JXLabel(resource.getString("logindialog.ededb.password"));
        usernameLabel.setLabelFor(usernameField);
        passwordLabel.setLabelFor(passwordField);

        endpointField.setText(controller.getConfigKey("ededb.endpoint"));

        endpointField.setColumns(10);
        usernameField.setColumns(10);
        passwordField.setColumns(10);

        usernameField.setText(session.getUsername());
        passwordField.setText(session.getPassword());

        labelPane.add(endpointLabel);
        labelPane.add(usernameLabel);
        labelPane.add(passwordLabel);
        fieldPane.add(endpointField);
        fieldPane.add(usernameField);
        fieldPane.add(passwordField);

        okButton = new JButton(resource.getString("logindialog.ededb.buttons.ok"));
        cancelButton = new JButton(resource.getString("logindialog.ededb.buttons.cancel"));

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final String tempEndpoint;
                final String tempUsername;
                final String tempPassword;

                if (!endpointField.getText().isEmpty()
                        && !usernameField.getText().isEmpty()
                        && passwordField.getPassword().length > 0) {

                    tempEndpoint = (String) endpointField.getText();
                    tempUsername = usernameField.getText();
                    tempPassword = new String(passwordField.getPassword());

                    Thread loginThread = new Thread(new Runnable() {

                        public void run() {
                            try {
                                session.userLogIn(tempUsername, tempPassword, tempEndpoint);
                                controller.setConfigKey("ededb.endpoint", tempEndpoint);
                                dialog.setVisible(false);
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
                                } else {
                                    JUIGLErrorInfoUtils.showErrorDialog(
                                            ex.getMessage(),
                                            ex.getLocalizedMessage(),
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
                            dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                            progress.setVisible(false);

                            usernameField.setText("");
                            passwordField.setText("");

                        }
                    });

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            okButton.setEnabled(false);
                            cancelButton.setEnabled(false);
                            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
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
                dialog.setVisible(false);
            }
        });

        endpointField.addKeyListener(new KeyAdapter() {

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
        
        JXPanel top = new JXPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
        try {
            top.add(new JLabel(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "login_48.png", 32, 32)));
        } catch (PerspectiveException ex) {
        }
        
        top.add(info);

        JXPanel center = new JXPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));
        center.add(labelPane);
        center.add(fieldPane);
        
        canvas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        canvas.add(top);
        canvas.add(center);
        canvas.add(buttonPane);
        canvas.add(progress);

        dialog.setTitle(resource.getString("logindialog.ededb.title"));
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.add(canvas);
        dialog.pack();
        dialog.setVisible(false);
        dialog.setLocationRelativeTo(null);
    }

    /**
     * Setter of LoginDialog visibility.
     * @param visibility boolean
     */
    public void setVisible(boolean visibility) {
        if (dialog != null) {
            dialog.setVisible(visibility);
        }
    }

    /**
     * Setter of localization resource budle path
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                dialog.setTitle(resource.getString("logindialog.ededb.title"));
                info.setText(resource.getString("logindialog.ededb.caution"));
                endpointLabel.setText(resource.getString("logindialog.ededb.endpoint"));
                usernameLabel.setText(resource.getString("logindialog.ededb.username"));
                passwordLabel.setText(resource.getString("logindialog.ededb.password"));
                okButton.setText(resource.getString("logindialog.ededb.buttons.ok"));
                cancelButton.setText(resource.getString("logindialog.ededb.buttons.cancel"));
                updateErrorTexts();
            }
        });

    }

    /**
     * Init/update texts. Vital for localization.
     */
    private void updateErrorTexts() {
        inputsErrorText = resource.getString("logindialog.ededb.errors.inputs.text");
        inputsErrorDesc = resource.getString("logindialog.ededb.errors.inputs.desc");
        credentialsErrorText = resource.getString("logindialog.ededb.errors.credentials.text");
        credentialsErrorDesc = resource.getString("logindialog.ededb.errors.credentials.desc");
        connectionErrorText = resource.getString("logindialog.ededb.errors.connection.text");
        connectionErrorDesc = resource.getString("logindialog.ededb.errors.connection.desc");
    }
}
