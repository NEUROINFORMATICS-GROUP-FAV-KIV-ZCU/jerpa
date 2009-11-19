package noname;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 07/18/09
 * @since 0.1.0
 */
public class JERPAFileNameFilter implements FilenameFilter {
	
	private String[] files;
	
	/** 
	 * Default constructor. In this case, this filter will be
	 * accept all java classes with <code>.java</code> extension.
	 */
	public JERPAFileNameFilter() {
		// do nothing
	}
	
	public JERPAFileNameFilter(String[] filesName) {
		this.files = filesName;
	}

	@Override
	public boolean accept(File dir, String name) {
		if (name.endsWith(JERPAUtils.JAVA_EXTENSION)) {
			if (files == null) {
				return true;
			} else {
				for (String item : files) {
					if (item.trim().equals(name.substring(0, name.lastIndexOf('.')))) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

}
