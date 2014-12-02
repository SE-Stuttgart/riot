package de.uni_stuttgart.riot.userManagement.logic.exception.user;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllUsersException extends LogicException {
	public GetAllUsersException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public GetAllUsersException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

    public int getErrorCode() {
        return ErrorCodes.GET_USER;
    }

    @Override
    public String getMessage() {
        return "unable to get the user";
    }

}
