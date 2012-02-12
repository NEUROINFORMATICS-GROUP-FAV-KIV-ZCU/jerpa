package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Table for viewing files in Import Wizard.
 *
 * @author Petr Miko
 */
public class ImportFilesTableModel extends AbstractTableModel implements ILanguage {

    private final static Logger log = Logger.getLogger(ImportFilesTableModel.class);

    private List<ImportFilesRowModel> data = new LinkedList<ImportFilesRowModel>();
    private List<String> columnNames;
    private static String resourceBundlePath;
    private static ResourceBundle resource;

    private final int COLUMN_NAME = 0;
    private final int COLUMN_URL = 1;
    private final int COLUMN_SAMPLING_RATE = 2;

    /**
     * Constructor.
     */
    public ImportFilesTableModel() {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        columnNames = new ArrayList<String>();
        columnNames.add("File name");
        columnNames.add("URL");
        columnNames.add("Sampling rate");
        setColumnNames();
    }

    private void setColumnNames() {
        columnNames.set(COLUMN_NAME, resource.getString("importTable.ededb.fileName"));
        columnNames.set(COLUMN_URL, resource.getString("importTable.ededb.url"));
        columnNames.set(COLUMN_SAMPLING_RATE, resource.getString("importTable.ededb.samplingRate"));

        this.fireTableStructureChanged();
    }


    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * Getter of column name
     *
     * @param columnIndex column index
     * @return String of column name
     */

    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && data.size() > rowIndex) {
            switch (columnIndex) {
                case COLUMN_NAME:
                    return data.get(rowIndex).getFile().getName();
                case COLUMN_URL:
                    return data.get(rowIndex).getFile().getAbsolutePath();
                case COLUMN_SAMPLING_RATE:
                    return data.get(rowIndex).getSamplingRate();
                default:
                    break;
            }
        }
        return null;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COLUMN_SAMPLING_RATE) {
            ImportFilesRowModel row = data.get(rowIndex);

            try {
                String value = (String) aValue;
                if (!value.isEmpty()) {
                    double samplingRate = Double.parseDouble(value);
                    row.setSamplingRate(samplingRate);
                }
            } catch (NumberFormatException e) {
                log.error(e);
            }
            fireTableDataChanged();
        }
    }

    /**
     * Getter of cell edit-state.
     *
     * @param rowIndex    row index
     * @param columnIndex column index
     * @return false (this table cannot be edited)
     */

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COLUMN_SAMPLING_RATE;
    }

    /**
     * Add row to table method.
     *
     * @param file file on file system
     */
    public void addRow(File file) {
        ImportFilesRowModel row = new ImportFilesRowModel();
        row.setFile(file);
        if (file != null && !isFileAdded(file)) {
            data.add(row);
        } else {
            log.error("Row wasn't added - file was null or is already added");
        }

        fireTableDataChanged();
    }

    private boolean isFileAdded(File file) {
        for(ImportFilesRowModel row : data){
           if(row.getFile().getAbsolutePath().equals(file.getAbsolutePath())){
               return true;
           }
        }

        return false;
    }

    /**
     * Method for removing specified file from model.
     *
     * @param index file index
     */
    public void removeRow(int index) {

        if (index >= 0 && index < data.size()) {
            data.remove(index);
        }
    }

    /**
     * Getter of files to be imported.
     *
     * @return list of files
     */
    public List<ImportFilesRowModel> getFiles() {
        return data;
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
     * @throws ch.ethz.origo.juigle.application.exception.JUIGLELangException
     *
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                setColumnNames();
            }
        });

    }
}
