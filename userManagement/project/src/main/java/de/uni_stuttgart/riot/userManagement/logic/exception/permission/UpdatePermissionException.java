package de.uni_stuttgart.riot.userManagement.logic.exception.permission;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdatePermissionException extends LogicException {

	public UpdatePermissionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public UpdatePermissionException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    
    /**
     * 
     */
    private static final long serialVersionUID = 1810099395980316871L;

    public int getErrorCode() {
        return ErrorCodes.UPDATE_PERMISSION;
    }

    @Override
    public String getMessage() {
        return "Unable to update the permission";
    }

}
