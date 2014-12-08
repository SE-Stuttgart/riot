package de.uni_stuttgart.riot.userManagement.service.logic;

import java.util.List;

import de.uni_stuttgart.riot.userManagement.dao.RoleDao;
import de.uni_stuttgart.riot.userManagement.dao.inMemory.RoleDaoInMemory;
import de.uni_stuttgart.riot.userManagement.resource.Role;
import de.uni_stuttgart.riot.userManagement.service.exception.role.AddRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.GetRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.UpdateRoleException;

public class RoleLogic {

    private RoleDao dao = new RoleDaoInMemory();

    public void addRole(Role role) throws AddRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddRoleException();
        }
    }

    public void addRoleToUser(int id, Role role) throws AddRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddRoleException();
        }
    }

    public void deleteRole(int id) throws DeleteRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new DeleteRoleException();
        }
    }

    public void updateRole(int id, Role role) throws UpdateRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new UpdateRoleException();
        }
    }

    public Role getRole(int id) throws GetRoleException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetRoleException();
        }
    }

    public List<Role> getAllRoles() throws GetAllRolesException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetAllRolesException();
        }
    }

    public List<Role> getAllRolesFromUser(int id) throws GetRoleException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetRoleException();
        }
    }
}
