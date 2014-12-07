package de.uni_stuttgart.riot.usermanagement.service.response;

import de.uni_stuttgart.riot.usermanagement.service.AuthenticationService;

/**
 * The authentication response message for the client used in {@link AuthenticationService}.
 * 
 * @author Marcel Lehwald
 *
 */
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;

    /**
     * Create a authentication response.
     * @param accessToken The valid access token generated when the user was successfully authenticated.
     * @param refreshToken The valid refresh token generated when the user was successfully authenticated.
     */
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
