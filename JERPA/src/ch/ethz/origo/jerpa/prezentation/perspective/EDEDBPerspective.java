/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
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
 *
 * @author Petr Miko
 */
public class EDEDBPerspective extends Perspective {

    private JUIGLEMenuItem ededbMenu;
    private JUIGLEMenuItem ededbMenu2;
    private EDEDSession session;
    private Controller controller;
    private JUIGLEMenuItem connect;
    private JUIGLEMenuItem disconnect;
    private JUIGLEMenuItem downloadFile;
    private JUIGLEMenuItem chooseDir;
    private JUIGLEMenuItem openDir;
    private JUIGLEMenuItem analyseFile;
    private JUIGLEMenuItem deleteFile;

    public EDEDBPerspective() {

        resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB";
        session = new EDEDSession();
        controller = new Controller(this,session);
        
    }

    @Override
    public Icon getPerspectiveIcon() throws PerspectiveException {
        return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "ededb_48.png", 32, 32);
    }

    @Override
    public String getTitle() {
        return resource.getString(getRBPerspectiveTitleKey());
    }

    @Override
    public String getRBPerspectiveTitleKey() {
        return "perspective.title";
    }

    @Override
    public void initPerspectiveMenuPanel() throws PerspectiveException {
        if (menuTaskPane == null) {
            menuTaskPane = new JXTaskPane();
            menuTaskPane.setOpaque(false);

            menuTaskPane.add(initMenu());
        }
    }

    private JUIGLEMenu initMenu() throws PerspectiveException {

        menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
                resourcePath);
        menu.setFloatable(false);
        menu.setRollover(true);

        try {
            menu.addItem(createEdedbMenu());
            menu.addItem(createEdedbMenu2());
            
            updateMenuItemVisibility();
            menu.addMenuSeparator();

            menu.addHeaderHideButton(false);
            menu.addFooterHideButton(false);
        } catch (JUIGLEMenuException e) {
            throw new PerspectiveException(e);
        }

        return menu;
    }

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

        ededbMenu2.addSubItem(downloadFile);
        ededbMenu2.addSubItem(analyseFile);
        ededbMenu2.addSubItem(deleteFile);
        
        return ededbMenu2;
    }

    @Override
    public void initPerspectivePanel() throws PerspectiveException {
        super.initPerspectivePanel();
        mainPanel = controller.initGraphics();
    }
    
    public void updateMenuItemVisibility() {
        
        connect.setVisible(!session.isConnected() && !controller.isFirstRun());
        disconnect.setVisible(session.isConnected() && !controller.isFirstRun());
        downloadFile.setVisible(session.isConnected() && !controller.isFirstRun());
        analyseFile.setVisible(session.isConnected() && !controller.isFirstRun());
        deleteFile.setVisible(session.isConnected() && !controller.isFirstRun());
    }
}
