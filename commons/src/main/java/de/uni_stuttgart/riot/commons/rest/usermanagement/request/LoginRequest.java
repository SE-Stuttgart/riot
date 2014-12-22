package de.uni_stuttgart.riot.commons.rest.usermanagement.request;

/**
 * The login request message of the client used in {@link AuthenticationService}.
 * 
 * @author Marcel Lehwald
 *
 */
public class LoginRequest {

    private String username;
    private String password;
    
    public LoginRequest(String username, String password) {
    	this.username = username;
    	this.password = password;
    }
    
    public LoginRequest() {
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}