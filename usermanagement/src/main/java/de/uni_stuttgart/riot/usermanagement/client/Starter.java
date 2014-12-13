package de.uni_stuttgart.riot.usermanagement.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Starter {

    public static void main(String[] args) {
        // Login into Usermanagement
        UserManagementClient client = new UserManagementClient("http://localhost:8080/riot.usermanagement/", "Test");
        client.login("Yoda", "YodaPW");
        
        // Using a secured Webservice without the need of giving authorization information.  
        Client c = ClientBuilder.newClient();
        WebTarget userstarget = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/users"); 
        WebTarget rolestarget = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/roles"); 
        WebTarget permissionstarget = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/permissions"); 

        
        
        // Printing the response
        Response rUsers = client.get(userstarget, MediaType.APPLICATION_JSON);
        String usersResult = rUsers.readEntity(String.class);
        System.out.println(usersResult);
        
        // Printing the response
        Response rRoles = client.get(rolestarget, MediaType.APPLICATION_JSON);
        String rolesResult = rRoles.readEntity(String.class);
        System.out.println(rolesResult);
        
        // Printing the response
        Response rPermissions = client.get(permissionstarget, MediaType.APPLICATION_JSON);
        String permissionsResult = rPermissions.readEntity(String.class);
        System.out.println(permissionsResult);
        client.logout();
    }

}
