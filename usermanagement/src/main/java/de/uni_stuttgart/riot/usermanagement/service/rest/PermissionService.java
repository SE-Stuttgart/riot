package de.uni_stuttgart.riot.usermanagement.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;

/**
 * The permissions service will handle any access (create, read, update, delete) to the permissions.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class PermissionService extends BaseResource<Permission> {

    /**
     * Constructor.
     */
    public PermissionService() {
        super(new PermissionSqlQueryDAO());
    }

    @Override
    public void init(Permission storable) {
    }

}
