package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ImportFilesTableModel;

import javax.swing.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Custom JTable for list of files to be imported.
 */
public class ImportFilesTable extends JTable {

    /**
     * Constructor.
     * Sets model with column names.
     */
    public ImportFilesTable() {
        super();

        this.setModel(new ImportFilesTableModel());
    }

    /**
     * Add row to table method.
     *
     * @param file file on file system
     */
    public void add(File file) {
        ImportFilesTableModel model = (ImportFilesTableModel) this.getModel();
        model.addRow(file);
    }

    /**
     * Method for removing specified row.
     *
     * @param index row index
     */
    public void remove(int index) {
        ImportFilesTableModel model = (ImportFilesTableModel) this.getModel();
        model.removeRow(index);
    }
}
