package ch.ethz.origo.jerpa.application.algorithms;

/**
 * This interface have to implement all methods / algorithms. If not, than
 * filter not be used.
 * 
 * @author Vaclav Souhrada
 * @version 0.3.3 (3/07/2010)
 * @since JERPA version 0.1.0 (11/26/09)
 * 
 */
public interface IAlgorithm {

	/**
	 * Main method for algorithms. This method will be called by JERPA algorithm
	 * loader after select alg. by user.
	 */
	public void buildAlgorithm();

}
