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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import ch.ethz.origo.juigle.application.exception.PropertiesException;

/**
 * Class for handling with configuration properties
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.9 (5/19/2010)
 * @since 0.1.0 (7/18/09)
 * 
 */
public class ConfigPropertiesLoader {

	/** Configuration file */
	private static final String PROPERTY_FILE = "config/config.properties";

	private static boolean isLoaded = false;

	/** Contains the properties */
	public static Properties properties = new Properties();

	/**
	 * Load properties
	 * 
	 * @throws PropertiesException
	 */
	public static void loadProperties() throws PropertiesException {
		if (!ConfigPropertiesLoader.isLoaded) {
			try {
				properties.load(new FileInputStream(new File(PROPERTY_FILE)));
				PropertyConfigurator.configure(properties);
			} catch (IOException e) {
				throw new PropertiesException("JERPA002:" + PROPERTY_FILE, e);
			}
			isLoaded = true;
		}
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static String getApplicationTitle() {
		return properties.getProperty("jerpa.application.title");
	}

	/**
	 * 
	 * 
	 * @return major version of application
	 */
	public static String getAppMajorVersion() {
		return properties.getProperty("jerpa.application.version.major");
	}

	/**
	 * 
	 * 
	 * @return minor version of application
	 */
	public static String getAppMinorVersion() {
		return properties.getProperty("jerpa.application.version.minor");
	}

	/**
	 * 
	 * 
	 * @return revision version of application
	 */
	public static String getAppRevisionVersion() {
		return properties.getProperty("jerpa.application.version.revision");
	}
	
	/**
	 * Return Major version of application as Integer
	 * 
	 * @return major version of application as Integer
	 */
	public static int getAppMajorVersionAsInt() {
		return Integer.valueOf(properties.getProperty("jerpa.application.version.major"));
	}

	/**
	 * 
	 * 
	 * @return minor version of application as Integer
	 */
	public static int getAppMinorVersionAsInt() {
		return Integer.valueOf(properties.getProperty("jerpa.application.version.minor"));
	}

	/**
	 * 
	 * 
	 * @return revision version of application as Integer
	 */
	public static int getAppRevisionVersionAsInt() {
		return Integer.valueOf(properties.getProperty("jerpa.application.version.revision"));
	}
	
	public static String getAppCopyright() {
		return properties.getProperty("jerpa.application.copyright");
	}

	/**
	 * Get property - get property by name (key)
	 * 
	 * @param key
	 *          property name
	 * @return value of property
	 */
	public static String getConfigProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Get property - main project extension.
	 * 
	 * @return name of the main project extension.
	 */
	public static String getProjectExtension() {
		return properties.getProperty("jerpa.extension.project", "jerpa");
	}

	/**
	 * Get property - list of all supported formats of file extension
	 * 
	 * @return list of all supported formats file extension
	 */
	public static String[] getListOfSupportedFileFormats() {
		return properties.getProperty("jerpa.extension.files").trim().split(",");
	}

	/**
	 * Get property - package path where perspectives are stored.
	 * 
	 * @return package path of perspectives java classes
	 */
	public static String getPerspectivePackagePath() {
		return properties.getProperty("jerpa.perspective.path.package");
	}

	/**
	 * Get property - default perspective.
	 * 
	 * @return default perspective defined by user.
	 */
	public static String getDefaultPerspective() {
		return properties.getProperty("jerpa.perspective.default");
	}

	/**
	 * Get property - list of all perspectives
	 * 
	 * @return list of all perspectives from config. file
	 */
	public static String[] getListOfPerspective() {
		return properties.getProperty("jerpa.perspective.list").split(",");
	}
	
	/**
	 * Get property - list of all authors
	 * 
	 * @return list of all authors from config. file
	 * @version 0.1.0 (5/19/2010)
	 * @since 0.1.9 (5/19/2010)
	 */
	public static String[] getListOfAuthors() {
		return properties.getProperty("jerpa.authors").split(",");
	}
	
	/**
	 * Get property - list of contributions
	 * 
	 * @return list of all contributions from config. file
	 * @version 0.1.0 (5/19/2010)
	 * @since 0.1.9 (5/19/2010)
	 */
	public static String[] getListOfContributions() {
		return properties.getProperty("jerpa.contributions").split(",");
	}
	
	/**
	 * Return home page of <code>JERPA</code> project
	 * 
	 * @return home page of JERPA project
	 * @version 0.2.0 (2/14/2010)
	 * @since 0.1.2 (1/24/2010)
	 */
	public static String getJERPAHomePage() {
		return properties.getProperty("jerpa.homepage");
	}
	
	/**
	 * Return path of perspective plugins xml
	 * 
	 * @return path of perspective plugins xml 
	 * @version 0.1.1 (3/28/2010)
	 * @since 0.1.8 (3/28/2010)
	 */
	public static String getPluginXMLLocation() {
		return properties.getProperty("plugin.xml.path");
	}

	/** Only for test */
	public static void main(String[] args) {
		try {
			ConfigPropertiesLoader.loadProperties();
		} catch (PropertiesException e) {
			e.printStackTrace();
		}
		System.out.println(ConfigPropertiesLoader.getDefaultPerspective());
	}
	
}