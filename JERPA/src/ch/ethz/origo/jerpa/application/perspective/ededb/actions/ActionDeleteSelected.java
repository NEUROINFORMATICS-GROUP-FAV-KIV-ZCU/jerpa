package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private Controller controller;
    private EDEDSession session;

    public ActionDeleteSelected(Controller controller, EDEDSession session) {
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.session = session;
    }

    public void actionPerformed(ActionEvent e) {

        List<DataRowModel> selected = controller.getSelectedFiles();

        if (!selected.isEmpty()) {
            int retValue = JOptionPane.showConfirmDialog(new JFrame(),
                    resource.getString("actiondelete.ededb.warning.text.part1")
                    + " " + selected.size() + " "
                    + resource.getString("actiondelete.ededb.warning.text.part2"),
                    resource.getString("actiondelete.ededb.warning.desc"),
                    JOptionPane.WARNING_MESSAGE);

            if (retValue == JOptionPane.CANCEL_OPTION) {
                return;
            }

            for (DataRowModel file : selected) {

                file.setSelected(false);

                String path = controller.getDownloadPath() + File.separator
                        + session.getUsername() + File.separator
                        + file.getFileInfo().getExperimentId()
                        + " - " + file.getFileInfo().getScenarioName()
                        + File.separator + file.getFileInfo().getFilename();

                File temp = new File(path);

                if (!temp.exists()) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            resource.getString("actiondelete.ededb.nolocal.text"),
                            resource.getString("actiondelete.ededb.nolocal.desc"),
                            JOptionPane.ERROR_MESSAGE);
                    controller.repaintAll();
                    continue;
                }

                boolean success = temp.delete();

                if (success) {
                    file.setDownloaded(resource.getString("table.ededb.datatable.state.no"));
                } else {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            resource.getString("actiondelete.ededb.error.text"),
                            resource.getString("actiondelete.ededb.error.desc"),
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateText() throws JUIGLELangException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
