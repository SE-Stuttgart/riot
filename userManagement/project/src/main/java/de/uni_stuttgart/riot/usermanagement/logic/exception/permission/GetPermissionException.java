package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving a specific permission.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetPermissionException extends LogicException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810099395980316871L;

    private static final String END_USER_MESSAGE = "Couldn't get the permission";

    /**
     * Constructs a new exception for the case that the retrieval of a specific permission fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetPermissionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that the retrieval of a specific permission fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the exception
     */
    public GetPermissionException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that the retrieval of a specific permission fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public GetPermissionException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.GET_PERMISSION;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
