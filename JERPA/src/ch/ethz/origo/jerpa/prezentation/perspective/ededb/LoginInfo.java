package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import java.util.ResourceBundle;
import org.apache.commons.lang.WordUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.jdesktop.swingx.JXPanel;

/**
 * @author Petr Miko
 */
public class LoginInfo extends JXPanel implements ILanguage {
    
    private ResourceBundle resource;
    private String resourceBundlePath;

    private JTextArea usernameText;
    private JTextArea directoryText;
    private EDEDSession session;
    private Controller controller;

    public LoginInfo(Controller controller, EDEDSession session) {
        super();
        
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.session = session;
        this.controller = controller;

        usernameText = createArea(resource.getString("sidebar.ededb.info.title.username"));
        directoryText = createArea(resource.getString("sidebar.ededb.info.title.downloaddir"));

        usernameText.setText(resource.getString("sidebar.ededb.info.text.notloggedin"));
        
        if(controller.isFirstRun())
            directoryText.setText(resource.getString("sidebar.ededb.info.text.dirnotset"));
        else
            directoryText.setText(controller.getDownloadPath());

        this.add(usernameText);
        this.add(directoryText);
    }

    private JTextArea createArea(String title) {
        JTextArea area = new JTextArea();

        area.setFocusable(false);
        area.setBackground(this.getBackground());
        area.setForeground(this.getForeground());

        TitledBorder titledBorder = new TitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.CENTER);

        area.setBorder(titledBorder);
        area.setWrapStyleWord(true);

        return area;
    }

    public void updateLoginInfo() {
        if (session.isConnected())
            usernameText.setText(session.getUsername());
        else
            usernameText.setText(resource.getString("sidebar.ededb.info.text.notloggedin"));

        if(controller.isFirstRun())
            directoryText.setText(resource.getString("sidebar.ededb.info.text.dirnotset"));
        else
            directoryText.setText(controller.getDownloadPath());
    }

    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        //not implemented
    }

    public void updateText() throws JUIGLELangException {
        usernameText = createArea(resource.getString("sidebar.ededb.info.title.username"));
        directoryText = createArea(resource.getString("sidebar.ededb.info.title.downloaddir"));
        updateLoginInfo();
    }
}
