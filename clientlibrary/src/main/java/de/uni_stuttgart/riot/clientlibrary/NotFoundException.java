package de.uni_stuttgart.riot.clientlibrary;

/**
 * Thrown when a GET request could not find its target.
 * 
 * @author Philipp Keck
 */
public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for NotFoundException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for NotFoundException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public NotFoundException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for NotFoundException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
