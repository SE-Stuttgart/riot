package de.uni_stuttgart.riot.usermanagement.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 
 * 
 * @author Marcel Lehwald
 *
 */
@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(AuthenticationException exception) {
        return Response.status(Status.UNAUTHORIZED)
                       .entity("")
                       .build();
    }

}
