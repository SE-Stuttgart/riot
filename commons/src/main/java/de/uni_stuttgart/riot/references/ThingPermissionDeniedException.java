package de.uni_stuttgart.riot.references;

import de.uni_stuttgart.riot.thing.rest.ThingPermission;

/**
 * This kind of exception is thrown when a {@link Reference} to a {@link Thing} could be resolved but its resolved result must not be used
 * due to missing {@link ThingPermission} of the user who resolved it on the respective thing.
 * 
 * @author Philipp Keck
 */
public class ThingPermissionDeniedException extends ResolveReferenceException {

    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for MissingThingPermissionException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public ThingPermissionDeniedException(String message) {
        super(message);
    }

    /**
     * Constructor for MissingThingPermissionException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public ThingPermissionDeniedException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for MissingThingPermissionException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public ThingPermissionDeniedException(Throwable cause) {
        super(cause);
    }

}
