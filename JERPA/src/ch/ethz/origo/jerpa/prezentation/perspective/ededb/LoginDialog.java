package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.net.ConnectException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

    public LoginDialog(Controller controller, EDEDSession session) {

        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.session = session;
    }

    public void createDialog() {
        final JDialog dialog = new JDialog();

        JXPanel canvas = new JXPanel(new BorderLayout());
        JXPanel labelPane = new JXPanel(new GridLayout(0, 1));
        JXPanel fieldPane = new JXPanel(new GridLayout(0, 1));
        JXPanel buttonPane = new JXPanel(new FlowLayout());

        JTextArea info = new JTextArea(resource.getString("logindialog.ededb.caution"));
        info.setEditable(false);
        info.setFocusable(false);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info.setBackground(canvas.getBackground());
        info.setForeground(canvas.getForeground());

        usernameField = new JFormattedTextField();
        passwordField = new JPasswordField();

        JLabel usernameLabel = new JXLabel(resource.getString("logindialog.ededb.username"));
        JLabel passwordLabel = new JXLabel(resource.getString("logindialog.ededb.password"));
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

        final JButton okButton = new JButton(resource.getString("logindialog.ededb.buttons.ok"));
        final JButton cancelButton = new JButton(resource.getString("logindialog.ededb.buttons.cancel"));

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String tempUsername;
                String tempPassword;

                if (usernameField.getText().length() > 0 && passwordField.getPassword().length > 0) {

                    tempUsername = usernameField.getText();
                    tempPassword = new String(passwordField.getPassword());
                    try {
                        session.userLogIn(tempUsername, tempPassword);

                        controller.update();
                        dialog.dispose();
                    } catch (WebServiceException ex) {
                        JUIGLErrorInfoUtils.showErrorDialog(
                                resource.getString("logindialog.ededb.errors.credentials.desc"),
                                resource.getString("logindialog.ededb.errors.credentials.text"),
                                ex);
                    } catch (ConnectException ex) {
                        JUIGLErrorInfoUtils.showErrorDialog(
                                resource.getString("logindialog.ededb.errors.connection.desc"),
                                resource.getString("logindialog.ededb.errors.connection.text"),
                                ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            resource.getString("logindialog.ededb.errors.inputs.text"),
                            resource.getString("logindialog.ededb.errors.inputs.desc"),
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

        canvas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        canvas.add(info, BorderLayout.NORTH);
        canvas.add(labelPane, BorderLayout.CENTER);
        canvas.add(fieldPane, BorderLayout.LINE_END);
        canvas.add(buttonPane, BorderLayout.SOUTH);

        dialog.setTitle(resource.getString("logindialog.ededb.title"));
        dialog.setResizable(false);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateText() throws JUIGLELangException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
