package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

public class UsermanagementClient {

	private final String PREFIX = "riot/api/v1/";
	
	private final String GET_USERS = PREFIX + "users";
	private final String GET_ROLES = PREFIX + "roles"; 
	private final String GET_PERMISSIONS = PREFIX + "permissions";
	private final String GET_USER_ROLES =  "/roles";

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
	private final String DELETE_USER_ROLE =  "/roles/"; 
	
	private final LoginClient loginClient;
	
	public UsermanagementClient(LoginClient loginClient) {
		this.loginClient = loginClient;
	}
	
	public UserResponse updateUser(long userID, UserRequest userRequest) throws RequestException, JsonParseException, JsonMappingException, IllegalStateException, IOException {
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+ PUT_UPDATE_USER+userID, userRequest);
		UserResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(), UserResponse.class);
		return result;	
	}
	
	public RoleResponse updateRole(long roleID, Role role) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, RequestException, IOException{
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+PUT_UPDATE_ROLE+roleID, role);
		RoleResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),RoleResponse.class);
		return result;
	}
	
	public PermissionResponse updatePermission(long permissionID, Permission permission) throws JsonParseException, JsonMappingException, IllegalStateException, IOException, RequestException{
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+PUT_UPDATE_PERMISSION+permissionID, permission);
		PermissionResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),PermissionResponse.class);
		return result;
	}
	
	public int removeUserRole(long userID, long roleID) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl()+PREFIX+"users/"+userID+DELETE_USER_ROLE+roleID);
		int result = response.getStatusLine().getStatusCode();
		return result;
	}
	
	public int removeUser(long userID) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl()+DELETE_USER+userID);
		int result = response.getStatusLine().getStatusCode();
		return result;
	}
	
	public int removeRole(long roleID) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl()+DELETE_ROLE+roleID);
		int result = response.getStatusLine().getStatusCode();
		return result;
	}
	
	public int removePermission(long permissionID) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl()+DELETE_PERMISSION+permissionID);
		int result = response.getStatusLine().getStatusCode();
		return result;
	}
	
	public UserResponse addUser(UserRequest userRequest) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+PUT_ADD_USER, userRequest);
		UserResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),UserResponse.class);
		return result;
	}
	
	public RoleResponse addRole(Role role) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+PUT_ADD_ROLE, role);
		RoleResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),RoleResponse.class);
		return result;
	}
	
	public int addUserRole(long userID, long roleID) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+PREFIX+"users/"+userID+PUT_ADD_USER_ROLE+roleID, "");
		return response.getStatusLine().getStatusCode();
	}
	
	public PermissionResponse addPermission(Permission permission) throws RequestException, JsonParseException, JsonMappingException, IllegalStateException, IOException{
		HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl()+PUT_ADD_PERMISSION, permission);
		PermissionResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),PermissionResponse.class);
		return result;
	}
	
	public Collection<RoleResponse> getUserRoles(long userid) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_USER+userid+GET_USER_ROLES);
		Collection<RoleResponse> result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),new TypeReference<Collection<RoleResponse>>() { });
		return result;
	}
	
	public UserResponse getUser(long id) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_USER+id);
		UserResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),UserResponse.class);
		return result;
	}
	
	public RoleResponse getRole(long id) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_ROLE+id);
		RoleResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),RoleResponse.class);
		return result;
	}
	
	public PermissionResponse getPermission(long id) throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_PERMISSION+id);
		PermissionResponse result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),PermissionResponse.class);
		return result;
	}
	
	public Collection<UserResponse> getUsers() throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_USERS);
		Collection<UserResponse> result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),new TypeReference<Collection<UserResponse>>() { });
		return result;
	}
	
	public Collection<RoleResponse> getRoles() throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_ROLES);
		Collection<RoleResponse> result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),new TypeReference<Collection<RoleResponse>>() { });
		return result;
	}
	
	public Collection<PermissionResponse> getPermissions() throws RequestException, JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException{
		HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl()+GET_PERMISSIONS);
		Collection<PermissionResponse> result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),new TypeReference<Collection<PermissionResponse>>() { });
		return result;
	}

}
