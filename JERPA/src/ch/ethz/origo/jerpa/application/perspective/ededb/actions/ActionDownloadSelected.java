package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.ConnectException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.ws.WebServiceException;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileDownload;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileState;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * Action class for downloading files selected in data view table of EDEDB.
 *
 * @author Petr Miko
 */
public class ActionDownloadSelected extends AbstractAction implements ILanguage {

	private ResourceBundle resource;
	private String resourceBundlePath;
	private final EDEDBController controller;
	private final EDEDClient session;
	private String emptyText, emptyDesc;
	private String existenceTextPart1, existenceTextPart2, existenceDesc;

	/**
	 * Constructor.
	 *
	 * @param controller EDEDB EDEDBController
	 * @param session EDEDClient.jar Session
	 */
	public ActionDownloadSelected(EDEDBController controller, EDEDClient session) {
		super();

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		initTexts();

		this.controller = controller;
		this.session = session;

		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
	}

	/**
	 * Method invoked by performed action.
	 * @param e action
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		List<DataRowModel> filesToDownload = controller.getSelectedFiles();

		if (filesToDownload.isEmpty()) {
			JOptionPane.showMessageDialog(
					new JFrame(),
					emptyText,
					emptyDesc,
					JOptionPane.INFORMATION_MESSAGE);
		}

		//bude stahovano paralelne tolik souboru, kolik je jader CPU
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for (DataRowModel file : filesToDownload) {

			if (controller.isAlreadyDownloaded(file.getFileInfo()) == FileState.HAS_COPY) {

				int retValue = JOptionPane.showConfirmDialog(
						null,
						existenceTextPart1
						+ file.getFileInfo().getFilename()
						+ existenceTextPart2,
						existenceDesc,
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (retValue != JOptionPane.YES_OPTION) {
					file.setSelected(false);

					controller.update();
					continue;
				}
				(new File(file.getFileInfo().getFilename())).delete();
			}

			if (file.getState() == FileState.DOWNLOADING) {
				file.setSelected(false);

				controller.update();
				continue;
			}

			if (session.isConnected()) {
				try {
					if (session.isServerAvailable()) {
						FileDownload fileDownload = new FileDownload(controller, session, file);

						pool.submit(fileDownload);

					}
				} catch (ConnectException ex) {
					JUIGLErrorInfoUtils.showErrorDialog(
							ex.getMessage(),
							ex.getLocalizedMessage(),
							ex);
				} catch (WebServiceException ex) {
					JUIGLErrorInfoUtils.showErrorDialog(
							ex.getMessage(),
							resource.getString("webserviceexception.ededb.text"),
							ex);
				}
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
	 * Update/init text method. Vital for localization.
	 */
	public void initTexts() {
		emptyText = resource.getString("actiondownload.ededb.empty.text");
		emptyDesc = resource.getString("actiondownload.ededb.empty.desc");

		existenceTextPart1 = resource.getString("actiondownload.ededb.existence.text.part1");
		existenceTextPart2 = resource.getString("actiondownload.ededb.existence.text.part2");
		existenceDesc = resource.getString("actiondownload.ededb.existence.desc");
	}
}
