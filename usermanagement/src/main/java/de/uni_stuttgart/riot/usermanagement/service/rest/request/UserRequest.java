/**
 * 
 */
package de.uni_stuttgart.riot.usermanagement.service.rest.request;

/**
 * @author Niklas Schnabel
 *
 */
public class UserRequest {

    private String username;
    private String password;

    public UserRequest() {
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
