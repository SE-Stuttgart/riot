package de.uni_stuttgart.riot.usermanagement.logic.exception;

import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * Exception for all errors regarding the logic of the user management.
 * 
 * @author Niklas Schnabel
 *
 */
public abstract class LogicException extends UserManagementException {

    private static final long serialVersionUID = 3792732636934644900L;

    /**
     * Constructor for LogicException.
     * 
     * @param cause
     *            The message that should be shown to the user.
     */
    public LogicException(Exception cause) {
        super(cause);
    }

    /**
     * Constructor for LogicException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public LogicException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for LogicException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public LogicException(String message) {
        super(message);
    }

}
