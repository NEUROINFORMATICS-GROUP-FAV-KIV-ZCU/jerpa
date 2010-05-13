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
package ch.ethz.origo.jerpa.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.4 (4/17/2010)
 * @since 0.1.0 (07/18/09)
 */
public class JERPAUtils {
	
	public static final String JAVA_EXTENSION = ".java";
	
	public static final String IMAGE_PATH = "ch/ethz/origo/jerpa/data/images/";
	
	public static final String PLUGIN_PERSPECTIVES_KEY = "Perspective";
	public static final String PLUGIN_ALGORITHMS_KEY = "Algorithms";

	private static List<String> listOfFilesToDelete;
	
	/**
	 * Add full file name (path + file name) to list which contains the list of 
	 * all files, which will be deleted while aplication is closing.
	 * 
	 * @param file full file name (path + file name)
	 * @version 0.1.0 (3/24/2010)
	 * @since 0.1.1 (3/24/2010)
	 */
	public static void addFileToDeleteList(String file) {
		if (listOfFilesToDelete == null) {
			listOfFilesToDelete = new ArrayList<String>();
		}
		listOfFilesToDelete.add(file);
	}
	
	public static void deleteFilesFromDeleteList() {
		if (listOfFilesToDelete != null) {
			for (String fileName : listOfFilesToDelete) {
				File file = new File(fileName);
				file.delete();
			}			
		}
	}
	
}