package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionChooseDownloadPath;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionConnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDisconnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDownloadSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author Petr Miko
 */
public class Controller {

    private EDEDBPerspective parent;
    private EDEDSession session;
    private LoginDialog loginDialog;
    private LoginInfo loginInfo;
    private Tables tables;
    private Toolbar toolbar;
    private Rights rights;
    private ActionDownloadSelected actionDownloadSelected;
    private ActionDisconnect actionDisconnect;
    private ActionConnect actionConnect;
    private ActionChooseDownloadPath actionChooseDownloadFolder;
    private String downloadPath;
    private boolean firstRun;
    private JXPanel mainPanel;
    private Properties properties;
    private final String configFile = "config/ededb.properties";

    public Controller(EDEDBPerspective parent, EDEDSession session) {
        this.parent = parent;
        this.session = session;

        properties = new Properties();
        initDownloadPath();

        mainPanel = new JXPanel();
        mainPanel.setLayout(new BorderLayout());

        initClasses();
    }

    private void initClasses() {
        tables = new Tables(this, session);
        loginDialog = new LoginDialog(this, session);
        loginInfo = new LoginInfo(this, session);

        initActions();

        toolbar = new Toolbar(this, session);

    }

    private void initActions() {

        actionConnect = new ActionConnect(loginDialog);
        actionDisconnect = new ActionDisconnect(this, session);
        actionDownloadSelected = new ActionDownloadSelected(this, session);
        actionChooseDownloadFolder = new ActionChooseDownloadPath(this, session);
    }

    public void update() {
        parent.updateMenuItemVisibility();
        loginInfo.updateLoginInfo();
        tables.updateExpTable();
        toolbar.updateButtonsVisibility();
    }

    public JXPanel initGraphics() {

        mainPanel.removeAll();

        if (firstRun) {
            mainPanel.add(new FirstRun(this), BorderLayout.CENTER);

        } else {
            JXPanel sidebar = new JXPanel(new BorderLayout());

            sidebar.add(loginInfo, BorderLayout.NORTH);
            sidebar.add(toolbar, BorderLayout.CENTER);

            mainPanel.add(tables, BorderLayout.CENTER);
            mainPanel.add(sidebar, BorderLayout.EAST);
        }

        mainPanel.revalidate();
        mainPanel.repaint();

        return mainPanel;
    }

    public ActionChooseDownloadPath getActionChooseDownloadFolder() {
        return actionChooseDownloadFolder;
    }

    public ActionConnect getActionConnect() {
        return actionConnect;
    }

    public ActionDisconnect getActionDisconnect() {
        return actionDisconnect;
    }

    public ActionDownloadSelected getActionDownloadSelected() {
        return actionDownloadSelected;
    }

    public Rights getRights() {
        return rights;
    }

    public void setRights(Rights rights) {
        this.rights = rights;
    }

    public List<DataRowModel> getSelectedFilesToDownload() {
        return tables.getFileDataToDownload();
    }

    private void initDownloadPath() {

        FileInputStream inPropStream = null;

        try {
            inPropStream = new FileInputStream(configFile);
            properties.load(inPropStream);
            downloadPath = properties.getProperty("ededb.downloadfolder");
            firstRun = false;
            inPropStream.close();
        } catch (IOException e) {
            firstRun = true;
        }
    }

    public void setDownloadPath(String downloadPath) {

        File config = new File(configFile);
        FileOutputStream outPropStream = null;
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException ex) {
                JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                    ex.getMessage(),ex);
            }
        }

        try {
            outPropStream = new FileOutputStream(configFile);
            properties.setProperty("ededb.downloadfolder", downloadPath);
            properties.store(outPropStream, "Author: Petr Miko");
            this.downloadPath = downloadPath;
            outPropStream.close();
        } catch (FileNotFoundException ex) {
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                    ex.getMessage(),ex);
        } catch (IOException ex) {
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                    ex.getMessage(),ex);
        }

    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public boolean isFirstRun() {
        return firstRun;
    }
}
