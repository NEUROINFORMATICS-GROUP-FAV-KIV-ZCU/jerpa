package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import java.io.File;

/**
 * Row attributes of ImportFilesTable
 */
public class ImportFilesRowModel {

    private File file;
    private double samplingRate;

    /**
     * Getter of a file to be imported.
     * @return java.io.File
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter of file to be imported.
     * @param file java.io.File
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter of DataFile's sampling rate.
     * @return sampling rate
     */
    public double getSamplingRate() {
        return samplingRate;
    }

    /**
     * Setter of DataFile's sampling rate.
     * @param samplingRate sampling rate
     */
    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }
}
