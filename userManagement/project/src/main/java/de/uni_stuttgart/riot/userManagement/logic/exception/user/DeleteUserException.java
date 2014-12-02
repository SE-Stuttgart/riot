package de.uni_stuttgart.riot.userManagement.logic.exception.user;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class DeleteUserException extends LogicException {
	public DeleteUserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
    public DeleteUserException(String message, Exception cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
    /**
     * 
     */
    private static final long serialVersionUID = 3251547602783328996L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_USER;
    }

    @Override
    public String getMessage() {
        return "unable to delete the user";
    }

}
