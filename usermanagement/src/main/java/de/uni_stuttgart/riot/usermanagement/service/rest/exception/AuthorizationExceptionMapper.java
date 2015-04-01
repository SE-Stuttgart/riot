package de.uni_stuttgart.riot.usermanagement.service.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

/**
 * The AuthorizationExceptionMapper class will handle any authorization exceptions thrown inside a REST service call and return an empty
 * response with HTTP 401/403 error code. This automatic mapping makes the catch of any authorization exception inside the service call
 * unnecessary.
 * 
 * @author Marcel Lehwald
 */
@Provider
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {

    @Override
    public Response toResponse(AuthorizationException exception) {
        if (exception instanceof UnauthenticatedException) {
            return Response.status(Status.UNAUTHORIZED).entity("").build();
        } else {
            return Response.status(Status.FORBIDDEN).entity("").build();
        }
    }

}
