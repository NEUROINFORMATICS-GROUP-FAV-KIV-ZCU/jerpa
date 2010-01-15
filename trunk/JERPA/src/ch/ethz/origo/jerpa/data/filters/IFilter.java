package ch.ethz.origo.jerpa.data.filters;

/**
 * This interface have to implement all filters. If not, than filter not 
 * be used.
 * 
 * @author Vaclav Souhrada
 * @version 0.2.0 (11/28/09)
 * @since JERPA version 0.1.0 (11/26/09)
 *
 */
public interface IFilter {
	
	public String getName();
	
	public String getVersion();
	
	public String getAuthorName();
	
	public String getDescription();

}
