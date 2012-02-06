package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Downloader;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Class for creating side-toolbar for EDEDB.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class Toolbar extends JPanel implements ILanguage, ActionListener, Observer {

	private static final long serialVersionUID = 2538082288377712201L;
	private ResourceBundle resource;
	private String resourceBundlePath;
	private final EDEDBController controller;
	private final EDEDClient session;
	private JButton connectButton, disconnectButton, downloadButton, deleteFileButton, visualizeFileButton, importToDbButton;
	private JRadioButton ownerButton, subjectButton, allButton;

	/**
	 * Creating main panel and setting elements into proper positions.
	 * 
	 * @param controller EDEDBController class of EDEDB
	 * @param session EDEDSession class
	 */
	public Toolbar(EDEDBController controller, EDEDClient session) {
		super();

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		this.session = session;
		this.controller = controller;

		final JPanel radioBar = new JPanel();

		setLayout(new VerticalLayout());
		radioBar.setLayout(new BoxLayout(radioBar, BoxLayout.LINE_AXIS));

		createButtons();

//		radioBar.add(allButton);
//		radioBar.add(ownerButton);
//		radioBar.add(subjectButton);

		this.add(connectButton);
		this.add(disconnectButton);
		this.add(radioBar);
		this.add(visualizeFileButton);
		this.add(downloadButton);
        this.add(importToDbButton);
		this.add(deleteFileButton);

		allButton.setSelected(true);
		disconnectButton.setVisible(false);
		downloadButton.setEnabled(false);

		revalidate();
		this.repaint();
	}

	/**
	 * Buttons initialization and setting their actions
	 */
	private void createButtons() {

		connectButton = new JButton();
		disconnectButton = new JButton();
		downloadButton = new JButton();
		visualizeFileButton = new JButton();
		deleteFileButton = new JButton();
        importToDbButton = new JButton();
		allButton = new JRadioButton();
		ownerButton = new JRadioButton();
		subjectButton = new JRadioButton();

		updateButtonsText();
		createIcons();

		final ButtonGroup group = new ButtonGroup();

		// controller.setRights(Rights.ALL);

		group.add(allButton);
		group.add(ownerButton);
		group.add(subjectButton);

		connectButton.setHorizontalAlignment(SwingConstants.LEFT);
		disconnectButton.setHorizontalAlignment(SwingConstants.LEFT);
		downloadButton.setHorizontalAlignment(SwingConstants.LEFT);
		deleteFileButton.setHorizontalAlignment(SwingConstants.LEFT);
		visualizeFileButton.setHorizontalAlignment(SwingConstants.LEFT);
        importToDbButton.setHorizontalAlignment(SwingConstants.LEFT);

		connectButton.addActionListener(controller.getActionConnect());
		disconnectButton.addActionListener(controller.getActionDisconnect());
		downloadButton.addActionListener(controller.getActionDownloadSelected());
		deleteFileButton.addActionListener(controller.getActionDeleteSelected());
		visualizeFileButton.addActionListener(controller.getActionVisualizeSelected());
        importToDbButton.addActionListener(controller.getActionImportWizard());

		allButton.addActionListener(this);
		allButton.setActionCommand("all");
		ownerButton.addActionListener(this);
		ownerButton.setActionCommand("owner");
		subjectButton.addActionListener(this);
		subjectButton.setActionCommand("subject");
	}

	/**
	 * Setting buttons visibility giving the exact situation.
	 */
	public void updateButtonsVisibility() {

		if (session.isConnected()) {
			connectButton.setVisible(false);
			disconnectButton.setVisible(true);
		}
		else {
			connectButton.setVisible(true);
			disconnectButton.setVisible(false);
		}

		if (session.isConnected() && !controller.isLock()) {
			downloadButton.setEnabled(true);
		}
		else {
			downloadButton.setEnabled(false);
		}

		if (Downloader.isDownloading() || controller.isLock()) {
			visualizeFileButton.setEnabled(false);
		}
		else {
			visualizeFileButton.setEnabled(true);
		}
	}

	/**
	 * Update text method. Vital for localization.
	 */
	public void updateButtonsText() {
		connectButton.setText(resource.getString("sidebar.ededb.toolbar.connect"));
		disconnectButton.setText(resource.getString("sidebar.ededb.toolbar.disconnect"));
		downloadButton.setText(resource.getString("sidebar.ededb.toolbar.download"));
		visualizeFileButton.setText(resource.getString("sidebar.ededb.toolbar.visualise"));
		deleteFileButton.setText(resource.getString("sidebar.ededb.toolbar.deletefile"));
		allButton.setText(resource.getString("sidebar.ededb.toolbar.all"));
		ownerButton.setText(resource.getString("sidebar.ededb.toolbar.owner"));
		subjectButton.setText(resource.getString("sidebar.ededb.toolbar.subject"));
        importToDbButton.setText(resource.getString("sidebar.ededb.toolbar.importToDb"));
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
				Toolbar.this.updateButtonsText();
			}
		});

	}

	/**
	 * Method setting buttons on/off.
	 * 
	 * @param active boolean setting buttons' activeness
	 */
	public void setButtonsEnabled(boolean active) {
		connectButton.setEnabled(active);
		disconnectButton.setEnabled(active);
		deleteFileButton.setEnabled(active);
		visualizeFileButton.setEnabled(active);
		downloadButton.setEnabled(active);
		allButton.setEnabled(active);
		ownerButton.setEnabled(active);
		subjectButton.setEnabled(active);
        importToDbButton.setEnabled(active);

		updateButtonsVisibility();
	}

	/**
	 * Method for creating and setting icons to buttons.
	 */
	private void createIcons() {
		try {
			visualizeFileButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "ededb_48.png", 32, 32));
			connectButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "login_48.png", 32, 32));
			disconnectButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "logout_48.png", 32, 32));
			downloadButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "arrow_down_green_48.png", 32, 32));
			deleteFileButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "cross_48.png", 32, 32));
            importToDbButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "database_48.png", 32, 32));
		}
		catch (final PerspectiveException ex) {
			JUIGLErrorInfoUtils.showErrorDialog(ex.getMessage(), ex.getLocalizedMessage(), ex);
		}
	}

	public void actionPerformed(ActionEvent event) {

		if ("all".equals(event.getActionCommand())) {
			allButton.setSelected(true);
			// controller.setRights(Rights.ALL);
		}
		else if ("owner".equals(event.getActionCommand())) {
			ownerButton.setSelected(true);
			// controller.setRights(Rights.OWNER);
		}
		else if ("subject".equals(event.getActionCommand())) {
			subjectButton.setSelected(true);
			// controller.setRights(Rights.SUBJECT);
		}

	}

	public void update(Observable o, Object arg) {

		updateButtonsVisibility();
		this.repaint();

	}
}
