package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Table model for list of users.
 *
 * @author Petr Miko
 */
public class UserTableModel extends DefaultTableModel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private LinkedList<String> columnNames;

    /**
     * Constructor method.
     */
    public UserTableModel() {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        initColumns();
    }

    /**
     * Adds column with name "users" in proper language.
     */
    private void initColumns() {
        columnNames = new LinkedList<String>();
        columnNames.add("tables.ededb.offline.users");
    }

    /**
     * Getter of editability cell state.
     * @param row row index
     * @param column column index
     * @return false (this table can not be edited)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
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
     * Getter of Column name
     * @param columnIndex column index
     * @return String of column name
     */
    @Override
    public String getColumnName(int columnIndex) {
        return resource.getString(columnNames.get(columnIndex));
    }

    /**
     * Method for adding row.
     * @param object
     */
    @Override
    public void addRow(Object[] object) {
        if (object != null && object.length > 0) {
            if (object[0] != null) {
                addRow((String) object[0]);
            }
        }
    }

    /**
     * Method for adding users name to table.
     * @param filename
     */
    public void addRow(String filename) {
        super.addRow(new Object[]{filename});
    }

    /**
     * Setter cell value method.
     * @param object input
     * @param rowIndex row index
     * @param columnIndex column index
     */
    @Override
    public void setValueAt(Object object, int rowIndex, int columnIndex) {
        if (object != null) {
            super.setValueAt(object, rowIndex, columnIndex);
        }
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

    /**
     * Clearing table method.
     */
    public void clear() {
        while (this.getRowCount() > 0) {
            this.removeRow(0);
        }
    }
}
