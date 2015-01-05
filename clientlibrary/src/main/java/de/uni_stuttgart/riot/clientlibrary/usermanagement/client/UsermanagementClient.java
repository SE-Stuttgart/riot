package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

public class UsermanagementClient {

	private final String PREFIX = "/riot/api/v1/";
	
	private final String GET_USERS = PREFIX + "users";
	private final String GET_ROLES = PREFIX + "roles"; 
	private final String GET_PERMISSIONS = PREFIX + "permissions";
	private final String GET_USER_ROLES = PREFIX + "/roles";

	private final String GET_USER = PREFIX + "users/"; 
	private final String GET_ROLE = PREFIX + "roles/"; 
	private final String GET_PERMISSION = PREFIX + "permissions/"; 
	
	private final String PUT_ADD_USER = PREFIX + "users";
	private final String PUT_ADD_ROLE = PREFIX + "roles"; 
	private final String PUT_ADD_PERMISSION = PREFIX + "permissions";
	private final String PUT_ADD_USER_ROLE =  "/roles/";

	
	private final String PUT_UPDATE_USER = PREFIX + "users/";
	private final String PUT_UPDATE_ROLE = PREFIX + "roles/"; 
	private final String PUT_UPDATE_PERMISSION = PREFIX + "permissions/";
	
	private final String DELETE_USER = PREFIX + "users/"; 
	private final String DELETE_ROLE = PREFIX + "roles/";
	private final String DELETE_PERMISSION = PREFIX + "permissions"; 
	private final String DELETE_USER_ROLE = PREFIX + "/roles/"; 
	
	private final LoginClient loginClient;
	
	public UsermanagementClient(LoginClient loginClient) {
		this.loginClient = loginClient;
	}
	
	public UserResponse updateUser(long userID, UserRequest userRequest) throws RequestException{
		Entity<UserRequest> entity = Entity.entity(userRequest, MediaType.APPLICATION_JSON);
		Response response = this.loginClient.put(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(PUT_UPDATE_USER+userID), entity);
		UserResponse result = response.readEntity(UserResponse.class);
		return result;	}
	
	public UserResponse updateRole(long roleID, Role role){
		return null;
	}
	
	public UserResponse updatePermission(long permissionID, Permission permission){
		return null;
	}
	
	public int removeUserRole(long userID, long roleID) throws RequestException{
		Response response = this.loginClient.delete(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(PREFIX+"users/"+userID+DELETE_USER_ROLE+roleID));
		int result = response.getStatus();
		return result;
	}
	
	public int removeUser(long userID) throws RequestException{
		Response response = this.loginClient.delete(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(DELETE_USER+userID));
		int result = response.getStatus();
		return result;
	}
	
	public int removeRole(long roleID) throws RequestException{
		Response response = this.loginClient.delete(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(DELETE_ROLE+roleID));
		int result = response.getStatus();
		return result;
	}
	
	public int removePermission(long permissionID) throws RequestException{
		Response response = this.loginClient.delete(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(DELETE_PERMISSION+permissionID));
		int result = response.getStatus();
		return result;
	}
	
	public UserResponse addUser(UserRequest userRequest) throws RequestException{
		Entity<UserRequest> entity = Entity.entity(userRequest, MediaType.APPLICATION_JSON);
		Response response = this.loginClient.put(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(PUT_ADD_USER), entity);
		UserResponse result = response.readEntity(UserResponse.class);
		return result;
	}
	
	public RoleResponse addRole(Role role) throws RequestException{
		Entity<Role> entity = Entity.entity(role, MediaType.APPLICATION_JSON);
		Response response = this.loginClient.put(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(PUT_ADD_ROLE), entity);
		RoleResponse result = response.readEntity(RoleResponse.class);
		return result;
	}
	
	public int addUserRole(long userID, long roleID) throws RequestException{
		Response response = this.loginClient.put(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(PREFIX+"users/"+userID+PUT_ADD_USER_ROLE+roleID), null);
		int result = response.getStatus();
		return result;
	}
	
	public PermissionResponse addRole(Permission permission) throws RequestException{
		Entity<Permission> entity = Entity.entity(permission, MediaType.APPLICATION_JSON);
		Response response = this.loginClient.put(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(PUT_ADD_PERMISSION), entity);
		PermissionResponse result = response.readEntity(PermissionResponse.class);
		return result;
	}
	
	public Collection<RoleResponse> getUserRoles(long userid) throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(userid+GET_USER_ROLES));
		Collection<RoleResponse> result = response.readEntity(new GenericType<Collection<RoleResponse>>(){});
		response.close();
		return result;
	}
	
	public UserResponse getUser(long id) throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_USER+id));
		UserResponse result = response.readEntity(UserResponse.class);
		response.close();
		return result;
	}
	
	public RoleResponse getRole(long id) throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_ROLE+id));
		RoleResponse result = response.readEntity(RoleResponse.class);
		response.close();
		return result;
	}
	
	public PermissionResponse getPermission(long id) throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_PERMISSION+id));
		PermissionResponse result = response.readEntity(PermissionResponse.class);
		response.close();
		return result;
	}
	
	public Collection<UserResponse> getUsers() throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_USERS));
		Collection<UserResponse> result = response.readEntity(new GenericType<Collection<UserResponse>>(){});
		response.close();
		return result;
	}
	
	public Collection<RoleResponse> getRoles() throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_ROLES));
		Collection<RoleResponse> result = response.readEntity(new GenericType<Collection<RoleResponse>>(){});
		response.close();
		return result;
	}
	
	public Collection<PermissionResponse> getPermissions() throws RequestException{
		Response response = this.loginClient.get(this.loginClient.getClient().target(this.loginClient.getServerUrl()).path(GET_PERMISSIONS));
		Collection<PermissionResponse> result = response.readEntity(new GenericType<Collection<PermissionResponse>>(){});
		response.close();
		return result;
	}

}
