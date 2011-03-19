package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;

/**
 * @author Petr Miko
 */
public class DataRowModel {

    private boolean toDownload;
    private DataFileInfo fileInfo;
    private boolean downloaded;

    public DataRowModel(DataFileInfo fileInfo, boolean downloaded){
        toDownload = false;
        this.fileInfo = fileInfo;
        this.downloaded = downloaded;
    }

    public boolean isToDownload() {
        return toDownload;
    }

    public void setToDownload(boolean toDownload) {
        this.toDownload = toDownload;
    }

    public DataFileInfo getFileInfo() {
        return fileInfo;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}
