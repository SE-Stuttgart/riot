package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while updating a role.
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdateRoleException extends LogicException {
    private static final long serialVersionUID = 1810429192580316871L;

    private static final String END_USER_MESSAGE = "Couldn't add a new role";

    /**
     * Constructs a new exception for the case that updating a role fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public UpdateRoleException(Exception cause) {
        super(cause);
    }

    /**
     * Constructs a new exception for the case that updating a role fails.
     * 
     * @param message
     *            Message for the user
     */
    public UpdateRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that updating a role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public UpdateRoleException(String message, Exception cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return ErrorCodes.DELETE_ROLE;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
