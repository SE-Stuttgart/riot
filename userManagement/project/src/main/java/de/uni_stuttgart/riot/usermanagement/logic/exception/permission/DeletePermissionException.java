package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class DeletePermissionException extends LogicException {

	public DeletePermissionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public DeletePermissionException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810899395980316871L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_PERMISSION;
    }

    @Override
    public String getMessage() {
        return "Unable to delete the permission";
    }

}
