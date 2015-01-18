package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while retrieving all permissions of a role.
 * 
 * @author Niklas Schnabel
 *
 */
public class GetPermissionsFromRoleException extends LogicException {

    private static final long serialVersionUID = 1810829195980316871L;

    private static final String END_USER_MESSAGE = "Couldn't retrieve the permissions of the role";

    /**
     * Constructs a new exception for the case that retrieving the permissions of a role fails.
     * 
     * @param message
     *            Message for the user
     */
    public GetPermissionsFromRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that retrieving the permissions of a role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the problem
     */
    public GetPermissionsFromRoleException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that retrieving the permissions of a role fails.
     * 
     * @param cause
     *            Root cause of the problem
     */
    public GetPermissionsFromRoleException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_ROLE;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
