package ch.ethz.origo.jerpa.application.project;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 10/21/2009 
 * @since 0.1.0 (06/07/2009)
 *     
 */
public interface Caretaker {

	/**
	 * Return class which represents memento state of project.
	 * 
	 * @return project memento state Class
	 */
	public Project getState();
}
