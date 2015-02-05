package de.uni_stuttgart.riot.android.account;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

//CHECKSTYLE:OFF FIXME PLEASE FIX THE CHECKSTYLE ERRORS IN THIS FILE AND DONT COMMIT FILES THAN CONTAIN CHECKSTYLE ERRORS
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
    /** The tag for the android logging **/
    private static final String TAG = "AndroidUser";

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
        account = getAccount(context);
        umClient = RIOTApiClient.getInstance().getUserManagementClient();
        loginClient = RIOTApiClient.getInstance().getLoginClient();

        if(account != null) {
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
    }

    /**
     * Get the fist android account for our account type
     *
     * @param context the android context
     */
    static public Account getAccount(Context context) {
        // search account
        AccountManager accountManager = AccountManager.get(context);
        Account[] allAccounts = accountManager
                .getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
        if (allAccounts.length > 0) {
            Account account = allAccounts[0];
            Log.v("AndroidUser", "Found account: " + account);
            return account;
        }
        return null;
    }

    /**
     * Create a new Android account
     *
     * @param username the username
     * @param context the android context
     *
     */
    boolean CreateAndroidAccount(String username, Context context) {
        Account account = new Account(username, context.getString(R.string.ACCOUNT_TYPE));
        boolean accountCreated = accountManager.addAccountExplicitly(account, "", new Bundle());
        if(accountCreated) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, context.getString(R.string.ACCOUNT_TYPE), 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, context.getString(R.string.ACCOUNT_TYPE), true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, context.getString(R.string.ACCOUNT_TYPE), new Bundle(),
                    Long.getLong(context.getString(R.string.ACCOUNT_SYNC_FREQ)));
        }
        return accountCreated;
    }

    /**
     * Log the user in.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     */
    public boolean logIn(String username, String password) {
        try {
            user = loginClient.login(username, password);
            if(user != null) {
                saveUserInAccount();
                return true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.v(TAG, "could not connect to server!");

            // FIXME Use NotificationManager
        } catch (RequestException e) {
            e.printStackTrace();
            Log.v(TAG, "could not connect to server!");

            // FIXME Use NotificationManager
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "could not connect to server!");

            // FIXME Use NotificationManager
        }
        return false;
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
