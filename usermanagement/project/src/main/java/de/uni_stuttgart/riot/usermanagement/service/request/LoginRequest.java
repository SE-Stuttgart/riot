package de.uni_stuttgart.riot.usermanagement.service.request;

import de.uni_stuttgart.riot.usermanagement.service.AuthenticationService;

/**
 * The login request message of the client used in {@link AuthenticationService}.
 * 
 * @author Marcel Lehwald
 *
 */
public class LoginRequest {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
