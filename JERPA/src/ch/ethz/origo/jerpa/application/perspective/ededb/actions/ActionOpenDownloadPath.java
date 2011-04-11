package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;

/**
 * Action class opening download folder in OS enviroment.
 *
 * @author Petr Miko
 */
public class ActionOpenDownloadPath extends AbstractAction {

    private EDEDBController controller;

    /**
     * Constructor.
     *
     * @param controller EDEDB EDEDBController
     */
    public ActionOpenDownloadPath(EDEDBController controller) {
        this.controller = controller;
        
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
    }

    /**
     * Performed action method
     * @param e action
     */
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
