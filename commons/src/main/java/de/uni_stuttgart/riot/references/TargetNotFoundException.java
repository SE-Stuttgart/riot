package de.uni_stuttgart.riot.references;

/**
 * This kind of exception is thrown when a {@link Reference} cannot be resolved because the target entity with the given ID does not exist
 * (anymore).
 * 
 * @author Philipp Keck
 */
public class TargetNotFoundException extends ResolveReferenceException {

    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for TargetNotFoundException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public TargetNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for TargetNotFoundException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public TargetNotFoundException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for TargetNotFoundException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public TargetNotFoundException(Throwable cause) {
        super(cause);
    }

}
