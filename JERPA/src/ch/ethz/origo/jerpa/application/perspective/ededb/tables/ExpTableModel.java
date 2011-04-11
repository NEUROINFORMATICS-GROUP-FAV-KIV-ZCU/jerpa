package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 * Table model for list of experiments.
 *
 * @author Petr Miko
 */
public class ExpTableModel extends AbstractTableModel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private LinkedList<ExperimentInfo> data;
    private LinkedList<String> columnNames;
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
        data = new LinkedList<ExperimentInfo>();
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
     * @return row count
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * Getter of column count.
     * @return column count
     */
    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * Getter of column name
     * @param columnIndex
     * @return String of column name
     */
    @Override
    public String getColumnName(int columnIndex) {
        return resource.getString(columnNames.get(columnIndex));
    }

    /**
     * Getter of table cell value according to row/column indices.
     * @param rowIndex row index
     * @param columnIndex column index
     * @return Object (Integer or String}
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0) {
            if (columnIndex == ID_COLUMN) {
                return data.get(rowIndex).getExperimentId();
            } else {
                return data.get(rowIndex).getScenarioName();

            }
        }
        return null;
    }

    /**
     * Getter of cell editability state.
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
     * @param experiment ExperimentInfo
     */
    public void addRow(ExperimentInfo experiment) {
        if (experiment != null && experiment.getScenarioName() != null) {
            data.add(experiment);
        } else {
            System.err.println("Row wasn't added - experiment or its name was null");
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
                fireTableStructureChanged();
            }
        });

    }
}
