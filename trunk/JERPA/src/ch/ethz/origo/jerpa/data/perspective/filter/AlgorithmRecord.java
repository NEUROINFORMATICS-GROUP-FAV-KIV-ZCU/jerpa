package ch.ethz.origo.jerpa.data.perspective.filter;

import ch.ethz.origo.juigle.data.tables.Record;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.2.0 (2/11/2010)
 * @since JERPA version 0.1.0 (11/29/09)
 * @see Record
 * @see Comparable
 */
public class AlgorithmRecord extends Record implements
		Comparable<AlgorithmRecord> {

	private String name;
	private String author;
	private String description;
	private String version;
	private String category;
	private Class<?> algorithmClass;

	/**
	 * Return name of algorithm
	 * 
	 * @return name of algorithm
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set algorithm name
	 * 
	 * @param name
	 *          of algorithm as String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return name of author
	 * 
	 * @return name of author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Set author's name
	 * 
	 * @param author
	 *          name of alg. author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Return basic description of algorithm
	 * 
	 * @return basic description of algorithm as String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set algorithm description
	 * 
	 * @param description
	 *          of algorithm
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Return current algorithm version
	 * 
	 * @return version of algorithm
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set algorithm version
	 * 
	 * @param version
	 *          of algorithm
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Set category of algorithm. For example <code>Filter</code>
	 * 
	 * @param category
	 *          name of category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Return category name of algorithm
	 * 
	 * @return category name
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Set Class of algorithm
	 * 
	 * @param algorithmClass
	 *          class
	 */
	public void setAlgorithmClass(Class<?> algorithmClass) {
		this.algorithmClass = algorithmClass;
	}

	/**
	 * Get algorithm class
	 * 
	 * @return class of algorithm
	 */
	public Class<?> getAlgorithmClass() {
		return algorithmClass;
	}

	@Override
	public String toString() {
		return name + " " + author + " " + version + " " + category;
	}

	@Override
	public int compareTo(AlgorithmRecord fr) {
		int res = 1;
		int nameEq = this.getName().compareTo(fr.getName());
		int versionEq = this.getVersion().compareTo(fr.getVersion());
		int author = this.getAuthor().compareTo(fr.getAuthor());

		if (author == 0 && versionEq == 0 && nameEq == 00) {
			res = 0;
		}
		return res;
	}

}