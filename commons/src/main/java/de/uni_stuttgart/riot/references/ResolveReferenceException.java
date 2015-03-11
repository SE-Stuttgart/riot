package de.uni_stuttgart.riot.references;

/**
 * This kind of exception is thrown when an error occurs while dereferencing a {@link Reference}. 
 * @author Philipp Keck
 */
public class ResolveReferenceException extends Exception {
    
    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for ResolveReferenceException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public ResolveReferenceException(String message) {
        super(message);
    }

    /**
     * Constructor for ResolveReferenceException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public ResolveReferenceException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for ResolveReferenceException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public ResolveReferenceException(Throwable cause) {
        super(cause);
    }

}
