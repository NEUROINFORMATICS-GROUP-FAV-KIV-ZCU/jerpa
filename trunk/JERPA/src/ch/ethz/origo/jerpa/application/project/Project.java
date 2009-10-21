package ch.ethz.origo.jerpa.application.project;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 10/21/2009 
 * @since 0.1.0 (06/07/2009)
 *     
 */
public abstract class Project extends Undoable {

	@Override
	public abstract ProjectMementoCaretaker createMemento();
	
	@Override
	public abstract void setMemento(ProjectMementoCaretaker memento);
	
		

}