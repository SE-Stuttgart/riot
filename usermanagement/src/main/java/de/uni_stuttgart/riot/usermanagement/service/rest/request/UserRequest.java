/**
 * 
 */
package de.uni_stuttgart.riot.usermanagement.service.rest.request;

/**
 * A request for a user.
 * 
 * @author Niklas Schnabel
 */
public class UserRequest {

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
