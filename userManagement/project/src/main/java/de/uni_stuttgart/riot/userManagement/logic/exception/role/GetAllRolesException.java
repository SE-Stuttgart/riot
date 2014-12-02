package de.uni_stuttgart.riot.userManagement.logic.exception.role;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllRolesException extends LogicException {
	public GetAllRolesException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public GetAllRolesException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 181042917258036871L;

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_ROLES;
    }

    @Override
    public String getMessage() {
        return "Unable to get the roles";
    }

}
