package de.uni_stuttgart.riot.userManagement.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.userManagement.dao.PremissionDao;
import de.uni_stuttgart.riot.userManagement.resource.Premission;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiError;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/premissions/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PremissionService {

	public static class PremissionServiceError
	{
		public static final ApiError UNIMPLEMENTED = new ApiError(1, "unimplemented..");
	}

	private PremissionDao dao;

	@GET
	public List<Premission> getPremissions() {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, PremissionServiceError.UNIMPLEMENTED);
	}

	@PUT
	public Response putPremission(Premission premission) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, PremissionServiceError.UNIMPLEMENTED);
	}

	@GET
	@Path("/{id}/")
	public List<Premission> getPremission(@PathParam("id") int id) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, PremissionServiceError.UNIMPLEMENTED);
	}

	@PUT
	@Path("/{id}/")
	public Response putPremission(@PathParam("id") int id, Premission premission) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, PremissionServiceError.UNIMPLEMENTED);
	}

	@DELETE
	@Path("/{id}/")
	public List<Premission> deletePremission(@PathParam("id") int id) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, PremissionServiceError.UNIMPLEMENTED);
	}
}
