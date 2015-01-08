package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.usermanagement.logic.RoleLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddPermissionToRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.RemovePermissionFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.UpdateRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

public class RoleLogicTest extends LogicTestBase {

    private RoleLogic rl = new RoleLogic();

    /*
     * Add role tests
     */

    @Test
    public void testAddRole_validRole() throws Exception {
        Role role = new Role("testRole");
        rl.addRole(role);

        Role role2 = rl.getRole(role.getId());
        assertEquals(role, role2);
    }

    @Test(expected = AddRoleException.class)
    public void testAddRole_null() throws AddRoleException {
        rl.addRole(null);
    }

    @Test(expected = AddRoleException.class)
    public void testAddRole_EmptyString() throws AddRoleException {
        rl.addRole(new Role(""));
    }

    /*
     * Delete role tests
     */

    @Test(expected = GetRoleException.class)
    public void testDeleteRole_validId() throws Exception {
        rl.deleteRole(1L);
        rl.getRole(1L);
    }

    @Test(expected = DeleteRoleException.class)
    public void testDeleteRole_invalidId() throws DeleteRoleException {
        rl.deleteRole(42L);
    }

    @Test(expected = DeleteRoleException.class)
    public void testDeleteRole_null() throws DeleteRoleException {
        rl.deleteRole(null);
    }

    /*
     * Update role tests
     */

    @Test
    public void testUpdateRole_validIdAndValidRole() throws Exception {
        rl.updateRole(new Role(1L, "newRole"));
        Role role = rl.getRole(1L);
        assertEquals("newRole", role.getRoleName());
    }

    @Test(expected = UpdateRoleException.class)
    public void testUpdateRole_invalidIdAndValidRole() throws Exception {
        rl.updateRole(new Role(42L, "newRole"));
    }

    @Test(expected = UpdateRoleException.class)
    public void testUpdateRole_validIdAndInvalidRole() throws Exception {
        rl.updateRole(new Role(1L, ""));
    }

    @Test(expected = UpdateRoleException.class)
    public void testUpdateRole_invalidIdAndInvalidRole() throws Exception {
        rl.updateRole(new Role(42L, ""));
    }

    @Test(expected = UpdateRoleException.class)
    public void testUpdateRole_null() throws Exception {
        rl.updateRole(new Role(null, null));
    }

    @Test(expected = UpdateRoleException.class)
    public void testUpdateRole_nullId() throws Exception {
        rl.updateRole(new Role(null, "test"));
    }

    @Test(expected = UpdateRoleException.class)
    public void testUpdateRole_nullRole() throws Exception {
        rl.updateRole(new Role(1L, null));
    }

    /*
     * Get role tests
     */

    @Test
    public void testGetRole_valid() throws Exception {
        Role expect = new Role(1L, "Master");
        Role role = rl.getRole(1L);
        assertEquals(expect, role);
    }

    @Test(expected = GetRoleException.class)
    public void testGetRole_invalid() throws Exception {
        rl.getRole(42L);
    }

    @Test(expected = GetRoleException.class)
    public void testGetRole_null() throws Exception {
        rl.getRole(null);
    }

    /*
     * Get all roles tests
     */

    @Test
    public void testGetAllRoles_valid() throws Exception {
        Collection<Role> allRoles = rl.getAllRoles();
        assertEquals(4, allRoles.size());
    }

    @Test
    public void testGetAllRoles_invalid() throws Exception {
        emptyDatabaseTables();
        Collection<Role> allRoles = rl.getAllRoles();
        assertEquals(0, allRoles.size());
    }

    /*
     * Get all permissions from role tests
     */

    @Test
    public void testGetAllPermissionsFromRole_valid() throws Exception {
        Collection<Permission> allPermissionsFromRole = rl.getAllPermissionsFromRole(1L);
        assertEquals(1, allPermissionsFromRole.size());
    }

    @Test(expected = GetPermissionsFromRoleException.class)
    public void testGetAllPermissionsFromRole_invalid() throws Exception {
        rl.getAllPermissionsFromRole(42L);
    }

    @Test(expected = GetPermissionsFromRoleException.class)
    public void testGetAllPermissionsFromRole_null() throws Exception {
        rl.getAllPermissionsFromRole(null);
    }

    /*
     * Add permission to role test
     */

    @Test
    public void testAddPermissiontToRole_valid() throws Exception {
        rl.addPermissionToRole(1L, 2L);
        Collection<Permission> allPermissionsFromRole = rl.getAllPermissionsFromRole(1L);
        assertEquals(2, allPermissionsFromRole.size());
    }

    @Test(expected = AddPermissionToRoleException.class)
    public void testAddPermissiontToRole_doubleEntry() throws Exception {
        rl.addPermissionToRole(1L, 2L);
        rl.addPermissionToRole(1L, 2L);
    }

    @Test(expected = AddPermissionToRoleException.class)
    public void testAddPermissiontToRole_null() throws Exception {
        rl.addPermissionToRole(null, null);
    }

    @Test(expected = AddPermissionToRoleException.class)
    public void testAddPermissiontToRole_nullRoleId() throws Exception {
        rl.addPermissionToRole(1L, null);
    }

    @Test(expected = AddPermissionToRoleException.class)
    public void testAddPermissiontToRole_nullPermissionId() throws Exception {
        rl.addPermissionToRole(null, 1L);
    }

    @Test(expected = AddPermissionToRoleException.class)
    public void testAddPermissiontToRole_invalidRoleId() throws Exception {
        rl.addPermissionToRole(42L, 1L);
    }

    @Test(expected = AddPermissionToRoleException.class)
    public void testAddPermissiontToRole_invalidPermissionId() throws Exception {
        rl.addPermissionToRole(1L, 42L);
    }

    /*
     * Remove permission from role tests
     */

    @Test
    public void testRemovePermissionFromRole_valid() throws Exception {
        rl.removePermissionFromRole(1L, 1L);

        try {
            rl.getAllPermissionsFromRole(1L).size();
        } catch (GetPermissionsFromRoleException e) {
            return;
        }
        fail("Should not reach this point, because the specified role should not have permissions anymore.");
    }

    @Test(expected = RemovePermissionFromRoleException.class)
    public void testRemovePermissionFromRole_invalidRoleId() throws Exception {
        rl.removePermissionFromRole(42L, 1L);
        assertEquals(1, rl.getAllPermissionsFromRole(1L).size());
    }

    @Test(expected = RemovePermissionFromRoleException.class)
    public void testRemovePermissionFromRole_invalidPermissionId() throws Exception {
        rl.removePermissionFromRole(1L, 42L);
        assertEquals(1, rl.getAllPermissionsFromRole(1L).size());
    }

    @Test(expected = RemovePermissionFromRoleException.class)
    public void testRemovePermissionFromRole_doubleRemove() throws Exception {
        rl.removePermissionFromRole(1L, 1L);
        rl.removePermissionFromRole(1L, 1L);
    }

    @Test(expected = RemovePermissionFromRoleException.class)
    public void testRemovePermissionFromRole_null() throws Exception {
        rl.removePermissionFromRole(null, null);
    }

    @Test(expected = RemovePermissionFromRoleException.class)
    public void testRemovePermissionFromRole_nullRoleId() throws Exception {
        rl.removePermissionFromRole(null, 1L);
    }

    @Test(expected = RemovePermissionFromRoleException.class)
    public void testRemovePermissionFromRole_nullPermissionId() throws Exception {
        rl.removePermissionFromRole(1L, null);
    }

}
