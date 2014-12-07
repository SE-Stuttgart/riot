package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while updating an user.
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdateUserException extends LogicException {

    private static final long serialVersionUID = -3633891147672137883L;

    private static final String END_USER_MESSAGE = "Couldn't update the user";

    /**
     * Constructs a new exception for the case that updating an user fails.
     * 
     * @param message
     *            Message for the user
     */
    public UpdateUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that updating an user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause for the exception
     */
    public UpdateUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that updating an user fails.
     * 
     * @param cause
     *            Root cause of the excpetion
     */
    public UpdateUserException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_USERS;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

}
