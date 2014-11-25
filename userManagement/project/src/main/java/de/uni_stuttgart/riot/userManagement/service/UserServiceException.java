package de.uni_stuttgart.riot.userManagement.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UserServiceException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public UserServiceException(String message, Status status) {
		super(Response.status(status)
				  	  .entity(message)
				  	  .build());
	}

}
