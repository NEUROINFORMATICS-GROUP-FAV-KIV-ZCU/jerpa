/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2011 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.importdialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math.MathException;
import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.EErrorTitleType;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectLoader;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.JUIGLEErrorParser;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.application.project.Project;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * 
 * Class which represents a logic of application import dialog
 * 
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 1.1.0 (4/26/2011)
 * @since 0.1.0 (3/20/2010)
 * @see ILanguage
 */
public class ImportDialogProvider implements ILanguage {

	/** Logger for this class */
	private static final Logger logger = Logger
			.getLogger(ImportDialogProvider.class);

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

	private String resourceBundlePath;

	private ResourceBundle resource;

	public ImportDialogProvider(SignalSessionManager app)
			throws PerspectiveException {
		this.app = app;
		dialogState = NONE;
		// set localized files
		setLocalizedResourceBundle(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY));
		// attach class as Observer of Language Observable
		LanguageObservable.getInstance().attach(this);
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
		try {
			updateText();
		} catch (JUIGLELangException e) {
			logger.error(e.getMessage(), e);
			throw new PerspectiveException(e.getMessage(), e);
		}
	}

	protected int closeImportDialog() {
		targetPanel = null;
		sourcePanel = null;
		// detach dialog window class from language observable
		LanguageObservable.getInstance().detach(dialogWindow);
		// close dialog window
		dialogWindow.setVisible(false);
		dialogWindow.dispose();
		dialogWindow = null;

		SignalPerspectiveObservable.getInstance().setState(
				SignalPerspectiveObservable.MSG_MODAL_DIALOG_CLOSED);
		// detach this class from Language observable
		LanguageObservable.getInstance().detach(this);

		return 0;
	}

	protected void setDialogContent() {
		switch (dialogState) {
		case TARGET_SELECTION:
			dialogWindow.backButton.setEnabled(false);
			dialogWindow.nextButton.setEnabled(true);
			dialogWindow.dialogPanel.removeAll();
			dialogWindow.dialogPanel.add(getTargetPanel());
			break;

		case SOURCE_SELECTION:
			dialogWindow.backButton.setEnabled(true);
			dialogWindow.nextButton.setEnabled(true);
			dialogWindow.dialogPanel.removeAll();
			dialogWindow.dialogPanel.add(getSourcePanel());
			break;

		case NONE:
			closeImportDialog();
			return;

		}
		setTextBasedOnDialogState();
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
	}

	protected void addFileToImportFileList() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(resource.getString("sp.diag.import.fc.title"));
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
			targetPanel = new TargetPanel(resource);
		}
		targetPanel.setVisible(true);
		return targetPanel;
	}

	private JPanel getSourcePanel() {
		if (sourcePanel == null) {
			sourcePanel = new SourcePanel(this, resource);
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
					importedProject = (SignalProject) projectsForImport.get(i);
				} else {
					SignalProjectLoader projectLoader = new SignalProjectLoader(
							filesForImport.get(i));
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

					logger.info("A new project " + targetProject.getName() + " added.");
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
					StringBuilder sb = new StringBuilder();
					sb.append("JERPA024:");
					sb.append(targetProject.getHeader().getSamplingInterval());
					sb.append(":");
					sb.append(importedProject.getHeader().getSamplingInterval());
					
					String errorText = JUIGLEErrorParser.getErrorMessage(sb.toString(),
							LangUtils.JERPA_ERROR_LIST_PATH);
					JUIGLErrorInfoUtils.showErrorDialog(
							JERPAUtils.getLocalizedErrorTitle(EErrorTitleType.IMPORT), errorText,
							null, Level.SEVERE);
					logger.error(errorText);

					continue;
				}

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null,
						"Error reading file: " + ex.getMessage(), "I/O error",
						JOptionPane.ERROR_MESSAGE);
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
				JUIGLErrorInfoUtils.showErrorDialog(JERPAUtils
						.getLocalizedErrorTitle(EErrorTitleType.AVERAGING_ERR),
						JUIGLEErrorParser.getErrorMessage("JERPA026",
								LangUtils.JERPA_ERROR_LIST_PATH), ex, Level.SEVERE);
				logger.error(ex.getMessage(), ex);
			} catch (CorruptedFileException e) {
				JUIGLErrorInfoUtils.showErrorDialog(JERPAUtils
						.getLocalizedErrorTitle(EErrorTitleType.AVERAGING_WARN),
						JUIGLEErrorParser.getErrorMessage("JERPA027",
								LangUtils.JERPA_ERROR_LIST_PATH), e, Level.WARNING);
				logger.warn(e.getMessage(), e);
			} catch (InvalidFrameIndexException e) {
				JUIGLErrorInfoUtils.showErrorDialog(JERPAUtils
						.getLocalizedErrorTitle(EErrorTitleType.INTERNAL), JUIGLEErrorParser
						.getErrorMessage("JERPA028", LangUtils.JERPA_ERROR_LIST_PATH), e,
						Level.SEVERE);
				logger.error(e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				JUIGLErrorInfoUtils.showErrorDialog(JERPAUtils
						.getLocalizedErrorTitle(EErrorTitleType.AVERAGING_ERR),
						JUIGLEErrorParser.getErrorMessage("JERPA029",
								LangUtils.JERPA_ERROR_LIST_PATH), e, Level.SEVERE);
				logger.error(e.getMessage(), e);
			} catch (MathException e) {
				JUIGLErrorInfoUtils.showErrorDialog(JERPAUtils
						.getLocalizedErrorTitle(EErrorTitleType.AVERAGING_ERR),
						JUIGLEErrorParser.getErrorMessage("JERPA030",
								LangUtils.JERPA_ERROR_LIST_PATH), e, Level.SEVERE);
				logger.error(e.getMessage(), e);
			}
			SignalSessionManager.appendEpochToProject(targetProject, framesValues,
					epoch);
		}

		try {
			SignalPerspectiveObservable.getInstance().setState(
					SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED);
		} catch (Exception e) {
			JUIGLErrorInfoUtils.showErrorDialog(JERPAUtils
					.getLocalizedErrorTitle(EErrorTitleType.UNEXPECTED), JUIGLEErrorParser
					.getErrorMessage("JERPA025", LangUtils.JERPA_ERROR_LIST_PATH), e,
					Level.SEVERE);
			logger.warn(e.getMessage(), e);
		}

	}

	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * NOT IMPLEMENTED FOR THIS CLASS
	 */
	@Override
	public void setResourceBundleKey(String key) {
		// NOT IMPLEMENTED FOR THIS CLASS
	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setTextBasedOnDialogState();
				if (targetPanel != null) {
					targetPanel.updateText(resource);
				}
				if (sourcePanel != null) {
					sourcePanel.updateText(resource);
				}
			}
		});
	}

	private void setTextBasedOnDialogState() {
		switch (dialogState) {
		case TARGET_SELECTION:
			dialogWindow.nextButton.setText(resource
					.getString("sp.diag.import.button.next"));
			break;

		case SOURCE_SELECTION:
			dialogWindow.nextButton.setText(resource
					.getString("sp.diag.import.button.finish"));
			break;
		}
	}

}
