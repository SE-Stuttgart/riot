package de.uni_stuttgart.riot.userManagement.service.logic;

import java.util.List;

import de.uni_stuttgart.riot.userManagement.dao.PermissionDao;
import de.uni_stuttgart.riot.userManagement.dao.inMemory.PermissionDaoInMemory;
import de.uni_stuttgart.riot.userManagement.resource.Permission;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.UpdatePermissionException;

public class PermissionLogic {

    private PermissionDao dao = new PermissionDaoInMemory();

    public void addPermission(Permission permission) throws AddPermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddPermissionException();
        }
    }

    public void deletePermission(int id) throws DeletePermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new DeletePermissionException();
        }
    }

    public void updatePermission(int id, Permission permission) throws UpdatePermissionException {
        try {
            // TODO
        } catch (Exception e) {
            throw new UpdatePermissionException();
        }
    }

    public Permission getPermission(int id) throws GetPermissionException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetPermissionException();
        }
    }

    public List<Permission> getAllPermissions() throws GetAllPermissionsException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetAllPermissionsException();
        }
    }

    public List<Permission> getAllPermissionsFromUser(int id) throws GetPermissionException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetPermissionException();
        }
    }
}
