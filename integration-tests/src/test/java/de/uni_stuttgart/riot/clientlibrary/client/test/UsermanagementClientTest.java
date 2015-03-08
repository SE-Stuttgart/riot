package de.uni_stuttgart.riot.clientlibrary.client.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.test.TestData;

@TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class UsermanagementClientTest extends BaseClientTest {

    UsermanagementClient usermanagementClient;

    @Before
    public void initClient() {
        usermanagementClient = new UsermanagementClient(getLoggedInConnector());
    }

    @Test
    public void getUserTest() throws RequestException, IOException, NotFoundException {
        User u = usermanagementClient.getUser(1);
        assertThat(u.getUsername(), is("Yoda"));
    }

    @Test
    public void updateUserTest() throws RequestException, IOException, NotFoundException {
        usermanagementClient.updateUser(2, new UserRequest("TEST", "sTEST12345!", "email@mail.com"));
        User updated = usermanagementClient.getUser(2);
        assertThat(updated.getUsername(), is("TEST"));
    }

    @Test
    public void updateRoleTest() throws RequestException, IOException, NotFoundException {
        usermanagementClient.updateRole(2, new Role("TEST"));
        Role updated = usermanagementClient.getRole(2);
        assertThat(updated.getRoleName(), is("TEST"));
    }

    @Test
    public void updatePermissionTest() throws RequestException, IOException, NotFoundException {
        usermanagementClient.updatePermission(2, new Permission("TEST"));
        Permission updated = usermanagementClient.getPermission(2);
        assertThat(updated.getPermissionValue(), is("TEST"));
    }

    @Test
    public void removeUserRoleTest() throws RequestException, IOException {
        usermanagementClient.removeUserRole(2, 2);
        Collection<Role> roles = usermanagementClient.getUserRoles(2);
        assertThat(roles, is(empty()));
    }

    @Test
    public void removeUserTest() throws RequestException, IOException {
        usermanagementClient.removeUser(2);
        Collection<User> user = usermanagementClient.getUsers();
        assertThat(user, hasSize(2));
    }

    @Test
    public void removeRoleTest() throws RequestException, IOException {
        usermanagementClient.removeRole(2);
        Collection<Role> roles = usermanagementClient.getRoles();
        assertThat(roles, hasSize(3));
    }

    @Test
    public void removePermissionTest() throws RequestException, IOException {
        usermanagementClient.removePermission(2);
        Collection<Permission> permissions = usermanagementClient.getPermissions();
        assertThat(permissions, hasSize(6));
    }

    @Test
    public void addUserTest() throws RequestException, IOException, NotFoundException {
        usermanagementClient.addUser(new UserRequest("TEST", "sTEST12345!", "email@mail.com"));
        User newUser = usermanagementClient.getUser(4);
        assertThat(newUser.getUsername(), is("TEST"));
    }

    @Test
    public void addRoleTest() throws RequestException, IOException {
        usermanagementClient.addRole(new Role("TEST"));
        Collection<Role> roles = usermanagementClient.getRoles();
        assertThat(roles, hasSize(5));
    }

    @Test
    public void addUserRoleTest() throws RequestException, IOException {
        usermanagementClient.addUserRole(1, 2);
        Collection<Role> roles = usermanagementClient.getUserRoles(1);
        assertThat(roles, hasSize(2));
    }

    @Test
    public void addPermissionTest() throws RequestException, IOException {
        usermanagementClient.addPermission(new Permission("TEST"));
        Collection<Permission> permissions = usermanagementClient.getPermissions();
        assertThat(permissions, hasSize(8));
    }

    @Test
    public void getUserRolesTest() throws RequestException, IOException {
        Collection<Role> roles = usermanagementClient.getUserRoles(1);
        assertThat(roles, hasSize(1));
    }

    @Test
    public void getRoleTest() throws RequestException, IOException, NotFoundException {
        Role role = usermanagementClient.getRole(1);
        assertThat(role.getRoleName(), is("Master"));
    }

    @Test
    public void getPermissionTest() throws RequestException, IOException, NotFoundException {
        Permission permission = usermanagementClient.getPermission(1);
        assertThat(permission.getPermissionValue(), is("thing:1:*"));
    }

    @Test
    public void getUsersTest() throws RequestException, IOException {
        Collection<User> users = usermanagementClient.getUsers();
        assertThat(users, hasSize(3));
    }

    @Test
    public void getRolesTest() throws RequestException, IOException {
        Collection<Role> roles = usermanagementClient.getRoles();
        assertThat(roles, hasSize(4));
    }

    @Test
    public void getPermissionsTest() throws RequestException, IOException {
        Collection<Permission> permissions = usermanagementClient.getPermissions();
        assertThat(permissions, hasSize(7));
    }

    @Test
    public void onlineStateTest() throws RequestException, IOException, NotFoundException {
        OnlineState onlinestateYoda = usermanagementClient.getOnlineState(1);
        OnlineState onlineStateNotYoda = usermanagementClient.getOnlineState(3);
        assertThat(onlinestateYoda, is(OnlineState.STATUS_ONLINE));
        assertThat(onlineStateNotYoda, is(OnlineState.STATUS_OFFLINE));
    }

}
