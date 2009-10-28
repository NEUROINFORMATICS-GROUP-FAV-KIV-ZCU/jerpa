package ch.ethz.origo.jerpa.application.perspective.signalprocess.project;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.origo.jerpa.application.project.Project;
import ch.ethz.origo.jerpa.application.project.ProjectMementoCaretaker;
import ch.ethz.origo.juigle.prezentation.JUIGLEFileChooser;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 10/25/2009 
 * @since 0.1.0 (06/07/2009)
 * @see Project    
 */
public class SignalProject extends Project {

	@Override
	public ProjectMementoCaretaker createMemento() {
		SignalProject project = new SignalProject();
		
		return new ProjectMementoCaretaker(project);
	}

	@Override
	public void setMemento(ProjectMementoCaretaker memento) {
		SignalProject project = (SignalProject)memento.getState();
	}

	@Override
	public void openFile() {
		JUIGLEFileChooser fileChooser = new JUIGLEFileChooser();
		fileChooser.setDialogTitle("Open file");
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"jERPA project file (*.erpa)", "erpa"));
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"European Data Format (*.edf, *.rec)", "edf", "rec"));
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"Pseudo signal generator (*.generator)", "generator"));
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"BrainStudio Format (*.xml)", "xml"));
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"Brain Vision Data Exchange Header File (*.vhdr)", "vhdr"));
		fileChooser
				.setFileFilter(new FileNameExtensionFilter(
						"All supported files (*.edf, *.erpa, *.generator, *.rec, *.vhdr, *.xml)",
						"edf", "esp", "generator", "rec", "vhdr", "xml"));

		fileChooser.setAcceptAllFileFilterUsed(false);

		if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			//loadFile(file);
		}
		
	}

	@Override
	public void saveFile() {
		// TODO Auto-generated method stub
		
	}

}
