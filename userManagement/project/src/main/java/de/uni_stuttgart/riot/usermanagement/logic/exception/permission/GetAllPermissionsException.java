package de.uni_stuttgart.riot.usermanagement.logic.exception.permission;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllPermissionsException extends LogicException {

	public GetAllPermissionsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public GetAllPermissionsException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810099395980316871L;

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_PERMISSIONS;
    }

    @Override
    public String getMessage() {
        return "Unable to get all permissions";
    }

}
