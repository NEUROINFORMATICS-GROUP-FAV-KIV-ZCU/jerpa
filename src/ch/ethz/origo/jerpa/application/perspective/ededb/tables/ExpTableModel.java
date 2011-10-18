package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.tier.border.Experiment;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Table model for list of experiments.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class ExpTableModel extends AbstractTableModel implements ILanguage {

	private static final long serialVersionUID = 1546774060278698090L;
	private ResourceBundle resource;
	private String resourceBundlePath;
	private final List<Experiment> data;
	private List<String> columnNames;
	private static final Logger log = Logger.getLogger(ExpTableModel.class);

	/**
	 * Index of Experiment id column.
	 */
	public static final int ID_COLUMN = 0;
	/**
	 * Index of Scenario name column.
	 */
	public static final int NAME_COLUMN = 1;

	/**
	 * Constructor of experiment table model.
	 */
	public ExpTableModel() {
		super();

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
		initColumns();
		data = new LinkedList<Experiment>();
	}

	/**
	 * Adds columns with names.
	 */
	private void initColumns() {
		columnNames = new LinkedList<String>();
		columnNames.add("table.ededb.exptable.expid");
		columnNames.add("table.ededb.exptable.scenarioname");
	}

	/**
	 * Getter of row count.
	 * 
	 * @return row count
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/**
	 * Getter of column count.
	 * 
	 * @return column count
	 */
	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	/**
	 * Getter of column name
	 * 
	 * @param columnIndex
	 * @return String of column name
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return resource.getString(columnNames.get(columnIndex));
	}

	/**
	 * Getter of table cell value according to row/column indices.
	 * 
	 * @param rowIndex row index
	 * @param columnIndex column index
	 * @return Object (Integer or String}
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= 0) {
			if (columnIndex == ID_COLUMN) {
				return data.get(rowIndex).getExperimentId();
			}
			else {
				return data.get(rowIndex).getTitle();

			}
		}
		return null;
	}

	/**
	 * Getter of cell modifiability state.
	 * 
	 * @param rowIndex row index
	 * @param columnIndex column index
	 * @return false (this table cannot be edited)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/**
	 * Add row to table method.
	 * 
	 * @param experiment ExperimentInfo
	 */
	public void addRow(Experiment experiment) {
		if (experiment != null && experiment.getTitle() != null) {
			data.add(experiment);
		}
		else {
			log.error("Row wasn't added - experiment or its name was null");
		}

		fireTableDataChanged();
	}

	/**
	 * Method clearing table.
	 */
	public void clear() {
		data.clear();
		fireTableDataChanged();
	}

	/**
	 * Setter of localization resource bundle path
	 * 
	 * @param path path to localization source file.
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Getter of path to resource bundle.
	 * 
	 * @return path to localization file.
	 */
	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * Setter of resource bundle key.
	 * 
	 * @param string key
	 */
	@Override
	public void setResourceBundleKey(String string) {
		throw new UnsupportedOperationException("Method is not implemented yet...");
	}

	/**
	 * Method invoked by change of LanguageObservable.
	 * 
	 * @throws JUIGLELangException
	 */
	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				fireTableStructureChanged();
			}
		});

	}
}
