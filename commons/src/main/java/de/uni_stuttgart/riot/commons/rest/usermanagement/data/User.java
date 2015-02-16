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
    public User(String username, String email, Collection<Role> roles) {
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
        this.email = email;
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    
}
