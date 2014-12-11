package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

public class PermissionLogicTest extends LogicTestBase {

    private PermissionLogic pl = new PermissionLogic();

    @Test
    public void testAddPermission() {

        boolean exceptionThrown = false;
        Permission permission = new Permission("JUnitTestPermission");

        try {
            pl.addPermission(permission);
            Permission permissionRead = pl.getPermission(5L);

            assertEquals(permission, permissionRead);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            pl.addPermission(null);
        } catch (AddPermissionException e) {
            // expected that the exception is thrown
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because adding the avlue null is not valid");
        } else {
            exceptionThrown = false;
        }

        try {
            pl.addPermission(new Permission(null));
        } catch (AddPermissionException e) {
            // expected that the exception is thrown
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because adding a permision containing a null value is not permitted");
        } else {
            exceptionThrown = false;
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

        try {
            pl.getPermission(null);
        } catch (GetPermissionException e) {
            // expected that the exception is thrown
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to retrieve a permission with null id");
        } else {
            exceptionThrown = false;
        }
    }

    @Test
    public void testUpdatePermission() {
        boolean exceptionThrown = false;

        try {
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
        } catch (GetPermissionException | UpdatePermissionException e) {
            fail(e.getMessage());
        }

        try {
            pl.updatePermission(1L, null);
        } catch (UpdatePermissionException e) {
            // expected that the exception is thrown
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to retrieve the permission");
        } else {
            exceptionThrown = false;
        }

        try {
            pl.updatePermission(null, new Permission("testValue"));
        } catch (UpdatePermissionException e) {
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
    public void testGetPermission() {
        boolean excpetionThrown = false;

        try {
            Permission p1 = pl.getPermission(1L);
            Permission p2 = pl.getPermission(1L);

            assertEquals(p1, p2);
        } catch (GetPermissionException e) {
            fail(e.getMessage());
        }

        try {
            pl.deletePermission(1L);
        } catch (DeletePermissionException e) {
            fail(e.getMessage());
        }

        try {
            pl.getPermission(1L);
        } catch (GetPermissionException e) {
            // Exception expected
            excpetionThrown = true;
        }

        if (!excpetionThrown) {
            fail("Shouldn't reach this point, because the permission was deleted");
        } else {
            excpetionThrown = false;
        }

        try {
            pl.getPermission(Long.MAX_VALUE);
        } catch (GetPermissionException e) {
            // Exception expected
            excpetionThrown = true;
        }

        if (!excpetionThrown) {
            fail("Shouldn't reach this point, because the permission with long max value does not exist");
        } else {
            excpetionThrown = false;
        }

        try {
            pl.getPermission(Long.MIN_VALUE);
        } catch (GetPermissionException e) {
            // Exception expected
            excpetionThrown = true;
        }

        if (!excpetionThrown) {
            fail("Shouldn't reach this point, because the permission with long min value does not exist");
        }
    }

    @Test
    public void testGetAllPermissions() {
        try {
            Collection<Permission> allPermissions = pl.getAllPermissions();

            assertEquals(4, allPermissions.size());

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
            assertEquals(4, allPermissions3.size());
        } catch (GetAllPermissionsException | GetPermissionException | DeletePermissionException | AddPermissionException e) {
            fail(e.getMessage());
        }

    }

}
