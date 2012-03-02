package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Petr Miko - miko.petr (at) gmail.com
 *         <p/>
 *         Class for properties operations.
 */
public class ConfigPropertiesLoader {

    private static final Logger log = Logger.getLogger(ConfigPropertiesLoader.class);


    private static Properties loadFile(String fileName) {
        Properties properties = new Properties();
        FileInputStream inPropStream = null;
        try {
            inPropStream = new FileInputStream("config/" + fileName);
            properties.load(inPropStream);
        } catch (IOException e) {
            log.error(e);
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - ConfigPropertiesLoader ERROR", e.getMessage(), e);
        } finally {
            try {
                if (inPropStream != null) {
                    inPropStream.close();
                }
            } catch (IOException e) {
                log.error(e);
            }
        }

        return properties;
    }

    /**
     * Loader of properties files.
     *
     * @param fileName file Name
     * @param key      identifier to value in properties file
     * @return proper value from properties file
     */
    public static String getProperty(String fileName, String key) {
        return loadFile(fileName).getProperty(key);
    }

    /**
     * Add/Set key in config properties file.
     *
     * @param fileName file name
     * @param key      String key
     * @param argument String value
     */
    public static void setProperty(String fileName, String key, String argument) {
        Properties properties = loadFile(fileName);
        OutputStream outPropStream = null;
        try {
            outPropStream = new FileOutputStream(fileName);
            properties.setProperty(key, argument);
            properties.store(outPropStream, null);
        } catch (IOException ex) {
            log.error(ex);
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - ConfigPropertiesLoader ERROR", ex.getMessage(), ex);
        } finally {
            try {
                if (outPropStream != null) {
                    outPropStream.close();
                }
            } catch (IOException e) {
                log.error(e);
            }
        }
    }
}
