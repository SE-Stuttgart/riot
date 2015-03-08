package de.uni_stuttgart.riot.clientlibrary;

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

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public void invalidateAccessToken() {
        this.accessToken = null;
    }
}
