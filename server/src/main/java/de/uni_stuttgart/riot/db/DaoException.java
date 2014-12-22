package de.uni_stuttgart.riot.db;

/**
 * The Class DaoException.
 */
public class DaoException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2732144845029713468L;

    /**
     * Instantiates a new dao exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new dao exception with a specific message.
     * 
     * @param message
     *            the message
     */
    public DaoException(String message) {
        super(message);
    }
}
