package de.uni_stuttgart.riot.commons.rest.usermanagement.request;


/**
 * The refresh request message of the client used in {@link AuthenticationService}.
 * 
 * @author Marcel Lehwald
 *
 */
public class RefreshRequest {

    private String refreshToken;

    
    public RefreshRequest(String refreshToken) {
    	this.refreshToken = refreshToken;
	}
    
    public RefreshRequest() {
	}
	
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}