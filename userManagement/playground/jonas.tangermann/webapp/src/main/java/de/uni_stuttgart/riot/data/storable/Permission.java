package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class Permission implements Storable{
	
	private final Long permissionID;
	private String permissionValue;
	
	public Permission(Long permissionID, String permissionValue) {
		this.permissionID = permissionID;
		this.setPermissionValue(permissionValue);
	}
	
	@Override
	public long getID() {
		return this.permissionID;
	}

	@Override
	public Collection<String> getSearchParam() {
		LinkedList<String> params = new LinkedList<String>();
		params.add(this.getPermissionValue());
		return params;
	}

	public String getPermissionValue() {
		return permissionValue;
	}

	public void setPermissionValue(String permissionValue) {
		this.permissionValue = permissionValue;
	}

}
