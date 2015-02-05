package de.uni_stuttgart.riot.android.account;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

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
    private User user;

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
     * @param context
     *            the activity
     */
    public AndroidUser(Context context) {
        accountManager = AccountManager.get(context);
        account = RIOTAccount.getRIOTAccount(context).getAccount();
        umClient = RIOTApiClient.getInstance().getUserManagementClient();
        loginClient = RIOTApiClient.getInstance().getLoginClient();

        String userData = accountManager.getUserData(account, USER);
        if (StringUtils.isNotEmpty(userData)) {
            try {
                user = new ObjectMapper().readValue(userData, User.class);
            } catch (JsonParseException e) {
                // FIXME Use NotificationManager
            } catch (JsonMappingException e) {
                // FIXME Use NotificationManager
            } catch (IOException e) {
                // FIXME Use NotificationManager
            }
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
            // FIXME Use NotificationManager
        } catch (RequestException e) {
            // FIXME Use NotificationManager
        } catch (IOException e) {
            // FIXME Use NotificationManager
        }
    }

    /**
     * Log the user out.
     */
    public void logOut() {
        try {
            loginClient.logout();
            user = null;
            saveUserInAccount();
        } catch (JsonGenerationException e) {
            // FIXME Use NotificationManager
        } catch (JsonMappingException e) {
            // FIXME Use NotificationManager
        } catch (UnsupportedEncodingException e) {
            // FIXME Use NotificationManager
        } catch (RequestException e) {
            // FIXME Use NotificationManager
        } catch (IOException e) {
            // FIXME Use NotificationManager
        }
    }

    /**
     * Fetch roles and permissions from the server.
     *
     * @throws RequestException
     *             the request exception
     */
    public void refreshRolesAndPermissions() throws RequestException {
        Collection<Role> userRoles = umClient.getUserRoles(user.getId());
        user.setRoles(userRoles);
        saveUserInAccount();
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
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
    public Collection<Role> getRoles() {
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

        for (Role roles : user.getRoles()) {
            if (StringUtils.equals(roles.getRoleName(), roleName)) {
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

        for (Role roles : user.getRoles()) {
            for (Permission permissions : roles.getPermissions()) {
                if (StringUtils.equals(permissions.getPermissionValue(), permissionName)) {
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
            try {
                String userJson = new ObjectMapper().writeValueAsString(user);
                accountManager.setUserData(account, USER, userJson);
            } catch (JsonGenerationException e) {
                // FIXME Use NotificationManager
            } catch (JsonMappingException e) {
                // FIXME Use NotificationManager
            } catch (IOException e) {
                // FIXME Use NotificationManager
            }
        } else {
            // delete the user
            accountManager.setUserData(account, USER, null);
        }
    }
}
