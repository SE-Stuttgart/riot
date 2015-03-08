package de.uni_stuttgart.riot.usermanagement.service.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * The UserManagementExceptionMapper class will handle any user management exceptions thrown inside a REST service call and return a proper
 * response message. This automatic mapping makes the catch of any user management exceptions inside the service call unnecessary.
 * 
 * @author Marcel Lehwald
 *
 */
@Provider
public class UserManagementExceptionMapper implements ExceptionMapper<LogicException> {

    // TODO This should not be here. Refactor this class in general.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Response toResponse(LogicException exception) {
        // TODO Return proper status codes, especially when user logs in with wrong credentials, a BAD REQUEST is not the correct answer.
        // TODO Log security-relevant incidents here.
        return Response.status(Status.BAD_REQUEST) // TODO status code
                .entity(OBJECT_MAPPER.createObjectNode().put("errorCode", exception.getErrorCode()).put("errorMessage", exception.getEndUserMessage()).toString()).type(MediaType.APPLICATION_JSON).build();
    }

}
