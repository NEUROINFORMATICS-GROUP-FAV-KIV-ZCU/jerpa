package ch.ethz.origo.jerpa.application.project;


/**
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.2.0 (11/18/09)
 * @since 0.1.0 (10/21/09)
 * @see Undoable    
 */
public abstract class Project extends Undoable {
	
	@Override
	public abstract ProjectMementoCaretaker createMemento();
	
	@Override
	public abstract void setMemento(ProjectMementoCaretaker memento);
	
	public abstract void openFile();
	
	public abstract void saveFile();
	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public abstract void closeBuffers();


}