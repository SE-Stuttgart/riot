package de.uni_stuttgart.riot.usermanagement.client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

public class Starter {
    
    public static void main(String[] args) throws Exception {
        // Login into Usermanagement
        UserManagementClient client = new UserManagementClient("http://localhost:8080/riot.usermanagement/", "Test");
        client.login("Yoda", "YodaPW");
        
        // Using a secured Webservice without the need of giving authorization information.  
        Client c = ClientBuilder.newClient();
        WebTarget userstarget = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/users/22"); 
        WebTarget rolestarget = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/roles"); 
        WebTarget permissionstarget = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/permissions"); 
        
        System.out.println(userstarget.getUri().toString());
        // Printing the response
        Response rUsers = client.get(userstarget, MediaType.APPLICATION_JSON);
        UserResponse usersResult = rUsers.readEntity(UserResponse.class);
        System.out.println(usersResult);
        client.logout();
    }

}
