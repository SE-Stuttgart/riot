package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

public class RequestExceptionWrapper {
	
	public int errorCode;
	public String errorMessage;
	
	public RequestExceptionWrapper(int errorCode,String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMassage) {
		this.errorMessage = errorMassage;
	}

	public RequestExceptionWrapper() {
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void throwIT() throws RequestException {
		throw new RequestException(this.getErrorCode() +" : " + this.getErrorMessage());
	}

}
