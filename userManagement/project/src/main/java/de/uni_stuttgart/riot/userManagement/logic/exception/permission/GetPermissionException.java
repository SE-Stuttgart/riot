package de.uni_stuttgart.riot.userManagement.logic.exception.permission;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetPermissionException extends LogicException {

	
	public GetPermissionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public GetPermissionException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810099395980316871L;

    public int getErrorCode() {
        return ErrorCodes.GET_PERMISSION;
    }

    @Override
    public String getMessage() {
        return "Unable to get the permission";
    }

}
