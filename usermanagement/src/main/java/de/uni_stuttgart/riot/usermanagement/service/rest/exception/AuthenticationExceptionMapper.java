package de.uni_stuttgart.riot.usermanagement.service.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Maps {@link AuthenticationException} to 401 responses.
 * 
 * @author Marcel Lehwald
 */
@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(AuthenticationException exception) {
        return Response.status(Status.UNAUTHORIZED).entity("").build();
    }

}
