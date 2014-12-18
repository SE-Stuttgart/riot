package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * The User class holds all basic information regarding to a user for the user management.
 * 
 * @author Jonas Tangermann
 *
 */
public class UMUser extends User implements Storable {

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

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> result = new LinkedList<SearchParameter>();
        result.add(new SearchParameter(SearchFields.USERNAME, this.getUsername()));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [id=");
        builder.append(id);
        builder.append(", username=");
        builder.append(username);
        builder.append(", hashedPassword=");
        builder.append(hashedPassword);
        builder.append(", passwordSalt=");
        builder.append(passwordSalt);
        builder.append("]");
        return builder.toString();
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
    public boolean equals(Object o) {
        if (o instanceof UMUser) {
            UMUser u = (UMUser) o;
            return this.id == u.getId() && this.username.equals(u.username) && this.hashedPassword.equals(u.hashedPassword) && this.passwordSalt.equals(u.passwordSalt);
        } else {
            return false;
        }
    }

    public int getHashIterations() {
        return hashIterations;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

}
