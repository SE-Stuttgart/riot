package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddRoleToUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetActiveTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetAllUsersException;
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

    @Test
    public void testAddUser() {
        boolean exceptionThrown = false;

        // should not be able to add null as user
        try {
            ul.addUser(null);
        } catch (AddUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a null value");
        } else {
            exceptionThrown = false;
        }

        // should be able to add a new user
        User user = new User("newUserName", "pw", "pwSalt");
        try {
            ul.addUser(user);
        } catch (AddUserException e) {
            fail(e.getMessage());
        }

        // should not be able to a user with the same user name twice
        try {
            ul.addUser(user);
        } catch (AddUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add the same user twice");
        } else {
            exceptionThrown = false;
        }

        // should not be able to add a user with the same user containg null as user name
        try {
            ul.addUser(new User(null, "pw", "pws"));
        } catch (AddUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a user with null as user name");
        } else {
            exceptionThrown = false;
        }

        // should not be able to add a user with the same user containing null as password
        try {
            ul.addUser(new User("IAmANewUser", null, "pws"));
        } catch (AddUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a user with null as password");
        } else {
            exceptionThrown = false;
        }
        // should not be able to add a user with the same user containing only null values
        try {
            ul.addUser(new User(null, null, null));
        } catch (AddUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a user containing only null values");
        } else {
            exceptionThrown = false;
        }
    }

    @Test
    public void testDeleteUser() {
        boolean exceptionThrown = false;

        // should not be able to delete null
        try {
            ul.deleteUser(null);
        } catch (DeleteUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to delete a null value");
        } else {
            exceptionThrown = false;
        }

        // should be able to delete user with id 1
        try {
            ul.deleteUser(1L);
        } catch (DeleteUserException e) {
            fail(e.getMessage());
        }

        // should not be able to delete user with id 1 again
        try {
            ul.deleteUser(1L);
        } catch (DeleteUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to delete a value twice");
        } else {
            exceptionThrown = false;
        }

        // should not be able to get the deleted user
        try {
            ul.getUser(1L);
        } catch (GetUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to get a deleted user");
        } else {
            exceptionThrown = false;
        }

        // should not be able to delete the user, because the id does not exist in the db
        try {
            ul.getUser(Long.MAX_VALUE);
        } catch (GetUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be able to delete the user");
        } else {
            exceptionThrown = false;
        }

        // should not be able to delete the user, because the id does not exist in the db
        try {
            ul.getUser(Long.MIN_VALUE);
        } catch (GetUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be able to delete the user");
        } else {
            exceptionThrown = false;
        }
    }

    @Test
    public void testUpdateUser() {
        boolean exceptionThrown = false;

        User user = null;
        User user2 = null;

        try {
            user = ul.getUser(1L);
            user2 = new User(user.getId(), "newUserName", user.getPassword(), user.getPasswordSalt());

            // the users should not be the same anymore
            assertNotEquals(user, user2);

            ul.updateUser(user.getId(), user2);

            User user3 = ul.getUser(1L);

            // user and user 3 should not be the same anymore
            assertNotEquals(user3, user);

            // user2 and user3 should be the same
            assertEquals(user3, user2);

        } catch (GetUserException | UpdateUserException e) {
            fail(e.getMessage());
        }

        // should not be able to update with a null value
        try {
            ul.updateUser(1L, null);
        } catch (UpdateUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be able to delete the user");
        } else {
            exceptionThrown = false;
        }

        // should not be able to update a none existing user
        try {
            ul.updateUser(42L, user2);
        } catch (UpdateUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be able to delete the user");
        } else {
            exceptionThrown = false;
        }

        // should not be able to update a user with the same user containg null as user name
        try {
            ul.updateUser(1L, new User(null, "pw", "pws"));
        } catch (UpdateUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a user with null as user name");
        } else {
            exceptionThrown = false;
        }

        // should not be able to update a user with the same user containing null as password
        try {
            ul.updateUser(1L, new User("IAmANewUser", null, "pws"));
        } catch (UpdateUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a user with null as password");
        } else {
            exceptionThrown = false;
        }

        // should not be able to update a user with the same user containing only null values
        try {
            ul.updateUser(1L, new User(null, null, null));
        } catch (UpdateUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a user containing only null values");
        } else {
            exceptionThrown = false;
        }
    }

    @Test
    public void testGetUserLong() {
        try {
            User user = ul.getUser(1L);

            assertEquals(user.getId(), 1);
            assertEquals(user.getUsername(), "Yoda");
            assertEquals(user.getPassword(), "YodaPW");
            assertEquals(user.getPasswordSalt(), "YodaSalt");
        } catch (GetUserException e) {
            fail(e.getMessage());
        }

        try {
            ul.getUser(42L);
        } catch (GetUserException e) {
            return;
        }
        fail("Should not reach this point, because user with id 42 does not exists");
    }

    @Test
    public void testGetUserString() {
        try {
            User user = ul.getUser("Yoda");

            assertEquals(user.getId(), 1);
            assertEquals(user.getUsername(), "Yoda");
            assertEquals(user.getPassword(), "YodaPW");
            assertEquals(user.getPasswordSalt(), "YodaSalt");
        } catch (GetUserException e) {
            fail(e.getMessage());
        }

        try {
            ul.getUser("BobaFett");
        } catch (GetUserException e) {
            return;
        }
        fail("Should not reach this point, because user with the name BobaFett does not exists");
    }

    @Test
    public void testGetAllUsers() {
        try {
            // should conain three users
            Collection<User> allUsers = ul.getAllUsers();
            assertEquals(3, allUsers.size());

            // delete all users
            for (User user : allUsers) {
                ul.deleteUser(user.getId());
            }

            // should contain zero users
            Collection<User> allUsers2 = ul.getAllUsers();
            assertEquals(0, allUsers2.size());

            // add the users again
            for (User user : allUsers) {
                ul.addUser(user);
            }

            // should contain three users again
            Collection<User> allUsers3 = ul.getAllUsers();
            assertEquals(3, allUsers3.size());

        } catch (GetAllUsersException | DeleteUserException | AddUserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddRoleToUser() {
        boolean exceptionThrown = false;
        try {
            ul.addRoleToUser(1L, 2L);
        } catch (AddRoleToUserException e) {
            fail(e.getMessage());
        }

        // should not be able to add the role to the user, because it already exists
        try {
            ul.addRoleToUser(1L, 2L);
        } catch (AddRoleToUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add the same role twice");
        } else {
            exceptionThrown = false;
        }

        // should not be able to add the role to a null value
        try {
            ul.addRoleToUser(null, 2L);
        } catch (AddRoleToUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a role to a null value");
        } else {
            exceptionThrown = false;
        }

        // should not be able to add a role with a null value to a user
        try {
            ul.addRoleToUser(1L, null);
        } catch (AddRoleToUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a role with a null value to a user");
        } else {
            exceptionThrown = false;
        }

        // should not be able to add the role to a non existing user
        try {
            ul.addRoleToUser(42L, 2L);
        } catch (AddRoleToUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a role to a non existing user");
        } else {
            exceptionThrown = false;
        }

        // should not be able to add a non existing role to an existing user
        try {
            ul.addRoleToUser(1L, 42L);
        } catch (AddRoleToUserException e) {
            // exception expected
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("Should not reach this point, because it should not be possible to add a non existing role to an existing user");
        } else {
            exceptionThrown = false;
        }
    }

    @Test
    public void testGetAllRolesFromUser() {
        try {
            // should get one role
            Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
            assertEquals(1, allRolesFromUser.size());

            Role role = allRolesFromUser.iterator().next();
            assertEquals("Master", role.getRoleName());
            assertEquals(1, role.getId());

            ul.removeRoleFromUser(1L, 1L);
            Collection<Role> allRolesFromUser2 = ul.getAllRolesFromUser(1L);
            assertEquals(0, allRolesFromUser2.size());
        } catch (GetRolesFromUserException | RemoveRoleFromUserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveRoleFromUser() {
        try {
            Collection<Role> allRolesFromUser = ul.getAllRolesFromUser(1L);
            ul.removeRoleFromUser(1L, 1L);

            Collection<Role> allRolesFromUser2 = ul.getAllRolesFromUser(1L);

            assertEquals(1, allRolesFromUser.size());
            assertEquals(0, allRolesFromUser2.size());
        } catch (RemoveRoleFromUserException | GetRolesFromUserException e) {
            fail(e.getMessage());
        }

        // should not throw an exception if the value is already removed
        try {
            ul.removeRoleFromUser(1L, 1L);
        } catch (RemoveRoleFromUserException e) {
            fail(e.getMessage());
        }

        // should not throw an exception if the user does not exist
        try {
            ul.removeRoleFromUser(42L, 1L);
        } catch (RemoveRoleFromUserException e) {
            fail(e.getMessage());
        }

        // should not throw an exception if the role does not exist
        try {
            ul.removeRoleFromUser(1L, 42L);
        } catch (RemoveRoleFromUserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetActiveTokensFromUser() {
        Token token = null;

        try {
            Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(1L);
            token = activeTokensFromUser.iterator().next();
            assertEquals(1, activeTokensFromUser.size());
            assertEquals("token1", token.getTokenValue());
        } catch (GetActiveTokenException e) {
            fail(e.getMessage());
        }

        // should not be to get the tokens of a non existent user
        try {
            Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(42L);
            assertEquals(0, activeTokensFromUser.size());

            activeTokensFromUser = ul.getActiveTokensFromUser(null);
            assertEquals(0, activeTokensFromUser.size());
        } catch (GetActiveTokenException e) {
            fail(e.getMessage());
        }

        try {
            DAO<Token> tokenDao = new TokenSqlQueryDAO(DatasourceUtil.getDataSource());
            tokenDao.delete(token);
        } catch (NamingException | DatasourceDeleteException e) {
            fail(e.getMessage());
        }

        // should not be to get the tokens, because they are deleted
        try {
            Collection<Token> activeTokensFromUser = ul.getActiveTokensFromUser(1L);
            assertEquals(0, activeTokensFromUser.size());
        } catch (GetActiveTokenException e) {
            fail(e.getMessage());
        }

    }

}
