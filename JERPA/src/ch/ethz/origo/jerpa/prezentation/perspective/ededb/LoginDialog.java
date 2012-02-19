package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Class creating login dialog for setting up connection to EEG/ERP Database.
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class LoginDialog extends JDialog implements ILanguage {

    private static final Logger log = Logger.getLogger(LoginDialog.class);
    protected ResourceBundle resource;
    protected String resourceBundlePath;

    private final int VISIBLE_LETTERS = 10;
    private JTextArea info;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel endpointLabel;

    protected JButton okButton, cancelButton;
    protected JToggleButton optionsButton;
    protected JFormattedTextField usernameField;
    protected JPasswordField passwordField;
    protected JFormattedTextField endpointField;
    protected JPanel morePane;
    protected final JProgressBar progress = new JProgressBar();

    /**
     * Constructor.
     */
    protected LoginDialog() {
        super();
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        createDialog();
    }

    /**
     * Creating LoginDialog JDialog with all its fields, labels and buttons
     * (with actions).
     */
    private void createDialog() {

        JPanel canvas = new JPanel();
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.PAGE_AXIS));

        progress.setIndeterminate(true);
        progress.setVisible(false);

        canvas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        canvas.add(createInfoPane());
        canvas.add(createCredentialsPane());
        canvas.add(createButtonPane());
        canvas.add(createMorePane());
        canvas.add(progress);

        updateTexts();

        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.add(canvas);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.pack();
    }

    /**
     * Creator of panel with additional login options.
     * @return panel with additional options
     */
    private JPanel createMorePane() {

        morePane = new JPanel();
        morePane.setLayout(new GridBagLayout());

        endpointLabel = new JLabel();
        endpointLabel.setLabelFor(endpointField);
        endpointField = new JFormattedTextField();
        endpointField.setColumns(VISIBLE_LETTERS);

        GridBagConstraints endpointLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints endpointFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        morePane.add(endpointLabel, endpointLabelConstraints);
        morePane.add(endpointField, endpointFieldConstraints);

        return morePane;
    }

    /**
     * Creator of panel containing information about login process.
     * @return panel with login information
     */
    private JPanel createInfoPane() {
        JPanel infoPane = new JPanel(new FlowLayout());

        info = new JTextArea();
        info.setEditable(false);
        info.setFocusable(false);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info.setBackground(infoPane.getBackground());
        info.setForeground(infoPane.getForeground());

        infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.LINE_AXIS));
        try {
            infoPane.add(new JLabel(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "login_48.png", 32, 32)));
        } catch (PerspectiveException ex) {
            log.error(ex);
        }

        infoPane.add(info);

        return infoPane;
    }

    /**
     * Creator of panel with buttons.
     * @return buttons panel
     */
    private JPanel createButtonPane() {
        JPanel buttonPane = new JPanel(new FlowLayout());

        okButton = new JButton();
        cancelButton = new JButton();
        optionsButton = new JToggleButton();

        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        buttonPane.add(optionsButton);

        return buttonPane;
    }

    /**
     * Creator of panel with credentials inputs.
     * @return credentials panel.
     */
    private JPanel createCredentialsPane() {
        JPanel credentialsPane = new JPanel(new GridBagLayout());

        usernameLabel = new JLabel();
        passwordLabel = new JLabel();
        usernameField = new JFormattedTextField();
        passwordField = new JPasswordField();

        usernameField.setColumns(VISIBLE_LETTERS);
        passwordField.setColumns(VISIBLE_LETTERS);
        usernameLabel.setLabelFor(usernameField);
        passwordLabel.setLabelFor(passwordField);

        GridBagConstraints usernameLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints usernameFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints passwordLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints passwordFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        credentialsPane.add(usernameLabel, usernameLabelConstraints);
        credentialsPane.add(usernameField, usernameFieldConstraints);
        credentialsPane.add(passwordLabel, passwordLabelConstraints);
        credentialsPane.add(passwordField, passwordFieldConstraints);

        return credentialsPane;
    }

    /**
     * Setter of localization resource bundle path
     *
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     *
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource bundle key.
     *
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     *
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                updateTexts();
            }
        });
    }

    /**
     * Update method to reset all texts.
     */
    private void updateTexts() {
        this.setTitle(resource.getString("logindialog.ededb.title"));
        info.setText(resource.getString("logindialog.ededb.caution"));
        endpointLabel.setText(resource.getString("logindialog.ededb.endpoint"));
        usernameLabel.setText(resource.getString("logindialog.ededb.username"));
        passwordLabel.setText(resource.getString("logindialog.ededb.password"));
        okButton.setText(resource.getString("logindialog.ededb.buttons.ok"));
        cancelButton.setText(resource.getString("logindialog.ededb.buttons.cancel"));
        optionsButton.setText(resource.getString("logindialog.ededb.buttons.more"));
    }
}
