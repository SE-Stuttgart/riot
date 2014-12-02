package de.uni_stuttgart.riot.userManagement.logic.exception.role;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetRoleException extends LogicException {
	public GetRoleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public GetRoleException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1810429172580316871L;

    public int getErrorCode() {
        return ErrorCodes.GET_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to get the role";
    }

}
