package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * Action for choosing download folder for EDEDB.
 *
 * @author Petr Miko
 */
public class ActionChooseDownloadPath extends AbstractAction {

    private Controller controller;

    /**
     * Constructor.
     *
     * @param controller EDEDB Controller
     */
    public ActionChooseDownloadPath(Controller controller) {
        this.controller = controller;
    }

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
