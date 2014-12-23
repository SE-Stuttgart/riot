package test;

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
import de.uni_stuttgart.riot.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.usermanagement.client.UsermanagementClient;

public class Starter {
    
    public static void main(String[] args) throws Exception {
    	LoginClient loginClient = new LoginClient("http://localhost:8080/", "Test");
    	UsermanagementClient client = new UsermanagementClient(loginClient);
    	loginClient.login("Vader", "VaderPW");
        System.out.println(client.getUsers());
        loginClient.logout();
    }

}
