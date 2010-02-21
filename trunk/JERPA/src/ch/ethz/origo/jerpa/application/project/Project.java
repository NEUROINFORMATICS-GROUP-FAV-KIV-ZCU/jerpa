package ch.ethz.origo.jerpa.application.project;

import java.io.File;


/**
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.2.1 (2/21/2010)
 * @since 0.1.0 (10/21/09)
 * @see Undoable    
 */
public abstract class Project extends Undoable {
	
	protected File projectFile; // neukladat
	
	@Override
	public abstract ProjectMementoCaretaker createMemento();
	
	@Override
	public abstract void setMemento(ProjectMementoCaretaker memento);
	
	public abstract void openFile();
	
	public abstract void saveFile();
	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public abstract void closeBuffers();
	
	public void setProjectFile(File file) {
		this.projectFile = file;
	}
	
	public File getProjectFile() {
		return projectFile;
	}

}