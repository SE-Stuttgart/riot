package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.usermanagement.service.rest.exception.UserManagementExceptionMapper;

/**
 * The permissions service will handle any access (create, read, update, delete) to the permissions.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionService extends BaseResource<Permission>{

    public PermissionService() throws SQLException, NamingException {
        super(new PermissionSqlQueryDAO(ConnectionMgr.openConnection(), false));
    }

    UserManagementFacade facade = UserManagementFacade.getInstance();
}
