package test;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;

public class Starter {

	public static void main(String[] args) throws Exception {
		LoginClient loginClient = new LoginClient("http://localhost:8080/", "Test");
		UsermanagementClient client = new UsermanagementClient(loginClient);
		loginClient.login("Yoda", "YodaPW");
		UserRequest uR = new UserRequest();
		uR.setPassword("TEST");
		uR.setUsername("TestUser2");
		System.out.println(client.updateUser(5, uR));
		loginClient.logout();
	}

}
