package ch.ethz.origo.jerpa.data.tier;

/**
 * Exception for purposes of exceptions during downloading of a file.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 * 
 */
public class DownloadException extends Exception {

	private static final long serialVersionUID = -3238626189684096781L;

	public DownloadException(Throwable throwable) {
		super(throwable);
	}

	@Override
	public String getMessage() {
		return "Error occurred during downloading! " + super.getMessage();
	}

}
