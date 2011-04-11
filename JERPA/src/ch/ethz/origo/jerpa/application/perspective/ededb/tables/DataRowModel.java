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

    /**
     * Constructor of row model for data table.
     * @param fileInfo Obtained from web service
     * @param downloaded Boolean, whether is file already localy present
     * @param location path to download folder of the exact experiment
     */
    public DataRowModel(DataFileInfo fileInfo, int downloaded, String location) {
        selected = false;
        this.fileInfo = fileInfo;
        this.downloaded = downloaded;
        String[] filename = fileInfo.getFilename().split("\\.");
        extension = filename[filename.length - 1];
        this.location = location;
    }

    /**
     * Getter whether is row selected.
     * @return true/false
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter of row's selection.
     * @param selected true/false
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Getter of furthure file information
     * @return
     */
    public DataFileInfo getFileInfo() {
        return fileInfo;
    }

    /**
     * Getter of file's local copy status.
     * @return DataRowModel.HAS_COPY/NO_COPY/DOWNLOADING/ERROR
     */
    public int getDownloaded() {
        return downloaded;
    }

    /**
     * Setter file's local copy status.
     * @param downloaded DataRowModel.HAS_COPY/NO_COPY/DOWNLOADING/ERROR
     */
    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * Getter of file's extension.
     * @return vhdr,eeg,...
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Getter of file's experiment download folder path.
     * @return path
     */
    public String getLocation() {
        return location;
    }
}
