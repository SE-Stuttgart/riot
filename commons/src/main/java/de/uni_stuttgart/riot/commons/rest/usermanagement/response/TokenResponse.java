package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;


/**
 * Wrapper around a {@link Token}.
 */
public class TokenResponse {

    private Token token;

    /**
     * Creates a new TokenResponse.
     * 
     * @param token
     *            The wrapped Ttoken.
     */
    public TokenResponse(Token token) {
        this.token = token;
    }

    public Long getId() {
        return token.getId();
    }

    public Timestamp getIssueTime() {
        return token.getIssueTime();
    }

    public Timestamp getExpirationTime() {
        return token.getExpirationTime();
    }

    public String getTokenValue() {
        return token.getTokenValue();
    }

    public String getRefreshtokenValue() {
        return token.getRefreshtokenValue();
    }

    public boolean isValid() {
        return token.isValid();
    }

}
