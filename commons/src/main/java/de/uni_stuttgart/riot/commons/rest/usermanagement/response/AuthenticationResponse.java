package de.uni_stuttgart.riot.commons.rest.usermanagement.response;


/**
 * The authentication response message for the client used in {@link AuthenticationService}.
 * 
 * @author Marcel Lehwald
 *
 */
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    /**
     * Create a authentication response.
     * 
     * @param accessToken
     *            The valid access token generated when the user was successfully authenticated.
     * @param refreshToken
     *            The valid refresh token generated when the user was successfully authenticated.
     * @param user
     *            The user.
     */
    public AuthenticationResponse(String accessToken, String refreshToken, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
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

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

}
