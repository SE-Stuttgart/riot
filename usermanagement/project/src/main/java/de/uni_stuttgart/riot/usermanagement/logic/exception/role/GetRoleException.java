package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving a specific role.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetRoleException extends LogicException {
    private static final long serialVersionUID = 1810429172580316871L;

    private static final String END_USER_MESSAGE = "Couldn't get the role";

    /**
     * Constructs a new exception for the case that retrieving a specific role fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that retrieving a specific role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public GetRoleException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that retrieving a specific role fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public GetRoleException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.GET_ROLE;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
