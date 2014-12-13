package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while adding a role to a user.
 * 
 * @author Niklas Schnabel
 *
 */
public class AddRoleToUserException extends LogicException {
    private static final long serialVersionUID = 1810899197980316871L;

    private static final String END_USER_MESSAGE = "Couldn't add the role to the user";

    /**
     * Constructs a new exception for the case that adding a role to a user fails.
     * 
     * @param message
     *            Message for the user
     */
    public AddRoleToUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that adding a role to a user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public AddRoleToUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that adding a role to a user fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public AddRoleToUserException(Exception cause) {
        super(cause);
    }

    @Override
    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_ROLE_TO_USER;
    }

}
