package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving a specific user.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetUserException extends LogicException {

    private static final long serialVersionUID = -3247077638592980957L;

    private static final String END_USER_MESSAGE = "Couldn't get the user";

    /**
     * Constructs a new exception for the case that retrieving a specific user fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that retrieving a specific user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public GetUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that retrieving a specific user fails.
     * 
     * @param cause
     *            Root cause of the excpetion
     */
    public GetUserException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.UPDATE_USER;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

}
