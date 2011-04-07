package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Action class for deletion of selected files' local copies.
 *
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private EDEDBController controller;
    private String warningTextPart1, warningTextPart2, warningDesc;
    private String downloadingText, downloadingDesc;
    private String errorText, errorDesc;
    private String emptyText, emptyDesc;

    public ActionDeleteSelected(EDEDBController controller) {
        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initTexts();

        this.controller = controller;

        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
    }

    public void actionPerformed(ActionEvent e) {

        List<DataRowModel> selected = controller.getSelectedFiles();
        List<DataRowModel> hasLocal = new LinkedList<DataRowModel>();

        if (!selected.isEmpty()) {
            for (DataRowModel temp : selected) {
                int exists = temp.getDownloaded();
                if (exists == DataRowModel.HAS_LOCAL_COPY || exists == DataRowModel.ERROR) {
                    hasLocal.add(temp);
                } else if (exists == DataRowModel.DOWNLOADING) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            downloadingText,
                            downloadingDesc,
                            JOptionPane.ERROR_MESSAGE);

                    controller.update();
                    temp.setSelected(false);
                } else {
                    temp.setSelected(false);
                }
            }
        }else{
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    emptyText,
                    emptyDesc,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!hasLocal.isEmpty()) {
            int retValue = JOptionPane.showConfirmDialog(new JFrame(),
                    warningTextPart1 + " " + hasLocal.size() + " " + warningTextPart2,
                    warningDesc,
                    JOptionPane.WARNING_MESSAGE);

            if (retValue == JOptionPane.CANCEL_OPTION) {
                return;
            }

            for (DataRowModel file : hasLocal) {

                file.setSelected(false);

                String path = file.getLocation() + File.separator + file.getFileInfo().getFilename();

                File temp = new File(path);

                if (!temp.exists()) {
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

                controller.fileChange();
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

    /**
     * Init/update method for texts. Vital for localization.
     */
    private void initTexts() {

        warningTextPart1 = resource.getString("actiondelete.ededb.warning.text.part1");
        warningTextPart2 = resource.getString("actiondelete.ededb.warning.text.part2");
        warningDesc = resource.getString("actiondelete.ededb.warning.desc");
        downloadingText = resource.getString("actiondelete.ededb.downloading.text");
        downloadingDesc = resource.getString("actiondelete.ededb.downloading.desc");
        errorText = resource.getString("actiondelete.ededb.error.text");
        errorDesc = resource.getString("actiondelete.ededb.error.desc");
        emptyText = resource.getString("actiondelete.ededb.empty.text");
        emptyDesc = resource.getString("actiondelete.ededb.empty.desc");
    }
}
