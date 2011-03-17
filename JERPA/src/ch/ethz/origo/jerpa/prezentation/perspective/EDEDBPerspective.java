/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;
import java.awt.BorderLayout;
import javax.swing.Icon;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author Petr Miko
 */
public class EDEDBPerspective extends Perspective {

    public EDEDBPerspective() {
        resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB";
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
            // initalize menu
            menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
                    resourcePath);
            menu.setFloatable(false);
            menu.setRollover(true);
            //menuTaskPane.add(getMenuItems());
        }
    }
}
