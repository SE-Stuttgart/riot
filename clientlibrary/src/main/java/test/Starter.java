package test;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

/**
 * TestClass.
 *
 */
public class Starter {
    /*
    public static void main(String[] args) throws Exception {
        LoginClient loginClient = new LoginClient("http://localhost:8080/", "Test");
        UsermanagementClient client = new UsermanagementClient(loginClient);
        loginClient.login("Yoda", "YodaPW");
        //GET
        System.out.println(client.getPermissions());
        System.out.println(client.getUsers());
        System.out.println(client.getRoles());
        System.out.println(client.getUserRoles(1L));
        RoleResponse rR = client.getRole(1L);
        System.out.println(rR);
        rR.getRole().setRoleName("Test");
        UserResponse uR = client.getUser(1L);
        System.out.println(uR);
        PermissionResponse pR = client.getPermission(1L);
        System.out.println(pR);
        //PUT
        pR.getPermission().setPermissionValue("TEST2");
        System.out.println(client.updatePermission(1, pR.getPermission()));
        System.out.println(client.updateRole(1, rR.getRole()));
        UserRequest uReq = new UserRequest();
        uReq.setPassword("Test");
        uReq.setUsername("TestUser");
        System.out.println(client.updateUser(2,uReq));
        uReq.setUsername("TestUser"+System.currentTimeMillis());
        UserResponse newUser = client.addUser(uReq);
        long id = newUser.getUser().getId();
        System.out.println(client.removeUser(id));
        client.addPermission(new Permission("Test"));
        loginClient.logout();
    }
    */
}
