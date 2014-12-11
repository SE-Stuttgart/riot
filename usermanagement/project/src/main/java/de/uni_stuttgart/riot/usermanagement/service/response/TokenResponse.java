package de.uni_stuttgart.riot.usermanagement.service.response;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

public class TokenResponse {

    private Token token;

    public TokenResponse() {

    }

    public TokenResponse(Token token) {
        this.token = token;
    }

    public Long getId() {
        return token.getId();
    }

    public void setId(Long id) {
        token.setId(id);
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

    public void setTokenValue(String tokenValue) {
        token.setTokenValue(tokenValue);
    }

    public String getRefreshtokenValue() {
        return token.getRefreshtokenValue();
    }

    public void setRefreshtokenValue(String refreshtokenValue) {
        token.setRefreshtokenValue(refreshtokenValue);
    }

    public boolean isValid() {
        return token.isValid();
    }

    public void setValid(boolean valid) {
        this.setValid(valid);
    }

}
