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
    public static final int ID_COLUMN = 0;
    public static final int NAME_COLUMN = 1;

    public ExpTableModel() {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
        initColumns();
        data = new LinkedList<ExperimentInfo>();
    }

    private void initColumns() {
        columnNames = new LinkedList<String>();
        columnNames.add("table.ededb.exptable.expid");
        columnNames.add("table.ededb.exptable.scenarioname");
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return resource.getString(columnNames.get(columnIndex));
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == ID_COLUMN) {
            return data.get(rowIndex).getExperimentId();
        } else {
            return data.get(rowIndex).getScenarioName();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addRow(ExperimentInfo experiment) {
        if(experiment!= null && experiment.getScenarioName() != null){
           data.add(experiment);
        }else{
            System.err.println("Row wasn't added - experiment or its name was null");
        }
        
        fireTableDataChanged();
    }

    public void clear() {
        data.clear();
        fireTableDataChanged();
    }

    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireTableStructureChanged();
            }
        });

    }
}
