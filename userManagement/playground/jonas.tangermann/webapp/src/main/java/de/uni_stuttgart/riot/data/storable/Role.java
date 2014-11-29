package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class Role implements Storable{
	
	private final Long id;
	private String roleName;
	
	public Role(Long id, String roleName) {
		this.id = id;
		this.setRoleName(roleName);
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public long getID() {
		return this.id;
	}

	@Override
	public Collection<String> getSearchParam() {
		LinkedList<String> params = new LinkedList<String>();
		params.add(this.getRoleName());
		return params;
	}

}
