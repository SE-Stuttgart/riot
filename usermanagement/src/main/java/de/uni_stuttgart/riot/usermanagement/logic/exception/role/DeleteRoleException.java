package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while deleting a role.
 * 
 * @author Niklas Schnabel
 *
 */
public class DeleteRoleException extends LogicException {
    private static final long serialVersionUID = 1810429195980316871L;

    private static final String END_USER_MESSAGE = "Couldn't delete the role";

    /**
     * Constructs a new exception for the case that deleting a role fails.
     * 
     * @param message
     *            Message for the user
     */
    public DeleteRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that deleting a role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public DeleteRoleException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that deleting a role fails.
     * 
     * @param cause
     *            Root cause for the exception
     */
    public DeleteRoleException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.DELETE_ROLE;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

}
