package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 * Table model for data view table.
 *
 * @author Petr Miko
 */
public class DataTableModel extends AbstractTableModel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private LinkedList<DataRowModel> data;
    private LinkedList<String> columnNames;
    private final int UNIT = 1024;
    public static final int ACTION_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int MIME_COLUMN = 2;
    public static final int SIZE_COLUMN = 3;
    public static final int DOWNLOADED_COLUMN = 4;

    public DataTableModel() {
        super();
        LanguageObservable.getInstance().attach(this);
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
                if (data.get(rowIndex).getDownloaded() == DataRowModel.NO_LOCAL_COPY) {
                    return resource.getString("table.ededb.datatable.state.no");
                } else if (data.get(rowIndex).getDownloaded() == DataRowModel.HAS_LOCAL_COPY) {
                    return resource.getString("table.ededb.datatable.state.yes");
                } else if (data.get(rowIndex).getDownloaded() == DataRowModel.DOWNLOADING) {
                    return resource.getString("table.ededb.datatable.state.downloading");
                } else {
                    return resource.getString("table.ededb.datatable.state.error");
                }
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object object, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            try{
                boolean tmp = (Boolean) object;
                data.get(rowIndex).setSelected(tmp);
            }catch(Exception e){
                System.err.println("DataTableModel set value: " + e);
            }
        } else if (columnIndex == getColumnCount() - 1) {
            try{
                data.get(rowIndex).setDownloaded((Integer) object);
            }catch(Exception e){
                System.err.println("DataTableModel set value: " + e);
            }
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0);
    }

    public void addRow(DataFileInfo fileInfo, int downloaded, String location) {
        if (fileInfo != null && location != null && (downloaded == DataRowModel.NO_LOCAL_COPY
                || downloaded == DataRowModel.HAS_LOCAL_COPY
                || downloaded == DataRowModel.DOWNLOADING
                || downloaded == DataRowModel.ERROR)) {
            data.add(new DataRowModel(fileInfo, downloaded, location));
        }else{
            System.err.println("Row was not added - invalid data format!");
        }
        this.fireTableDataChanged();
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

    private String countFileSize(long length) {

        if (length < UNIT) {
            return length + " B";
        }
        int exp = (int) (Math.log(length) / Math.log(UNIT));
        String pre = "KMGTPE".charAt(exp - 1) + "i";

        return String.format("%.1f %sB", length / Math.pow(UNIT, exp), pre);

    }
}
