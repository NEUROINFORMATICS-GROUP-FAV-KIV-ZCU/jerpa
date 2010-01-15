package ch.ethz.origo.jerpa.application.exception;

/**
 * Vyj�mka popisuj�c� v�skyt neposta�uj�c�ch dat.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0
 * @see Exception
 * 
 */
public class InsufficientDataException extends Exception {
	
	/** Only for serialization */
	private static final long serialVersionUID = 7306275700995574014L;

	/**
	 * Constructs a new InvalidFrameException exception with the specified cause
	 * and a detail message of <tt>(cause==null ? null : cause.toString())</tt>
	 * (which typically contains the class and detail message of <tt>cause</tt>).
	 * This constructor is useful for exceptions that are little more than
	 * wrappers for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 * 
	 * @param cause
	 *          the cause (which is saved for later retrieval by the
	 *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
	 *          and indicates that the cause is nonexistent or unknown.)
	 */
	public InsufficientDataException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructs a new InvalidFrameException exception with the specified detail
	 * message.
	 * 
	 * @param message
	 *          the detail message (which is saved for later retrieval by the
	 *          {@link #getMessage()} method).
	 */
	public InsufficientDataException(String message) {
		
	}

	/**
	 * Constructs a new InvalidFrameException exception with the specified detail
	 * message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this exception's detail message.
	 * 
	 * @param message
	 *          the detail message (which is saved for later retrieval by the
	 *          {@link #getMessage()} method).
	 * @param cause
	 *          the cause (which is saved for later retrieval by the
	 *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
	 *          and indicates that the cause is nonexistent or unknown.)
	 */
	public InsufficientDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
