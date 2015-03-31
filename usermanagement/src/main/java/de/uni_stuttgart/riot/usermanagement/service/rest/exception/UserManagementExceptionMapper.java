package de.uni_stuttgart.riot.usermanagement.service.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * The UserManagementExceptionMapper class will handle any user management exceptions thrown inside a REST service call and return a proper
 * response message. This automatic mapping makes the catch of any user management exceptions inside the service call unnecessary.
 * 
 * @author Marcel Lehwald
 *
 */
@Provider
public class UserManagementExceptionMapper implements ExceptionMapper<UserManagementException> {

    @Override
    public Response toResponse(UserManagementException exception) {
        return Response.status(Status.BAD_REQUEST).entity(exception).type(MediaType.APPLICATION_JSON).build();
    }

}
