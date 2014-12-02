package de.uni_stuttgart.riot.userManagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;

public class User implements Storable {

    private Long id;
    private String username;
    private String password;
    private String passwordSalt;

    public User() {
        // TODO Auto-generated constructor stub
    }

    public User(Long id, String username, String password, String passwordSalt) {
        this.id = id;
        this.setUsername(username);
        this.setPassword(password);
        this.setPasswordSalt(passwordSalt);
    }

    @Override
    public long getId() {
        return this.id;
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
        builder.append(", password=");
        builder.append(password);
        builder.append(", passwordSalt=");
        builder.append(passwordSalt);
        builder.append("]");
        return builder.toString();
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

    // @Override
    // public boolean equals(Object o) {
    // if (o instanceof User) {
    // User u = (User) o;
    // return this.id.equals(u.getId()) && this.username.equals(u.username) && this.password.equals(u.password) &&
    // this.passwordSalt.equals(u.passwordSalt);
    // } else {
    // return false;
    // }
    // }

}
