package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;

/**
 * Row Model used in data table.
 *
 * @author Petr Miko
 */
public class DataRowModel {

    private boolean selected;
    private DataFileInfo fileInfo;
    private int downloaded;
    private String extension;
    private String location;

    public static final int NO_LOCAL_COPY = 0;
    public static final int HAS_LOCAL_COPY = 1;
    public static final int DOWNLOADING = 2;
    public static final int ERROR = 3;

    public DataRowModel(DataFileInfo fileInfo, int downloaded, String location){
        selected = false;
        this.fileInfo = fileInfo;
        this.downloaded = downloaded;
        String[] filename = fileInfo.getFilename().split("\\.");
        extension = filename[filename.length - 1];
        this.location = location;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public DataFileInfo getFileInfo() {
        return fileInfo;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public String getLocation(){
        return location;
    }
}
