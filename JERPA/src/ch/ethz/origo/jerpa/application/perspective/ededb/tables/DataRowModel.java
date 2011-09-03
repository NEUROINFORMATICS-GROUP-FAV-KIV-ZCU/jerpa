package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileState;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;

/**
 * Row Model used in data table.
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class DataRowModel {

	private boolean selected;
	private final DataFileInfo fileInfo;
	private FileState state;
	private final String extension;
	private final String location;

	/**
	 * Constructor of row model for data table.
	 *
	 * @param fileInfo Obtained from web service
	 * @param state Enumeration of file's current download state (downloaded,
	 *            corrupted, etc.)
	 * @param location path to download folder of the exact experiment
	 */
	public DataRowModel(DataFileInfo fileInfo, FileState state, String location) {
		selected = false;
		this.fileInfo = fileInfo;
		this.state = state;
		String[] filename = fileInfo.getFilename().split("\\.");
		extension = filename[filename.length - 1];
		this.location = location;
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
	public DataFileInfo getFileInfo() {
		return fileInfo;
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
	 * @param downloaded DataRowModel.HAS_COPY/NO_COPY/DOWNLOADING/ERROR
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

	/**
	 * Getter of file's experiment download folder path.
	 *
	 * @return path
	 */
	public String getLocation() {
		return location;
	}
}
