package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileState;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowSorter;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.UserTableModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;

/**
 * Class for viewing already downloaded experiment files.
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class OfflineTables extends JSplitPane {

	private static final long serialVersionUID = 3312424890794605301L;
	private UserTableModel userModel;
	private ExpTableModel expModel;
	private DataTableModel dataModel;
	private final EDEDBController controller;
	private String username;
	private final List<String> folders;
	private JXTable expTable;

	/**
	 * Constructor method creating JSplitPane with tables.
	 *
	 * @param controller
	 */
	public OfflineTables(EDEDBController controller) {
		super();

		this.controller = controller;
		folders = new ArrayList<String>();

		this.setResizeWeight(.5d);

		JSplitPane leftSide = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSide.setResizeWeight(.5d);
		leftSide.setTopComponent(createUserTable());
		leftSide.setBottomComponent(createExpTable());

		this.setLeftComponent(leftSide);
		this.setRightComponent(createDataTable());

		updateUserTable();

		this.setDividerLocation(300 + this.getInsets().left);
	}

	/**
	 * Table of users with local copies.
	 *
	 * @return JScrollPane with JXTable in it
	 */
	private Container createUserTable() {
		userModel = new UserTableModel();
		final JXTable userTable = new JXTable(userModel);

		userTable.setAutoCreateRowSorter(true);
		userTable.setFillsViewportHeight(true);
		userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ListSelectionModel selectionModel = userTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && userTable.getSelectedRows().length > 0) {
					username = "" + userModel.getValueAt(userTable.getSelectedRow(), 0);
					updateExpTable();
				}
			}
		});

		return new JScrollPane(userTable);
	}

	/**
	 * Table of users experiments with local copies.
	 *
	 * @return JScrollPane with JXTable in it
	 */
	private Container createExpTable() {
		expModel = new ExpTableModel();
		expTable = new JXTable(expModel);

		expTable.setAutoCreateRowSorter(true);
		expTable.setFillsViewportHeight(true);
		expTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		expTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		ListSelectionModel selectionModel = expTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && expTable.getSelectedRow() != -1) {
					folders.clear();
					for (Integer i : expTable.getSelectedRows()) {
						folders.add(expTable.getValueAt(i, 0) + " - " + expTable.getValueAt(i, 1));
					}

					updateDataTable();
				}
			}
		});

		return new JScrollPane(expTable);
	}

	/**
	 * Table of local data files.
	 *
	 * @return JScrollPane with JXTable in it
	 */
	private Container createDataTable() {
		dataModel = new DataTableModel();

		final JXTable dataTable = new JXTable(dataModel);
		dataTable.setRowSorter(new DataRowSorter(dataModel));

		dataTable.setFillsViewportHeight(true);
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		dataTable.removeColumn(dataTable.getColumnModel().getColumn(DataTableModel.DOWNLOADED_COLUMN));
		dataTable.removeColumn(dataTable.getColumnModel().getColumn(DataTableModel.MIME_COLUMN));

		dataTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (dataTable.getSelectedRow() != -1) {
					if (dataTable.getSelectedColumn() != DataTableModel.ACTION_COLUMN) {
						dataModel.getData().get(dataTable.getSelectedRow())
						.setSelected(!dataModel.getData().get(dataTable.getSelectedRow()).isSelected());
					}

					dataTable.revalidate();
					dataTable.repaint();
				}
			}
		});

		return new JScrollPane(dataTable);
	}

	/**
	 * Method clearing user view table of all data.
	 */
	public void clearUserTable() {
		userModel.clear();
	}

	/**
	 * Method clearing experiment view table of all data.
	 */
	public void clearExpTable() {
		expModel.clear();
	}

	/**
	 * Method clearing data view table of all data.
	 */
	public void clearDataTable() {
		dataModel.clear();
	}

	/**
	 * Loading all usernames with local copies on machine.
	 */
	public void updateUserTable() {
		if (controller.getDownloadPath() != null) {

			Thread updateUserThread = new Thread(new Runnable() {

				@Override
				public void run() {

					File downloadFolder = new File(controller.getDownloadPath());
					File[] experiments = downloadFolder.listFiles();

					clearUserTable();

					if (experiments != null) {
						for (File experiment : experiments) {
							if (experiment.isDirectory()) {
								userModel.addRow(experiment.getName());
								userModel.fireTableDataChanged();
							}
						}
					}
					repaint();
				}
			});

			updateUserThread.start();
		}
	}

	/**
	 * Loading all users experiments which has local copies. User (=row in user
	 * table) muset be selected first.
	 */
	public void updateExpTable() {

		Thread updateExpThread = new Thread(new Runnable() {

			@Override
			public void run() {

				File downloadFolder = new File(controller.getDownloadPath() + File.separator + username);
				File[] experiments = downloadFolder.listFiles();

				clearExpTable();

				if (experiments != null) {
					for (File experiment : experiments) {
						if (experiment.isDirectory()) {
							try {
								String[] name = experiment.getName().split(" - ");
								ExperimentInfo data = new ExperimentInfo();
								data.setExperimentId(Integer.parseInt(name[0]));
								data.setScenarioName(name[1]);
								expModel.addRow(data);
								dataModel.clear();
							}
							catch (Exception e) {}
						}
					}
				}
				repaint();
				Working.setActivity(false, "working.ededb.update.exptable");
			}
		});

		Working.setActivity(true, "working.ededb.update.exptable");
		updateExpThread.start();

	}

	/**
	 * Loading local data files' names.
	 */
	public synchronized void updateDataTable() {

		final boolean tmpMode = controller.isOfflineMode();

		Thread updateDataThread = new Thread(new Runnable() {

			@Override
			public void run() {

				File downloadFolder = null;
				File[] files = null;

				clearDataTable();

				for (String folder : folders) {

					downloadFolder = new File(controller.getDownloadPath() + File.separator + username + File.separator
							+ folder);
					files = downloadFolder.listFiles();

					if (files != null) {
						for (File file : files) {
							if (file.isFile()) {
								DataFileInfo info = new DataFileInfo();
								info.setFilename(file.getName());
								info.setLength(file.length());
								info.setScenarioName(folder);

								dataModel.addRow(info, FileState.HAS_COPY, downloadFolder.getAbsolutePath());
								dataModel.fireTableDataChanged();
							}
						}
					}
					revalidate();
					repaint();
				}
				if (tmpMode) {
					Working.setActivity(false, "working.ededb.update.datatable");
				}
			}
		});

		if (tmpMode) {
			Working.setActivity(true, "working.ededb.update.datatable");
		}
		updateDataThread.start();

	}

	/**
	 * File update method.
	 */
	public void checkLocalCopies() {
		updateUserTable();
		updateExpTable();
		updateDataTable();
	}

	/**
	 * Getter of information about selected rows in data table.
	 *
	 * @return List of DataRowModel information
	 */
	public List<DataRowModel> getSelectedFiles() {
		List<DataRowModel> data = dataModel.getData();
		ArrayList<DataRowModel> selectedFiles = new ArrayList<DataRowModel>();

		for (DataRowModel row : data) {
			if (row.isSelected()) {
				selectedFiles.add(row);
			}
		}
		return selectedFiles;
	}
}
