package ch.ethz.origo.jerpa.data.tier.border;

public class DataFile {

	private int fileId;
	private double samplingRate;
	private String fileName;
	private long fileLength;
	private int experimentId;
	private String mimeType;
    private long revision;

	public DataFile() {}

	public int getExperimentId() {
		return experimentId;
	}

	public int getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public long getFileLength() {
		return fileLength;
	}

	public double getSamplingRate() {
		return samplingRate;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public void setSamplingRate(double samplingRate) {
		this.samplingRate = samplingRate;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }
}
