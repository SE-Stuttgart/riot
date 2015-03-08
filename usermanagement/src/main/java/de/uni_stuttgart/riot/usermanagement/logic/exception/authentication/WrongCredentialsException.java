package de.uni_stuttgart.riot.usermanagement.logic.exception.authentication;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;

/**
 * Exception is thrown if the user cannot be logged in because of the provided credentials. That is, nothing went wrong on the technical
 * side, it's just that the credentials are wrong.
 * 
 * @author Philipp Keck
 */
public class WrongCredentialsException extends LoginException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     * 
     * @param message
     *            Message that is shown to the user
     * @param cause
     *            Root cause for the exception
     */
    public WrongCredentialsException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception.
     * 
     * @param message
     *            that is shown to the user
     */
    public WrongCredentialsException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     * 
     * @param cause
     *            Root cause for the exception
     */
    public WrongCredentialsException(Exception cause) {
        super("Wrong username/password", cause);
    }

    /**
     * Constructs a new exception.
     */
    public WrongCredentialsException() {
        super("Wrong username/password");
    }

    @Override
    public int getErrorCode() {
        return ErrorCodes.INVALID_CREDENTIALS;
    }

}
