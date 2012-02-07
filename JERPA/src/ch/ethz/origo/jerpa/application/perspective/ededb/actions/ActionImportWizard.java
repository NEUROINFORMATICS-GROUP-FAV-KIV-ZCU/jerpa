package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.ImportWizardLogic;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.ImportWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action for creating new Import Wizard frame.
 *
 * @author Petr Miko
 */
public class ActionImportWizard extends AbstractAction {

    public ActionImportWizard(){
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
    }

    public void actionPerformed(ActionEvent e) {
        new ImportWizardLogic();
    }
}
