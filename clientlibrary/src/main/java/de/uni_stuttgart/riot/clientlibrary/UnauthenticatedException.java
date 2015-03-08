package de.uni_stuttgart.riot.clientlibrary;

/**
 * Thrown when the user could not be authenticated against the server, despite token refreshs etc.
 * 
 * @author Philipp Keck
 */
public class UnauthenticatedException extends RequestException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param message
     *            Exception message.
     */
    public UnauthenticatedException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            Cause of the error.
     */
    public UnauthenticatedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     *            Exception message.
     * @param cause
     *            Cause of the error.
     */
    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
