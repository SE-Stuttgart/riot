package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

/**
 * Exception for errors at requesting a rest service. 
 */
public class RequestException extends Exception{

    private static final long serialVersionUID = 1L;
    /**
     * Constructor.
     * @param message Exception message
     */
    public RequestException(String message) {
        super(message);
    }
    /**
     * Constructor.
     * @param cause cause
     */
    public RequestException(Exception cause) {
        super(cause);
    }
}
