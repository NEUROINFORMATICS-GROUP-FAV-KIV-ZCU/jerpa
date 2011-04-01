package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.SOAPException_Exception;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;
;import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.ws.WebServiceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Thread extending class for file download.
 *
 * @author Petr Miko
 */


public class FileDownload implements Runnable, ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private DataRowModel rowData;
    private Controller controller;
    private EDEDSession session;
    private String errorText, errorDesc;

    /**
     * Constructor. Sets up which file will be downloaded by saving DataRowModel object.
     *
     * @param controller EDEDB Controller
     * @param session EDEDClient.jar Session
     * @param rowData Information about file, which selected to download
     */
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
        FileOutputStream fstream = null;
        File destFolder = new File(rowData.getLocation());

        if (!destFolder.exists()) {
            boolean success = (new File(rowData.getLocation())).mkdirs();

            if (!success) {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        errorText,
                        errorDesc,
                        JOptionPane.ERROR_MESSAGE);

                return;
            }
        }

        rowData.setDownloaded(DataRowModel.DOWNLOADING);
        try {
            fstream = new FileOutputStream(new File(rowData.getLocation()
                    + File.separator + rowData.getFileInfo().getFilename()));

            DataHandler incommingFile;
            try {
                incommingFile = session.getService().downloadFile(rowData.getFileInfo().getFileId());
                incommingFile.writeTo(fstream);
            } catch (SOAPException_Exception ex) {
                JUIGLErrorInfoUtils.showErrorDialog(
                        ex.getMessage(),
                        resource.getString("soapexception.ededb.text"),
                        ex);
                controller.setUserLoggedIn(false);
            } catch (WebServiceException e){
                JUIGLErrorInfoUtils.showErrorDialog(
                        e.getMessage(),
                        resource.getString("webserviceexception.ededb.text"),
                        e);
                controller.setUserLoggedIn(false);
            }

            fstream.close();

            rowData.setDownloaded(DataRowModel.HAS_LOCAL_COPY);

            controller.fileChange();

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
        } finally {
            if (fstream != null) {
                try {
                    fstream.close();
                } catch (IOException ex) {
                    JUIGLErrorInfoUtils.showErrorDialog(
                            ex.getMessage(),
                            ex.getLocalizedMessage(),
                            ex);
                }
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
     * Init/update method of text. Vital for localization.
     */
    private void initTexts() {

        errorText = resource.getString("filedownload.ededb.error.text");
        errorDesc = resource.getString("filedownload.ededb.error.desc");
    }
}
