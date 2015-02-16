package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * The User class holds all basic information regarding to a user.
 * 
 * @author Niklas Schnabel
 *
 */
@JsonIgnoreProperties({ "hashedPassword", "passwordSalt", "hashIterations", "loginAttemptCount" })
public class User extends Storable {

    protected String username;
    protected String email;
    private transient Collection<Role> roles;

    /**
     * Constructs a new user.
     */
    public User() {
        super(-1L);
        username = null;
    }

    /**
     * Constructs a new user.
     * 
     * @param username
     *            The name of the user
     * @param email
     *            The email of the user
     */
    public User(String username, String email) {
        super(-1L);
        this.username = username;
        this.email = email;
    }

    /**
     * Constructs a new user.
     * 
     * @param username
     *            The name of the user
     * @param roles
     *            The roles of the user
     * @param email
     *            The email of the user 
     */
    public User(String username, String email,Collection<Role> roles) {
        super(-1L);
        this.username = username;
        this.email = email;
        this.setRoles(roles);
    }

    /**
     * Constructs a new user.
     * 
     * @param id
     *            The id of the user
     * @param username
     *            The name of the user
     * @param email
     *            The email of the user 
     */
    public User(long id, String username, String email) {
        super(id);
        this.username = username;
    }

    /**
     * Returns the user name.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user name.
     * 
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     *  Sets the user email.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User [username=" + username + "]";
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

}
