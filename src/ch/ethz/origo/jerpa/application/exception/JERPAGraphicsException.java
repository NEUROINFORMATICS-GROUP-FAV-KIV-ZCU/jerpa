package ch.ethz.origo.jerpa.application.exception;

/**
 * Exception caused by <code>JERPA</code> graphics error. Not
 * <code>JUIGLE</code> error.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (4/17/2010)
 * @since 1.0 (4/17/2010)
 * @see Exception
 */
public class JERPAGraphicsException extends Exception {

	/** Only for serializatation */
	private static final long serialVersionUID = 2124911892583499525L;

	/**
	 * Constructs a new JERPA Graphics exception with the specified cause and a
	 * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for exceptions that are little more than wrappers for
	 * other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 * 
	 * @param cause
	 *          the cause (which is saved for later retrieval by the
	 *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
	 *          and indicates that the cause is nonexistent or unknown.)
	 */
	public JERPAGraphicsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause is
	 * not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 * 
	 * @param message
	 *          the detail message. The detail message is saved for later
	 *          retrieval by the {@link #getMessage()} method.
	 */
	public JERPAGraphicsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new JERPA Graphics exception with the specified detail message
	 * and cause.
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
	public JERPAGraphicsException(String message, Throwable cause) {
		super(message, cause);
	}

}
