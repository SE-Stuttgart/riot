package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while deleting a user.
 * 
 * @author Niklas Schnabel
 *
 */
public class DeleteUserException extends LogicException {
    private static final long serialVersionUID = 3251547602783328996L;

    private static final String END_USER_MESSAGE = "Couldn't delete the user";

    /**
     * Constructs a new exception for the case that deleting a user fails.
     * 
     * @param message
     *            Message for the user
     */
    public DeleteUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that deleting a user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public DeleteUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that deleting a user fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public DeleteUserException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.DELETE_USER;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    @Override
    public String getMessage() {
        return "unable to delete the user";
    }

}
