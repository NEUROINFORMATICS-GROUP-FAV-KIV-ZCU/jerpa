package ch.ethz.origo.jerpa.application.exception;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 11/18/09
 * @since 0.1.0 (05/18/09)
 * @see Exception
 * 
 */
public class InvalidFrameIndexException extends Exception {

	/**
	 * Only for serialization
	 */
	private static final long serialVersionUID = -5670645373996544061L;

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
	public InvalidFrameIndexException(Throwable cause) {
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
	public InvalidFrameIndexException(String message) {
		
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
	public InvalidFrameIndexException(String message, Throwable cause) {
		super(message, cause);
	}

}