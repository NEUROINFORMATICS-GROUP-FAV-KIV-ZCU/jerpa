package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Class for creating undecorated dialog showing "working animation"
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public final class Working extends JPanel implements ILanguage {

    private static final long serialVersionUID = 4807698208851104534L;
    private static Working instance = new Working();

    private static volatile Map<String, Integer> operations;
    private static volatile Map<Integer, Integer> downloads;

    private static JTextArea operationsField;
    private static JProgressBar progress;

    private static String resourceBundlePath;
    private static ResourceBundle resource;

    private static DataFileDao dataFileDao = DaoFactory.getDataFileDao();

    /**
     * Method creating JDialog
     */
    private Working() {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        progress = new JProgressBar();
        setLayout(new BorderLayout());

        operations = new LinkedHashMap<String, Integer>();
        downloads = new LinkedHashMap<Integer, Integer>();

        operationsField = new JTextArea();
        operationsField.setEditable(false);
        operationsField.setBackground(getBackground());
        operationsField.setForeground(getForeground());

        JScrollPane operationsPane = new JScrollPane(operationsField);
        operationsPane.setPreferredSize(new Dimension(30, 50));
        operationsPane.setFocusable(false);

        updateOperations();

        this.add(operationsPane, BorderLayout.CENTER);
        this.add(progress, BorderLayout.SOUTH);
    }

    /**
     * Method setting visibility according to input integer
     *
     * @param add       operation of adding or removing Working action
     * @param operation ongoing operation
     */
    public static void setActivity(boolean add, String operation) {

        synchronized (Working.class) {
            if (add) {
                operations.put(operation, (operations.get(operation) == null ? 1 : operations.get(operation) + 1));
                if (operations.size() == 1) {
                    progress.setIndeterminate(true);

                    if (instance.getRootPane() != null) {
                        instance.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    } else {
                        instance.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                }
            } else {
                if (operations.get(operation) != null && operations.get(operation) == 1) {
                    operations.remove(operation);
                    if (operations.isEmpty()) {
                        progress.setIndeterminate(false);

                        if (instance.getRootPane() != null) {
                            instance.getRootPane().setCursor(Cursor.getDefaultCursor());
                        } else {
                            instance.setCursor(Cursor.getDefaultCursor());
                        }
                    }
                } else {
                    operations.put(operation, (operations.get(operation) == null || operations.get(operation) <= 0 ? 1 : operations.get(operation) - 1));
                }
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
     *
     * @param percent How many percents is downloaded
     * @param file    Specific file
     */
    public static void setDownload(int percent, DataFile file) {

        synchronized (Working.class) {
            if (percent == 100) {
                downloads.remove(file.getDataFileId());
            } else {
                downloads.put(file.getDataFileId(), percent);
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
     * Prints out ongoing operations into JTextArea
     */
    private static void updateOperations() {

        StringBuilder builder = new StringBuilder();
        synchronized (Working.class) {
            operationsField.removeAll();

            if (!operations.isEmpty()) {
                for (String operation : operations.keySet()) {
                    if (builder.length() != 0) {
                        builder.append("\n");
                    }
                    builder.append("[").append(operations.get(operation)).append("] ").append(resource.getString(operation));
                }
            } else {
                builder.append(resource.getString("working.ededb.no"));
            }

            if (!downloads.isEmpty()) {
                DataFile file;
                for (Integer fileIds : downloads.keySet()) {
                    file = dataFileDao.get(fileIds);
                    if (builder.length() != 0) {
                        builder.append("\n");
                    }
                    builder.append(file.getFilename()).append(" (ID ").append(file.getDataFileId()).append("):").append(downloads.get(fileIds)).append("%");
                }
            }

            progress.setToolTipText(builder.toString());
            operationsField.setText(builder.toString());
        }

    }

    public static JPanel getWorkingPanel() {
        return instance;
    }

    /**
     * Setter of localization resource bundle path
     *
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     *
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource bundle key.
     *
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     *
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                updateOperations();
            }
        });

    }
}
