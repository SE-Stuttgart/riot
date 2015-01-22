/**
 * 
 */
package de.uni_stuttgart.riot.commons.rest.usermanagement.request;

/**
 * A request for a user.
 * 
 * @author Niklas Schnabel
 */
public class UserRequest {

    private String username;
    private String password;
    
    /**
     * Const.
     * @param username .
     * @param password .
     */
    public UserRequest(String username, String password) {
        this.password = password;
        this.username = username;
    }
    
    /**
     * Default const.
     */
    public UserRequest() {
    }

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

