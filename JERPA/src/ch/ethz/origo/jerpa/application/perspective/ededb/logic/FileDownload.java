package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.SOAPException_Exception;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;
import javax.xml.ws.WebServiceException;
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
    private EDEDBController controller;
    private EDEDClient session;
    private String errorText, errorDesc;

    /**
     * Constructor. Sets up which file will be downloaded by saving DataRowModel object.
     *
     * @param controller EDEDB EDEDBController
     * @param session EDEDClient.jar Session
     * @param rowData Information about file, which selected to download
     */
    public FileDownload(EDEDBController controller, EDEDClient session, DataRowModel rowData) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initTexts();

        this.controller = controller;
        this.session = session;
        this.rowData = rowData;
    }

    /**
     * Synchronized run method of downloading process.
     */
    @Override
    public synchronized void run() {

        Working.setActivity(true, "working.ededb.downloading");

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

        rowData.setSelected(false);

        controller.addDownloading(rowData.getFileInfo().getFileId());

        try {
            fstream = new FileOutputStream(new File(rowData.getLocation()
                    + File.separator + rowData.getFileInfo().getFilename()));

            InputStream inStream = null;
            int in;
            int prev = 0;
            int change;
            long counter = 0;
            try {
                inStream = session.getService().downloadFile(rowData.getFileInfo().getFileId()).getInputStream();
                rowData.setDownloaded(DataRowModel.DOWNLOADING);
                Working.setDownload(0, rowData.getFileInfo());
                while ((in = inStream.read()) != -1) {
                    fstream.write(in);
                    change = (int) (((++counter * 100) / rowData.getFileInfo().getLength()));
                    if (change - prev > 0) {
                        prev = change;
                        Working.setDownload(change, rowData.getFileInfo());
                    }
                }
                fstream.close();
            } catch (SOAPException_Exception ex) {
                JUIGLErrorInfoUtils.showErrorDialog(
                        ex.getMessage(),
                        resource.getString("soapexception.ededb.text"),
                        ex);
                controller.setUserLoggedIn(false);
            } catch (WebServiceException e) {
                JUIGLErrorInfoUtils.showErrorDialog(
                        e.getMessage(),
                        resource.getString("webserviceexception.ededb.text"),
                        e);
                controller.setUserLoggedIn(false);
            } finally {
                if (fstream != null) {
                    fstream.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            }

            fstream.close();

            controller.removeDownloading(rowData.getFileInfo().getFileId());

            return;

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

            Working.setActivity(false, "working.ededb.downloading");

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

            controller.removeDownloading(rowData.getFileInfo().getFileId());
        }
    }

    /**
     * Setter of localization resource budle path
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     * @throws JUIGLELangException
     */
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
