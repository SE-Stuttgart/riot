package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;
import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.config.Configuration;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.AuthenticationLogic;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

;

public class AuthenticationLogicTest extends LogicTestBase {

    private static final String WRONG_USERNAMEEXCEPTION = "Wrong Username/Password";
    private static final String PASSWORD_TOO_MANY_TIMES_WRONG = "Password was too many times wrong. Please change the password.";

    private AuthenticationLogic al = new AuthenticationLogic();

    @Before
    public void setUp() throws Exception {
        // load shiro configuration
        Factory<SecurityManager> factory = new IniSecurityManagerFactory();
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    /*
     * Login tests
     */

    @Test
    public void testLoginValid() throws Exception {
        Token token = al.login("Yoda", "YodaPW");

        assertFalse(token.getTokenValue().isEmpty());
        assertFalse(token.getRefreshtokenValue().isEmpty());
    }

    @Test(expected = UserManagementException.class)
    public void testLoginInvalidUsername() throws Exception {
        al.login("Invalid", "YodaPW");
    }

    @Test(expected = UserManagementException.class)
    public void testLoginInvalidPassword() throws Exception {
        al.login("Yoda", "Invalid");
    }

    @Test(expected = UserManagementException.class)
    public void testLoginEmptyUsername() throws Exception {
        al.login("", "YodaPW");
    }

    @Test(expected = UserManagementException.class)
    public void testLoginEmptyPassword() throws Exception {
        al.login("Yoda", "");
    }

    @Test(expected = UserManagementException.class)
    public void testLoginNull() throws Exception {
        al.login(null, null);
    }

    @Test(expected = UserManagementException.class)
    public void testLoginNullUsername() throws Exception {
        al.login(null, "YodaPW");
    }

    @Test(expected = UserManagementException.class)
    public void testLoginNullPassword() throws Exception {
        al.login("Invalid", null);
    }

    @Test
    @TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
    public void testLoginMaxRetries() throws Exception {
        for (int i = 0; i <= Configuration.getInt(ConfigurationKey.um_maxLoginRetries); i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (UserManagementException e) {
                assertEquals(e.getClass(), UserManagementException.class);
                assertEquals(WRONG_USERNAMEEXCEPTION, e.getMessage());
            }
        }

        try {
            al.login("Yoda", "Invalid");
        } catch (UserManagementException e) {
            assertEquals(PASSWORD_TOO_MANY_TIMES_WRONG, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    @TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
    public void testLoginMaxRetriesReset() throws Exception {
        for (int i = 0; i <= Configuration.getInt(ConfigurationKey.um_maxLoginRetries) - 1; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (UserManagementException e) {
                assertEquals(WRONG_USERNAMEEXCEPTION, e.getMessage());
            }
        }

        // reset the login retry count with a correct login
        al.login("Yoda", "YodaPW");

        for (int i = 0; i <= Configuration.getInt(ConfigurationKey.um_maxLoginRetries) - 1; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (UserManagementException e) {
                assertEquals(WRONG_USERNAMEEXCEPTION, e.getMessage());
            }
        }
    }

    @Test
    @TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
    public void testLoginMaxRetriesUpdateUser() throws Exception {
        for (int i = 0; i <= Configuration.getInt(ConfigurationKey.um_maxLoginRetries); i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (UserManagementException e) {
                assertEquals(WRONG_USERNAMEEXCEPTION, e.getMessage());
            }
        }

        // reset the login retry count through updating of the user password
        UserLogic ul = new UserLogic();
        UMUser user = ul.getUser("Yoda");
        ul.updateUser(user, "newPW12!");

        for (int i = 0; i <= Configuration.getInt(ConfigurationKey.um_maxLoginRetries); i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (UserManagementException e) {
                assertEquals(WRONG_USERNAMEEXCEPTION, e.getMessage());
            }
        }

        try {
            al.login("Yoda", "Invalid");
        } catch (UserManagementException e) {
            assertEquals(PASSWORD_TOO_MANY_TIMES_WRONG, e.getMessage());
            return;
        }
        fail();
    }

    /*
     * Refresh token tests
     */

    @Test
    public void testRefreshTokenValid() throws Exception {
        Token token = al.refreshToken("token1R");

        assertFalse(token.getTokenValue().isEmpty());
        assertFalse(token.getRefreshtokenValue().isEmpty());
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testRefreshTokenInvalid() throws Exception {
        al.refreshToken("Invalid");
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testRefreshTokenNull() throws Exception {
        al.refreshToken(null);
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testRefreshTokenEmpty() throws Exception {
        al.refreshToken("");
    }

    /*
     * Logout tests
     */

    @Test(expected = ExpiredCredentialsException.class)
    public void testLogoutValid() throws Exception {
        al.logout("token1");

        // should fail, because this token should be marked as invalid after the logout process
        al.refreshToken("token1R");
    }

    @Test(expected = UserManagementException.class)
    public void testLogoutInvalid() throws Exception {
        al.logout("invalid");
    }

    @Test(expected = UserManagementException.class)
    public void testLogoutNull() throws Exception {
        al.logout(null);
    }

    @Test(expected = UserManagementException.class)
    public void testLogoutEmpty() throws Exception {
        al.logout("");
    }

}
