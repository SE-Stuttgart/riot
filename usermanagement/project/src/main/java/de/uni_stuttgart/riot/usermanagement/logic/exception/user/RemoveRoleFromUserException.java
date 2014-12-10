package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while removing a role from a user.
 * 
 * @author Niklas Schnabel
 *
 */
public class RemoveRoleFromUserException extends LogicException {
    private static final long serialVersionUID = 1210899197980316871L;

    private static final String END_USER_MESSAGE = "Couldn't remove the role from the user";

    /**
     * Constructs a new exception for the case that removing a role from a user fails.
     * 
     * @param message
     *            Message for the user
     */
    public RemoveRoleFromUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that removing a role from a user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public RemoveRoleFromUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that removing a role from a user fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public RemoveRoleFromUserException(Exception cause) {
        super(cause);
    }

    @Override
    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    public int getErrorCode() {
        return ErrorCodes.REMOVE_ROLE_FROM_USER;
    }

}
