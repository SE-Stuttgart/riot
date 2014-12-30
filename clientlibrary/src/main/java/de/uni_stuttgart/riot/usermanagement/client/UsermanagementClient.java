package de.uni_stuttgart.riot.usermanagement.client;

import java.util.Collection;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

public class UsermanagementClient {

	private final String PREFIX = "/riot/api/v1/";
	
	private final String GET_USERS = PREFIX + "users";
	private final String GET_ROLES = PREFIX + "roles"; 
	private final String GET_PERMISSIONS = PREFIX + "permissions";
	private final String GET_USER_ROLES = PREFIX + "/{userID}/roles";

	private final String GET_USER = PREFIX + "users/{userID}"; 
	private final String GET_ROLE = PREFIX + "roles/{roleID}"; 
	private final String GET_PERMISSION = PREFIX + "permissions/{permissionID}"; 
	
	private final String PUT_ADD_USER = PREFIX;
	private final String PUT_ADD_ROLE = PREFIX; 
	private final String PUT_ADD_PERMISSION = PREFIX;
	private final String PUT_ADD_USER_ROLE = PREFIX + "/{userID}/roles/{roleID}";

	
	private final String PUT_UPDATE_USER = PREFIX;
	private final String PUT_UPDATE_ROLE = PREFIX; 
	private final String PUT_UPDATE_PERMISSION = PREFIX;
	
	private final String DELETE_USER = PREFIX; 
	private final String DELETE_ROLE = PREFIX; 
	private final String DELETE_PERMISSION = PREFIX; 
	private final String DELETE_REMOVE_USER_ROLE = PREFIX + "/{userID}/roles/{roleID}"; 
	
	private final LoginClient loginClient;
	
	public UsermanagementClient(LoginClient loginClient) {
		this.loginClient = loginClient;
	}
	
	public Collection<UserResponse> getUsers() throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_USERS));
		Collection<UserResponse> result = response.readEntity(new GenericType<Collection<UserResponse>>(){});
		return result;
	}

}
