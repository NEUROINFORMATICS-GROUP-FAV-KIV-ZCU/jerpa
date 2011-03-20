package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileDownload;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.ConnectException;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.ws.WebServiceException;

/**
 * @author Petr Miko
 */
public class ActionDownloadSelected extends AbstractAction implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    
    private Controller controller;
    private EDEDSession session;

    public ActionDownloadSelected(Controller controller, EDEDSession session) {
        super();
        
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
        
        this.controller = controller;
        this.session = session;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (session.isConnected()) {
            try {
                if (session.isServerAvailable()) {
                    List<DataRowModel> filesToDownload = controller.getSelectedFiles();

                    if (filesToDownload.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            new JFrame(),
                            resource.getString("actiondownload.ededb.empty.text"),
                            resource.getString("actiondownload.ededb.empty.desc"),
                            JOptionPane.INFORMATION_MESSAGE);
                    }

                    for (DataRowModel file : filesToDownload) {

                        if (controller.isAlreadyDownloaded(file.getFileInfo())) {

                            int retValue = JOptionPane.showConfirmDialog(
                                    null,
                                    resource.getString("actiondownload.ededb.existence.text.part1")
                                    + file.getFileInfo().getFilename()
                                    + resource.getString("actiondownload.ededb.existence.text.part2"),
                                    resource.getString("actiondownload.ededb.existence.desc"),
                                    JOptionPane.YES_NO_OPTION);

                            if (retValue == JOptionPane.NO_OPTION) {
                                file.setSelected(false);
                                controller.repaintAll();
                                continue;
                            }

                        }

                        FileDownload fileDownload = new FileDownload(controller, session, file);
                        fileDownload.start();
                        
                        file.setSelected(false);
                        controller.repaintAll();
                    }

                }
            } catch (ConnectException ex) {
                JUIGLErrorInfoUtils.showErrorDialog(
                                ex.getMessage(),
                                ex.getLocalizedMessage(),
                                ex);
            } catch (WebServiceException ex) {
                JUIGLErrorInfoUtils.showErrorDialog(
                                ex.getMessage(),
                                ex.getLocalizedMessage(),
                                ex);
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
