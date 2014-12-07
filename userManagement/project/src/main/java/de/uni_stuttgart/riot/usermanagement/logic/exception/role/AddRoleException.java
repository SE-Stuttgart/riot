package de.uni_stuttgart.riot.usermanagement.logic.exception.role;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AddRoleException extends LogicException {
	public AddRoleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public AddRoleException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810829195980316871L;

    public int getErrorCode() {
        return ErrorCodes.ADD_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to add the new role";
    }

}
