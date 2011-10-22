package ch.ethz.origo.jerpa.data.tier;

/**
 * Enum for description of file's download state.
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 *
 */
public enum FileState {

	/**
	 * File has not been downloaded yet.
	 */
	NO_COPY,

	/**
	 * File is successfully downloaded.
	 */
	HAS_COPY,

	/**
	 * File was being downloaded but is not complete (size)
	 */
	CORRUPTED,

	/**
	 * File is dowloading right now.
	 */
	DOWNLOADING
}
