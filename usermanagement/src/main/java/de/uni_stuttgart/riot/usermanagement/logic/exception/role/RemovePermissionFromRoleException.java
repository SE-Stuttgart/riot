package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while removing a permission from role.
 * 
 * @author Niklas Schnabel
 *
 */
public class RemovePermissionFromRoleException extends LogicException {

    private static final long serialVersionUID = 1810829195980316871L;

    private static final String END_USER_MESSAGE = "Couldn't remove the permission from the role";

    /**
     * Constructs a new exception for the case that removing a permission from a role fails.
     * 
     * @param message
     *            Message for the user
     */
    public RemovePermissionFromRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that removing a permission from a role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the problem
     */
    public RemovePermissionFromRoleException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that removing a permission from a role fails.
     * 
     * @param cause
     *            Root cause of the problem
     */
    public RemovePermissionFromRoleException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_ROLE;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
