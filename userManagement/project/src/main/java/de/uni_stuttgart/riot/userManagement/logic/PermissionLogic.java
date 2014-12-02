package de.uni_stuttgart.riot.userManagement.logic;

import java.util.List;

import de.uni_stuttgart.riot.userManagement.data.DAO;
import de.uni_stuttgart.riot.userManagement.data.memorydao.MemoryDAO;
import de.uni_stuttgart.riot.userManagement.data.storable.Permission;
import de.uni_stuttgart.riot.userManagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.userManagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.userManagement.logic.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.userManagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.userManagement.logic.exception.permission.UpdatePermissionException;

public class PermissionLogic {

    private DAO<Permission> dao = new MemoryDAO<Permission>();

    public void addPermission(Permission permission) throws AddPermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddPermissionException(e.getMessage(),e);
        }
    }

    public void deletePermission(int id) throws DeletePermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new DeletePermissionException(e.getMessage(),e);
        }
    }

    public void updatePermission(int id, Permission permission) throws UpdatePermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new UpdatePermissionException(e.getMessage(),e);
        }
    }

    public Permission getPermission(int id) throws GetPermissionException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetPermissionException(e.getMessage(),e);
        }
    }

    public List<Permission> getAllPermissions() throws GetAllPermissionsException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetAllPermissionsException(e.getMessage(),e);
        }
    }

    public List<Permission> getAllPermissionsFromUser(int id) throws GetPermissionException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetPermissionException(e.getMessage(),e);
        }
    }
}
