package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Petr Miko
 */
public class FileDownload extends Thread implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    
    private DataRowModel rowData;
    private Controller controller;
    private EDEDSession session;

    public FileDownload(Controller controller, EDEDSession session, DataRowModel rowData) {
        super();
        
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.session = session;
        this.rowData = rowData;
    }

    @Override
    public void run() {
        FileOutputStream fstream;
        String path = controller.getDownloadPath() + File.separator
                + session.getUsername() + File.separator
                + rowData.getFileInfo().getExperimentId()
                + " - " + rowData.getFileInfo().getScenarioName();
        File destFolder = new File(path);

        if (!destFolder.exists()) {
            boolean success = (new File(path)).mkdirs();

            if (!success) {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        resource.getString("filedownload.ededb.error.text"),
                        resource.getString("filedownload.ededb.error.desc"),
                        JOptionPane.ERROR_MESSAGE);
                try {
                    this.join();
                } catch (InterruptedException e) {
                    JUIGLErrorInfoUtils.showErrorDialog(
                                e.getMessage(),
                                e.getLocalizedMessage(),
                                e);
                }
                return;
            }
        }

        rowData.setDownloaded(resource.getString("table.ededb.datatable.state.downloading"));
        try {
            fstream = new FileOutputStream(new File(path
                    + File.separator + rowData.getFileInfo().getFilename()));
            fstream.write(session.getService().getDataFileBinaryWhereFileId(rowData.getFileInfo().getFileId()));
            fstream.close();

            rowData.setDownloaded(resource.getString("table.ededb.datatable.state.yes"));
            controller.repaintAll();
            this.join();
        } catch (FileNotFoundException e) {
            JUIGLErrorInfoUtils.showErrorDialog(
                                e.getMessage(),
                                e.getLocalizedMessage(),
                                e);
        } catch (IOException e) {
            JUIGLErrorInfoUtils.showErrorDialog(
                                e.getMessage(),
                                e.getLocalizedMessage(),
                                e);
        } catch (InterruptedException e) {
            JUIGLErrorInfoUtils.showErrorDialog(
                                e.getMessage(),
                                e.getLocalizedMessage(),
                                e);
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
