package de.uni_stuttgart.riot.usermanagement.logic.test;

import static org.junit.Assert.assertFalse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.usermanagement.logic.AuthenticationLogic;
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

    private AuthenticationLogic al = new AuthenticationLogic();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // disable logging of shiro
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();

        // load shiro configuration
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("src/main/webapp/WEB-INF/shiro.ini");
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
