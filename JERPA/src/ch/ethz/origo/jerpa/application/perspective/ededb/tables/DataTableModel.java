package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
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
        columnNames.add("table.ededb.datatable.download");
        columnNames.add("table.ededb.datatable.filename");
        columnNames.add("table.ededb.datatable.mime");
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
                return data.get(rowIndex).isToDownload();
            case 1:
                return data.get(rowIndex).getFileInfo().getFilename();
            case 2:
                return data.get(rowIndex).getFileInfo().getMimeType();
            case 3:
                if (data.get(rowIndex).isDownloaded()) {
                    return resource.getString("table.ededb.datatable.state.yes");
                } else {
                    return resource.getString("table.ededb.datatable.state.no");
                }
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object object, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            data.get(rowIndex).setToDownload((Boolean) object);
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0);
    }

    public void addRow(DataFileInfo fileInfo, boolean downloaded) {
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
}
