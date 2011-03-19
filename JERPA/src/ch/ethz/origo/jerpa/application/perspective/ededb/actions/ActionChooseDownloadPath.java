package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * @author Petr Miko
 */
public class ActionChooseDownloadPath extends AbstractAction {

    private Controller controller;
    private EDEDSession session;

    public ActionChooseDownloadPath(Controller controller, EDEDSession session) {
        this.controller = controller;
        this.session = session;
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
