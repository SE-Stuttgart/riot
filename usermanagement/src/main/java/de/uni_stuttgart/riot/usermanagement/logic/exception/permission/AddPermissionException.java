package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while creating a new permission.
 * 
 * @author Niklas Schnabel
 *
 */
public class AddPermissionException extends LogicException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String END_USER_MESSAGE = "Couldn't add a new permission";

    /**
     * Constructs a new exception for the case that adding a new permission fails.
     * 
     * @param message
     *            Message that is shown to the user
     */
    public AddPermissionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that adding a new permission fails.
     * 
     * @param message
     *            Message that is shown to the user
     * @param cause
     *            Root cause for the exception
     */
    public AddPermissionException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that adding a new permission fails.
     * 
     * @param cause
     *            Root cause for exception
     */
    public AddPermissionException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_USER;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
