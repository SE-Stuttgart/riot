package de.uni_stuttgart.riot.usermanagement.logic.exception.authentication;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while creating a new set of tokens.
 * 
 * @author Niklas Schnabel
 *
 */
public class GenerateTokenException extends LogicException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception for the case that creating a set of tokens fails.
     * 
     * @param message
     *            Message that is shown to the user
     * @param cause
     *            Root cause for the exception
     */
    public GenerateTokenException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that creating a set of tokens fails.
     * 
     * @param message
     *            that is shown to the user
     */
    public GenerateTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that creating a set of tokens fails.
     * 
     * @param cause
     *            Root cause for the exception
     */
    public GenerateTokenException(Exception cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        return ErrorCodes.GET_TOKEN;
    }

}
