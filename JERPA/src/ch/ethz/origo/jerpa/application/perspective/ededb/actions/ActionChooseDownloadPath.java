package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Action for choosing download folder for EDEDB.
 *
 * @author Petr Miko
 */
public class ActionChooseDownloadPath extends AbstractAction {

    private EDEDBController controller;

    /**
     * Constructor.
     *
     * @param controller EDEDB EDEDBController
     */
    public ActionChooseDownloadPath(EDEDBController controller) {
        this.controller = controller;
        
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
    }

    /**
     * Method invoked by performed action.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(new JPanel());

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            controller.setDownloadPath(selected.getAbsolutePath());
            controller.update();
        }
    }
}
