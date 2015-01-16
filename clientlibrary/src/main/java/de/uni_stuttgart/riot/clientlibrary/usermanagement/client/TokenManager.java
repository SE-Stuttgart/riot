package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

/**
 * The Token Manager defines how the tokens should be managed in the application. For example android variables can get lost, if the
 * application gets destroyed.
 * 
 * @author Niklas Schnabel
 */
public interface TokenManager {

    /**
     * Gets the access token.
     *
     * @return the access token
     */
    String getAccessToken();

    /**
     * Sets the access token.
     *
     * @param accessToken
     *            the new access token
     */
    void setAccessToken(String accessToken);

    /**
     * Gets the refresh token.
     *
     * @return the refresh token
     */
    String getRefreshToken();

    /**
     * Sets the refresh token.
     *
     * @param refreshToken
     *            the new refresh token
     */
    void setRefreshToken(String refreshToken);
}
