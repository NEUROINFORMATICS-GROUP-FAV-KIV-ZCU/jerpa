package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Petr Miko
 */
public class UserTableModel extends DefaultTableModel implements ILanguage {
    
    private ResourceBundle resource;
    private String resourceBundlePath;
    
    private LinkedList<String> columnNames;
    
    public UserTableModel(){
        super();
        
        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
        
        initColumns();
    }
    
    private void initColumns() {
        columnNames = new LinkedList<String>();
        columnNames.add("tables.ededb.offline.users");
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
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
