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
 *    Copyright (C) 2009 - 2011 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ch.ethz.origo.jerpa.jerpalang.LangUtils;

/**
 * Class contains utilities which are used by application. Next contains static
 * methods provided some utilities for application and also contains main
 * constants.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.5 (5/27/2011)
 * @since 0.1.0 (07/18/09)
 */
public class JERPAUtils {

	/** Extension of java classes */
	public static final String JAVA_EXTENSION = ".java";
	/** Path to application used images */
	public static final String IMAGE_PATH = "ch/ethz/origo/jerpa/data/images/";
	/** PLUGIN key for perspective */
	public static final String PLUGIN_PERSPECTIVES_KEY = "Perspective";
	/** PLUGIN key for algorithms for EEG processing */
	public static final String PLUGIN_ALGORITHMS_KEY = "Algorithms";

	private static List<String> listOfFilesToDelete;

	/**
	 * Add full file name (path + file name) to list which contains the list of
	 * all files, which will be deleted while application is closing.
	 * 
	 * @param file
	 *          full file name (path + file name)
	 * @version 0.1.0 (3/24/2010)
	 * @since 0.1.1 (3/24/2010)
	 */
	public static void addFileToDeleteList(String file) {
		if (listOfFilesToDelete == null) {
			listOfFilesToDelete = new ArrayList<String>();
		}
		listOfFilesToDelete.add(file);
	}

	/**
	 * This method delete all created files from list.
	 */
	public static void deleteFilesFromDeleteList() {
		if (listOfFilesToDelete != null) {
			for (String fileName : listOfFilesToDelete) {
				File file = new File(fileName);
				file.delete();
			}
		}
	}

	/**
	 * Return localized title of error based on <code>EErrorType</code> (type of
	 * error).
	 * 
	 * @param errorType
	 *          type of error - enum of <code>EErrorType</code>
	 * 
	 * @return localized title of error based on <code>EErrorType</code>
	 * 
	 * @since 0.1.5 (5/27/2011)
	 */
	public static String getLocalizedErrorTitle(EErrorTitleType errorType) {
		ResourceBundle resource = ResourceBundle
				.getBundle(LangUtils.MAIN_FILE_PATH);
		switch (errorType) {
		case AVERAGING_ERR:
			return resource.getString("jerpa.error.averaging");
		
		case AVERAGING_WARN:
			return resource.getString("jerpa.warn.averaging");
		
		case INTERNAL:
			return resource.getString("jerpa.error.internal");
			
		case IMPORT:
			return resource.getString("jerpa.error.import");

		case UNEXPECTED:
		default:
			return resource.getString("jerpa.error.unexpected");
		}
	}
}