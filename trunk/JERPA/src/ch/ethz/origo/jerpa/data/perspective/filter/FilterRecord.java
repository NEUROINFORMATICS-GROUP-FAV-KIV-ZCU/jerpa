package ch.ethz.origo.jerpa.data.perspective.filter;

import ch.ethz.origo.juigle.data.tables.Record;

/**
 * 
 * @author Vaclav Souhrada
 * @version 0.1.2 (2/08/2010)
 * @since JERPA version 0.1.0 (11/29/09)
 * @see Record
 * @see Comparable
 */
public class FilterRecord extends Record implements Comparable<FilterRecord> {

	private String name = "";
	private String author = "";
	private String description = "";
	private String version = "";
	private String category = "";

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String toString() {
		return name + " " + author + " " + version + " " + category ;
	}

	@Override
	public int compareTo(FilterRecord fr) {
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