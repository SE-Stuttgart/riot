package de.uni_stuttgart.riot.userManagement.service;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import de.uni_stuttgart.riot.userManagement.security.TokenUtil;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.userManagement.service.request.LoginRequest;
import de.uni_stuttgart.riot.userManagement.service.request.RefreshRequest;
import de.uni_stuttgart.riot.userManagement.service.response.AuthenticationResponse;
import de.uni_stuttgart.riot.userManagement.service.response.LogoutResponse;


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

		String authToken = TokenUtil.generateToken();
		String refreshToken = TokenUtil.generateToken();
		
		//TODO save tokens in db

		return new AuthenticationResponse(authToken, refreshToken);
	}

	@PUT
	@Path("/refresh/")
	public AuthenticationResponse refresh(RefreshRequest request) {
		String authToken = TokenUtil.generateToken();
		String refreshToken = TokenUtil.generateToken();
		
		//TODO save tokens in db

		return new AuthenticationResponse(authToken, refreshToken);
	}

	@GET
	@Path("/logout/")
	public LogoutResponse logout() {
		//TODO
		//return new LogoutResponse();
		return null;
	}
}
