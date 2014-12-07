package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving all permissions.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllPermissionsException extends LogicException {

    private static final long serialVersionUID = 1810099395980316871L;

    private static final String END_USER_MESSAGE = "Couldn't get all permissions";

    /**
     * Constructs a new exception for the case that the retrieval of all permissions fails.
     * 
     * @param message
     *            Message shown to the user
     */
    public GetAllPermissionsException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that the retrieval of all permissions fails.
     * 
     * @param message
     *            Message shown to the user
     * @param cause
     *            Root cause of the exception
     */
    public GetAllPermissionsException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that the retrieval of all permissions fails.
     * 
     * @param cause
     *            Root cause of the exception
     */
    public GetAllPermissionsException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_PERMISSIONS;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

}
