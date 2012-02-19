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
public class EDEDBProperties {

    private static final Logger log = Logger.getLogger(EDEDBProperties.class);
    private static final String fileName = "config/ededb.properties";
    private static Properties properties = new Properties() {
        {
            FileInputStream inPropStream = null;
            try {
                inPropStream = new FileInputStream(fileName);
                this.load(inPropStream);
                log.info("EDEDB properties loaded successfully.");
            } catch (IOException e) {
                log.error(e);
                JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR", e.getMessage(), e);
            } finally {

                try {
                    if (inPropStream != null) {
                        inPropStream.close();
                    }
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    };

    /**
     * Loader of properties files.
     *
     * @param key identifier to value in properties file
     * @return proper value from properties file
     */
    public static String getConfigKey(String key) {
        return properties.getProperty(key);
    }

    /**
     * Add/Set key in config properties file.
     *
     * @param key      String key
     * @param argument String value
     */
    public static void setConfigKey(String key, String argument) {

        OutputStream outPropStream = null;
        try {
            outPropStream = new FileOutputStream(fileName);
            properties.setProperty(key, argument);
            properties.store(outPropStream, null);
        } catch (IOException ex) {
            log.error(ex);
            JUIGLErrorInfoUtils.showErrorDialog("JERPA - EDEDB ERROR", ex.getMessage(), ex);
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
