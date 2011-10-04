package ch.ethz.origo.jerpa.data.tier;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.data.tier.border.Experiment;

/**
 * Interface for EDEDB data tier.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 * 
 */

public interface Storage {

	/**
	 * Store file method.
	 * 
	 * @param fileInfo file meta information
	 * @param inStream input stream to file
	 * @throws StorageException exception on the side of storage
	 */
	public void writeFile(DataFile fileInfo, InputStream inStream) throws StorageException;

	/**
	 * Store file method (overwrite).
	 * 
	 * @param fileInfo file meta information
	 * @param inStream input stream to file
	 * @throws StorageException exception on the side of storage
	 */
	public void overwriteFile(DataFile fileInfo, InputStream inStream) throws StorageException;

	/**
	 * Getter of file's size.
	 * 
	 * @param fileId file identifier
	 * @return file's size
	 */
	public long getFileLength(int fileId) throws StorageException;

	/**
	 * Read from store method.
	 * 
	 * @param fileId file identifier
	 * @return file
	 * @throws StorageException exception on the side of storage
	 */
	public File getFile(int fileId) throws StorageException;

	/**
	 * Method for figuring out the file's state.
	 * 
	 * @param fileInfo file information
	 * @return FileState
	 * @throws StorageException exception on the side of storage
	 */
	public FileState getFileState(DataFile fileInfo) throws StorageException;

	/**
	 * Method for removing data file.
	 * 
	 * @param fileId file identifier
	 * @throws StorageException exception on the side of storage
	 */
	public void removeFile(int fileId) throws StorageException;

	/**
	 * Method for obtaining available data files in storage.
	 * 
	 * @param experimentsId id of selected experiments
	 * @return list of data files
	 * @throws StorageException exception on the side of storage
	 */
	public List<DataFile> getDataFiles(List<Integer> experimentsId) throws StorageException;

	/**
	 * Method for obtaining available experiments in storage.
	 * 
	 * @return experiments' information
	 * @throws StorageException exception on the side of storage
	 */
	public List<Experiment> getExperiments() throws StorageException;

}
