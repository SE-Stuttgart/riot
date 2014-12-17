package de.uni_stuttgart.riot.usermanagement.logic.exception.authentication;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while logging out.
 * 
 * @author Niklas Schnabel
 *
 */
public class LogoutException extends LogicException {

    /**
     * 
     */
    private static final long serialVersionUID = -5647256573410094208L;

    private static final String END_USER_MESSAGE = "Logout failed";

    /**
     * Constructs a new exception for the case that logging out fails.
     * 
     * @param message
     *            Message that is shown to the user
     * @param cause
     *            Root cause for the exception
     */
    public LogoutException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that logging out fails.
     * 
     * @param message
     *            that is shown to the user
     */
    public LogoutException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that logging out fails.
     * 
     * @param cause
     *            Root cause for the exception
     */
    public LogoutException(Exception cause) {
        super(cause);
    }

    @Override
    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    @Override
    public int getErrorCode() {
        return ErrorCodes.LOGOUT;
    }

}
