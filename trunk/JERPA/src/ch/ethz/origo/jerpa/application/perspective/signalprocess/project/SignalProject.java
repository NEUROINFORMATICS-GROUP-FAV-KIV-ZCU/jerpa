package ch.ethz.origo.jerpa.application.perspective.signalprocess.project;

import ch.ethz.origo.jerpa.application.project.Project;
import ch.ethz.origo.jerpa.application.project.ProjectMementoCaretaker;

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

}
