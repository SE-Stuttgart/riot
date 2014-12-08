package de.uni_stuttgart.riot.usermanagement.service.request;

import de.uni_stuttgart.riot.usermanagement.service.AuthenticationService;

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
