package ch.ethz.origo.jerpa.data.perspective.filter;

import ch.ethz.origo.juigle.data.tables.Record;

/**
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/29/09)
 * @since JERPA version 0.1.0 (11/29/09)
 * @see Record
 * @see Comparable
 */
public class FilterRecord extends Record implements Comparable<FilterRecord> {

	private String name = "";
	private String author = "";
	private String description = "";
	private String version = "";

	
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
	
	public String toString() {
		return name + " " + author + " " + version;
	}

	@Override
	public int compareTo(FilterRecord fr) {
		int res = 0;
		int nameEq = this.getName().compareTo(fr.getName());
		int versionEq = this.getVersion().compareTo(fr.getVersion());
		int author = this.getAuthor().compareTo(fr.getAuthor());
	
		if (nameEq == 0 && versionEq == 0 && author == 0) {
			return 0;
		} else if (nameEq == 0 && versionEq != 1 && author == 0) {
			return 1;
		} else if (nameEq == 0 && author == 1) {
			return 1;
		}
		return res;
	}
	
}