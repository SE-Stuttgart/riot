package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving all roles.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllRolesException extends LogicException {
    private static final long serialVersionUID = 181042917258036871L;

    private static final String END_USER_MESSAGE = "Couldn't get all roles";

    /**
     * Constructs a new exception for the case that retrieving all roles fail.
     * 
     * @param message
     *            Message for the user
     */
    public GetAllRolesException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that retrieving all roles fail.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the problem
     */
    public GetAllRolesException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that retrieving all roles fail.
     * 
     * @param cause
     *            Root cause of the problem
     */
    public GetAllRolesException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_ROLES;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
