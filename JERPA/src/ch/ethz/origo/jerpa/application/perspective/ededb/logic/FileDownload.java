package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Petr Miko
 */
public class FileDownload extends Thread implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    
    private DataRowModel rowData;
    private Controller controller;
    private EDEDSession session;

    private String errorText, errorDesc;
    private String tableValueDownloading, tableValueYes;

    public FileDownload(Controller controller, EDEDSession session, DataRowModel rowData) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initTexts();

        this.controller = controller;
        this.session = session;
        this.rowData = rowData;
    }

    @Override
    public synchronized void run() {
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
                        errorText,
                        errorDesc,
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

        rowData.setDownloaded(tableValueDownloading);
        try {
            fstream = new FileOutputStream(new File(path
                    + File.separator + rowData.getFileInfo().getFilename()));
            fstream.write(session.getService().getDataFileBinaryWhereFileId(rowData.getFileInfo().getFileId()));
            fstream.close();

            rowData.setDownloaded(tableValueYes);
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

    private void initTexts(){

        errorText = resource.getString("filedownload.ededb.error.text");
        errorDesc = resource.getString("filedownload.ededb.error.desc");

        tableValueDownloading = resource.getString("table.ededb.datatable.state.downloading");
        tableValueYes = resource.getString("table.ededb.datatable.state.yes");

    }
}
