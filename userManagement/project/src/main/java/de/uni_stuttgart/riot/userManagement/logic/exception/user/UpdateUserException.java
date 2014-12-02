package de.uni_stuttgart.riot.userManagement.logic.exception.user;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdateUserException extends LogicException {
	public UpdateUserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public UpdateUserException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_USERS;
    }

    @Override
    public String getMessage() {
        return "unable to get all users";
    }

}
