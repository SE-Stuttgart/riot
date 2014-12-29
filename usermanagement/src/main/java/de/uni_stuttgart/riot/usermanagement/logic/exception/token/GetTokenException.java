package de.uni_stuttgart.riot.usermanagement.logic.exception.token;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while getting a token.
 * 
 * @author Marcel Lehwald
 *
 */
public class GetTokenException extends LogicException {
    private static final long serialVersionUID = 1810899195980316872L;

    private static final String END_USER_MESSAGE = "Couldn't get token";

    /**
     * Constructs a new exception for the case that getting a token fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that getting a token fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public GetTokenException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that adding getting a token fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public GetTokenException(Exception cause) {
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
