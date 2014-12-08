package de.uni_stuttgart.riot.userManagement.service.exception;

/**
 * The class is used as part of the ApiErrorResponse to produce an error response to the user.
 * 
 * @author Marcel Lehwald
 *
 */
public class ApiError {
	
	private final int errorCode;
	private final String errorMessage;

	public ApiError(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
