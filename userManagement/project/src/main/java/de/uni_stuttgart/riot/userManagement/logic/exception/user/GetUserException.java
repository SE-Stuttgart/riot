package de.uni_stuttgart.riot.userManagement.logic.exception.user;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetUserException extends LogicException {
	public GetUserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public GetUserException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

    public int getErrorCode() {
        return ErrorCodes.UPDATE_USER;
    }

    @Override
    public String getMessage() {
        return "unable to update the user";
    }

}
