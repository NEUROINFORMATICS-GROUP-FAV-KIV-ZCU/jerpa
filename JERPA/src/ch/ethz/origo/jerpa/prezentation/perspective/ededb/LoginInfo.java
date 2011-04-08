package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.jdesktop.swingx.JXPanel;

/**
 * Class of GUI fields containing login username and download folder information.
 *
 * @author Petr Miko
 */
public class LoginInfo extends JXPanel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private JTextArea usernameText;
    private JTextArea directoryText;
    private EDEDClient session;
    private EDEDBController controller;

    private TitledBorder usernameBorder;
    private TitledBorder directoryBorder;

    /**
     * Constructor creating JXPanel for information fields.
     *
     * @param controller EDEDB EDEDBController
     * @param session EDEDSession
     */
    public LoginInfo(EDEDBController controller, EDEDClient session) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.session = session;
        this.controller = controller;

        usernameText = createArea();
        directoryText = createArea();

        usernameBorder = new TitledBorder(resource.getString("sidebar.ededb.info.title.username"));
        directoryBorder = new TitledBorder(resource.getString("sidebar.ededb.info.title.downloaddir"));

        usernameBorder.setTitleJustification(TitledBorder.CENTER);
        directoryBorder.setTitleJustification(TitledBorder.CENTER);

        usernameText.setBorder(usernameBorder);
        directoryText.setBorder(directoryBorder);

        usernameText.setText(resource.getString("sidebar.ededb.info.text.notloggedin"));

        if (controller.isFirstRun()) {
            directoryText.setText(resource.getString("sidebar.ededb.info.text.dirnotset"));
        } else {
            directoryText.setText(controller.getDownloadPath());
        }

        this.add(usernameText);
        this.add(directoryText);
    }

    /**
     * Method creating JTextArea with certain parameters.
     * @return
     */
    private JTextArea createArea() {
        JTextArea area = new JTextArea();

        area.setFocusable(false);
        area.setBackground(this.getBackground());
        area.setForeground(this.getForeground());

        area.setWrapStyleWord(true);

        return area;
    }

    /**
     * Shown information update method.
     */
    public void updateLoginInfo() {
        if (session.isConnected()) {
            usernameText.setText(session.getUsername());
        } else {
            usernameText.setText(resource.getString("sidebar.ededb.info.text.notloggedin"));
        }

        if (controller.isFirstRun()) {
            directoryText.setText(resource.getString("sidebar.ededb.info.text.dirnotset"));
        } else {
            directoryText.setText(controller.getDownloadPath());
        }
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
                usernameBorder.setTitle(resource.getString("sidebar.ededb.info.title.username"));
                directoryBorder.setTitle(resource.getString("sidebar.ededb.info.title.downloaddir"));

                updateLoginInfo();
            }
        });
    }
}
