package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionAnalyseSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionChooseDownloadPath;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionConnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDeleteSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDisconnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDownloadSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionOpenDownloadPath;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.swingx.JXPanel;

/**
 * Main class for EDEDB controlling.
 *
 * @author Petr Miko
 */
public class Controller {

    private EDEDBPerspective parent;
    private EDEDSession session;
    private LoginDialog loginDialog;
    private LoginInfo loginInfo;
    private OnlineTables onlineTables;
    private OfflineTables offlineTables;
    private Toolbar toolbar;
    private Rights rights;
    private ActionAnalyseSelected actionAnalyseSelected;
    private ActionDownloadSelected actionDownloadSelected;
    private ActionDeleteSelected actionDeleteSelected;
    private ActionDisconnect actionDisconnect;
    private ActionConnect actionConnect;
    private ActionChooseDownloadPath actionChooseDownloadFolder;
    private ActionOpenDownloadPath actionOpenDownloadPath;
    private String downloadPath;
    private boolean firstRun;
    private JXPanel mainPanel;
    private Properties properties;
    private final String configFile = "config/ededb.properties";
    private boolean onlineTab;
    private boolean lock;

    /**
     * Constructor.
     *
     * @param parent EDEDBPerspective
     * @param session EDEDClient.jar session
     */
    public Controller(EDEDBPerspective parent, EDEDSession session) {
        this.parent = parent;
        this.session = session;

        properties = new Properties();
        initDownloadPath();

        mainPanel = new JXPanel();
        mainPanel.setLayout(new BorderLayout());

        initClasses();
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

        actionConnect = new ActionConnect(loginDialog);
        actionDisconnect = new ActionDisconnect(this, session);
        actionDownloadSelected = new ActionDownloadSelected(this, session);
        actionDeleteSelected = new ActionDeleteSelected(this);
        actionChooseDownloadFolder = new ActionChooseDownloadPath(this);
        actionOpenDownloadPath = new ActionOpenDownloadPath(this);
        actionAnalyseSelected = new ActionAnalyseSelected(this);
    }

    /**
     * Update method for EDEDB. Controles mainly connection to EEG/ERP Database.
     */
    public void update() {
        Working.show();

        parent.updateMenuItemVisibility();
        loginInfo.updateLoginInfo();
        toolbar.updateButtonsVisibility();
        onlineTables.updateExpTable();

        Working.hide();
        repaintAll();
    }

    /**
     * Repaint method for EDEDB. Updates tables content for changes with local file copies.
     */
    public void repaintAll() {

        if(!lock){
            offlineTables.checkLocalCopies();
            onlineTables.checkLocalCopies();
        }

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
            JTabbedPane tabs = new JTabbedPane();

            tabs.addTab("Online", onlineTables);
            tabs.addTab("Offline", offlineTables);

            onlineTab = true;

            tabs.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    JTabbedPane tabs = (JTabbedPane) e.getSource();

                    if (tabs.getSelectedIndex() == 0) {
                        onlineTab = true;
                    } else {
                        onlineTab = false;
                    }

                    if(!lock){
                        toolbar.updateButtonsVisibility();
                    }
                }
            });

            sidebar.add(loginInfo, BorderLayout.NORTH);
            sidebar.add(toolbar, BorderLayout.CENTER);

            mainPanel.add(tabs, BorderLayout.CENTER);
            mainPanel.add(sidebar, BorderLayout.EAST);
        }

        mainPanel.revalidate();
        mainPanel.repaint();

        return mainPanel;
    }

    /**
     * Following methods are action getters.
     * @return download action
     */
    public ActionChooseDownloadPath getActionChooseDownloadFolder() {
        return actionChooseDownloadFolder;
    }

    /**
     *
     * @return delete action
     */
    public ActionDeleteSelected getActionDeleteSelected() {
        return actionDeleteSelected;
    }

    /**
     *
     * @return connect action
     */
    public ActionConnect getActionConnect() {
        return actionConnect;
    }

    /**
     *
     * @return disconnect action
     */
    public ActionDisconnect getActionDisconnect() {
        return actionDisconnect;
    }

    /**
     *
     * @return download action
     */
    public ActionDownloadSelected getActionDownloadSelected() {
        return actionDownloadSelected;
    }

    /**
     *
     * @return open download folder action
     */
    public ActionOpenDownloadPath getActionOpenDownloadPath() {
        return actionOpenDownloadPath;
    }

    /**
     *
     * @return open in analyze perspective action
     */
    public ActionAnalyseSelected getActionAnalyseSelected() {
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
    }

    /**
     * Method for finding out if are any rows in data view table selected.
     * @return true/false
     */
    public boolean isSelectedFiles() {
        if (onlineTab) {
            return (!onlineTables.getSelectedFiles().isEmpty());
        } else {
            return (!offlineTables.getSelectedFiles().isEmpty());
        }
    }

    /**
     * Getter of list with info about selected rows in data table.
     * @return List of DataRowModel
     */
    public List<DataRowModel> getSelectedFiles() {
        if (onlineTab) {
            return onlineTables.getSelectedFiles();
        } else {
            return offlineTables.getSelectedFiles();
        }
    }

    /**
     * Method that unselects all rows in data table.
     */
    public void unselectAllFiles() {
        for (DataRowModel selected : getSelectedFiles()) {
            selected.setSelected(false);
        }
        repaintAll();
    }

    /**
     * Tries to read user's download folder location from properties file.
     * If the file doesn't exist, boolean firstRun is set.
     */
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

    /**
     * Setter for absolute location of download folder path.
     * @param downloadPath Absolute folder location String
     */
    public void setDownloadPath(String downloadPath) {

        File config = new File(configFile);
        FileOutputStream outPropStream = null;
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException ex) {
                JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                        ex.getMessage(), ex);
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
                    ex.getMessage(), ex);
        } catch (IOException ex) {
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR",
                    ex.getMessage(), ex);
        }
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
    public int isAlreadyDownloaded(DataFileInfo info) {

        String path = getDownloadPath() + File.separator
                + session.getUsername() + File.separator
                + info.getExperimentId() + " - " + info.getScenarioName()
                + File.separator + info.getFilename();

        File file = new File(path);

        if (!file.exists()) {
            return DataRowModel.NO_LOCAL_COPY;
        }

        if (file.length() != info.getLength()) {
            return DataRowModel.ERROR;
        }

        return DataRowModel.HAS_LOCAL_COPY;

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
                repaintAll();
            }
        });
    }

    /**
     * Getter of EDEDB online/offline view selection
     * @return true/false
     */
    public boolean isOnlineTab() {
        return onlineTab;
    }

    /**
     * Getter whether are EDEDB control elements locked (disabled).
     * @return true/false
     */
    public boolean isLock(){
        return lock;
    }
}
