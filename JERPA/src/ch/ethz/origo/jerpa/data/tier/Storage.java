package ch.ethz.origo.jerpa.data.tier;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.data.tier.border.Experiment;
import ch.ethz.origo.jerpa.data.tier.border.Person;
import ch.ethz.origo.jerpa.data.tier.border.ResearchGroup;
import ch.ethz.origo.jerpa.data.tier.border.Scenario;
import ch.ethz.origo.jerpa.data.tier.border.Weather;

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
	public void writeDataFile(DataFile fileInfo, InputStream inStream) throws StorageException;

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

	/**
	 * Method for setting all people into data layer.
	 * 
	 * @param people list of people
	 * @throws StorageException exception on the side of storage
	 */
	public void setPeople(List<Person> people) throws StorageException;

	/**
	 * Setter of all experiments into data layer.
	 * 
	 * @param experiments list of experiments
	 * @throws StorageException exception on the side of storage
	 */
	public void setExperiments(List<Experiment> experiments) throws StorageException;

	/**
	 * Setter of scenarios into data layer.
	 * 
	 * @param scenarios list of scenarios
	 * @throws StorageException exception on the side of storage
	 */
	public void setScenarios(List<Scenario> scenarios) throws StorageException;

	/**
	 * Setter of weathers into data layer.
	 * 
	 * @param weathers list of weathers
	 * @throws StorageException exception on the side of storage
	 */
	public void setWeathers(List<Weather> weathers) throws StorageException;

	/**
	 * Setter of research groups into data layer.
	 * 
	 * @param groups research group list
	 * @throws StorageException exception on the side of storage
	 */
	public void setResearchGroups(List<ResearchGroup> groups) throws StorageException;

	/**
	 * Setter of data files into data layer.
	 * 
	 * @param dataFiles data files list
	 * @throws StorageException exception on the side of storage
	 */
	public void setDataFiles(List<DataFile> dataFiles) throws StorageException;

    /**
     * Getter of revision number of last synchronization.
     *
     * @return revision number
     * @throws StorageException exception on the side of storage
     */
    public long getLastRevision(String table) throws StorageException;

}
