package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

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

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileState;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Action class for deletion of selected files' local copies.
 *
 * @author Petr Miko
 */
public class ActionDeleteSelected extends AbstractAction implements ILanguage {

	private ResourceBundle resource;
	private String resourceBundlePath;
	private final EDEDBController controller;
	private String warningTextPart1, warningTextPart2, warningDesc;
	private String downloadingText, downloadingDesc;
	private String errorText, errorDesc;
	private String emptyText, emptyDesc;

	/**
	 * Constructor method for action delete selected files.
	 * @param controller
	 */
	public ActionDeleteSelected(EDEDBController controller) {
		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		initTexts();

		this.controller = controller;

		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
	}

	/**
	 * Method invoked by performed action
	 * @param e action
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		List<DataRowModel> selected = controller.getSelectedFiles();
		List<DataRowModel> hasLocal = new LinkedList<DataRowModel>();

		if (!selected.isEmpty()) {
			for (DataRowModel temp : selected) {
				FileState exists = temp.getState();
				if (exists == FileState.HAS_COPY || exists == FileState.CORRUPTED) {
					hasLocal.add(temp);
				} else if (exists == FileState.DOWNLOADING) {
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

	/**
	 * Setter of localization resource budle path
	 * @param path path to localization source file.
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Getter of path to resource bundle.
	 * @return path to localization file.
	 */
	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * Setter of resource budle key.
	 * @param string key
	 */
	@Override
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
