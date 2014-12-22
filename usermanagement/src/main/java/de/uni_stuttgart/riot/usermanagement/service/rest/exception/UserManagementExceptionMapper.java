package de.uni_stuttgart.riot.usermanagement.service.rest.exception;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;

/**
 * The UserManagementExceptionMapper class will handle any user management exceptions thrown inside a REST service call and return 
 * a proper response message. This automatic mapping makes the catch of any user management exceptions inside the service call
 * unnecessary.
 * 
 * @author Marcel Lehwald
 *
 */
@Provider
public class UserManagementExceptionMapper implements ExceptionMapper<LogicException> {

    @Override
    public Response toResponse(LogicException exception) {
        exception.printStackTrace();
        return Response.status(Status.BAD_REQUEST)                  //TODO status code
                       .entity(Json.createObjectBuilder()
                                   .add("errorCode", exception.getErrorCode())
                                   .add("errorMessage", exception.getEndUserMessage())
                                   .build())
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

}
