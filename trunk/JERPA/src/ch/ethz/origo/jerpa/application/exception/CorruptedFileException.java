package ch.ethz.origo.jerpa.application.exception;

/**
 * V�jimka signalizuj�c� po�kozenou logickou strukturu na��tan�ho souboru.
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (01/17/2010)
 * @since 0.1.0 
 * @see Exception
 */
public class CorruptedFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4827743439734189547L;

	/**
	 * Constructs a new CorruptedFileException exception with the specified cause
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
	public CorruptedFileException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructs a new CorruptedFileException exception with the specified detail
	 * message.
	 * 
	 * @param message
	 *          the detail message (which is saved for later retrieval by the
	 *          {@link #getMessage()} method).
	 */
	public CorruptedFileException(String message) {
		
	}

	/**
	 * Constructs a new CorruptedFileException exception with the specified detail
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
	public CorruptedFileException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
