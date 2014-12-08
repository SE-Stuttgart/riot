package de.uni_stuttgart.riot.userManagement.security;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.shiro.authz.AuthorizationException;

/**
 * 
 * @author Marcel Lehwald
 *
 */
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {

	@Override
	public Response toResponse(AuthorizationException exception) {
        return Response.status(Status.FORBIDDEN)
        			   .entity("")
        			   .build();
	}

}
