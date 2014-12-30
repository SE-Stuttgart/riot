package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * The User class holds all basic information regarding to a user for the user management.
 * 
 * @author Jonas Tangermann
 *
 */
public class UMUser extends User {

    private String hashedPassword;
    private String passwordSalt;
    private int hashIterations;

    public UMUser() {
        super();
    }

    public UMUser(long id, String username, String hashedPassword, String passwordSalt, int hashIterations) {
        super(id, username);
        this.setHashedPassword(hashedPassword);
        this.setPasswordSalt(passwordSalt);
        this.setHashIterations(hashIterations);
    }

    public UMUser(String username, String hashedPassword, String passwordSalt, int hashIterations) {
        super(username);
        this.setHashedPassword(hashedPassword);
        this.setPasswordSalt(passwordSalt);
        this.setHashIterations(hashIterations);
    }

    public UMUser(String username) {
        super(username);
        this.setHashedPassword(null);
        this.setPasswordSalt(null);
        this.setHashIterations(-1);
    }


    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }


    @Override
	public String toString() {
		return "UMUser [hashedPassword=" + hashedPassword + ", passwordSalt="
				+ passwordSalt + ", hashIterations=" + hashIterations + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + hashIterations;
		result = prime * result
				+ ((hashedPassword == null) ? 0 : hashedPassword.hashCode());
		result = prime * result
				+ ((passwordSalt == null) ? 0 : passwordSalt.hashCode());
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
		UMUser other = (UMUser) obj;
		if (hashIterations != other.hashIterations)
			return false;
		if (hashedPassword == null) {
			if (other.hashedPassword != null)
				return false;
		} else if (!hashedPassword.equals(other.hashedPassword))
			return false;
		if (passwordSalt == null) {
			if (other.passwordSalt != null)
				return false;
		} else if (!passwordSalt.equals(other.passwordSalt))
			return false;
		return true;
	}

	public int getHashIterations() {
        return hashIterations;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

}
