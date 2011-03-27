package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private Controller controller;
    private String warningTextPart1, warningTextPart2, warningDesc;
    private String noLocalText, noLocalDesc;
    private String errorText, errorDesc;

    public ActionDeleteSelected(Controller controller) {
        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initTexts();

        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {

        List<DataRowModel> selected = controller.getSelectedFiles();

        if (!selected.isEmpty()) {
            int retValue = JOptionPane.showConfirmDialog(new JFrame(),
                    warningTextPart1 + " " + selected.size() + " " + warningTextPart2,
                    warningDesc,
                    JOptionPane.WARNING_MESSAGE);

            if (retValue == JOptionPane.CANCEL_OPTION) {
                return;
            }

            for (DataRowModel file : selected) {

                file.setSelected(false);

                String path = file.getLocation() + File.separator + file.getFileInfo().getFilename();

                File temp = new File(path);

                if (!temp.exists()) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            noLocalText,
                            noLocalDesc,
                            JOptionPane.ERROR_MESSAGE);
                    controller.repaintAll();
                    continue;
                }

                boolean success = temp.delete();

                if (!success) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            errorText,
                            errorDesc,
                            JOptionPane.ERROR_MESSAGE);
                }

                controller.repaintAll();
            }
        }
    }

    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initTexts();
            }
        });

    }

    private void initTexts() {

        warningTextPart1 = resource.getString("actiondelete.ededb.warning.text.part1");
        warningTextPart2 = resource.getString("actiondelete.ededb.warning.text.part2");
        warningDesc = resource.getString("actiondelete.ededb.warning.desc");
        noLocalText = resource.getString("actiondelete.ededb.nolocal.text");
        noLocalDesc = resource.getString("actiondelete.ededb.nolocal.desc");
        errorText = resource.getString("actiondelete.ededb.error.text");
        errorDesc = resource.getString("actiondelete.ededb.error.desc");

    }
}
