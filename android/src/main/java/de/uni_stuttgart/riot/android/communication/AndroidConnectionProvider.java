package de.uni_stuttgart.riot.android.communication;

import java.io.IOException;

import de.uni_stuttgart.riot.android.account.AuthConstants;
import de.uni_stuttgart.riot.clientlibrary.ConnectionInformation;
import de.uni_stuttgart.riot.clientlibrary.ConnectionInformationProvider;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * The Token Manager saves the tokens in a account using the AccountManager. This enables the Token Manager to restore the tokens, even if
 * the instance was destroyed.
 */
public class AndroidConnectionProvider implements ConnectionInformationProvider {

    /**
     * This field can be used to store the activity that performs the current request. Make sure to always clear this field!
     */
    static final ThreadLocal<Activity> REQUESTING_ACTIVITY = new ThreadLocal<Activity>();

    private static final String DEFAULT_HOST = "belgrad.informatik.uni-stuttgart.de";
    private static final int DEFAULT_PORT = 8181;
    private static final String DEFAULT_PATH = "/riot/api/v1/";
    private static final String TAG = "AndroidConnectionProvider";
    private static final String CONNECTION_PREFERENCES = "ConnectionPreferences";

    /**
     * The singleton instance.
     */
    private static final AndroidConnectionProvider INSTANCE = new AndroidConnectionProvider();

    private ServerConnector connector;
    private Context context;
    private AccountManager accountManager;
    private Account currentAccount;

    /**
     * Singleton constructor.
     */
    private AndroidConnectionProvider() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return The instance.
     */
    public static AndroidConnectionProvider getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the connector.
     *
     * @param context
     *            Some Android context to be used for loading preferences, etc.
     * @return The connector.
     */
    public static ServerConnector getConnector(Context context) {
        return INSTANCE.internalGetConnector(context);
    }

    /**
     * Instantiates the connection provider.
     *
     * @param androidContext
     *            Some Android context to be used for loading preferences, etc.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void init(Context androidContext) {
        this.context = androidContext.getApplicationContext();
        this.accountManager = AccountManager.get(this.context);
        if (!loadCurrentAccount()) {
            loadFirstAccount();
        }
    }

    /**
     * Gets the connector, initializes it if necessary.
     *
     * @param androidContext
     *            The Android context to use if initialization is necessary.
     */
    private synchronized ServerConnector internalGetConnector(Context androidContext) {
        if (connector == null) {
            if (accountManager == null) {
                init(androidContext);
            }
            connector = new ServerConnector(this);
        }
        return connector;
    }

    /**
     * Reads the preferences and detects which one of the accounts was previously selected. Loads that one.
     *
     * @return True if successful.
     */
    private boolean loadCurrentAccount() {
        SharedPreferences prefs = context.getSharedPreferences(CONNECTION_PREFERENCES, 0);
        String accountName = prefs.getString("currentAccountName", null);
        if (accountName == null) {
            currentAccount = null;
            return false;
        } else {
            for (Account account : accountManager.getAccountsByType(AuthConstants.ACCOUNT_TYPE)) {
                if (account.name.equals(accountName)) {
                    currentAccount = account;
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Loads any of the accounts (the first one).
     *
     * @return True if successful.
     */
    private boolean loadFirstAccount() {
        Account[] accounts = accountManager.getAccountsByType(AuthConstants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            currentAccount = accounts[0];
            saveCurrentAccountName();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Saves the name of the current account to the settings so that {@link #loadCurrentAccount()} can restore it.
     */
    private void saveCurrentAccountName() {
        SharedPreferences prefs = context.getSharedPreferences(CONNECTION_PREFERENCES, 0);
        prefs.edit().putString("currentAccountName", currentAccount.name).commit();
    }

    /**
     * Gets the currently active account.
     *
     * @return The current account or <tt>null</tt>.
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     * Changes the currently active account.
     *
     * @param account
     *            The new account.
     */
    public void setCurrentAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null!");
        }
        currentAccount = account;
        saveCurrentAccountName();
    }

    /**
     * Gets the name of the currently active account.
     *
     * @return The name of the current account or <tt>null</tt>.
     */
    public String getCurrentAccountName() {
        return (currentAccount == null) ? null : currentAccount.name;
    }

    /**
     * Saves new account data, making this the active account. This is a convenience method that simply calls a couple of others.
     *
     * @param account
     *            The new account.
     * @param accessToken
     *            The access token.
     * @param refreshToken
     *            The refresh token.
     * @param user
     *            The new user.
     */
    public void saveNewAccountInformation(Account account, String accessToken, String refreshToken, User user) {
        setCurrentAccount(account);
        // Set refresh token before auth token! See TokenManager.setRefreshToken() for more information.
        setRefreshToken(refreshToken);
        setAccessToken(accessToken);
        if (connector != null && user != null) {
            connector.setCurrentUser(user);
        }
    }

    @Override
    public String getAccessToken() {
        if (currentAccount == null) {
            return null;
        } else {
            try {
                Activity requestingActivity = REQUESTING_ACTIVITY.get();
                if (requestingActivity == null) {
                    // The request is executed in a background thread.
                    // We use blockingGetAuthToken, which will show an Android notification in case the authentication fails.
                    return accountManager.blockingGetAuthToken(currentAccount, AuthConstants.ACCESS_TOKEN_TYPE, true);
                } else {
                    // The request is executed in the context of an activity, i.e. in the foreground.
                    // We use getAuthToken and pass this activity to indicate that its context can be used to display a login form, if
                    // necessary.
                    return accountManager //
                            .getAuthToken(currentAccount, AuthConstants.ACCESS_TOKEN_TYPE, null, requestingActivity, null, null) //
                            .getResult() //
                            .getString(AccountManager.KEY_AUTHTOKEN);
                }
            } catch (AuthenticatorException e) {
                Log.e(TAG, "Error when getting auth token!", e);
                return null;
            } catch (OperationCanceledException e) {
                Log.e(TAG, "Error when getting auth token!", e);
                throw new AbortTaskException(e);
            } catch (IOException e) {
                Log.e(TAG, "Error when getting auth token!", e);
                return null;
            }

        }
    }

    @Override
    public void setAccessToken(String accessToken) {
        if (currentAccount == null) {
            loadCurrentAccount();
            loadFirstAccount();
        }
        if (currentAccount != null) {
            invalidateAccessToken();
            if (accessToken != null) {
                accountManager.setAuthToken(currentAccount, AuthConstants.ACCESS_TOKEN_TYPE, accessToken);
            }
        }
    }

    @Override
    public String getRefreshToken() {
        return (currentAccount == null) ? null : accountManager.getPassword(currentAccount);
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        if (currentAccount == null) {
            loadCurrentAccount();
            loadFirstAccount();
        }
        if (currentAccount != null) {
            accountManager.setPassword(currentAccount, refreshToken);
        }
    }

    @Override
    public void invalidateAccessToken() {
        if (currentAccount != null) {
            String accessToken = accountManager.peekAuthToken(currentAccount, AuthConstants.ACCESS_TOKEN_TYPE);
            if (accessToken != null) {
                accountManager.invalidateAuthToken(AuthConstants.ACCOUNT_TYPE, accessToken);
            }
        }
    }

    @Override
    public ConnectionInformation getInformation() {
        return (context == null) ? null : loadConnectionInformation(context);
    }

    /**
     * Loads the connection information from the preferences store.
     *
     * @param androidContext
     *            The Android context.
     * @return The loaded information.
     */
    public ConnectionInformation loadConnectionInformation(Context androidContext) {
        SharedPreferences prefs = androidContext.getSharedPreferences(CONNECTION_PREFERENCES, 0);
        String host = prefs.getString("host", DEFAULT_HOST);
        int port = prefs.getInt("port", DEFAULT_PORT);
        String path = prefs.getString("path", DEFAULT_PATH);
        return new ConnectionInformation(host, port, path);
    }

    /**
     * Saves and applies the new connection information.
     *
     * @param androidContext
     *            The Android context.
     * @param host
     *            The new host name (or IP).
     * @param port
     *            The new port number.
     * @param path
     *            The new REST base path.
     */
    public void setNewConnectionInformation(Context androidContext, String host, int port, String path) {
        SharedPreferences prefs = androidContext.getSharedPreferences(CONNECTION_PREFERENCES, 0);
        prefs.edit().putString("host", host).putInt("port", port).putString("path", path).commit();
        if (connector != null) {
            connector.refreshConnectionInformation();
        }
    }

    @Override
    public ConnectionInformation getNewInformation(ConnectionInformation oldInformation) {
        return getInformation();
    }

    @Override
    public boolean relogin(ServerConnector serverConnector) {
        return false;
    }

    @Override
    public boolean handlesTokenRefresh() {
        return true;
    }

}
