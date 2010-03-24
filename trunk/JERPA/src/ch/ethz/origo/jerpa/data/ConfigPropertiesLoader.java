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
 * @version 0.1.7 (3/24/2010)
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
				throw new PropertiesException(ConfigPropertiesLoader.class.getName()
						+ " - cannot read config properties", e);
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
	 * Return home page of <code>JERPA</code> project
	 * 
	 * @return home page of JERPA project
	 * @version 0.2.0 (2/14/2010)
	 * @since 0.1.2 (1/24/2010)
	 */
	public static String getJERPAHomePage() {
		return properties.getProperty("jerpa.homepage");
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