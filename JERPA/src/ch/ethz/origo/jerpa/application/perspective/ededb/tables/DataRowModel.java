package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;

/**
 * @author Petr Miko
 */
public class DataRowModel {

    private boolean selected;
    private DataFileInfo fileInfo;
    private int downloaded;

    public static final int NO_LOCAL_COPY = 0;
    public static final int HAS_LOCAL_COPY = 1;
    public static final int DOWNLOADING = 2;

    public DataRowModel(DataFileInfo fileInfo, int downloaded){
        selected = false;
        this.fileInfo = fileInfo;
        this.downloaded = downloaded;
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
}
