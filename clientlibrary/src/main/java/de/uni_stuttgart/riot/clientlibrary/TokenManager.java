package de.uni_stuttgart.riot.clientlibrary;

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
     * Mark the current access token as invalid and never return it again.
     */
    void invalidateAccessToken();

    /**
     * Gets the refresh token.
     *
     * @return the refresh token
     */
    String getRefreshToken();

    /**
     * Sets the refresh token. Note that using this method may clear the access token (but does not have to)! For example, the Android
     * AccountManager clears all AuthTokens when the password (=refresh token) is changed. So always call {@link #setRefreshToken(String)}
     * before {@link #setAccessToken(String)}.
     *
     * @param refreshToken
     *            the new refresh token
     */
    void setRefreshToken(String refreshToken);

}
