package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.usermanagement.logic.AuthenticationLogic;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LoginException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LogoutException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.RefreshException;
import de.uni_stuttgart.riot.usermanagement.logic.test.common.LogicTestBase;

/**
 *
 * @author Niklas Schnabel
 *
 */
public class AuthenticationLogicTest extends LogicTestBase {

    private static final String WRONG_USERNAME_EXCEPTION = "de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LoginException: Wrong Username/Password";
    private static final String PASSWORD_TOO_MANY_TIMES_WRONG = "de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LoginException: Password was too many times wrong. Please change the password.";

    private AuthenticationLogic al = new AuthenticationLogic();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // disable logging of shiro
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();

        // load shiro configuration
        Factory<SecurityManager> factory = new IniSecurityManagerFactory();
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    /*
     * Login tests
     */

    @Test
    public void testLogin_valid() throws Exception {
        Token token = al.login("Yoda", "YodaPW");

        assertFalse(token.getTokenValue().isEmpty());
        assertFalse(token.getRefreshtokenValue().isEmpty());
    }

    @Test(expected = LoginException.class)
    public void testLogin_invalidUsername() throws Exception {
        al.login("Invalid", "YodaPW");
    }

    @Test(expected = LoginException.class)
    public void testLogin_invalidPassword() throws Exception {
        al.login("Yoda", "Invalid");
    }

    @Test(expected = LoginException.class)
    public void testLogin_emptyUsername() throws Exception {
        al.login("", "YodaPW");
    }

    @Test(expected = LoginException.class)
    public void testLogin_emptyPassword() throws Exception {
        al.login("Yoda", "");
    }

    @Test(expected = LoginException.class)
    public void testLogin_null() throws Exception {
        al.login(null, null);
    }

    @Test(expected = LoginException.class)
    public void testLogin_nullUsername() throws Exception {
        al.login(null, "YodaPW");
    }

    @Test(expected = LoginException.class)
    public void testLogin_nullPassword() throws Exception {
        al.login("Invalid", null);
    }

    @Test
    public void testLogin_maxRetries() throws Exception {
        for (int i = 0; i <= AuthenticationLogic.MAX_LOGIN_RETRIES; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (LoginException e) {
                assertEquals(WRONG_USERNAME_EXCEPTION, e.getMessage());
            }
        }

        try {
            al.login("Yoda", "Invalid");
        } catch (LoginException e) {
            assertEquals(PASSWORD_TOO_MANY_TIMES_WRONG, e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testLogin_maxRetriesReset() throws Exception {
        for (int i = 0; i <= AuthenticationLogic.MAX_LOGIN_RETRIES - 1; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (LoginException e) {
                assertEquals(WRONG_USERNAME_EXCEPTION, e.getMessage());
            }
        }

        // reset the login retry count with a correct login
        al.login("Yoda", "YodaPW");

        for (int i = 0; i <= AuthenticationLogic.MAX_LOGIN_RETRIES - 1; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (LoginException e) {
                assertEquals(WRONG_USERNAME_EXCEPTION, e.getMessage());
            }
        }
    }

    @Test
    public void testLogin_maxRetriesUpdateUser() throws Exception {
        for (int i = 0; i <= AuthenticationLogic.MAX_LOGIN_RETRIES; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (LoginException e) {
                assertEquals(WRONG_USERNAME_EXCEPTION, e.getMessage());
            }
        }

        // reset the login retry count through updating of the user password
        UserLogic ul = new UserLogic();
        UMUser user = ul.getUser("Yoda");
        ul.updateUser(user, "newPW12!");

        for (int i = 0; i <= AuthenticationLogic.MAX_LOGIN_RETRIES; i++) {
            try {
                al.login("Yoda", "Invalid");
            } catch (LoginException e) {
                assertEquals(WRONG_USERNAME_EXCEPTION, e.getMessage());
            }
        }

        try {
            al.login("Yoda", "Invalid");
        } catch (LoginException e) {
            assertEquals(PASSWORD_TOO_MANY_TIMES_WRONG, e.getMessage());
            return;
        }
        fail();
    }

    /*
     * Refresh token tests
     */

    @Test
    public void testRefreshToken_valid() throws Exception {
        Token token = al.refreshToken("token1R");

        assertFalse(token.getTokenValue().isEmpty());
        assertFalse(token.getRefreshtokenValue().isEmpty());
    }

    @Test(expected = RefreshException.class)
    public void testRefreshToken_invalid() throws Exception {
        al.refreshToken("Invalid");
    }

    @Test(expected = RefreshException.class)
    public void testRefreshToken_null() throws Exception {
        al.refreshToken(null);
    }

    @Test(expected = RefreshException.class)
    public void testRefreshToken_empty() throws Exception {
        al.refreshToken("");
    }

    /*
     * Logout tests
     */

    @Test(expected = RefreshException.class)
    public void testLogout_valid() throws Exception {
        al.logout("token1");

        // should fail, because this token should be marked as invalid after the logout process
        al.refreshToken("token1R");
    }

    @Test(expected = LogoutException.class)
    public void testLogout_invalid() throws Exception {
        al.logout("invalid");
    }

    @Test(expected = LogoutException.class)
    public void testLogout_null() throws Exception {
        al.logout(null);
    }

    @Test(expected = LogoutException.class)
    public void testLogout_empty() throws Exception {
        al.logout("");
    }

}
