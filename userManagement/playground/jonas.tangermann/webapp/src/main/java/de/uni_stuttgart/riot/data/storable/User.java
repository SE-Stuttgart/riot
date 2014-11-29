package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class User implements Storable {

	private final Long id;
	private String username;
	private String password;
	private String passwordSalt;
	
	public User(Long id, String username, String password, String passwordSalt) {
		this.id = id;
		this.setUsername(username);
		this.setPassword(password);
		this.setPasswordSalt(passwordSalt);
	}
	
	@Override
	public long getID() {
		return this.id;
	}

	@Override
	public Collection<String> getSearchParam() {
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.username);
		return result;
	}
	
	public String toString(){
		return "["+this.id+":"+this.username+"]";
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
	
	public boolean equals(Object o){
		if(o instanceof User){
			User u = (User)o;
			return this.id.equals(u.getID()) 
					&& this.username.equals(u.username)
					&& this.password.equals(u.password)
					&& this.passwordSalt.equals(u.passwordSalt);
		} else {
			return false;
		}
	}


}
