package de.uni_stuttgart.riot.usermanagement.logic.exception.user;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while adding a new user.
 * 
 * @author Niklas Schnabel
 *
 */
public class AddUserException extends LogicException {
    private static final long serialVersionUID = 1810899195980316871L;

    private static final String END_USER_MESSAGE = "Couldn't add a new user";

    /**
     * Constructs a new exception for the case that adding a new user fails.
     * 
     * @param message
     *            Message for the user
     */
    public AddUserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that adding a new user fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public AddUserException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that adding a new user fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public AddUserException(Exception cause) {
        super(cause);
    }

    @Override
    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_USER;
    }

}
