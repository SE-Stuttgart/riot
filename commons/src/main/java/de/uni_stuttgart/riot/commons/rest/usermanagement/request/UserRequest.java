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
    private String email;
    
    /**
     * Const.
     * @param username .
     * @param password .
     * @param email .
     */
    public UserRequest(String username, String password, String email) {
        this.password = password;
        this.username = username;
        this.email = email;
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

    /**
     * Getter email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter email.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

}

