package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.RoleLogic;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

public class RoleLogicTest extends LogicTestBase {

    private RoleLogic rl = new RoleLogic();

    /*
     * Add role tests
     */

    @Test
    public void testAddRoleValidRole() throws Exception {
        Role role = new Role("testRole");
        rl.addRole(role);

        Role role2 = rl.getRole(role.getId());
        assertEquals(role, role2);
    }

    @Test(expected = UserManagementException.class)
    public void testAddRoleNull() throws UserManagementException {
        rl.addRole(null);
    }

    @Test(expected = UserManagementException.class)
    public void testAddRoleEmptyString() throws UserManagementException {
        rl.addRole(new Role(""));
    }

    /*
     * Delete role tests
     */

    @Test(expected = UserManagementException.class)
    public void testDeleteRoleValidId() throws Exception {
        rl.deleteRole(1L);
        rl.getRole(1L);
    }

    @Test(expected = UserManagementException.class)
    public void testDeleteRoleInvalidId() throws UserManagementException {
        rl.deleteRole(42L);
    }

    @Test(expected = UserManagementException.class)
    public void testDeleteRoleNull() throws UserManagementException {
        rl.deleteRole(null);
    }

    /*
     * Update role tests
     */

    @Test
    public void testUpdateRoleValidIdAndValidRole() throws Exception {
        rl.updateRole(new Role(1L, "newRole"));
        Role role = rl.getRole(1L);
        assertEquals("newRole", role.getRoleName());
    }

    @Test(expected = UserManagementException.class)
    public void testUpdateRoleInvalidIdAndValidRole() throws Exception {
        rl.updateRole(new Role(42L, "newRole"));
    }

    @Test(expected = UserManagementException.class)
    public void testUpdateRoleValidIdAndInvalidRole() throws Exception {
        rl.updateRole(new Role(1L, ""));
    }

    @Test(expected = UserManagementException.class)
    public void testUpdateRoleInvalidIdAndInvalidRole() throws Exception {
        rl.updateRole(new Role(42L, ""));
    }

    @Test(expected = UserManagementException.class)
    public void testUpdateRoleNull() throws Exception {
        rl.updateRole(new Role(null, null));
    }

    @Test(expected = UserManagementException.class)
    public void testUpdateRoleNullId() throws Exception {
        rl.updateRole(new Role(null, "test"));
    }

    @Test(expected = UserManagementException.class)
    public void testUpdateRoleNullRole() throws Exception {
        rl.updateRole(new Role(1L, null));
    }

    /*
     * Get role tests
     */

    @Test
    public void testGetRoleValid() throws Exception {
        Role expect = new Role(1L, "Master");
        Role role = rl.getRole(1L);
        assertEquals(expect, role);
    }

    @Test(expected = UserManagementException.class)
    public void testGetRoleInvalid() throws Exception {
        rl.getRole(42L);
    }

    @Test(expected = UserManagementException.class)
    public void testGetRoleNull() throws Exception {
        rl.getRole(null);
    }

    /*
     * Get all roles tests
     */

    @Test
    public void testGetAllRolesValid() throws Exception {
        Collection<Role> allRoles = rl.getAllRoles();
        assertEquals(4, allRoles.size());
    }

    @Test
    @TestData({ "/schema/schema_usermanagement.sql" })
    public void testGetAllRolesInvalid() throws Exception {
        Collection<Role> allRoles = rl.getAllRoles();
        assertEquals(0, allRoles.size());
    }

    /*
     * Get all permissions from role tests
     */

    @Test
    public void testGetAllPermissionsFromRoleValid() throws Exception {
        Collection<Permission> allPermissionsFromRole = rl.getAllPermissionsFromRole(1L);
        assertEquals(1, allPermissionsFromRole.size());
    }

    @Test
    public void testGetAllPermissionsFromRoleInvalid() throws Exception {
        assertEquals(0, rl.getAllPermissionsFromRole(42L).size());
    }

    @Test(expected = UserManagementException.class)
    public void testGetAllPermissionsFromRoleNull() throws Exception {
        rl.getAllPermissionsFromRole(null);
    }

    /*
     * Add permission to role test
     */

    @Test
    public void testAddPermissiontToRoleValid() throws Exception {
        rl.addPermissionToRole(1L, 2L);
        Collection<Permission> allPermissionsFromRole = rl.getAllPermissionsFromRole(1L);
        assertEquals(2, allPermissionsFromRole.size());
    }

    @Test(expected = UserManagementException.class)
    public void testAddPermissiontToRoleDoubleEntry() throws Exception {
        rl.addPermissionToRole(1L, 2L);
        rl.addPermissionToRole(1L, 2L);
    }

    @Test(expected = UserManagementException.class)
    public void testAddPermissiontToRoleNull() throws Exception {
        rl.addPermissionToRole(null, null);
    }

    @Test(expected = UserManagementException.class)
    public void testAddPermissiontToRoleNullRoleId() throws Exception {
        rl.addPermissionToRole(1L, null);
    }

    @Test(expected = UserManagementException.class)
    public void testAddPermissiontToRoleNullPermissionId() throws Exception {
        rl.addPermissionToRole(null, 1L);
    }

    @Test(expected = UserManagementException.class)
    public void testAddPermissiontToRoleInvalidRoleId() throws Exception {
        rl.addPermissionToRole(42L, 1L);
    }

    @Test(expected = UserManagementException.class)
    public void testAddPermissiontToRoleInvalidPermissionId() throws Exception {
        rl.addPermissionToRole(1L, 42L);
    }

    /*
     * Remove permission from role tests
     */

    @Test
    public void testRemovePermissionFromRoleValid() throws Exception {
        rl.removePermissionFromRole(1L, 1L);
        assertEquals(0, rl.getAllPermissionsFromRole(1L).size());
    }

    @Test(expected = UserManagementException.class)
    public void testRemovePermissionFromRoleInvalidRoleId() throws Exception {
        rl.removePermissionFromRole(42L, 1L);
        assertEquals(1, rl.getAllPermissionsFromRole(1L).size());
    }

    @Test(expected = UserManagementException.class)
    public void testRemovePermissionFromRoleInvalidPermissionId() throws Exception {
        rl.removePermissionFromRole(1L, 42L);
        assertEquals(1, rl.getAllPermissionsFromRole(1L).size());
    }

    @Test(expected = UserManagementException.class)
    public void testRemovePermissionFromRoleDoubleRemove() throws Exception {
        rl.removePermissionFromRole(1L, 1L);
        rl.removePermissionFromRole(1L, 1L);
    }

    @Test(expected = UserManagementException.class)
    public void testRemovePermissionFromRoleNull() throws Exception {
        rl.removePermissionFromRole(null, null);
    }

    @Test(expected = UserManagementException.class)
    public void testRemovePermissionFromRoleNullRoleId() throws Exception {
        rl.removePermissionFromRole(null, 1L);
    }

    @Test(expected = UserManagementException.class)
    public void testRemovePermissionFromRoleNullPermissionId() throws Exception {
        rl.removePermissionFromRole(1L, null);
    }

}
