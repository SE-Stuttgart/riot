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

import de.uni_stuttgart.riot.userManagement.dao.RoleDao;
import de.uni_stuttgart.riot.userManagement.resource.Role;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiError;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/roles/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleService {

	public static class RoleServiceError
	{
		public static final ApiError UNIMPLEMENTED = new ApiError(1, "unimplemented..");
	}

	private RoleDao dao;

	@GET
	public List<Role> getRoles() {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, RoleServiceError.UNIMPLEMENTED);
	}

	@PUT
	public Response putRole(Role role) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, RoleServiceError.UNIMPLEMENTED);
	}

	@GET
	@Path("/{id}/")
	public List<Role> getRole(@PathParam("id") int id) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, RoleServiceError.UNIMPLEMENTED);
	}

	@PUT
	@Path("/{id}/")
	public Response putRole(@PathParam("id") int id, Role role) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, RoleServiceError.UNIMPLEMENTED);
	}

	@DELETE
	@Path("/{id}/")
	public List<Role> deleteRole(@PathParam("id") int id) {
		throw new ApiErrorResponse(Response.Status.BAD_REQUEST, RoleServiceError.UNIMPLEMENTED);
	}

}
