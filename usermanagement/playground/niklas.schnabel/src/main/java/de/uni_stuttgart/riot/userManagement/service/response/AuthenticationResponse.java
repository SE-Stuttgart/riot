package de.uni_stuttgart.riot.userManagement.service.response;

/**
 * 
 * @author Marcel Lehwald
 *
 */
public class AuthenticationResponse {

	private String accessToken;
	private String refreshToken;

	public AuthenticationResponse() {

	}

	public AuthenticationResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
