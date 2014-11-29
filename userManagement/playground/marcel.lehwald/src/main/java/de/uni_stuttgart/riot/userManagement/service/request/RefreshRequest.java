package de.uni_stuttgart.riot.userManagement.service.request;

/**
 * 
 * @author Marcel Lehwald
 *
 */
public class RefreshRequest {

	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
