package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionVisualizeSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionChooseDownloadPath;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionConnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDeleteSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDisconnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDownloadSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionOpenDownloadPath;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.FirstRun;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginInfo;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.OfflineTables;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.OnlineTables;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Toolbar;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXPanel;

/**
 * Main class for EDEDB controlling.
 *
 * @author Petr Miko
 */
public class EDEDBController {

    private EDEDBPerspective parent;
    private EDEDClient session;
    private LoginDialog loginDialog;
    private LoginInfo loginInfo;
    private OnlineTables onlineTables;
    private OfflineTables offlineTables;
    private Toolbar toolbar;
    private Rights rights;
    private ActionVisualizeSelected actionAnalyseSelected;
    private ActionDownloadSelected actionDownloadSelected;
    private ActionDeleteSelected actionDeleteSelected;
    private ActionDisconnect actionDisconnect;
    private ActionConnect actionConnect;
    private ActionChooseDownloadPath actionChooseDownloadFolder;
    private ActionOpenDownloadPath actionOpenDownloadPath;
    private String downloadPath;
    private boolean firstRun;
    private JXPanel mainPanel, tableViewPanel;
    private Properties properties;
    private final String configFolder = "config";
    private final String configFile = "config/ededb.properties";
    private boolean offlineMode;
    private boolean lock;
    private Set<Integer> downloadingFiles;

    /**
     * Constructor.
     *
     * @param parent EDEDBPerspective
     * @param session EDEDClient.jar session
     */
    public EDEDBController(EDEDBPerspective parent, EDEDClient session) {
        this.parent = parent;
        this.session = session;

        properties = new Properties();
        String temPath = getConfigKey("ededb.downloadfolder");
        File tempDownloadFolder = (temPath == null
                ? null : new File(temPath));

        if (tempDownloadFolder == null || !(tempDownloadFolder.exists())) {
            firstRun = true;
        } else {
            setDownloadPath(temPath);
            firstRun = false;
        }

        mainPanel = new JXPanel();
        mainPanel.setLayout(new BorderLayout());

        initClasses();

        downloadingFiles = new HashSet<Integer>();
    }

    /**
     * Init method for all used classes in EDEDB.
     */
    private void initClasses() {

        onlineTables = new OnlineTables(this, session);
        offlineTables = new OfflineTables(this);
        loginDialog = new LoginDialog(this, session);
        loginInfo = new LoginInfo(this, session);

        //Toolbar uses Actions so Actions HAS TO BE initialised firstly!
        initActions();

        toolbar = new Toolbar(this, session);

    }

    /**
     * Init method for all actions used in EDEDB.
     */
    private void initActions() {

        actionConnect = new ActionConnect(this);
        actionDisconnect = new ActionDisconnect(this);
        actionDownloadSelected = new ActionDownloadSelected(this, session);
        actionDeleteSelected = new ActionDeleteSelected(this);
        actionChooseDownloadFolder = new ActionChooseDownloadPath(this);
        actionOpenDownloadPath = new ActionOpenDownloadPath(this);
        actionAnalyseSelected = new ActionVisualizeSelected(this);
    }

    /**
     * Actions performed at changing connection status.
     * logged in - show login dialog
     * logged out - closing connection, clearing online tables.
     */
    public void setUserLoggedIn(boolean loggedIn) {

        if (loggedIn) {
            loginDialog.setVisible(true);
            if (session.isConnected()) {
                updateTableView();
                onlineTables.updateExpTable();
            }
        } else {
            session.userLogout();
            updateTableView();

            onlineTables.clearDataTable();
            onlineTables.clearExpTable();
        }

        update();
    }

    /**
     * Actions performed if file was deleted/downloaded.
     * (updating file information in online and offline tables)
     */
    public void fileChange() {

        onlineTables.checkLocalCopies();
        offlineTables.updateDataTable();

        update();
    }

    /**
     * Repaint method for EDEDB. Updates tables content for changes with local file copies.
     */
    public void update() {

        parent.updateMenuItemVisibility();
        loginInfo.updateLoginInfo();
        toolbar.updateButtonsVisibility();

        onlineTables.repaint();
        offlineTables.repaint();
        toolbar.repaint();
        loginInfo.repaint();
    }

    /**
     * Method creating EDEDB Perspective's enviroment.
     * @return JXPanel with all EDEDB Elements.
     */
    public JXPanel initGraphics() {

        mainPanel.removeAll();

        if (firstRun) {
            mainPanel.add(new FirstRun(this), BorderLayout.CENTER);

        } else {
            JXPanel sidebar = new JXPanel(new BorderLayout());
            tableViewPanel = new JXPanel(new BorderLayout());

            offlineMode = true;
            updateTableView();

            sidebar.add(loginInfo, BorderLayout.NORTH);
            sidebar.add(toolbar, BorderLayout.CENTER);
            sidebar.add(new Working(), BorderLayout.SOUTH);

            mainPanel.add(tableViewPanel, BorderLayout.CENTER);
            mainPanel.add(sidebar, BorderLayout.EAST);
        }

        mainPanel.revalidate();
        mainPanel.repaint();

        return mainPanel;
    }

    /**
     * This method sets up the table view panel in accordance to the offlineMode
     * boolean.
     */
    private void updateTableView() {
        tableViewPanel.removeAll();

        if (session.isConnected()) {
            tableViewPanel.add(onlineTables, BorderLayout.CENTER);
        } else {
            tableViewPanel.add(offlineTables, BorderLayout.CENTER);
            offlineTables.updateUserTable();
        }

        tableViewPanel.revalidate();
        tableViewPanel.repaint();
    }

    /**
     * Getter of choose download folder path.
     * @return download action
     */
    public ActionChooseDownloadPath getActionChooseDownloadFolder() {
        return actionChooseDownloadFolder;
    }

    /**
     * Get action delete selected.
     * @return delete action
     */
    public ActionDeleteSelected getActionDeleteSelected() {
        return actionDeleteSelected;
    }

    /**
     * Getter of action connect.
     * @return connect action
     */
    public ActionConnect getActionConnect() {
        return actionConnect;
    }

    /**
     * Getter of action disconnect.
     * @return disconnect action
     */
    public ActionDisconnect getActionDisconnect() {
        return actionDisconnect;
    }

    /**
     * Getter of action download selected.
     * @return download action
     */
    public ActionDownloadSelected getActionDownloadSelected() {
        return actionDownloadSelected;
    }

    /**
     * Getter of action open download path.
     * @return open download folder action
     */
    public ActionOpenDownloadPath getActionOpenDownloadPath() {
        return actionOpenDownloadPath;
    }

    /**
     * Getter of action analyse selected.
     * @return open in analyze perspective action
     */
    public ActionVisualizeSelected getActionAnalyseSelected() {
        return actionAnalyseSelected;
    }

    /**
     * Returns which rigths has user selected in that time.
     * @return owner/subject
     */
    public Rights getRights() {
        return rights;
    }

    /**
     * Setter of rigths.
     * @param rights owner/subject
     */
    public void setRights(Rights rights) {
        this.rights = rights;

        if (session.isConnected()) {
            onlineTables.updateExpTable();
            update();
        }
    }

    /**
     * Method for finding out if are any rows in data view table selected.
     * @return true/false
     */
    public boolean isSelectedFiles() {
        if (offlineMode) {
            return (!offlineTables.getSelectedFiles().isEmpty());
        } else {
            return (!onlineTables.getSelectedFiles().isEmpty());
        }
    }

    /**
     * Getter of list with info about selected rows in data table.
     * @return List of DataRowModel
     */
    public List<DataRowModel> getSelectedFiles() {
        if (offlineMode) {
            return offlineTables.getSelectedFiles();
        } else {
            return onlineTables.getSelectedFiles();
            
        }
    }

    /**
     * Method that unselects all rows in data table.
     */
    public void unselectAllFiles() {
        for (DataRowModel selected : getSelectedFiles()) {
            selected.setSelected(false);
        }

        update();
    }

    /**
     * Setter for absolute location of download folder path.
     * @param downloadPath Absolute folder location String
     */
    public void setDownloadPath(String downloadPath) {

        setConfigKey("ededb.downloadfolder", downloadPath);
        this.downloadPath = downloadPath;
    }

    /**
     * Getter of download path string.
     * @return Absolute path to download folder
     */
    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     * Setter of firstRun boolean.
     * @param firstRun true/false
     */
    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    /**
     * Getter of firstRun boolean.
     * @return true/false
     */
    public boolean isFirstRun() {
        return firstRun;
    }

    /**
     * Method which finds out whether the files is already downloaded.
     * @param info DataFile
     * @return integer of DataRowModel (HAS_LOCAL/NO_LOCAL/ERROR)
     */
    public synchronized int isAlreadyDownloaded(DataFileInfo info) {

        String path = getDownloadPath() + File.separator
                + session.getUsername() + File.separator
                + info.getExperimentId() + " - " + info.getScenarioName()
                + File.separator + info.getFilename();

        File file = new File(path);

        if (!file.exists()) {
            return DataRowModel.NO_LOCAL_COPY;
        } else if (isDownloading(info.getFileId())) {
            return DataRowModel.DOWNLOADING;
        } else if (file.length() != info.getLength()) {
            return DataRowModel.ERROR;
        } else {
            return DataRowModel.HAS_LOCAL_COPY;
        }
    }

    /**
     * Sets whether control elements of EDEDB are active or not.
     * @param active true/false
     */
    public void setElementsActive(final boolean active) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lock = !active;
                toolbar.setButtonsEnabled(active);
                parent.setMenuItemEnabled(active);

                update();
            }
        });
    }

    /**
     * Setter of EDEDB online/offline view selection.
     * After setting the mode, table view panel is updated.
     * @param offlineMode true/false
     */
    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;

        updateTableView();
    }

    /**
     * Getter of EDEDB online/offline view selection
     * @return true/false
     */
    public boolean isOfflineMode() {
        return offlineMode;
    }

    /**
     * Getter whether are EDEDB control elements locked (disabled).
     * @return true/false
     */
    public boolean isLock() {
        return lock;
    }

    /**
     * Getter of config file location.
     * @return relative path to config file
     */
    public String getConfigFilePath() {
        return configFile;
    }

    /**
     * Adds file id to set of downloading files.
     * @param fileId 
     */
    public synchronized void addDownloading(int fileId) {
        downloadingFiles.add(fileId);

        fileChange();
    }

    /**
     * Checks whether the files is currently downloading.
     * @param fileId
     * @return true/false
     */
    public synchronized boolean isDownloading(int fileId) {
        if (downloadingFiles.contains(fileId)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checking whether there is any downloading currently.
     * @return
     */
    public synchronized boolean isDownloading() {
        return !downloadingFiles.isEmpty();
    }

    /**
     * Removes file id from set of currently downloading files.
     * @param fileId
     * @return removal success true/false
     */
    public synchronized boolean removeDownloading(int fileId) {
        boolean success = downloadingFiles.remove(fileId);

        for (DataRowModel row : onlineTables.getRows()) {
            if (row.getFileInfo().getFileId() == fileId) {
                row.setDownloaded(isAlreadyDownloaded(row.getFileInfo()));
            }
        }

        fileChange();
        return success;
    }

    /**
     * Count of currently downloading files.
     * @return size of downloading files set
     */
    public int getDownloadingSize() {
        return downloadingFiles.size();
    }

    /**
     * Getter of String key from config properties file.
     * @param key String key
     * @return String value
     */
    public String getConfigKey(String key) {
        FileInputStream inPropStream = null;
        String tmp = null;
        try {
            inPropStream = new FileInputStream(configFile);
            properties.load(inPropStream);
            tmp = properties.getProperty(key);
            inPropStream.close();

        } catch (IOException e) {
        }

        return tmp;
    }

    /**
     * Add/Set key in config properties file.
     * @param key String key
     * @param argument String value
     */
    public void setConfigKey(String key, String argument) {

        File config = new File(configFile);
        if (!config.exists()) {
            try {
                (new File(configFolder)).mkdirs();
                config.createNewFile();
            } catch (IOException ex) {
                JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                        ex.getMessage(), ex);
            }
        }

        FileOutputStream outPropStream = null;
        try {
            outPropStream = new FileOutputStream(configFile);
            properties.setProperty(key, argument);
            properties.store(outPropStream, null);
            outPropStream.close();
        } catch (IOException ex) {
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                    ex.getMessage(), ex);
        }
    }
}
