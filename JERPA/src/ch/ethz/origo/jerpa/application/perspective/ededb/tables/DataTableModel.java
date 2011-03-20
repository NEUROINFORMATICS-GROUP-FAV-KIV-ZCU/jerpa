package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Petr Miko
 */
public class DataTableModel extends AbstractTableModel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private LinkedList<DataRowModel> data;
    private LinkedList<String> columnNames;
    
    private final int UNIT = 1024;

    public DataTableModel() {
        super();
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
        initColumns();
        data = new LinkedList<DataRowModel>();
    }

    @Override
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    private void initColumns() {
        columnNames = new LinkedList<String>();
        columnNames.add("table.ededb.datatable.action");
        columnNames.add("table.ededb.datatable.filename");
        columnNames.add("table.ededb.datatable.mime");
        columnNames.add("table.ededb.datatable.size");
        columnNames.add("table.ededb.datatable.localcopy");
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
        switch (columnIndex) {
            case 0:
                return data.get(rowIndex).isSelected();
            case 1:
                return data.get(rowIndex).getFileInfo().getFilename();
            case 2:
                return data.get(rowIndex).getFileInfo().getMimeType();
            case 3: 
                return countFileSize(data.get(rowIndex).getFileInfo().getLength());
            case 4:
                return data.get(rowIndex).getDownloaded();
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object object, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            data.get(rowIndex).setSelected((Boolean) object);
        }
        else if(columnIndex == getColumnCount() - 1)
            data.get(rowIndex).setDownloaded((String) object);

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0);
    }

    public void addRow(DataFileInfo fileInfo, String downloaded) {
        data.add(new DataRowModel(fileInfo, downloaded));
    }

    public List<DataRowModel> getData() {
        return data;
    }

    public void clear() {
        data.clear();
        this.fireTableDataChanged();
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        //not implemented
    }

    public void updateText() throws JUIGLELangException {
        //not implemented
                
    }

    private String countFileSize(long length) {
        
    if (length < UNIT)
        return length + " B";
    
    int exp = (int) (Math.log(length) / Math.log(UNIT));
    String pre = "KMGTPE".charAt(exp-1) + "i";
    
    return String.format("%.1f %sB", length / Math.pow(UNIT, exp), pre);
        
    }
}
