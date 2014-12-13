package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while adding a permission to a role.
 * 
 * @author Niklas Schnabel
 *
 */
public class AddPermissionToRoleException extends LogicException {

    private static final long serialVersionUID = 1810829195980316871L;

    private static final String END_USER_MESSAGE = "Couldn't add the permissiont to the role";

    /**
     * Constructs a new exception for the case that adding a permission to a role fails.
     * 
     * @param message
     *            Message for the user
     */
    public AddPermissionToRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that adding a permission to a role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the problem
     */
    public AddPermissionToRoleException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that adding a permission to a role fails.
     * 
     * @param cause
     *            Root cause of the problem
     */
    public AddPermissionToRoleException(Exception cause) {
        super(cause);
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_PERMISSION_TO_ROLE;
    }
}
