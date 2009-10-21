package ch.ethz.origo.jerpa.application.project;

/**
 * Memento Pattern for undo/redo mechanism in projects.
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 10/21/2009 
 * @since 0.1.0
 *      
 */
public interface IProjectOriginator {

	public ProjectMementoCaretaker createMemento();
	
	public void setMemento(ProjectMementoCaretaker memento);
}
