package test;

import de.uni_stuttgart.riot.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.usermanagement.client.UsermanagementClient;

public class Starter {
    
    public static void main(String[] args) throws Exception {
    	LoginClient loginClient = new LoginClient("http://localhost:8080/", "Test");
    	UsermanagementClient client = new UsermanagementClient(loginClient);
    	loginClient.login("Yoda", "YodaPW");
        System.out.println(client.getUsers());
        loginClient.logout();
    }

}
