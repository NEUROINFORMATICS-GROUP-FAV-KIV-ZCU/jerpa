package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXPanel;

/**
 * Class for creating undecorated dialog showing "working animation"
 *
 * @author Petr Miko
 */
public class Working extends JXPanel implements ILanguage {

    private static Working instance;
    private static HashMap<String, Integer> operations;
    private static JTextArea operationsField;
    private static String resourceBundlePath;
    private static ResourceBundle resource;
    private static JProgressBar progress;
    public static Cursor busyCursor;
    public static Cursor defaultCursor;
    private static JScrollPane operationsPane;
    private static HashMap<DataFileInfo, Integer> downloads;

    /**
     *  Method creating JDialog
     */
    public Working() {
        super();
        instance = this;

        busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        defaultCursor = Cursor.getDefaultCursor();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        progress = new JProgressBar();
        this.setLayout(new BorderLayout());

        operations = new HashMap<String, Integer>();
        downloads = new HashMap<DataFileInfo, Integer>();

        operationsField = new JTextArea();
        operationsField.setEditable(false);
        operationsField.setBackground(this.getBackground());
        operationsField.setForeground(this.getForeground());

        operationsPane = new JScrollPane(operationsField);
        operationsPane.setPreferredSize(new Dimension(30, 50));
        operationsPane.setFocusable(false);

        updateOperations();

        this.add(operationsPane, BorderLayout.CENTER);
        this.add(progress, BorderLayout.SOUTH);
    }

    /**
     * Method setting visibility according to input integer
     */
    public static synchronized void setActivity(boolean add, String operation) {

        if (instance == null) {
            new Working();
        }

        final boolean tmp = add;

        if (add) {
            operations.put(operation, (operations.get(operation) == null ? 1 : operations.get(operation) + 1));
            if (operations.size() == 1) {
                progress.setIndeterminate(true);

                if (instance.getRootPane() != null) {
                    instance.getRootPane().setCursor(busyCursor);
                } else {
                    instance.setCursor(busyCursor);
                }
            }
        } else {
            if (operations.get(operation) != null && operations.get(operation) == 1) {
                operations.remove(operation);
                if (operations.isEmpty()) {
                    progress.setIndeterminate(false);

                    if (instance.getRootPane() != null) {
                        instance.getRootPane().setCursor(defaultCursor);
                    } else {
                        instance.setCursor(defaultCursor);
                    }
                }
            } else {
                operations.put(operation, (operations.get(operation) == null || operations.get(operation) <= 0
                        ? 1 : operations.get(operation) - 1));
            }
        }

        updateOperations();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                instance.revalidate();
                instance.repaint();
            }
        });
    }

    /**
     * Setting currently downloading file.
     * @param percent How many percents is downloaded
     * @param file Specific file
     */
    public static void setDownload(int percent, DataFileInfo file) {
        if (instance == null) {
            new Working();
        }

        if (percent == 100) {
            downloads.remove(file);
        } else {
            downloads.put(file, percent);
        }

        updateOperations();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                instance.revalidate();
                instance.repaint();
            }
        });
    }

    /**
     * Prints out ongoing operations into JTextArea
     */
    private static void updateOperations() {

        String temp = "";
        operationsField.removeAll();

        if (!operations.isEmpty()) {
            for (String operation : operations.keySet()) {
                if (temp.equals("")) {
                    temp = "[" + operations.get(operation) + "] " + resource.getString(operation);
                } else {
                    temp += "\n[" + operations.get(operation) + "] " + resource.getString(operation);
                }
            }
        } else {
            temp = resource.getString("working.ededb.no");
        }

        if (!downloads.isEmpty()) {
            for (DataFileInfo file : downloads.keySet()) {
                if (temp.equals("")) {
                    temp = file.getFilename() + " (ID " + file.getFileId() + "):" + downloads.get(file) + "%";
                } else {
                    temp += "\n" + file.getFilename() + " (ID " + file.getFileId() + "):" + downloads.get(file) + "%";
                }
            }
        }

        progress.setToolTipText(temp);

        operationsField.setText(temp);
    }

    /**
     * Setter of localization resource budle path
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                updateOperations();
            }
        });

    }
}
