package ch.ethz.origo.jerpa.application.project;
/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 10/21/2009 
 * @since 0.1.0 (06/07/2009)
 * @see Caretaker    
 */
public class ProjectMementoCaretaker implements Caretaker {

	private Project state;
	
	public ProjectMementoCaretaker(Project state) {
		this.state = state;
	}
	
	@Override
	public Project getState() {
		return state;
	}
	
}
