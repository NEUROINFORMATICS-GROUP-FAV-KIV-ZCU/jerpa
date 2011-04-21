package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.PerspectiveLoader;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;
import ch.ethz.origo.jerpa.prezentation.perspective.SignalPerspective;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.application.observers.PerspectiveObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Action class for opening selected file in SignalPerspective.
 *
 * @author Petr Miko
 */
public class ActionAnalyseSelected extends AbstractAction implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private String tooManyText, tooManyDesc;
    private String noFileText, noFileDesc;
    private String downloadingText, downloadingDesc;
    private String notSupportedText, notSupportedDesc;
    private String doneText, doneDesc;
    private String emptyText, emptyDesc;
    private EDEDBController controller;
    private final String[] extensions = {
        Const.EDF_FILE_EXTENSION,
        Const.EDF_FILE_EXTENSION2,
        Const.GENERATOR_EXTENSION,
        Const.KIV_FILE_EXTENSION,
        Const.VHDR_EXTENSION};

    /**
     * Constructor method for action of analyse selected.
     * @param controller EDEDBController
     */
    public ActionAnalyseSelected(EDEDBController controller) {
        this.controller = controller;

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initTexts();
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));

    }

    /**
     * This method gets selected rows, get file ids a downloads them. During downloading changes state of file in table.
     * @param e performed action
     */
    public void actionPerformed(ActionEvent e) {

        List<DataRowModel> selectedFiles = controller.getSelectedFiles();

        if (selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    emptyText,
                    emptyDesc,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (selectedFiles.size() > 1) {
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    tooManyText,
                    tooManyDesc,
                    JOptionPane.ERROR_MESSAGE);
            controller.unselectAllFiles();

            return;
        }

        if (selectedFiles.size() == 1 && !controller.isDownloading()) {

            DataRowModel selected = selectedFiles.get(0);
            
            if(selected.getDownloaded() == DataRowModel.DOWNLOADING)
                return;

            if (selected.getDownloaded() == DataRowModel.DOWNLOADING) {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        downloadingText,
                        downloadingDesc,
                        JOptionPane.ERROR_MESSAGE);
                controller.unselectAllFiles();
                return;
            }

            final File file = new File(selected.getLocation() + File.separator + selected.getFileInfo().getFilename());

            if (!file.exists()) {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        noFileText,
                        noFileDesc,
                        JOptionPane.ERROR_MESSAGE);
                controller.unselectAllFiles();

                return;
            }

            if (!isAnalysable(selected.getExtension())) {
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        notSupportedText + printExtensions(),
                        notSupportedDesc,
                        JOptionPane.ERROR_MESSAGE);
                controller.unselectAllFiles();

                return;
            }


           SignalPerspective signalPersp = null;

            try {
                signalPersp = (SignalPerspective) PerspectiveLoader.getInstance().getPerspective(
                        SignalPerspective.ID_PERSPECTIVE);
            } catch (PerspectiveException ex) {
                JUIGLErrorInfoUtils.showErrorDialog(
                        ex.getMessage(),
                        ex.getLocalizedMessage(),
                        ex);
            }

            if (signalPersp != null) {
                final SignalPerspective persp = signalPersp;
                Thread openFile = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean opened = persp.openFile(file);

                        controller.setElementsActive(true);

                        controller.unselectAllFiles();
                        Working.setActivity(false,"working.ededb.visualise");

                        if (opened) {

                            JOptionPane.showMessageDialog(
                                    new JFrame(),
                                    doneText,
                                    doneDesc,
                                    JOptionPane.INFORMATION_MESSAGE);
                            PerspectiveObservable.getInstance().changePerspective(persp);
                        }
                    }
                });
                controller.setElementsActive(false);
                Working.setActivity(true,"working.ededb.visualise");
                openFile.start();

            }

        }
    }

    /**
     * Init/Update method for texts.
     */
    private void initTexts() {
        tooManyText = resource.getString("actionanalyse.ededb.toomany.text");
        tooManyDesc = resource.getString("actionanalyse.ededb.toomany.desc");
        noFileText = resource.getString("actionanalyse.ededb.nofile.text");
        noFileDesc = resource.getString("actionanalyse.ededb.nofile.desc");
        notSupportedText = resource.getString("actionanalyse.ededb.notsupported.text");
        notSupportedDesc = resource.getString("actionanalyse.ededb.notsupported.desc");
        downloadingText = resource.getString("actionanalyse.ededb.downloading.text");
        downloadingDesc = resource.getString("actionanalyse.ededb.downloading.desc");
        doneText = resource.getString("actionanalyse.ededb.done.text");
        doneDesc = resource.getString("actionanalyse.ededb.done.desc");
        emptyText = resource.getString("actionanalyse.ededb.empty.text");
        emptyDesc = resource.getString("actionanalyse.ededb.empty.desc");
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
    @Override
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initTexts();
            }
        });

    }
}
