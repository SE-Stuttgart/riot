package de.uni_stuttgart.riot.userManagement.logic.exception.role;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdateRoleException extends LogicException {
	public UpdateRoleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public UpdateRoleException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810429192580316871L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to update the role";
    }

}
