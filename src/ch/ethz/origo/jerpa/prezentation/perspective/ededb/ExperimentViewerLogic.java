package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.Storage;
import ch.ethz.origo.jerpa.data.tier.StorageException;
import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.data.tier.border.Experiment;
import ch.ethz.origo.jerpa.ededclient.generated.DataDownloadException_Exception;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

public class ExperimentViewerLogic extends ExperimentViewer implements Observer, ILanguage {

	private static final long serialVersionUID = 4318865850000265030L;
	private ResourceBundle resource;
	private String resourceBundlePath;

	private final EDEDBController controller;
	private final Storage storage;
	private final EDEDClient session;

	private final static Logger log = Logger.getLogger(ExperimentViewerLogic.class);

	private String expInfoText;
	private String expInfoDesc;
	private String errorConnectionText;
	private String errorConnectionDesc;
	private String errorRangeDesc;
	private List<Integer> selectedExps;

	public ExperimentViewerLogic(EDEDBController controller, EDEDClient session) {
		super();

		this.controller = controller;
		storage = controller.getStorage();
		this.session = session;

		LanguageObservable.getInstance().attach(this);

		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
		initTexts();

		initExperimentTable();
		initDataTable();

		updateExpTable();

	}

	private void initExperimentTable() {
		selectedExps = new ArrayList<Integer>();

		ListSelectionModel selectionModel = expTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && expTable.getSelectedRow() != -1) {
					selectedExps.clear();
					for (Integer i : expTable.getSelectedRows()) {
						selectedExps.add((Integer) expTable.getValueAt(i, 0));
					}

					Thread updateDataThread = new Thread(new Runnable() {

						@Override
						public void run() {
							updateDataTable();
							Working.setActivity(false, "working.ededb.update.datatable");
						}
					});

					Working.setActivity(true, "working.ededb.update.datatable");
					updateDataThread.start();
				}
			}
		});
	}

	private void initDataTable() {
		dataTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (dataTable.getSelectedColumn() != DataTableModel.ACTION_COLUMN && dataTable.getSelectedRow() != -1) {
					dataModel.getData().get(dataTable.getSelectedRow())
					        .setSelected(!dataModel.getData().get(dataTable.getSelectedRow()).isSelected());
				}

				dataTable.revalidate();
				dataTable.repaint();
			}
		});

	}

	public void updateExpTable() {
		Thread updateExpThread = new Thread(new Runnable() {

			@Override
			public void run() {

				if (controller.isServiceOffline()) {
					try {
						List<Experiment> experiments = storage.getExperiments();

						for (Experiment exp : experiments) {
							exp.setScenarioName("Test");
							expModel.addRow(exp);
						}
					}
					catch (StorageException exception) {
						log.error(exception.getMessage(), exception);
						JUIGLErrorInfoUtils.showErrorDialog("Storage exception.", exception.getMessage(), exception);
					}
				}
				else {

					if (session.isConnected()) {
						List<ExperimentInfo> availableExperiments;

						availableExperiments = session.getService().getExperiments(controller.getRights());

						if (availableExperiments != null) {
							JOptionPane.showMessageDialog(new JFrame(), availableExperiments.size() + " " + expInfoText, expInfoDesc,
							        JOptionPane.INFORMATION_MESSAGE);

							clearExpTable();

							for (ExperimentInfo availableExperiment : availableExperiments) {

								Experiment experiment = new Experiment();
								experiment.setExperimentId(availableExperiment.getExperimentId());
								experiment.setScenarioId(availableExperiment.getScenarioId());

								// experiment.setScenarioName(availableExperiment.getScenarioName());
								experiment.setScenarioName("Temporary debug name");

								expModel.addRow(experiment);
							}

							clearDataTable();
						}
						else {
							JOptionPane.showMessageDialog(new JFrame(), errorConnectionText, errorConnectionDesc, JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						clearExpTable();
						clearDataTable();
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
	 * Method clearing all data from experiment view table
	 */
	public void clearExpTable() {
		expModel.clear();
	}

	/**
	 * Method clearing all data from data view table
	 */
	public void clearDataTable() {
		dataModel.clear();
	}

	/**
	 * Method filing data view table with experiment's files information. Shown
	 * information depends on selected experiment in experiment view table.
	 * 
	 * @param row selected experiment in experiment view table
	 */
	public synchronized void updateDataTable() {

		clearDataTable();

		if (controller.isServiceOffline()) {

			try {
				List<DataFile> dataFiles = storage.getDataFiles(selectedExps);

				for (DataFile file : dataFiles)
					dataModel.addRow(file, storage.getFileState(file));
			}
			catch (StorageException exception) {
				log.error(exception.getMessage(), exception);
				JUIGLErrorInfoUtils.showErrorDialog("Storage exception.", exception.getMessage(), exception);
			}
			finally {
				repaint();
			}

		}
		else {

			List<DataFileInfo> dataFileInfos = new LinkedList<DataFileInfo>();

			List<Integer> tmp = new ArrayList<Integer>(selectedExps);

			for (Integer expId : tmp) {

				try {
					dataFileInfos.addAll(session.getService().getExperimentFiles(expId));
				}
				catch (DataDownloadException_Exception e) {

					JUIGLErrorInfoUtils.showErrorDialog(e.getMessage(), resource.getString("soapexception.ededb.text"), e);
				}
				catch (Exception e) {

					JUIGLErrorInfoUtils.showErrorDialog(errorRangeDesc, e.getMessage(), e);
				}
			}

			try {
				for (DataFileInfo info : dataFileInfos) {

					DataFile file = new DataFile();
					file.setExperimentId(info.getExperimentId());
					file.setFileId(info.getFileId());
					file.setFileLength(info.getFileLength());
					file.setFileName(info.getFileName());
					file.setMimeType(info.getMimeType());

					dataModel.addRow(file, storage.getFileState(file));
				}
			}
			catch (StorageException exception) {
				log.error(exception.getMessage(), exception);
				JUIGLErrorInfoUtils.showErrorDialog("Storage exception.", exception.getMessage(), exception);
			}
			finally {
				repaint();
			}
		}
	}

	/**
	 * Returns selected files in data view table.
	 * 
	 * @return list DataRowModel (files info) of users selection in data view
	 *         table
	 */
	public List<DataRowModel> getSelectedFiles() {
		List<DataRowModel> data = dataModel.getData();
		List<DataRowModel> selectedFiles = new ArrayList<DataRowModel>();

		for (DataRowModel row : data) {
			if (row.isSelected()) {
				selectedFiles.add(row);
			}
		}
		return selectedFiles;
	}

	/**
	 * Method for returning row contents.
	 * 
	 * @return rows of data table
	 */
	public List<DataRowModel> getRows() {
		return dataModel.getData();
	}

	/**
	 * Init/update text method. Vital for localization.
	 */
	private void initTexts() {
		expInfoText = resource.getString("tables.ededb.exp.info.text");
		expInfoDesc = resource.getString("tables.ededb.exp.info.desc");
		errorConnectionText = resource.getString("tables.ededb.exp.connection.text");
		errorConnectionDesc = resource.getString("tables.ededb.exp.connection.desc");
		errorRangeDesc = resource.getString("tables.ededb.data.exception.desc");
	}

	/**
	 * Setter of localization resource bundle path
	 * 
	 * @param path path to localization source file.
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Getter of path to resource bundle.
	 * 
	 * @return path to localization file.
	 */
	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * Setter of resource bundle key.
	 * 
	 * @param string key
	 */
	@Override
	public void setResourceBundleKey(String string) {
		throw new UnsupportedOperationException("Method is not implemented yet...");
	}

	/**
	 * Method invoked by change of LanguageObservable.
	 * 
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

	@Override
	public void update(Observable o, Object arg) {

		try {
			for (DataRowModel row : dataModel.getData()) {
				if (row.getState() != FileState.DOWNLOADING) {
					row.setState(storage.getFileState(row.getFileInfo()));
				}
			}

			this.repaint();
		}
		catch (StorageException exception) {
			log.error(exception.getMessage(), exception);
			JUIGLErrorInfoUtils.showErrorDialog("Storage exception.", exception.getMessage(), exception);
		}
	}

}
