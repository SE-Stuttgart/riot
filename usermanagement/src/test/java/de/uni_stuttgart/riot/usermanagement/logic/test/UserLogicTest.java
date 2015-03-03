package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddRoleToUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetActiveTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetRolesFromUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.RemoveRoleFromUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.UpdateUserException;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

public class UserLogicTest extends LogicTestBase {

    private UserLogic ul;

    @Before
    public void initLogic() {
        ul = new UserLogic();
    }

    /*
     * Add user tests
     */

    @Test
    @TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
    public void testAddUserValid() throws Exception {
        UMUser user1 = ul.addUser("newUserName", "email@test.org", "pwW123!");
        UMUser user2 = ul.getUser(user1.getId());
        assertEquals(user1, user2);
    }

    @Test(expected = AddUserException.class)
    public void testAddUserEmptyUserName() throws Exception {
        ul.addUser("", "email@test.org", "pw");
    }

    @Test(expected = AddUserException.class)
    public void testAddUserEmptyPw() throws Exception {
        ul.addUser("newUserName", "email@test.org", "");
    }

    @Test(expected = AddUserException.class)
    public void testAddUserDoubleAdd() throws Exception {
        ul.addUser("newUserName", "email@test.org", "pw");
        ul.addUser("newUserName", "email@test.org", "pw");
    }

    @Test(expected = AddUserException.class)
    public void testAddUserNull() throws Exception {
        ul.addUser(null, null, null);
    }

    @Test(expected = AddUserException.class)
    public void testAddUserNullUserName() throws Exception {
        ul.addUser(null, null, "pw");
    }

    @Test(expected = AddUserException.class)
    public void testAddUserNullPassword() throws Exception {
        ul.addUser("newUserName", null, null);
    }

    /*
     * Delete User tests
     */

    @Test(expected = GetUserException.class)
    public void testDeleteUserValid() throws Exception {
        ul.deleteUser(1L);
        ul.getUser(1L);
    }

    @Test(expected = DeleteUserException.class)
    public void testDeleteUserInvalid() throws Exception {
        ul.deleteUser(42L);
    }

    @Test(expected = DeleteUserException.class)
    public void testDeleteUserDouble() throws Exception {
        ul.deleteUser(1L);
        ul.deleteUser(1L);
    }

    @Test(expected = DeleteUserException.class)
    public void testDeleteUserNull() throws Exception {
        ul.deleteUser(null);
    }

    /*
     * Update user tests
     */

    @Test
    public void testUpdateUserValid() throws Exception {
        UMUser user = ul.getUser(1L);

        user.setUsername("brandNewUserName");
        ul.updateUser(user, null);

        UMUser user2 = ul.getUser(1L);
        assertEquals("brandNewUserName", user2.getUsername());
    }

    @Test
    @TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
    public void testUpdateUserValidPwChange() throws Exception {
        UMUser user = ul.getUser(1L);
        String pwHashBefore = user.getHashedPassword();

        ul.updateUser(user, "newPw1!");

        UMUser user2 = ul.getUser(1L);
        assertNotEquals(pwHashBefore, user2.getHashedPassword());
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserInvalidId() throws Exception {
        UMUser user = new UMUser(42L, "userName", "email@test.org", "hashedPw", "salt", 42);
        ul.updateUser(user, "pw");
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserEmptyUserName() throws Exception {
        ul.updateUser(new UMUser(1L, "", "email@test.org", "hashedPw", "pwSalt", 42), "pw");
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserEmptyHashPw() throws Exception {
        ul.updateUser(new UMUser(1L, "newUserName", "email@test.org", "", "pwSalt", 42), "pw");
    }

    @Test
    public void testUpdateUserEmptyClearPw() throws Exception {
        ul.updateUser(new UMUser(1L, "newUserName", "email@test.org", "hashedPw", "pwSalt", 42), "");
        UMUser user = ul.getUser(1L);
        assertEquals("hashedPw", user.getHashedPassword());
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserEmptyPwSalt() throws Exception {
        ul.updateUser(new UMUser(1L, "newUserName", "email@test.org", "hashedPw", "", 42), "pw");
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserNull() throws Exception {
        ul.updateUser(null, null);
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserNullUserName() throws Exception {
        ul.updateUser(new UMUser(null, "hashedPw", "email@test.org", "pwSalt", 42), "pw");
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserNullHashedPassword() throws Exception {
        ul.updateUser(new UMUser("newUserName", "email@test.org", null, "pwSalt", 42), "pw");
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserNullClearPassword() throws Exception {
        ul.updateUser(new UMUser("newUserName", "email@test.org", "hashedPw", "pwSalt", 42), null);
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUserNullSalt() throws Exception {
        ul.updateUser(new UMUser("newUserName", "email@test.org", "hashedPw", null, 42), "pw");
    }

    @Test(expected = UpdateUserException.class)
    public void ttestUpdateUserNullUser() throws Exception {
        ul.updateUser(new UMUser(null, null, null, null, 42), "pw");
    }

    @Test
    public void testUpdateUserNullPw() throws Exception {
        ul.updateUser(new UMUser(1L, "newUserName", "email@test.org", "hashedPw", "pwSalt", 42), null);
        UMUser user = ul.getUser(1L);
        assertEquals("hashedPw", user.getHashedPassword());
    }

    /*
     * Get user by id tests
     */

    @Test
    public void testGetUserLongValid() throws Exception {
        UMUser user1 = new UMUser(1L, "Yoda", "yoda@force.org", "yPYMjqXzWOPKaAKNJXfEw7Gu3EnckZmoWUuEhOqz/7IqGd4Ub+3/X3uANlO0mkIOqIMhxhUi/ieU1KZt2BK+eg==", "108bgacl42gihhrdlfcm8e6ls6gm2q45q00boauiv5kkduf1ainv", 200000);
        UMUser user2 = ul.getUser(1L);
        assertEquals(user1, user2);
    }

    @Test(expected = GetUserException.class)
    public void testGetUserLongInvalid() throws Exception {
        ul.getUser(42L);
    }

    @Test(expected = GetUserException.class)
    public void testGetUserLongNull() throws Exception {
        Long l = null;
        ul.getUser(l);
    }

    /*
     * Get user by user name tests
     */

    @Test
    public void testGetUserStringValid() throws Exception {
        UMUser user1 = new UMUser(1L, "Yoda", "yoda@force.org",  "yPYMjqXzWOPKaAKNJXfEw7Gu3EnckZmoWUuEhOqz/7IqGd4Ub+3/X3uANlO0mkIOqIMhxhUi/ieU1KZt2BK+eg==", "108bgacl42gihhrdlfcm8e6ls6gm2q45q00boauiv5kkduf1ainv", 200000);
        UMUser user2 = ul.getUser("Yoda");
        assertEquals(user1, user2);
    }

    @Test(expected = GetUserException.class)
    public void testGetUserStringInvalid() throws Exception {
        ul.getUser("blubb");
    }

    @Test(expected = GetUserException.class)
    public void testGetUserStringNull() throws Exception {
        String u = null;
        ul.getUser(u);
    }

    /*
     * Get all users test
     */

    @Test
    public void testGetAllUsersValid() throws Exception {
        Collection<UMUser> allUsers = ul.getAllUsers();
        assertEquals(3, allUsers.size());
    }

    @Test
    @TestData({ "/schema/schema_usermanagement.sql" })
    public void testGetAllUsersEmpty() throws Exception {
        Collection<UMUser> allUsers = ul.getAllUsers();
        assertEquals(0, allUsers.size());
    }

    /*
     * Add role to user tests
     */

    @Test
    public void testAddRoleToUserValid() throws Exception {
        int roleCount = ul.getAllRolesFromUser(1L).size();
        ul.addRoleToUser(1L, 2L);
        assertEquals(roleCount + 1, ul.getAllRolesFromUser(1L).size());
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUserInvalidUser() throws Exception {
        ul.addRoleToUser(42L, 2L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUserInvalidRole() throws Exception {
        ul.addRoleToUser(1L, 42L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUserDouble() throws Exception {
        ul.addRoleToUser(1L, 2L);
        ul.addRoleToUser(1L, 2L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUserNullUser() throws Exception {
        ul.addRoleToUser(null, 2L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUserNullRole() throws Exception {
        ul.addRoleToUser(1L, null);
    }

    /*
     * Get all roles from user tests
     */

    @Test
    public void testGetAllRolesFromUserValid() throws Exception {
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
        assertEquals(1, allRolesFromUser.size());
    }

    @Test
    public void testGetAllRolesFromUserInvalid() throws Exception {
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(42L);
        assertEquals(0, allRolesFromUser.size());
    }

    @Test(expected = GetRolesFromUserException.class)
    public void testGetAllRolesFromUserNull() throws Exception {
        ul.getAllRolesFromUser(null);
    }

    @Test
    @TestData({ "/schema/schema_usermanagement.sql" })
    public void testGetAllRolesFromUserEmpty() throws Exception {
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
        assertEquals(0, allRolesFromUser.size());
    }

    /*
     * Remove role from user tests
     */

    @Test
    public void testRemoveRoleFromUserValid() throws Exception {
        ul.removeRoleFromUser(1L, 1L);
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
        assertEquals(0, allRolesFromUser.size());
    }

    @Test(expected = RemoveRoleFromUserException.class)
    public void testRemoveRoleFromUserInvalidUser() throws Exception {
        ul.removeRoleFromUser(42L, 1L);
    }

    @Test(expected = RemoveRoleFromUserException.class)
    public void testRemoveRoleFromUserInvalidRole() throws Exception {
        ul.removeRoleFromUser(1L, 42L);
    }

    @Test(expected = RemoveRoleFromUserException.class)
    public void testRemoveRoleFromUserDouble() throws Exception {
        ul.removeRoleFromUser(1L, 1L);
        ul.removeRoleFromUser(1L, 1L);
    }

    /*
     * Get active tokens from user tests
     */

    @Test
    public void testGetActiveTokensFromUserValid() throws Exception {
        Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(1L);
        Token token = activeTokensFromUser.iterator().next();
        assertEquals(1, activeTokensFromUser.size());
        assertEquals("token1", token.getTokenValue());
    }

    @Test
    public void testGetActiveTokensFromUserInvalid() throws Exception {
        Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(42L);
        assertEquals(0, activeTokensFromUser.size());
    }

    @Test(expected = GetActiveTokenException.class)
    public void testGetActiveTokensFromUserNull() throws Exception {
        ul.getActiveTokensFromUser(null);
    }

    @Test
    @TestData({ "/schema/schema_usermanagement.sql" })
    public void testGetActiveTokensFromUserEmpty() throws Exception {
        Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(1L);
        assertEquals(0, activeTokensFromUser.size());
    }
}
