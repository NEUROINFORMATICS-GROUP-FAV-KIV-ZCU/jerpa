package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Downloader;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.dao.DaoException;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.prezentation.perspective.SignalPerspective;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.PerspectiveLoader;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.application.observers.PerspectiveObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Action class for opening selected file in SignalPerspective.
 *
 * @author Petr Miko
 */
public class ActionVisualizeSelected extends AbstractAction implements ILanguage {

    private static final long serialVersionUID = -7447433391197216763L;
    private final static Logger log = Logger.getLogger(ActionVisualizeSelected.class);
    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();
    private ResourceBundle resource;
    private String resourceBundlePath;
    private final EDEDBController controller;
    private final String[] extensions = {
            Const.EDF_FILE_EXTENSION,
            Const.EDF_FILE_EXTENSION2,
            Const.GENERATOR_EXTENSION,
            Const.KIV_FILE_EXTENSION,
            Const.VHDR_EXTENSION
    };

    /**
     * Constructor method for action of analyze selected.
     *
     * @param controller EDEDBController
     */
    public ActionVisualizeSelected(EDEDBController controller) {
        this.controller = controller;

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));

    }

    /**
     * This method gets selected rows, get file ids a downloads them. During
     * downloading changes state of file in table.
     *
     * @param e performed action
     */

    public void actionPerformed(ActionEvent e) {
        List<DataRowModel> selectedFiles = controller.getSelectedFiles();

        if (selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(),
                    resource.getString("actionanalyse.ededb.empty.text"),
                    resource.getString("actionanalyse.ededb.empty.desc"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (selectedFiles.size() > 1) {
            JOptionPane.showMessageDialog(new JFrame(),
                    resource.getString("actionanalyse.ededb.toomany.text"),
                    resource.getString("actionanalyse.ededb.toomany.desc"),
                    JOptionPane.ERROR_MESSAGE);
            controller.unselectAllFiles();

            return;
        }

        if (selectedFiles.size() == 1 && !Downloader.isDownloading()) {

            DataRowModel selected = selectedFiles.get(0);
            final DataFile file = selected.getDataFile();

            try {
                switch (dataFileDao.getFileState(file)) {
                    case DOWNLOADING:
                        JOptionPane.showMessageDialog(new JFrame(),
                                resource.getString("actionanalyse.ededb.downloading.text"),
                                resource.getString("actionanalyse.ededb.downloading.desc"),
                                JOptionPane.ERROR_MESSAGE);
                        controller.unselectAllFiles();
                        return;
                    case HAS_COPY:
                        break;
                    default:
                        JOptionPane.showMessageDialog(new JFrame(),
                                resource.getString("actionanalyse.ededb.nofile.text"),
                                resource.getString("actionanalyse.ededb.nofile.desc"),
                                JOptionPane.ERROR_MESSAGE);
                        controller.unselectAllFiles();

                        return;
                }
            } catch (HeadlessException e1) {
                Log.error(e1.getMessage(), e1);
            }

            if (!isAnalysable(selected.getExtension())) {
                JOptionPane.showMessageDialog(new JFrame(),
                        resource.getString("actionanalyse.ededb.notsupported.text") + printExtensions(),
                        resource.getString("actionanalyse.ededb.notsupported.desc"),
                        JOptionPane.ERROR_MESSAGE);
                controller.unselectAllFiles();

                return;
            }

            SignalPerspective signalPersp = null;

            try {
                signalPersp = (SignalPerspective) PerspectiveLoader.getInstance().getPerspective(SignalPerspective.ID_PERSPECTIVE);
            } catch (PerspectiveException ex) {
                JUIGLErrorInfoUtils.showErrorDialog(ex.getMessage(), ex.getLocalizedMessage(), ex);
            }

            if (signalPersp != null) {
                final SignalPerspective persp = signalPersp;
                Thread openFile = new Thread(new Runnable() {


                    public void run() {
                        boolean opened = false;

                        try {
                            opened = persp.openFileFromDB(file);
                        } catch (DaoException e1) {
                            log.error(e1);
                        }
                        controller.setElementsActive(true);

                        controller.unselectAllFiles();
                        Working.setActivity(false, "working.ededb.visualise");

                        if (opened) {

                            JOptionPane.showMessageDialog(new JFrame(),
                                    resource.getString("actionanalyse.ededb.done.text"),
                                    resource.getString("actionanalyse.ededb.done.desc"),
                                    JOptionPane.INFORMATION_MESSAGE);
                            PerspectiveObservable.getInstance().changePerspective(persp);
                        }
                    }
                });
                controller.setElementsActive(false);
                Working.setActivity(true, "working.ededb.visualise");
                openFile.start();

            }

        }
    }

    /**
     * Checks if file is analysable in JERPA.
     *
     * @param extension File extension
     * @return true/false
     */
    private boolean isAnalysable(String extension) {

        for (String ext : extensions) {
            if (extension.equals(ext)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Prints extensions in format "extension, extension, ..."
     *
     * @return String of extensions
     */
    private String printExtensions() {
        String exts = "";

        for (int i = 0; i < extensions.length; i++) {
            exts += extensions[i];
            if (i != extensions.length - 1) {
                exts += ", ";
            }
        }
        return exts;
    }

    /**
     * Setter of localization resource budle path
     *
     * @param path path to localization source file.
     */

    public void setLocalizedResourceBundle(String path) {
        resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     *
     * @return path to localization file.
     */

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     *
     * @param string key
     */

    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     *
     * @throws JUIGLELangException
     */

    public void updateText() throws JUIGLELangException {
    }
}
