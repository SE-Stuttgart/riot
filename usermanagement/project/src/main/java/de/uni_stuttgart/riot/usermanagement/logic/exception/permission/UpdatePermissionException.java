package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while updating a permission.
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdatePermissionException extends LogicException {

    private static final long serialVersionUID = 1810099395980316871L;

    private static final String END_USER_MESSAGE = "Couldn't update the permission";

    /**
     * Constructs a new exception for the case that updating a permission fails.
     * 
     * @param message
     *            Message for the user
     */
    public UpdatePermissionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that updating a permission fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public UpdatePermissionException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that updating a permission fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public UpdatePermissionException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.UPDATE_PERMISSION;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
