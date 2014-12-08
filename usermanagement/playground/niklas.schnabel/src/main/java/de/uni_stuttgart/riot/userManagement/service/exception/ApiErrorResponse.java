package de.uni_stuttgart.riot.userManagement.service.exception;

import javax.json.Json;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * This is the error exception which can be thrown from the REST API to respond with a proper error message. All API requests 
 * which raises an exception and need to response with an appropriate error message should throw this exception. The exception 
 * will create a JSON response based on the errorCode and errorMessage to provide the user with information on the cause of the error.
 * 
 * @author Marcel Lehwald
 *
 */
public class ApiErrorResponse extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public ApiErrorResponse(Status httpStatus, ApiError error) {
		super(Response.status(httpStatus)
				  	  .entity(Json.createObjectBuilder()
				  			  	  .add("errorCode", error.getErrorCode())
				  			  	  .add("errorMessage", error.getErrorMessage())
				  			  	  .build())
				  	  .type(MediaType.APPLICATION_JSON)
				  	  .build());
	}
}