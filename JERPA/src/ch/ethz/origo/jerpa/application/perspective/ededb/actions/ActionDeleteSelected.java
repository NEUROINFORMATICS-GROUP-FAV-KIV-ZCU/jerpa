package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Action class for deletion of selected files' local copies.
 *
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction implements ILanguage{

    private static final long serialVersionUID = -1107239494943191652L;
    private final EDEDBController controller;
    private static final Logger log = Logger.getLogger(ActionDeleteSelected.class);
    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();
    private static String resourceBundlePath;
    private static ResourceBundle resource;

    /**
     * Constructor method for action delete selected files.
     *
     * @param controller
     */
    public ActionDeleteSelected(EDEDBController controller) {

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
    }

    /**
     * Method invoked by performed action
     *
     * @param e action
     */
    public void actionPerformed(ActionEvent e) {

        List<DataRowModel> selected = controller.getSelectedFiles();

        if (!selected.isEmpty()) {
            List<DataFile> toDelete = new ArrayList<DataFile>();
            for (DataRowModel row : selected) {

                if (FileState.HAS_COPY == row.getState()) {
                    DataFile file = row.getDataFile();
                    toDelete.add(file);
                }
            }

            if (toDelete.isEmpty()) {
                JOptionPane.showMessageDialog(null, resource.getString("actiondelete.ededb.error.text"),
                        resource.getString("actiondelete.ededb.error.desc"), JOptionPane.INFORMATION_MESSAGE);
            } else {
                int choice = JOptionPane.showConfirmDialog(null, resource.getString("actiondelete.ededb.warning.text.part1")
                        + " " + toDelete.size()
                        + " " + resource.getString("actiondelete.ededb.warning.text.part2"),
                        resource.getString("actiondelete.ededb.warning.desc"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (choice == JOptionPane.NO_OPTION)
                    return;

                for (DataFile file : toDelete)
                    dataFileDao.removeFile(file);
                controller.update();
            }
        } else {
            JOptionPane.showMessageDialog(null, resource.getString("actiondelete.ededb.empty.text"),
                    resource.getString("actiondelete.ededb.empty.desc"),
                    JOptionPane.INFORMATION_MESSAGE);
        }

        controller.unselectAllFiles();
    }

    /**
	 * Setter of localization resource bundle path
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
	 * Setter of resource bundle key.
	 *
	 * @param string key
	 */
	public void setResourceBundleKey(String string) {
		throw new UnsupportedOperationException("Method is not implemented yet...");
	}

	/**
	 * Method invoked by change of LanguageObservable.
	 *
	 * @throws ch.ethz.origo.juigle.application.exception.JUIGLELangException
	 */
	public void updateText() throws JUIGLELangException {
	}
}
