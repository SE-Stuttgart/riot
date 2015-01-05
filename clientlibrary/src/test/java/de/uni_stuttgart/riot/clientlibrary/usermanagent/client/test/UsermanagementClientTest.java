package de.uni_stuttgart.riot.clientlibrary.usermanagent.client.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;

public class UsermanagementClientTest {
	
	LoginClient loginClient = new LoginClient("http://localhost:8080/", "Test");
	UsermanagementClient client = new UsermanagementClient(loginClient);
	
	@Test
	public void testremoveUser() {
		
	}

}
