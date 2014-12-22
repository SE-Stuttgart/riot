package de.uni_stuttgart.riot.commons.rest.usermanagement;

public class RequestError {
	
	public int errorCode;
	public String errorMessage;
	
	public RequestError(int errorCode,String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMassage) {
		this.errorMessage = errorMassage;
	}

	public RequestError() {
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void throwIT() throws Exception {
		throw new Exception(this.getErrorCode() +" : " + this.getErrorMessage());
	}

}
