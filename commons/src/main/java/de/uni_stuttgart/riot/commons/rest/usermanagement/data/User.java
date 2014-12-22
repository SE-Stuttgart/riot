package de.uni_stuttgart.riot.usermanagement.data.storable;

/**
 * The User class holds all basic information regarding to a user.
 * 
 * @author Niklas Schnabel
 *
 */
public class User {

    // TODO This class should be moved to a better place than this. Maybe the commons project would be the right place

    protected long id;
    protected String username;

    /**
     * Constructs a new user.
     */
    public User() {
        id = -1;
        username = null;
    }

    /**
     * Constructs a new user.
     * 
     * @param username
     *            The name of the user
     */
    public User(String username) {
        this.id = -1;
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
        this.id = id;
        this.username = username;
    }

    /**
     * Returns the id.
     * 
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
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
