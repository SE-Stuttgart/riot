package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The permissions service will handle any access (create, read, update, delete) to the permissions.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionService extends BaseResource<Permission> {

    UserManagementFacade facade = UserManagementFacade.getInstance();

    /**
     * Const.
     * 
     * @throws SQLException .
     * @throws NamingException .
     */
    public PermissionService() throws SQLException, NamingException {
        super(new PermissionSqlQueryDAO(ConnectionMgr.openConnection(), false));
    }

    @Override
    public void init(Permission storable) {
    }
}
