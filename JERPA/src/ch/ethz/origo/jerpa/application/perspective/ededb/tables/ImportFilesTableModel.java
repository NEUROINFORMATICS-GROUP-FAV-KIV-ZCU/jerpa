package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Table for viewing files in Import Wizard.
 *
 * @author Petr Miko
 */
public class ImportFilesTableModel extends AbstractTableModel {

    private final static Logger log = Logger.getLogger(ImportFilesTableModel.class);

    private List<File> data = new LinkedList<File>();
    private List<String> columnNames;

    public ImportFilesTableModel(){
        super();

        columnNames = new ArrayList<String>();
        columnNames.add("File name");
        columnNames.add("URL");
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
			if (columnIndex == 0) {
				return data.get(rowIndex).getName();
			}
			else {
				return data.get(rowIndex).getAbsolutePath();
			}
		}
		return null;
    }

    /**
	 * Getter of cell edit-state.
	 *
	 * @param rowIndex row index
	 * @param columnIndex column index
	 * @return false (this table cannot be edited)
	 */

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

    /**
	 * Add row to table method.
	 *
	 * @param file file on file system
	 */
	public void addRow(File file) {
		if (file != null && !data.contains(file)) {
			data.add(file);
		}
		else {
			log.error("Row wasn't added - file was null or is already added");
		}

		fireTableDataChanged();
	}

    /**
     * Method for removing specified file from model.
     *
     * @param index file index
     */
    public void removeRow(int index) {

        if(index >=0 && index < data.size()){
            data.remove(index);
        }
    }
}
