package de.uni_stuttgart.riot.thing.client;

/**
 * This exception is raised when a (mirrored) Thing is accessed that cannot be resolved.
 * 
 * @author Philipp Keck
 */
public class ThingNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for ThingNotFoundException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public ThingNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for ThingNotFoundException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public ThingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for ThingNotFoundException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public ThingNotFoundException(Throwable cause) {
        super(cause);
    }
}
