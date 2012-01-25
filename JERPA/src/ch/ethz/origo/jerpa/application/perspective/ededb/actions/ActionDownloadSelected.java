package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Downloader;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Action class for downloading files selected in data view table of EDEDB.
 * 
 * @author Petr Miko
 */
public class ActionDownloadSelected extends AbstractAction implements ILanguage {

	private static final long serialVersionUID = -6502996494668699031L;
	private ResourceBundle resource;
	private String resourceBundlePath;
	private final EDEDBController controller;
	private final Downloader downloader;
	private String emptyText, emptyDesc;

	/**
	 * Constructor.
	 * 
	 * @param controller EDEDB EDEDBController
     * @param downloader EDEDB Downloader
	 */
	public ActionDownloadSelected(EDEDBController controller, Downloader downloader) {
		super();

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		initTexts();

		this.controller = controller;
		this.downloader = downloader;

		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
	}

	/**
	 * Method invoked by performed action.
	 * 
	 * @param e action
	 */
	public void actionPerformed(ActionEvent e) {

		List<DataRowModel> filesToDownload = controller.getSelectedFiles();

		if (filesToDownload.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(), emptyText, emptyDesc, JOptionPane.INFORMATION_MESSAGE);
		}

		for (DataRowModel file : filesToDownload) {

			if (file.getState() == FileState.DOWNLOADING) {
				file.setSelected(false);

				controller.update();
				continue;
			}

			downloader.download(file.getDataFile());
		}
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
	 * @throws JUIGLELangException
	 */
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				initTexts();
			}
		});

	}

	/**
	 * Update/init text method. Vital for localization.
	 */
	public void initTexts() {
		emptyText = resource.getString("actiondownload.ededb.empty.text");
		emptyDesc = resource.getString("actiondownload.ededb.empty.desc");
	}
}
