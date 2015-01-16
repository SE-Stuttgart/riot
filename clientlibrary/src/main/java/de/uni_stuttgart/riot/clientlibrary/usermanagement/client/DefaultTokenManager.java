package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

/**
 * Default implementation of a {@link TokenManager}, which saves the tokens only in memory.
 * 
 * @author Niklas Schnabel
 */
public class DefaultTokenManager implements TokenManager {

    /** The access token. */
    private String accessToken;

    /** The refresh token. */
    private String refreshToken;

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#getAccessToken()
     */
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#setAccessToken(java.lang.String)
     */
    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#getRefreshToken()
     */
    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager#setRefreshToken(java.lang.String)
     */
    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
