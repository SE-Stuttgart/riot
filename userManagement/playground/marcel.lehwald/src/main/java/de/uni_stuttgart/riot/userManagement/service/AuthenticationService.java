package de.uni_stuttgart.riot.userManagement.service;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import de.uni_stuttgart.riot.userManagement.service.request.LoginRequest;
import de.uni_stuttgart.riot.userManagement.service.request.RefreshRequest;
import de.uni_stuttgart.riot.userManagement.service.response.AuthenticationResponse;


/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/auth/")
@Consumes("application/json")
@Produces("application/json")
public class AuthenticationService {

	@PUT
	@Path("/login/")
	public AuthenticationResponse login(LoginRequest request) {
		Subject u = SecurityUtils.getSubject();
		System.out.println(u.isAuthenticated());
		u.login(new UsernamePasswordToken("Yoda", "YodaPW"));
		System.out.println(u.isAuthenticated());

		return new AuthenticationResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString());
	}

	@PUT
	@Path("/refresh/")
	public AuthenticationResponse refresh(RefreshRequest request) {

		return new AuthenticationResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString());
	}
}
