package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.List;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.memorydao.MemoryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;

public class PermissionLogic {

    private DAO<Permission> dao = new MemoryDAO<Permission>();

    public void addPermission(Permission permission) throws AddPermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddPermissionException(e);
        }
    }

    public void deletePermission(int id) throws DeletePermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new DeletePermissionException(e);
        }
    }

    public void updatePermission(int id, Permission permission) throws UpdatePermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new UpdatePermissionException(e);
        }
    }

    public Permission getPermission(int id) throws GetPermissionException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetPermissionException(e);
        }
    }

    public List<Permission> getAllPermissions() throws GetAllPermissionsException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetAllPermissionsException(e);
        }
    }

    public List<Permission> getAllPermissionsFromUser(int id) throws GetPermissionException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetPermissionException(e);
        }
    }
}
