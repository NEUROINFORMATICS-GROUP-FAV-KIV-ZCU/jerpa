package ch.ethz.origo.jerpa.data.tier.pojo;

import java.sql.Blob;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class DataFile {
    private int dataFileId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getDataFileId() {
        return dataFileId;
    }

    public void setDataFileId(int dataFileId) {
        this.dataFileId = dataFileId;
    }

    private double samplingRate;

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    private Blob fileContent;

    public Blob getFileContent() {
        return fileContent;
    }

    public void setFileContent(Blob fileContent) {
        this.fileContent = fileContent;
    }

    private String mimetype;

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private long fileLength;

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataFile dataFile = (DataFile) o;

        if (dataFileId != dataFile.dataFileId) return false;
        if (fileLength != dataFile.fileLength) return false;
        if (Double.compare(dataFile.samplingRate, samplingRate) != 0) return false;
        if (version != dataFile.version) return false;
        if (fileContent != null ? !fileContent.equals(dataFile.fileContent) : dataFile.fileContent != null)
            return false;
        if (filename != null ? !filename.equals(dataFile.filename) : dataFile.filename != null) return false;
        if (mimetype != null ? !mimetype.equals(dataFile.mimetype) : dataFile.mimetype != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = dataFileId;
        temp = samplingRate != +0.0d ? Double.doubleToLongBits(samplingRate) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (fileContent != null ? fileContent.hashCode() : 0);
        result = 31 * result + (mimetype != null ? mimetype.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (int) (fileLength ^ (fileLength >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Experiment experiment;

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    private Collection<History> histories;

    public Collection<History> getHistories() {
        return histories;
    }

    public void setHistories(Collection<History> histories) {
        this.histories = histories;
    }
}
