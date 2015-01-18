package de.uni_stuttgart.riot.android.account;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import com.google.gson.Gson;

import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
/**
 * This class contains the currently logged in user and allows the management of this user. The clear text password will not be saved
 * persistently, because of security reasons. Instead the authentication and refresh tokens are saved persistently for further
 * authentication. <br>
 * <br>
 * After the user is logged in, calls to the API can be made independently from this class over the {@link RIOTApiClient}, because the
 * tokens are saved via the {@link AccountManager}. <br>
 * <br>
 * The class contains also the user and its permissions.
 * 
 * @author Niklas Schnabel
 */
public class AndroidUser {

    /** Used as key for saving the user information in the account via the {@link AccountManager} */
    private static final String USER = "user";

    /** The user. */
    private UserResponse user;

    /** The user management client client. */
    private UsermanagementClient umClient;

    /** The login client. */
    private LoginClient loginClient;

    /** The account manager. */
    private AccountManager accountManager;

    /** The account. */
    private Account account;

    /**
     * Instantiates a new user.
     *
     * @param activity
     *            the activity
     */
    public AndroidUser(Activity activity) {
        accountManager = AccountManager.get(activity);
        account = RIOTAccount.getRIOTAccount(activity).getAccount();
        umClient = RIOTApiClient.getInstance().getUserManagementClient();
        loginClient = RIOTApiClient.getInstance().getLoginClient();

        String userData = accountManager.getUserData(account, USER);
        if (StringUtils.isNotEmpty(userData)) {
            user = new Gson().fromJson(userData, UserResponse.class);
        }
    }

    /**
     * Log the user in.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     */
    public void logIn(String username, String password) {
        try {
            user = loginClient.login(username, password);
            saveUserInAccount();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Log the user out.
     */
    public void logOut() {
        try {
            loginClient.logout();
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Fetch roles and permissions from the server.
     */
    public void refreshRolesAndPermissions() {
        try {
            Collection<RoleResponse> userRoles = umClient.getUserRoles(user.getUser().getId());
            user.setRoles(userRoles);
            saveUserInAccount();
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public UserResponse getUser() {
        return user;
    }

    /**
     * Gets the locally cached roles. To update the roles use {@link #refreshRolesAndPermissions()}. <br>
     * <br>
     * Roles added to the collection will not be synced with the server. To add new roles to a user, use the UserManagementClient (
     * {@link RIOTApiClient#getUserManagementClient()}).
     * 
     *
     * @return the roles
     */
    public Collection<RoleResponse> getRoles() {
        return user == null ? null : user.getRoles();
    }

    /**
     * Checks if the user has the given role.
     *
     * @param roleName
     *            the role name
     * @return true, if successful
     */
    public boolean hasRole(String roleName) {
        if (user == null) {
            return false;
        }

        for (RoleResponse roles : user.getRoles()) {
            if (StringUtils.equals(roles.getRole().getRoleName(), roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the roles assigned to the user has the given permission.
     *
     * @param permissionName
     *            the permission name
     * @return true, if successful
     */
    public boolean hasPermission(String permissionName) {
        if (user == null) {
            return false;
        }

        for (RoleResponse roles : user.getRoles()) {
            for (PermissionResponse permissions : roles.getPermissions()) {
                if (StringUtils.equals(permissions.getPermission().getPermissionValue(), permissionName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Save user in account.
     */
    private void saveUserInAccount() {
        if (user != null) {
            String userJson = new Gson().toJson(user);
            accountManager.setUserData(account, USER, userJson);
        } else {
            // delte the user
            accountManager.setUserData(account, USER, null);
        }
    }
}
