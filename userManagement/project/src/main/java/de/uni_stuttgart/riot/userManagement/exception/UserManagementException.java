package de.uni_stuttgart.riot.userManagement.exception;

public abstract class UserManagementException extends Exception {

	private static final long serialVersionUID = 1182375069868647185L;

	public UserManagementException(String message, Exception cause) {
		super(message,cause);
	}
	
	public UserManagementException(String message) {
		super(message);
	}
	
    public String getEndUserMessage(){
    	return this.getMessage();
    }
    
    public abstract int getErrorCode();

}
