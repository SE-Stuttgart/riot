package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving all roles from a user.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetRolesFromUserException extends LogicException {
    private static final long serialVersionUID = 1210899197980316871L;

    private static final String END_USER_MESSAGE = "Couldn't retrieve all roles from the user";

    /**
     * Constructs a new exception for the case that the retrieving of all roles of a user fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetRolesFromUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that the retrieving of all roles of a user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public GetRolesFromUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that the retrieving of all roles of a user fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public GetRolesFromUserException(Exception cause) {
        super(cause);
    }

    @Override
    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    public int getErrorCode() {
        return ErrorCodes.GET_ROLES_FROM_USER;
    }

}
