package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class User implements Storable {

	private final Long id;
	private final String username;
	
	public User(Long id, String username) {
		this.id = id;
		this.username = username;
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

}
