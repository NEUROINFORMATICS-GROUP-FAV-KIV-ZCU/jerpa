package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.importdialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math.MathException;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectLoader;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.project.Project;

/**
 * T��da s prezenta�n� a aplika�n� logikou dialogu pro import pr�m�r�.
 * 
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/20/2010)
 * @since 0.1.0 (3/20/2010)
 */
public class ImportDialogProvider {

	private SignalSessionManager app;

	private DialogWindow dialogWindow;
	private TargetPanel targetPanel;
	private SourcePanel sourcePanel;
	private DefaultListModel projectListModel;
	private SignalProject targetProject;
	
	protected static final int NONE = 0;
	protected static final int TARGET_SELECTION = 1;
	protected static final int SOURCE_SELECTION = 2;
	protected static final int BUTTON_NEXT = 3;
	protected static final int BUTTON_BACK = 4;
	
	private int dialogState;
	private List<File> filesForImport;
	private List<Project> projectsForImport;
	private boolean newProject;

	public ImportDialogProvider(SignalSessionManager app) throws PerspectiveException {
		this.app = app;
		dialogState = NONE;
		init();
	}

	private void init() throws PerspectiveException {
		newProject = false;
		dialogState = TARGET_SELECTION;
		dialogWindow = new DialogWindow(this, getTargetPanel());
		if (app.getCurrentProject() == null) {
			targetPanel.importRadioButton.setEnabled(false);
			targetPanel.newProjectRadioButton.setSelected(true);
			targetPanel.projectNameTextField.setEnabled(true);
		} else {
			targetPanel.importRadioButton.setEnabled(true);
			targetPanel.importRadioButton.setSelected(true);
			targetPanel.projectNameTextField.setEnabled(false);
		}
		dialogWindow.setVisible(true);
		filesForImport = new ArrayList<File>();
		projectsForImport = new ArrayList<Project>();
		for (int i = 0; i < app.getProjectsNames().length; i++) {
			projectsForImport.add(app.getProject(i));
		}
	}

	protected int closeImportDialog() {
		targetPanel = null;
		sourcePanel = null;
		dialogWindow.setVisible(false);
		dialogWindow.dispose();
		dialogWindow = null;
		SignalPerspectiveObservable.getInstance().setState(SignalPerspectiveObservable.MSG_MODAL_DIALOG_CLOSED);
		return 0;
	}

	protected void setDialogContent() {
		switch (dialogState) {
		case TARGET_SELECTION:
			dialogWindow.backButton.setEnabled(false);
			dialogWindow.nextButton.setEnabled(true);
			dialogWindow.nextButton.setText("Next >");
			dialogWindow.dialogPanel.removeAll();
			dialogWindow.dialogPanel.add(getTargetPanel());
			break;

		case SOURCE_SELECTION:
			dialogWindow.backButton.setEnabled(true);
			dialogWindow.nextButton.setEnabled(true);
			dialogWindow.nextButton.setText("Finish");
			dialogWindow.dialogPanel.removeAll();
			dialogWindow.dialogPanel.add(getSourcePanel());
			break;

		case NONE:
			closeImportDialog();
			return;

		}
		dialogWindow.repaint();
	}

	protected void setDialogState(int button) throws ProjectOperationException {
		switch (dialogState) {
		case TARGET_SELECTION:
			if (button == BUTTON_NEXT) {
				dialogState = SOURCE_SELECTION;
			}
			break;

		case SOURCE_SELECTION:
			if (button == BUTTON_NEXT) {
				importProject();
				dialogState = NONE;
			} else if (button == BUTTON_BACK) {
				dialogState = TARGET_SELECTION;
			}
			break;
		}
	}

	protected void setOpenedProjectsList() {
		sourcePanel.projectsList.removeAll();

		projectListModel.removeAllElements();

		if (sourcePanel.projectsButton.isSelected()) {
			for (int i = 0; i < projectsForImport.size(); i++) {
				projectListModel.addElement(projectsForImport.get(i).getName());
			}
		} else if (sourcePanel.filesButton.isSelected()) {
			for (int i = 0; i < filesForImport.size(); i++) {
				projectListModel.addElement(filesForImport.get(i));
			}
		}
		// sourcePanel.projectsList.setModel(projectListModel);
	}

	protected void addFileToImportFileList() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open file");
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"jERP Studio project file (*.esp)", "esp"));

		fileChooser.setAcceptAllFileFilterUsed(false);

		if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			filesForImport.add(file);
		}

	}

	protected void removeProjectsFromImportProjectsList(int[] indexes) {
		List<Project> newProjectsForImport = new ArrayList<Project>();
		a: for (int i = 0; i < projectsForImport.size(); i++) {
			for (int j = 0; j < indexes.length; j++) {
				if (indexes[j] == i) {
					continue a;
				}
			}
			newProjectsForImport.add(projectsForImport.get(i));
		}

		projectsForImport = newProjectsForImport;

	}

	protected void removeFilesFromImportFilesList(int[] indexes) {
		List<File> newFilesForImport = new ArrayList<File>();
		a: for (int i = 0; i < filesForImport.size(); i++) {
			for (int j = 0; j < indexes.length; j++) {
				if (indexes[j] == i) {
					continue a;
				}
			}
			newFilesForImport.add(filesForImport.get(i));
		}

		filesForImport = newFilesForImport;

	}

	protected void commitChanges() {
		switch (dialogState) {
		case TARGET_SELECTION:
			if (targetPanel.newProjectRadioButton.isSelected()) {
				targetProject = new SignalProject();
				targetProject.setName(targetPanel.projectNameTextField.getText());
				newProject = true;
			} else if (targetPanel.importRadioButton.isSelected()) {
				targetProject = app.getCurrentProject();
			}
		}
	}

	private JPanel getTargetPanel() {
		if (targetPanel == null) {
			targetPanel = new TargetPanel();
		}
		targetPanel.setVisible(true);
		return targetPanel;
	}

	private JPanel getSourcePanel() {
		if (sourcePanel == null) {
			sourcePanel = new SourcePanel(this);
			projectListModel = new DefaultListModel();
			sourcePanel.projectsList.setModel(projectListModel);
		}

		if (app.getCurrentProject() == null) {
			sourcePanel.projectsButton.setEnabled(false);
			sourcePanel.filesButton.setEnabled(true);
			sourcePanel.filesButton.setSelected(true);
		} else {
			sourcePanel.projectsButton.setEnabled(true);
			sourcePanel.filesButton.setEnabled(true);
			sourcePanel.filesButton.setSelected(true);
		}

		sourcePanel.setVisible(true);
		return sourcePanel;
	}

	private void importProject() throws ProjectOperationException {
		ListModel model = sourcePanel.projectsList.getModel();

		for (int i = 0; i < model.getSize(); i++) {
			SignalProject importedProject;
			try {

				if (sourcePanel.projectsButton.isSelected()) {
					importedProject = (SignalProject)projectsForImport.get(i);
				} else {
					SignalProjectLoader projectLoader = new SignalProjectLoader(filesForImport.get(i));
					importedProject = projectLoader.loadProject();
				}

				if (newProject) {
					Header header = importedProject.getHeader().clone();
					header.setSubjectName("");
					header.setPersonalNumber("");
					header.setDocName("");
					header.setNumberOfSamples(0);
					BufferCreator bc = new BufferCreator(header);
					targetProject.setBuffer(bc.getBuffer());
					targetProject.setHeader(header);
					app.addProject(targetProject);
					// System.out.println("new project added");
					newProject = false;

					List<Integer> selectedChannels = new ArrayList<Integer>();
					for (int j = 0; j < app.getCurrentProject().getHeader()
							.getNumberOfChannels(); j++) {
						selectedChannels.add(j);
					}
					app.getCurrentProject().setSelectedChannels(selectedChannels);

					List<Integer> averagedChannels = new ArrayList<Integer>();
					for (int j = 0; j < app.getCurrentProject().getHeader()
							.getNumberOfChannels(); j++) {
						averagedChannels.add(j);
					}
					app.getCurrentProject().setAveragedSignalsIndexes(averagedChannels);

				}

				if (Math.abs(targetProject.getHeader().getSamplingInterval()
						- importedProject.getHeader().getSamplingInterval()) > 0.5) {
					JOptionPane.showMessageDialog(null,
							"Sampling rate not equal:\nTarget project: "
									+ targetProject.getHeader().getSamplingInterval()
									+ "\nImported project: "
									+ importedProject.getHeader().getSamplingInterval(),
							"Import error", JOptionPane.ERROR_MESSAGE);
					continue;
				}

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Error reading file: "
						+ ex.getMessage(), "I/O error", JOptionPane.ERROR_MESSAGE);
				continue;
			} catch (CorruptedFileException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(),
						"Corrupted project file", JOptionPane.ERROR_MESSAGE);
				continue;
			}

			List<float[]> framesValues = new ArrayList<float[]>();
			Epoch epoch = new Epoch(importedProject.getHeader().getNumberOfChannels());

			try {
				AveragingDataManager.calculateAverages(importedProject, framesValues,
						epoch);
			} catch (NullPointerException ex) {
				JOptionPane.showMessageDialog(null, "Error calculating averages: "
						+ ex.getMessage(), "Averaging error", JOptionPane.ERROR_MESSAGE);
			} catch (CorruptedFileException e) {
				JOptionPane.showMessageDialog(null, "No averaged epochs in project",
						"Averaging warning", JOptionPane.WARNING_MESSAGE);
			} catch (InvalidFrameIndexException e) {
				JOptionPane.showMessageDialog(null,
						"Internal error while calculating averages: " + e.getMessage(),
						"Internal error", JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, "ANOVA calculating error: "
						+ e.getMessage(), "Averaging error", JOptionPane.ERROR_MESSAGE);
				System.err.println("Chyba p�i v�po�tu pr�m�ru ANOVOU");
			} catch (MathException e) {
				JOptionPane.showMessageDialog(null,
						"Math Exception: " + e.getMessage(), "Averaging error",
						JOptionPane.ERROR_MESSAGE);
			}
			SignalSessionManager.appendEpochToProject(targetProject, framesValues, epoch);
		}
		
		try {
			SignalPerspectiveObservable.getInstance().setState(SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Unexpected exception after importing averages: " + e.getMessage(),
					"Unexpected exception", JOptionPane.ERROR_MESSAGE);
		}

	}
}
