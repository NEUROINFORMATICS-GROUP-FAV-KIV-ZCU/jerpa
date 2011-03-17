/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginInfo;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Tables;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Toolbar;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;
import java.awt.BorderLayout;
import javax.swing.Icon;
import javax.swing.JSplitPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author Petr Miko
 */
public class EDEDBPerspective extends Perspective {

    private JUIGLEMenuItem ededbMenu;
    private EDEDSession session;

    public EDEDBPerspective() {

        resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB";
        session = new EDEDSession();

//        try {
//            session.userLogIn("petrmiko", "heslo");
//            if(session.isConnected()){
//                System.out.println("Connected");
//            }
//        } catch (WebServiceException ex) {
//            Logger.getLogger(EDEDBPerspective.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ConnectException ex) {
//            Logger.getLogger(EDEDBPerspective.class.getName()).log(Level.SEVERE, null, ex);
//        }

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

        final JUIGLEMenuItem connect = new JUIGLEMenuItem();
        final JUIGLEMenuItem disconnect = new JUIGLEMenuItem();
        final JUIGLEMenuItem download = new JUIGLEMenuItem();
        final JUIGLEMenuItem delete = new JUIGLEMenuItem();

        ededbMenu.setResourceBundleKey("menu.ededb.title");
        connect.setResourceBundleKey("menu.ededb.connect");
        disconnect.setResourceBundleKey("menu.ededb.disconnect");
        download.setResourceBundleKey("menu.ededb.download");
        delete.setResourceBundleKey("menu.ededb.delete");

        ededbMenu.addSubItem(connect);
        ededbMenu.addSubItem(disconnect);
        ededbMenu.addSubItem(download);
        ededbMenu.addSubItem(delete);

        return ededbMenu;
    }

    @Override
    public void initPerspectivePanel() throws PerspectiveException {
        super.initPerspectivePanel();
        mainPanel.setLayout(new BorderLayout());
        
        Tables tables = new Tables(session);
        JXPanel sidebar = new JXPanel(new BorderLayout());
        
        LoginInfo loginInfo = new LoginInfo(session);        
        Toolbar toolbar = new Toolbar(session, tables);
        
        sidebar.add(loginInfo, BorderLayout.NORTH);
        sidebar.add(toolbar, BorderLayout.CENTER);
        
        mainPanel.add(tables, BorderLayout.CENTER);
        mainPanel.add(sidebar, BorderLayout.EAST);

    }
}
