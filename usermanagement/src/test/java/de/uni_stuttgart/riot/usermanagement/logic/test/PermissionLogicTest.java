package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

public class PermissionLogicTest extends LogicTestBase {

    private PermissionLogic pl;

    @Before
    public void initLogic() {
        pl = new PermissionLogic();
    }

    @Test
    public void testAddPermission() throws UserManagementException {
        Permission permission = new Permission("JUnitTestPermission");
        pl.addPermission(permission);
        Permission permissionRead = pl.getPermission("JUnitTestPermission");
        assertEquals(permission, permissionRead);
    }

    @Test(expected = AddPermissionException.class)
    public void shoudFailOnNullAdd() throws UserManagementException {
        pl.addPermission(null);
    }

    @Test(expected = AddPermissionException.class)
    public void shoudFailOnEmptyAdd() throws UserManagementException {
        pl.addPermission(new Permission(null));
    }

    @Test
    public void shouldDeletePermission() throws DeletePermissionException {
        pl.deletePermission(1L);
    }

    @Test(expected = DeletePermissionException.class)
    public void shouldFailOnDoubleDelete() throws DeletePermissionException {
        pl.deletePermission(1L);
        pl.deletePermission(1L); // Intentional
    }

    @Test(expected = GetPermissionException.class)
    public void shouldFailOnGettingDeletedPermission() throws UserManagementException {
        pl.deletePermission(1L);
        pl.getPermission(1L);
    }

    @Test
    public void testUpdatePermission() throws UserManagementException {
        Permission permission = pl.getPermission(1L);
        String permissionValue = permission.getPermissionValue();

        // update the permission with a new value
        permission.setPermissionValue("newPermissionValue");
        pl.updatePermission(permission.getId(), permission);

        assertNotEquals(permission.getPermissionValue(), permissionValue);

        // get the updated permission
        Permission permissionUpdated = pl.getPermission(permission.getId());

        assertNotEquals(permissionUpdated.getPermissionValue(), permissionValue);
        assertEquals(permissionUpdated, permission);
    }

    @Test(expected = UpdatePermissionException.class)
    public void shouldThrowOnNullUpdate() throws UpdatePermissionException {
        pl.updatePermission(1L, null);
    }

    @Test(expected = UpdatePermissionException.class)
    public void shouldThrowOnUpdateNonExisting() throws UpdatePermissionException {
        pl.updatePermission(null, new Permission("testValue"));
    }

    @Test
    public void shouldGetPermission() throws GetPermissionException {
        Permission p1 = pl.getPermission(1L);
        Permission p2 = pl.getPermission(1L);
        assertEquals(p1, p2);
    }

    @Test(expected = GetPermissionException.class)
    public void shouldFailGettingPermissionAfterDelete() throws UserManagementException {
        pl.deletePermission(1L);
        pl.getPermission(1L);

    }

    @Test(expected = GetPermissionException.class)
    public void shouldFailGettingNonexistentPermission() throws GetPermissionException {
        pl.getPermission(Long.MAX_VALUE);
    }

    @Test(expected = GetPermissionException.class)
    public void shouldFailGettingNonexistentPermission2() throws GetPermissionException {
        pl.getPermission(Long.MIN_VALUE);
    }

    @Test
    public void testGetAllPermissions() throws UserManagementException {
        Collection<Permission> allPermissions = pl.getAllPermissions();

        assertEquals(7, allPermissions.size());

        Permission p1 = pl.getPermission(1L);
        assertEquals(allPermissions.iterator().next(), p1);

        for (Permission permission : allPermissions) {
            pl.deletePermission(permission.getId());
        }

        Collection<Permission> allPermissions2 = pl.getAllPermissions();
        assertEquals(0, allPermissions2.size());

        for (Permission permission : allPermissions) {
            pl.addPermission(permission);
        }

        Collection<Permission> allPermissions3 = pl.getAllPermissions();
        assertEquals(7, allPermissions3.size());

    }

}
