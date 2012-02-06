package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.application.perspective.ededb.actions.*;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.*;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;

/**
 * Main class for EDEDB controlling.
 * 
 * @author Petr Miko
 */
public class EDEDBController extends Observable {

	private final EDEDBPerspective parent;
	private final EDEDClient service;
	private LoginDialog loginDialog;
	private LoginInfo loginInfo;
	private ExperimentViewerLogic experimentViewer;
	private Toolbar toolbar;
	// private Rights rights;
	private ActionVisualizeSelected actionVisualizeSelected;
	private ActionDownloadSelected actionDownloadSelected;
	private ActionDeleteSelected actionDeleteSelected;
	private ActionDisconnect actionDisconnect;
	private ActionConnect actionConnect;
    private ActionImportWizard actionImportWizard;
    private Downloader downloader;
    private DataSyncer syncer;

	private final static Logger log = Logger.getLogger(EDEDBController.class);

	private boolean offlineMode = true;
	private boolean lockMode;

	/**
	 * Constructor.
	 * 
	 * @param parent EDEDBPerspective
	 * @param service EDEDClient.jar service
	 */
	public EDEDBController(EDEDBPerspective parent, EDEDClient service) {
		this.parent = parent;
		this.service = service;

		initClasses();

	}

	/**
	 * Init method for all used classes in EDEDB.
	 */
	private void initClasses() {

		downloader = new Downloader(this, service);
		experimentViewer = new ExperimentViewerLogic(this);
		loginDialog = new LoginDialog(service);
		loginInfo = new LoginInfo(this, service);

		// Toolbar uses Actions so Actions HAS TO BE initialized firstly!
		initActions();

		toolbar = new Toolbar(this, service);

		downloader.addObserver(experimentViewer);
		downloader.addObserver(toolbar);
		addObserver(experimentViewer);
		addObserver(toolbar);
		addObserver(parent);

        syncer = new DataSyncer(service,this);

	}

	/**
	 * Init method for all actions used in EDEDB.
	 */
	private void initActions() {

        actionImportWizard = new ActionImportWizard();
		actionConnect = new ActionConnect(this);
		actionDisconnect = new ActionDisconnect(this);
		actionDownloadSelected = new ActionDownloadSelected(this, downloader);
		actionDeleteSelected = new ActionDeleteSelected(this);
		actionVisualizeSelected = new ActionVisualizeSelected(this);
	}

	/**
	 * Actions performed at changing connection status. logged in - show login
	 * dialog logged out - closing connection, clearing online tables.
     * @param loggedIn is user logged in
     */
	public void setUserLoggedIn(boolean loggedIn) {

		if (loggedIn) {
			loginDialog.setVisible(true);
			if (service.isConnected()) {
				setServiceOffline(false);
                syncer.syncNow();
			}
		}
		else {
			service.userLogout();
			setServiceOffline(true);
		}
        loginInfo.updateLoginInfo();
		update();
	}

	/**
	 * Repaint method for EDEDB. Updates tables content for changes with local
	 * file copies.
	 */
	public void update() {

		setChanged();
		notifyObservers();
	}

	/**
	 * Method creating EDEDB Perspective's environment.
	 * 
//	 * @return JXPanel with all EDEDB Elements.
     * @param mainPanel perspective panel
	 */
	public void initGraphics(JXPanel mainPanel) {

		JXPanel sidebar = new JXPanel(new BorderLayout());
        JXPanel tableViewPanel = new JXPanel(new BorderLayout());

		offlineMode = true;
		tableViewPanel.add(experimentViewer, BorderLayout.CENTER);

		sidebar.add(loginInfo, BorderLayout.NORTH);
		sidebar.add(toolbar, BorderLayout.CENTER);
		sidebar.add(new Working(), BorderLayout.SOUTH);

		mainPanel.add(tableViewPanel, BorderLayout.CENTER);
		mainPanel.add(sidebar, BorderLayout.EAST);

		mainPanel.revalidate();
		mainPanel.repaint();
	}

	/**
	 * Get action delete selected.
	 * 
	 * @return delete action
	 */
	public ActionDeleteSelected getActionDeleteSelected() {
		return actionDeleteSelected;
	}

	/**
	 * Getter of action connect.
	 * 
	 * @return connect action
	 */
	public ActionConnect getActionConnect() {
		return actionConnect;
	}

	/**
	 * Getter of action disconnect.
	 * 
	 * @return disconnect action
	 */
	public ActionDisconnect getActionDisconnect() {
		return actionDisconnect;
	}

	/**
	 * Getter of action download selected.
	 * 
	 * @return download action
	 */
	public ActionDownloadSelected getActionDownloadSelected() {
		return actionDownloadSelected;
	}

    /**
     * Getter of action import wizard.
     * @return action of invoking new action wizard frame
     */
    public ActionImportWizard getActionImportWizard() {
        return actionImportWizard;
    }

    /**
	 * Getter of action visualize selected.
	 * 
	 * @return open in analyze perspective action
	 */
	public ActionVisualizeSelected getActionVisualizeSelected() {
		return actionVisualizeSelected;
	}

	// /**
	// * Returns which rigths has user selected in that time.
	// *
	// * @return owner/subject
	// */
	// public Rights getRights() {
	// return rights;
	// }
	//
	// /**
	// * Setter of rights.
	// *
	// * @param rights owner/subject
	// */
	// public void setRights(Rights rights) {
	// this.rights = rights;
	//
	// if (service.isConnected()) {
	// experimentViewer.updateExpTable();
	// update();
	// }
	// }

	/**
	 * Method for finding out if are any rows in data view table selected.
	 * 
	 * @return true/false
	 */
	public boolean isSelectedFiles() {
		return (!experimentViewer.getSelectedFiles().isEmpty());
	}

	/**
	 * Getter of list with info about selected rows in data table.
	 * 
	 * @return List of DataRowModel
	 */
	public List<DataRowModel> getSelectedFiles() {
		return experimentViewer.getSelectedFiles();
	}

	/**
	 * Method that unselects all rows in data table.
	 */
	public void unselectAllFiles() {
		for (DataRowModel selected : getSelectedFiles()) {
			selected.setSelected(false);
		}

		update();
	}

	/**
	 * Sets whether control elements of EDEDB are active or not.
	 * 
	 * @param active true/false
	 */
	public void setElementsActive(final boolean active) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				lockMode = !active;
				toolbar.setButtonsEnabled(active);
				parent.setMenuItemEnabled(active);

				EDEDBController.this.update();
			}
		});
	}

	/**
	 * Setter of EDEDB online/offline view selection. After setting the mode,
	 * table view panel is updated.
	 *
	 * @param offlineMode true/false
	 */
	public void setServiceOffline(boolean offlineMode) {
		this.offlineMode = offlineMode;
	}

	/**
	 * Getter of EDEDB online/offline view selection
	 * 
	 * @return true/false
	 */
	public boolean isServiceOffline() {
		return offlineMode;
	}

	/**
	 * Getter whether are EDEDB control elements locked (disabled).
	 * 
	 * @return true/false
	 */
	public boolean isLock() {
		return lockMode;
	}

	/**
	 * Getter of data file download manager.
	 * 
	 * @return download manager
	 */
	public Downloader getDownloader() {
		return downloader;
	}

}
