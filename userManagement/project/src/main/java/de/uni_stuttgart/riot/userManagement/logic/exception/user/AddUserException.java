package de.uni_stuttgart.riot.userManagement.logic.exception.user;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AddUserException extends LogicException {
    public AddUserException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public AddUserException(String message, Exception cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1810899195980316871L;

    public int getErrorCode() {
        return ErrorCodes.ADD_USER;
    }

}
