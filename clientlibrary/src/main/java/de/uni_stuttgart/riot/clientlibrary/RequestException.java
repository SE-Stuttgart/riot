package de.uni_stuttgart.riot.clientlibrary;

/**
 * Exception for errors at requesting a rest service.
 */
public class RequestException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param message
     *            Exception message.
     */
    public RequestException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            Cause of the error.
     */
    public RequestException(Throwable cause) {
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
    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
