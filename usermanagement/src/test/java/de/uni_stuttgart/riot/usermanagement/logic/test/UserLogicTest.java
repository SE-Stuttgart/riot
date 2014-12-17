package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
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

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UserLogicTest extends LogicTestBase {

    private UserLogic ul;

    @Before
    public void initLogic() {
        ul = new UserLogic();
    }

    /*
     * Add user tests
     */

    /*
     * Add user tests
     */

    @Test
    public void testAddUser_valid() throws Exception {
        User user = new User("newUserName", "pw", "pwSalt");
        ul.addUser(user);
        User user2 = ul.getUser(user.getId());
        assertEquals(user, user2);
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_emptyUserName() throws Exception {
        ul.addUser(new User("", "pw", "pwSalt"));
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_emptyPw() throws Exception {
        ul.addUser(new User("newUserName", "", "pwSalt"));
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_emptyPwSalt() throws Exception {
        ul.addUser(new User("newUserName", "pw", ""));
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_doubleAdd() throws Exception {
        User user = new User("newUserName", "pw", "pwSalt");
        ul.addUser(user);
        ul.addUser(user);
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_null() throws Exception {
        ul.addUser(null);
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_nullUserName() throws Exception {
        User user = new User(null, "pw", "pwSalt");
        ul.addUser(user);
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_nullPassword() throws Exception {
        User user = new User("newUserName", null, "pwSalt");
        ul.addUser(user);
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_nullSalt() throws Exception {
        User user = new User("newUserName", "pw", null);
        ul.addUser(user);
    }

    @Test(expected = AddUserException.class)
    public void testAddUser_nullUser() throws Exception {
        User user = new User(null, null, null);
        ul.addUser(user);
    }

    /*
     * Delete User tests
     */

    @Test(expected = GetUserException.class)
    public void testDeleteUser_valid() throws Exception {
        ul.deleteUser(1L);
        ul.getUser(1L);
    }

    @Test(expected = DeleteUserException.class)
    public void testDeleteUser_invalid() throws Exception {
        ul.deleteUser(42L);
    }

    @Test(expected = DeleteUserException.class)
    public void testDeleteUser_double() throws Exception {
        ul.deleteUser(1L);
        ul.deleteUser(1L);
    }

    @Test(expected = DeleteUserException.class)
    public void testDeleteUser_null() throws Exception {
        ul.deleteUser(null);
    }

    /*
     * Update user tests
     */

    @Test
    public void testUpdateUser_valid() throws Exception {
        User user = ul.getUser(1L);

        user.setUsername("brandNewUserName");
        ul.updateUser(user);

        User user2 = ul.getUser(1L);
        assertEquals("brandNewUserName", user2.getUsername());
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_invalidId() throws Exception {
        User user = new User(42L, "userName", "password", "salt");
        ul.updateUser(user);
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_emptyUserName() throws Exception {
        ul.updateUser(new User(1L, "", "pw", "pwSalt"));
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_emptyPw() throws Exception {
        ul.updateUser(new User(1L, "newUserName", "", "pwSalt"));
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_emptyPwSalt() throws Exception {
        ul.updateUser(new User(1L, "newUserName", "pw", ""));
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_null() throws Exception {
        ul.updateUser(null);
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_nullUserName() throws Exception {
        ul.updateUser(new User(null, "pw", "pwSalt"));
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_nullPassword() throws Exception {
        ul.updateUser(new User("newUserName", null, "pwSalt"));
    }

    @Test(expected = UpdateUserException.class)
    public void testUpdateUser_nullSalt() throws Exception {
        ul.updateUser(new User("newUserName", "pw", null));
    }

    @Test(expected = UpdateUserException.class)
    public void ttestUpdateUser_nullUser() throws Exception {
        ul.updateUser(new User(null, null, null));
    }

    /*
     * Get user by id tests
     */

    @Test
    public void testGetUserLong_valid() throws Exception {
        User user1 = new User(1L, "Yoda", "YodaPW", "YodaSalt");
        User user2 = ul.getUser(1L);
        assertEquals(user1, user2);
    }

    @Test(expected = GetUserException.class)
    public void testGetUserLong_invalid() throws Exception {
        ul.getUser(42L);
    }

    @Test(expected = GetUserException.class)
    public void testGetUserLong_null() throws Exception {
        Long l = null;
        ul.getUser(l);
    }

    /*
     * Get user by user name tests
     */

    @Test
    public void testGetUserString_valid() throws Exception {
        User user1 = new User(1L, "Yoda", "YodaPW", "YodaSalt");
        User user2 = ul.getUser("Yoda");
        assertEquals(user1, user2);
    }

    @Test(expected = GetUserException.class)
    public void testGetUserString_invalid() throws Exception {
        ul.getUser("blubb");
    }

    @Test(expected = GetUserException.class)
    public void testGetUserString_null() throws Exception {
        String u = null;
        ul.getUser(u);
    }

    /*
     * Get all users test
     */

    @Test
    public void testGetAllUsers_valid() throws Exception {
        Collection<User> allUsers = ul.getAllUsers();
        assertEquals(3, allUsers.size());
    }

    @Test
    public void testGetAllUsers_empty() throws Exception {
        emptyDatabaseTables();
        Collection<User> allUsers = ul.getAllUsers();
        assertEquals(0, allUsers.size());
    }

    /*
     * Add role to user tests
     */

    @Test
    public void testAddRoleToUser_valid() throws Exception {
        int roleCount = ul.getAllRolesFromUser(1L).size();
        ul.addRoleToUser(1L, 2L);
        assertEquals(roleCount + 1, ul.getAllRolesFromUser(1L).size());
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUser_invalidUser() throws Exception {
        ul.addRoleToUser(42L, 2L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUser_invalidRole() throws Exception {
        ul.addRoleToUser(1L, 42L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUser_double() throws Exception {
        ul.addRoleToUser(1L, 2L);
        ul.addRoleToUser(1L, 2L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUser_nullUser() throws Exception {
        ul.addRoleToUser(null, 2L);
    }

    @Test(expected = AddRoleToUserException.class)
    public void testAddRoleToUser_nullRole() throws Exception {
        ul.addRoleToUser(1L, null);
    }

    /*
     * Get all roles from user tests
     */

    @Test
    public void testGetAllRolesFromUser_valid() throws Exception {
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
        assertEquals(1, allRolesFromUser.size());
    }

    @Test
    public void testGetAllRolesFromUser_invalid() throws Exception {
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(42L);
        assertEquals(0, allRolesFromUser.size());
    }

    @Test(expected = GetRolesFromUserException.class)
    public void testGetAllRolesFromUser_null() throws Exception {
        ul.getAllRolesFromUser(null);
    }

    @Test
    public void testGetAllRolesFromUser_empty() throws Exception {
        emptyDatabaseTables();
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
        assertEquals(0, allRolesFromUser.size());
    }

    /*
     * Remove role from user tests
     */

    @Test
    public void testRemoveRoleFromUser_valid() throws Exception {
        ul.removeRoleFromUser(1L, 1L);
        Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
        assertEquals(0, allRolesFromUser.size());
    }

    @Test(expected = RemoveRoleFromUserException.class)
    public void testRemoveRoleFromUser_invalidUser() throws Exception {
        ul.removeRoleFromUser(42L, 1L);
    }

    @Test(expected = RemoveRoleFromUserException.class)
    public void testRemoveRoleFromUser_invalidRole() throws Exception {
        ul.removeRoleFromUser(1L, 42L);
    }

    @Test(expected = RemoveRoleFromUserException.class)
    public void testRemoveRoleFromUser_double() throws Exception {
        ul.removeRoleFromUser(1L, 1L);
        ul.removeRoleFromUser(1L, 1L);
    }

    /*
     * Get active tokens from user tests
     */

    @Test
    public void testGetActiveTokensFromUser_valid() throws Exception {
        Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(1L);
        Token token = activeTokensFromUser.iterator().next();
        assertEquals(1, activeTokensFromUser.size());
        assertEquals("token1", token.getTokenValue());
    }

    @Test
    public void testGetActiveTokensFromUser_invalid() throws Exception {
        Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(42L);
        assertEquals(0, activeTokensFromUser.size());
    }

    @Test(expected = GetActiveTokenException.class)
    public void testGetActiveTokensFromUser_null() throws Exception {
        ul.getActiveTokensFromUser(null);
    }

    @Test
    public void testGetActiveTokensFromUser_empty() throws Exception {
        emptyDatabaseTables();
        Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(1L);
        assertEquals(0, activeTokensFromUser.size());
    }
}
