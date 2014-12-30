package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

/**
 * The User class holds all basic information regarding to a user.
 * 
 * @author Niklas Schnabel
 *
 */
public class User extends Storable {


    protected String username;

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
     */
    public User(String username) {
    	super(-1L);
        this.username = username;
    }

    /**
     * Constructs a new user.
     * 
     * @param id
     *            The id of the user
     * @param username
     *            The name of the user
     */
    public User(long id, String username) {
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

}
