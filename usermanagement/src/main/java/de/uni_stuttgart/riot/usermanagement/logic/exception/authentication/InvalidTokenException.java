package de.uni_stuttgart.riot.usermanagement.logic.exception.authentication;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an invalid token was given.
 * 
 * @author Niklas Schnabel
 *
 */
public class InvalidTokenException extends LogicException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     * 
     * @param message
     *            Message that is shown to the user
     * @param cause
     *            Root cause for the exception
     */
    public InvalidTokenException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception.
     * 
     * @param message
     *            that is shown to the user
     */
    public InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     * 
     * @param cause
     *            Root cause for the exception
     */
    public InvalidTokenException(Exception cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        return ErrorCodes.INVALID_TOKEN;
    }

}
