package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;

/**
 * Row Model used in data table.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class DataRowModel {

	private boolean selected;
	private final DataFile dataFile;
	private FileState state;
	private final String extension;

	/**
	 * Constructor of row model for data table.
	 * 
	 * @param dataFile Obtained from web service
	 * @param state Enumeration of file's current download state (downloaded,
	 *            corrupted, etc.)
	 */
	public DataRowModel(DataFile dataFile, FileState state) {
		selected = false;
		this.dataFile = dataFile;
		this.state = state;
		String[] filename = dataFile.getFilename().split("\\.");
		extension = filename[filename.length - 1];
	}

	/**
	 * Getter whether is row selected.
	 * 
	 * @return true/false
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Setter of row's selection.
	 * 
	 * @param selected true/false
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Getter of further file information
	 * 
	 * @return
	 */
	public DataFile getDataFile() {
		return dataFile;
	}

	/**
	 * Getter of file's local copy status.
	 * 
	 * @return DataRowModel.HAS_COPY/NO_COPY/DOWNLOADING/ERROR
	 */
	public FileState getState() {
		return state;
	}

	/**
	 * Setter file's local copy status.
	 * 
	 * @param state DataRowModel.HAS_COPY/NO_COPY/DOWNLOADING/ERROR
	 */
	public void setState(FileState state) {
		this.state = state;
	}

	/**
	 * Getter of file's extension.
	 * 
	 * @return vhdr,eeg,...
	 */
	public String getExtension() {
		return extension;
	}
}
