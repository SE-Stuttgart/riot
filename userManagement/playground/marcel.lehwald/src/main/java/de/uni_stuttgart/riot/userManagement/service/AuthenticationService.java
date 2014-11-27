package de.uni_stuttgart.riot.userManagement.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import de.uni_stuttgart.riot.userManagement.resource.User;

@Path("/authenticate")
@Consumes("application/json")
@Produces("application/json")
public class AuthenticationService {

	@PUT
	public Response putUser(User user) {
		Subject u = SecurityUtils.getSubject();
		System.out.println(u.isAuthenticated());
		u.login(new UsernamePasswordToken("Yoda", "YodaPW"));
		System.out.println(u.isAuthenticated());
		return Response.ok().build();
	}

}
