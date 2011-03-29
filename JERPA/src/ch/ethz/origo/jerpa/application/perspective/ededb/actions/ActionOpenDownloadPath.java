package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;

/**
 * Action class opening download folder in OS enviroment.
 *
 * @author Petr Miko
 */
public class ActionOpenDownloadPath extends AbstractAction {

    private Controller controller;

    /**
     * Constructor.
     *
     * @param controller EDEDB Controller
     */
    public ActionOpenDownloadPath(Controller controller) {
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            File file = new File(controller.getDownloadPath());
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (IOException ex) {
            JUIGLErrorInfoUtils.showErrorDialog(
                    ex.getMessage(),
                    ex.getLocalizedMessage(),
                    ex);
        }
    }
}
