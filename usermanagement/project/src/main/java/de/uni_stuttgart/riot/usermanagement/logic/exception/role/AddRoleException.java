package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * Exception is thrown, if an error occurs while adding a new role.
 * 
 * @author Niklas Schnabel
 *
 */
public class AddRoleException extends LogicException {

    private static final long serialVersionUID = 1810829195980316871L;

    private static final String END_USER_MESSAGE = "Couldn't add a new role";

    /**
     * Constructs a new exception for the case that adding a new a role fails.
     * 
     * @param message
     *            Message for the user
     */
    public AddRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception for the case that adding a new a role fails.
     * 
     * @param message
     *            Message for the user
     * @param cause
     *            Root cause of the problem
     */
    public AddRoleException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception for the case that adding a new a role fails.
     * 
     * @param cause
     *            Root cause of the problem
     */
    public AddRoleException(Exception cause) {
        super(cause);
    }

    public int getErrorCode() {
        return ErrorCodes.ADD_ROLE;
    }

    public String getEndUserMessage() {
        return END_USER_MESSAGE;
    }
}
