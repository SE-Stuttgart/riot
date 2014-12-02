package de.uni_stuttgart.riot.userManagement.logic.exception;

import de.uni_stuttgart.riot.userManagement.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public abstract class LogicException extends UserManagementException {
   
	public LogicException(String message, Exception cause) {
		super(message, cause);
	}

	public LogicException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 3792732636934644900L;

}
