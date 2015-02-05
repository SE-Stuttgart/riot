package de.uni_stuttgart.riot.clientlibrary.usermanagement.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.DefaultTokenManager;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.test.TestData;

@TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class UsermanagementClientTest extends ShiroEnabledTest {

    @Test
    public void getUserTest() throws ClientProtocolException, RequestException, IOException {
        LoginClient loginClient = new LoginClient("http://localhost:" + getPort(), "TestThing", new DefaultTokenManager());
        loginClient.login("Yoda", "YodaPW");
        UsermanagementClient usermanagementClient = new UsermanagementClient(loginClient);
        User u = usermanagementClient.getUser(1);
        assertEquals("Yoda", u.getUsername());
    }

    @Test
    public void updateUserTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.updateUser(2, new UserRequest("TEST", "sTEST12345!"));
        User updated = usermanagementClient.getUser(2);
        assertEquals("TEST", updated.getUsername());
    }

    @Test
    public void updateRoleTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.updateRole(2, new Role("TEST"));
        Role updated = usermanagementClient.getRole(2);
        assertEquals("TEST", updated.getRoleName());
    }

    @Test
    public void updatePermissionTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.updatePermission(2, new Permission("TEST"));
        Permission updated = usermanagementClient.getPermission(2);
        assertEquals("TEST", updated.getPermissionValue());
    }

    @Test
    public void removeUserRoleTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.removeUserRole(2, 2);
        Collection<Role> roles = usermanagementClient.getUserRoles(2);
        assertEquals(0, roles.size());
    }

    @Test
    public void removeUserTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.removeUser(2);
        Collection<User> user = usermanagementClient.getUsers();
        assertEquals(2, user.size());
    }

    @Test
    public void removeRoleTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.removeRole(2);
        Collection<Role> roles = usermanagementClient.getRoles();
        assertEquals(3, roles.size());
    }

    @Test
    public void removePermissionTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.removePermission(2);
        Collection<Permission> permissions = usermanagementClient.getPermissions();
        assertEquals(3, permissions.size());
    }

    @Test
    public void addUserTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.addUser(new UserRequest("TEST", "sTEST12345!"));
        User newUser = usermanagementClient.getUser(4);
        assertEquals("TEST", newUser.getUsername());
    }

    @Test
    public void addRoleTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.addRole(new Role("TEST"));
        Collection<Role> roles = usermanagementClient.getRoles();
        assertEquals(5, roles.size());
    }

    @Test
    public void addUserRoleTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.addUserRole(1, 2);
        Collection<Role> roles = usermanagementClient.getUserRoles(1);
        assertEquals(2, roles.size());
    }

    @Test
    public void addPermissionTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        usermanagementClient.addPermission(new Permission("TEST"));
        Collection<Permission> permissions = usermanagementClient.getPermissions();
        assertEquals(5, permissions.size());
    }

    @Test
    public void getUserRolesTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        Collection<Role> roles = usermanagementClient.getUserRoles(1);
        assertEquals(1, roles.size());
    }

    @Test
    public void getRoleTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        Role role = usermanagementClient.getRole(1);
        assertEquals("Master", role.getRoleName());
    }

    @Test
    public void getPermissionTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        Permission permission = usermanagementClient.getPermission(1);
        assertEquals("lightsaber:*", permission.getPermissionValue());
    }

    @Test
    public void getUsersTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        Collection<User> users = usermanagementClient.getUsers();
        assertEquals(3, users.size());
    }

    @Test
    public void getRolesTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        Collection<Role> roles = usermanagementClient.getRoles();
        assertEquals(4, roles.size());
    }

    @Test
    public void getPermissionsTest() throws ClientProtocolException, RequestException, IOException {
        UsermanagementClient usermanagementClient = this.getLogedInUserMClient();
        Collection<Permission> permissions = usermanagementClient.getPermissions();
        assertEquals(4, permissions.size());
    }

    public UsermanagementClient getLogedInUserMClient() throws ClientProtocolException, RequestException, IOException {
        LoginClient loginClient = new LoginClient("http://localhost:" + getPort(), "TestThing", new DefaultTokenManager());
        loginClient.login("Yoda", "YodaPW");
        return new UsermanagementClient(loginClient);
    }
}
