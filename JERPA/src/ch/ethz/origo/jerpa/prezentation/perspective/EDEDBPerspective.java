/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Controller;
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
    private EDEDSession session;
    private Controller controller;
    private JUIGLEMenuItem connect;
    private JUIGLEMenuItem disconnect;
    private JUIGLEMenuItem download;
    private JUIGLEMenuItem chooseDir;

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
        download = new JUIGLEMenuItem();
        chooseDir = new JUIGLEMenuItem();

        ededbMenu.setResourceBundleKey("menu.ededb.title");
        connect.setResourceBundleKey("menu.ededb.connect");
        disconnect.setResourceBundleKey("menu.ededb.disconnect");
        download.setResourceBundleKey("menu.ededb.download");
        chooseDir.setResourceBundleKey("menu.ededb.choosedir");
        
        connect.setAction(controller.getActionConnect());
        disconnect.setAction(controller.getActionDisconnect());
        download.setAction(controller.getActionDownloadSelected());
        chooseDir.setAction(controller.getActionChooseDownloadFolder());
        

        ededbMenu.addSubItem(connect);
        ededbMenu.addSubItem(disconnect);
        ededbMenu.addSubItem(download);
        ededbMenu.addSubItem(chooseDir);
        
        updateMenuItemVisibility();

        return ededbMenu;
    }

    @Override
    public void initPerspectivePanel() throws PerspectiveException {
        super.initPerspectivePanel();
        mainPanel = controller.initGraphics();
    }
    
    public void updateMenuItemVisibility() {
        connect.setVisible(!session.isConnected() && !controller.isFirstRun());
        disconnect.setVisible(session.isConnected() && !controller.isFirstRun());
        download.setVisible(session.isConnected() && !controller.isFirstRun());
    }

}
