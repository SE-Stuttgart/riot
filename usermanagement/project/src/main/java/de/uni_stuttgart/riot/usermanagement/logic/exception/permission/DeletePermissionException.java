package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while deleting a permission.
 * 
 * @author Niklas Schnabel
 *
 */
public class DeletePermissionException extends LogicException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String END_USER_MESSAGE = "Couldn't delete the permission";

    /**
     * Constructs a new exception for the case that deleting a permission fails.
     * 
     * @param message
     *            Message shown to the user
     */
    public DeletePermissionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that deleting a permission fails.
     * 
     * @param message
     *            Message shown to the user
     * @param cause
     *            Root cause of the exception
     */
    public DeletePermissionException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that deleting a permission fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public DeletePermissionException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.DELETE_PERMISSION;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

}
