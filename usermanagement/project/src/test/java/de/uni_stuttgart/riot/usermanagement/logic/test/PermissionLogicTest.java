package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

public class PermissionLogicTest extends LogicTestBase {

    private PermissionLogic pl = new PermissionLogic();

    @Test
    public void testAddPermission() {

        Permission permission = new Permission("JUnitTestPermission");

        try {
            pl.addPermission(permission);
            Permission permissionRead = pl.getPermission(5L);

            assertEquals(permission, permissionRead);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeletePermission() {
        boolean exceptionThrown = false;

        try {
            pl.deletePermission(1L);
        } catch (DeletePermissionException e) {
            fail(e.getMessage());
        }

        try {
            pl.deletePermission(1L);
        } catch (DeletePermissionException e) {
            // expected that the exception is thrown
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because the permission with id 1 was deleted twice");
        } else {
            exceptionThrown = false;
        }

        try {
            pl.getPermission(1L);
        } catch (GetPermissionException e) {
            // expected that the exception is thrown
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to retrieve the permission");
        } else {
            exceptionThrown = false;
        }
    }

    @Test
    public void testUpdatePermission() {
        try {
            Permission permission = pl.getPermission(1L);
            String permissionValue = permission.getPermissionValue();

            permission.setPermissionValue("newPermissionValue");
            pl.updatePermission(permission.getId(), permission);

            assertNotEquals(permission, permission);
        } catch (GetPermissionException | UpdatePermissionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPermission() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAllPermissions() {
        fail("Not yet implemented");
    }

}
