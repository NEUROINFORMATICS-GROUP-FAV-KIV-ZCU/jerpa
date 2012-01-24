package ch.ethz.origo.jerpa.data.tier.dao;

/**
 * @author Petr Miko
 *         <p/>
 *         Exception for data layer purposes.
 */
public class DaoException extends Exception {

    public DaoException(Throwable throwable) {
        super(throwable);
    }

    public DaoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
