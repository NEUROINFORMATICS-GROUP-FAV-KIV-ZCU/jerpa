package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Observable;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionConnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDeleteSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDisconnect;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionDownloadSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.actions.ActionVisualizeSelected;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.data.tier.Storage;
import ch.ethz.origo.jerpa.data.tier.StorageException;
import ch.ethz.origo.jerpa.data.tier.StorageFactory;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.ExperimentViewerLogic;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.LoginInfo;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Toolbar;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * Main class for EDEDB controlling.
 * 
 * @author Petr Miko
 */
public class EDEDBController extends Observable {

	private final EDEDBPerspective parent;
	private final EDEDClient session;
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
	private final JXPanel mainPanel;
	private JXPanel tableViewPanel;
	private Downloader downloader;

	private final static Logger log = Logger.getLogger(EDEDBController.class);

	private Storage storage;
	private boolean offlineMode = true;
	private boolean lockMode;

	/**
	 * Constructor.
	 * 
	 * @param parent EDEDBPerspective
	 * @param session EDEDClient.jar session
	 */
	public EDEDBController(EDEDBPerspective parent, EDEDClient session) {
		this.parent = parent;
		this.session = session;

		try {
			storage = StorageFactory.getStorage();
			new DataSyncer(session, this, storage);
		}
		catch (StorageException e) {
			log.error(e);
			JUIGLErrorInfoUtils.showErrorDialog("Storage exception.", e.getMessage(), e);
		}

		mainPanel = new JXPanel();
		mainPanel.setLayout(new BorderLayout());

		initClasses();

	}

	/**
	 * Init method for all used classes in EDEDB.
	 */
	private void initClasses() {

		downloader = new Downloader(this, session);
		experimentViewer = new ExperimentViewerLogic(this, session);
		loginDialog = new LoginDialog(session);
		loginInfo = new LoginInfo(this, session);

		// Toolbar uses Actions so Actions HAS TO BE initialized firstly!
		initActions();

		toolbar = new Toolbar(this, session);

		downloader.addObserver(experimentViewer);
		downloader.addObserver(toolbar);
		addObserver(experimentViewer);
		addObserver(toolbar);
		addObserver(parent);

	}

	/**
	 * Init method for all actions used in EDEDB.
	 */
	private void initActions() {

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
			if (session.isConnected()) {
				setServiceOffline(false);
			}
		}
		else {
			session.userLogout();
			setServiceOffline(true);
		}

		experimentViewer.clearDataTable();
		experimentViewer.clearExpTable();

		updateTableView();
		experimentViewer.updateExpTable();

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
	 * @return JXPanel with all EDEDB Elements.
	 */
	public JXPanel initGraphics() {

		mainPanel.removeAll();

		JXPanel sidebar = new JXPanel(new BorderLayout());
		tableViewPanel = new JXPanel(new BorderLayout());

		offlineMode = true;
		updateTableView();

		sidebar.add(loginInfo, BorderLayout.NORTH);
		sidebar.add(toolbar, BorderLayout.CENTER);
		sidebar.add(new Working(), BorderLayout.SOUTH);

		mainPanel.add(tableViewPanel, BorderLayout.CENTER);
		mainPanel.add(sidebar, BorderLayout.EAST);

		mainPanel.revalidate();
		mainPanel.repaint();

		return mainPanel;
	}

	/**
	 * This method sets up the table view panel in accordance to the offlineMode
	 * boolean.
	 */
	private void updateTableView() {
		tableViewPanel.removeAll();

		tableViewPanel.add(experimentViewer, BorderLayout.CENTER);

		tableViewPanel.revalidate();
		tableViewPanel.repaint();
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
	// if (session.isConnected()) {
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

		updateTableView();
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
	 * Getter of data tier storage.
	 * 
	 * @return data storage
	 */
	public Storage getStorage() {
		return storage;
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
