package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.List;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.memorydao.MemoryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.UpdateRoleException;

public class RoleLogic {

    private DAO<Role> dao = new MemoryDAO<Role>();

    public void addRole(Role role) throws AddRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddRoleException(e.getMessage(),e);
        }
    }

    public void addRoleToUser(int id, Role role) throws AddRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new AddRoleException(e.getMessage(),e);
        }
    }

    public void deleteRole(int id) throws DeleteRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new DeleteRoleException(e.getMessage(),e);
        }
    }

    public void updateRole(int id, Role role) throws UpdateRoleException {
        try {
            // TODO
        } catch (Exception e) {
            throw new UpdateRoleException(e.getMessage(),e);
        }
    }

    public Role getRole(int id) throws GetRoleException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetRoleException(e.getMessage(),e);
        }
    }

    public List<Role> getAllRoles() throws GetAllRolesException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetAllRolesException(e.getMessage(),e);
        }
    }

    public List<Role> getAllRolesFromUser(int id) throws GetRoleException {
        try {
            // TODO
            return null;
        } catch (Exception e) {
            throw new GetRoleException(e.getMessage(),e);
        }
    }
}
