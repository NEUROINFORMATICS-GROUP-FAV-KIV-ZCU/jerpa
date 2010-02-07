package ch.ethz.origo.jerpa.data.filters;

/**
 * This interface have to implement all methods / algorithms. If not, than filter not 
 * be used.
 * 
 * @author Vaclav Souhrada
 * @version 0.3.0 (2/07/09)
 * @since JERPA version 0.1.0 (11/26/09)
 *
 */
public interface IAlgorithmDescriptor {
	
	/**
	 * Return filter name
	 * 
	 * @return filter name as String
	 */
	public String getName();
	
	/**
	 * Return filter current version	
	 * 
	 * @return version of filter
	 */
	public String getVersion();
	
	/**
	 * Return author's name of algorithm
	 * @return
	 */
	public String getAuthorName();
	
	/**
	 * Return basic description of algorithm
	 * @return
	 */
	public String getDescription();

}
