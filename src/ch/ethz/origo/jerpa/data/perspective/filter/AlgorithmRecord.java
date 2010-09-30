/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.data.perspective.filter;

import ch.ethz.origo.juigle.data.tables.Record;
import ch.ethz.origo.juigle.plugin.IPluggable;

/**
 * Object as envelope of record, which contains informations about 
 * current plugin. Plugins represents EEG processing method are inserted 
 * to this class.
 * 
 * @author Vaclav Souhrada
 * @version 0.2.1 (3/13/2010)
 * @since JERPA version 0.1.0 (11/29/09)
 * @see Record
 * @see Comparable
 */
public class AlgorithmRecord extends Record implements
		Comparable<AlgorithmRecord> {

	private String name;
	private String author;
	private String description;
	private String basicDescription;
	private String version;
	private String category;
	private IPluggable plugin;

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
	 * Return basic description of algorithm
	 * 
	 * @return basic description of algorithm as String
	 */
	public String getBasicDescription() {
		return basicDescription;
	}

	/**
	 * Set algorithm BASIC description
	 * 
	 * @param basic
	 *          description of algorithm
	 */
	public void setBasicDescription(String basicDescription) {
		this.basicDescription = basicDescription;
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
	 * Set Plugin class <code>Pluggable</code> of algorithm
	 * 
	 * @param algorithmClass
	 *          class
	 */
	public void setAlgorithmClass(IPluggable algorithmClass) {
		this.plugin = algorithmClass;
	}

	/**
	 * Get algorithm class
	 * 
	 * @return class of algorithm
	 */
	public IPluggable getAlgorithmClass() {
		return plugin;
	}

	@Override
	public String toString() {
		return name + " - " + author + " - " + version + " - " + category;
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