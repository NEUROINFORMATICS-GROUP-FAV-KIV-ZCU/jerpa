package ch.ethz.origo.jerpa.application.perspective.weka.project;

import ch.ethz.origo.juigle.application.project.Project;
import ch.ethz.origo.juigle.application.project.ProjectMementoCaretaker;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 10/25/2009 
 * @since 0.1.0 (06/07/2009)
 * @see Project    
 */
public class WEKAProject extends Project {

	@Override
	public ProjectMementoCaretaker createMemento() {
		WEKAProject project = new WEKAProject();
		return new ProjectMementoCaretaker(project);
	}

	@Override
	public void setMemento(ProjectMementoCaretaker memento) {
		WEKAProject project = (WEKAProject)memento.getState();
	}

	@Override
	public void openFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeBuffers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

}
