package ch.ethz.origo.jerpa.data.tier;

/**
 * 
 * @author Petr Miko - miko.petr [at] gmail.com
 * 
 */

public class StorageException extends Exception {

	private static final long serialVersionUID = -2108769821410352487L;

    public StorageException(String description) {
		super(description);
	}

	public StorageException(Throwable throwable) {
		super(throwable);
	}

	@Override
	public String getMessage() {
		return "Data storage error occured! " + super.getMessage();
	}

}
