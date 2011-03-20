package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;

/**
 * @author Petr Miko
 */
public class DataRowModel {

    private boolean selected;
    private DataFileInfo fileInfo;
    private String downloaded;

    public DataRowModel(DataFileInfo fileInfo, String downloaded){
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

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }
}
