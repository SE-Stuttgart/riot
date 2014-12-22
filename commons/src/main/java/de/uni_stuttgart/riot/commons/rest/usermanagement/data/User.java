package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Ignore;

/**
 * The User class holds all basic information regarding to a user.
 * @author Jonas Tangermann
 *
 */
public class User extends Storable {

    private String username;
    private String password;
    private String passwordSalt;

    public User() {
    }

    public User(long id, String username, String password, String passwordSalt) {
    	super(id);
    	this.setUsername(username);
        this.setPassword(password);
        this.setPasswordSalt(passwordSalt);
    }

    public User(String username, String password, String passwordSalt) {
    	super(-1L);
        this.setUsername(username);
        this.setPassword(password);
        this.setPasswordSalt(passwordSalt);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((passwordSalt == null) ? 0 : passwordSalt.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordSalt == null) {
			if (other.passwordSalt != null)
				return false;
		} else if (!passwordSalt.equals(other.passwordSalt))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
