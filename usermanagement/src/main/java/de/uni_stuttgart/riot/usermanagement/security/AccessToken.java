package de.uni_stuttgart.riot.usermanagement.security;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * An AuthenticationToken implementation for an access token. Access tokens are usually used in REST APIs where the access token will be
 * send along with the payload each request to authenticate the user.
 * 
 * @author Marcel Lehwald
 *
 */
public class AccessToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;
    private String token;

    /**
     * Create a access token for authentication.
     * 
     * @param token The access token.
     */
    public AccessToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return getToken();
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }

    public String getToken() {
        return token;
    }

}
