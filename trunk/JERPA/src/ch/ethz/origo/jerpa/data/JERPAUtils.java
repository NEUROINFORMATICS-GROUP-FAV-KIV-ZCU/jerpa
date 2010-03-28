package ch.ethz.origo.jerpa.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.3 (3/28/2010)
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