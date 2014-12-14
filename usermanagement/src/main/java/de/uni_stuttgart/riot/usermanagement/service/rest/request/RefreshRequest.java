package de.uni_stuttgart.riot.usermanagement.service.rest.request;

import de.uni_stuttgart.riot.usermanagement.service.rest.AuthenticationService;

/**
 * The refresh request message of the client used in {@link AuthenticationService}.
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
