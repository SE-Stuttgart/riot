package de.uni_stuttgart.riot.userManagement.logic.exception.role;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class DeleteRoleException extends LogicException {
	public DeleteRoleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public DeleteRoleException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810429195980316871L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to delete the role";
    }

}
