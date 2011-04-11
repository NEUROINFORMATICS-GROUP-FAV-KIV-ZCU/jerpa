package ch.ethz.origo.jerpa.prezentation.perspective;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;
import javax.swing.Icon;
import org.jdesktop.swingx.JXTaskPane;

/**
 * EEG/ERP Database Experiment Browser (EDEDB) perspective class. Provides user interface for working with remote data in Database, download them
 * and open them in SignalPerspective.
 *
 * @author Petr Miko
 */
public class EDEDBPerspective extends Perspective {

    private static final String ID_PERSPECTIVE = EDEDBPerspective.class.getName();
    private JUIGLEMenuItem ededbMenu;
    private JUIGLEMenuItem ededbMenu2;
    private EDEDClient session;
    private EDEDBController controller;
    private JUIGLEMenuItem connect;
    private JUIGLEMenuItem disconnect;
    private JUIGLEMenuItem downloadFile;
    private JUIGLEMenuItem chooseDir;
    private JUIGLEMenuItem openDir;
    private JUIGLEMenuItem analyseFile;
    private JUIGLEMenuItem deleteFile;
    private boolean menuInited = false;

    /**
     * Constructor creating EEG/ERP Database - Experiment Data Browser Perspective
     */
    public EDEDBPerspective() {
        resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB";
        session = new EDEDClient();
        controller = new EDEDBController(this, session);

    }

    /**
     * Override method for setting perspective's icon
     * @return perspective's icon
     * @throws PerspectiveException
     */
    @Override
    public Icon getPerspectiveIcon() throws PerspectiveException {
        return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "ededb_48.png", 32, 32);
    }
    
    /**
     * Override method for getting perspective's title
     * @return perspective's title
     */
    @Override
    public String getTitle() {
        return resource.getString(getRBPerspectiveTitleKey());
    }

    /**
     * An override method setting perspective's resource bundle title key.
     * @return title key
     */
    @Override
    public String getRBPerspectiveTitleKey() {
        return "perspective.title";
    }

    /**
     * An override method for gettting perspective's id
     * @return String ID
     */
    @Override
    public String getID() {
        return ID_PERSPECTIVE;
    }

    /**
     * An override method for initialising perspective's menu panel.
     * @throws PerspectiveException
     */
    @Override
    public void initPerspectiveMenuPanel() throws PerspectiveException {
        if (menuTaskPane == null) {
            menuTaskPane = new JXTaskPane();
            menuTaskPane.setOpaque(false);

            menuTaskPane.add(initMenu());
        }
    }

    /**
     * Method creating initialised menu.
     * @return JUIGLEMenu inited
     * @throws PerspectiveException
     */
    private JUIGLEMenu initMenu() throws PerspectiveException {

        menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
                resourcePath);
        menu.setFloatable(false);
        menu.setRollover(true);

        try {
            menu.addItem(createEdedbMenu());
            menu.addItem(createEdedbMenu2());

            menuInited = true;

            updateMenuItemVisibility();
            menu.addMenuSeparator();

            menu.addHeaderHideButton(false);
            menu.addFooterHideButton(false);
        } catch (JUIGLEMenuException e) {
            throw new PerspectiveException(e);
        }

        return menu;
    }

    /**
     * Creating items for first EDEDB menu.
     * @return first EDEDB JUIGLEMenuItem
     */
    private JUIGLEMenuItem createEdedbMenu() {
        ededbMenu = new JUIGLEMenuItem(getLocalizedString("menu.ededb.title"));

        connect = new JUIGLEMenuItem();
        disconnect = new JUIGLEMenuItem();
        openDir = new JUIGLEMenuItem();
        chooseDir = new JUIGLEMenuItem();

        ededbMenu.setResourceBundleKey("menu.ededb.title");
        connect.setResourceBundleKey("menu.ededb.connect");
        disconnect.setResourceBundleKey("menu.ededb.disconnect");
        openDir.setResourceBundleKey("menu.ededb.opendir");
        chooseDir.setResourceBundleKey("menu.ededb.choosedir");

        connect.setAction(controller.getActionConnect());
        disconnect.setAction(controller.getActionDisconnect());
        chooseDir.setAction(controller.getActionChooseDownloadFolder());
        openDir.setAction(controller.getActionOpenDownloadPath());

        ededbMenu.addSubItem(connect);
        ededbMenu.addSubItem(disconnect);
        ededbMenu.addSubItem(chooseDir);
        ededbMenu.addSubItem(openDir);

        return ededbMenu;
    }

    /**
     * Creating items for second EDEDB menu.
     * @return second EDEDB JUIGLEMenuItem
     */
    private JUIGLEMenuItem createEdedbMenu2() {
        ededbMenu2 = new JUIGLEMenuItem(getLocalizedString("menu2.ededb.title"));

        downloadFile = new JUIGLEMenuItem();
        analyseFile = new JUIGLEMenuItem();
        deleteFile = new JUIGLEMenuItem();

        ededbMenu2.setResourceBundleKey("menu2.ededb.title");
        downloadFile.setResourceBundleKey("menu2.ededb.download");
        analyseFile.setResourceBundleKey("menu2.ededb.analysefile");
        deleteFile.setResourceBundleKey("menu2.ededb.delete");

        downloadFile.setAction(controller.getActionDownloadSelected());
        deleteFile.setAction(controller.getActionDeleteSelected());
        analyseFile.setAction(controller.getActionAnalyseSelected());

        ededbMenu2.addSubItem(downloadFile);
        ededbMenu2.addSubItem(analyseFile);
        ededbMenu2.addSubItem(deleteFile);

        return ededbMenu2;
    }

    /**
     * Override method for initialising perspective panel.
     * @throws PerspectiveException
     */
    @Override
    public void initPerspectivePanel() throws PerspectiveException {
        super.initPerspectivePanel();
        mainPanel = controller.initGraphics();
    }

    /**
     * Method for updating menu items visibility
     */
    public void updateMenuItemVisibility() {

        if (!controller.isLock() && menuInited) {

            connect.setVisible(!session.isConnected() && !controller.isFirstRun());
            disconnect.setVisible(session.isConnected() && !controller.isFirstRun());

            downloadFile.setEnabled(session.isConnected() && !controller.isFirstRun()
                    && controller.isOnlineTab());
            analyseFile.setEnabled(!controller.isFirstRun());
            deleteFile.setEnabled(!controller.isFirstRun());

            if (controller.isDownloading()) {
                analyseFile.setEnabled(false);
            } else {
                analyseFile.setEnabled(true);
            }
        }
    }

    /**
     * Method setting all items active/inactive in same time.
     * @param active true/false
     */
    public void setMenuItemEnabled(boolean active) {
        connect.setEnabled(active);
        disconnect.setEnabled(active);
        openDir.setEnabled(active);
        chooseDir.setEnabled(active);

        downloadFile.setEnabled(active);
        analyseFile.setEnabled(active);
        deleteFile.setEnabled(active);
    }
}
