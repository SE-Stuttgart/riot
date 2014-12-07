package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving all users.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllUsersException extends LogicException {
    /**
     * 
     */
    private static final long serialVersionUID = -7161371919988111037L;

    private static final String END_USER_MESSAGE = "Couldn't get all user";

    /**
     * Constructs a new exception for the case that retrieving all users fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetAllUsersException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that retrieving all users fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the problem
     */
    public GetAllUsersException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that retrieving all users fails.
     * 
     * @param cause
     *            Root cause of the problem
     */
    public GetAllUsersException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.GET_USER;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

}
