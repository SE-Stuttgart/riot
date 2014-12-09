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
        WebTarget target = c.target("http://localhost:8080/riot.usermanagement/").path("api/v1/users"); 
       
        // Printing the response
        Response r = client.get(target, MediaType.APPLICATION_JSON);
        String result = r.readEntity(String.class);
        System.out.println(result);
        
        // Test after Token is invalid
        Response r2 = client.get(target, MediaType.APPLICATION_JSON);
        String result2 = r2.readEntity(String.class);
        System.out.println(result2);
    }

}
