package de.uni_stuttgart.riot.android.account;

import java.io.IOException;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.uni_stuttgart.riot.android.calendar.CalendarSyncService;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.UnauthenticatedException;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;

/**
 * Implement AbstractAccountAuthenticator and stub out all of its methods.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "AccountAuthenticator";

    private final Context context;

    /**
     * Simple constructor.
     *
     * @param context
     *            the android context.
     */
    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        if (!accountType.equals(AuthConstants.ACCOUNT_TYPE) || (authTokenType != null && !authTokenType.equals(AuthConstants.ACCESS_TOKEN_TYPE))) {
            Log.v(TAG, "addAccount for invalid types " + accountType + " " + authTokenType);
            return null;
        }

        Log.v(TAG, "addAccount called, showing activity");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction("de.uni_stuttgart.riot.android.account.ADD");
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle reply = new Bundle();
        reply.putParcelable(AccountManager.KEY_INTENT, intent);
        return reply;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse r, Account account, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse r, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        // Only our own token type is supported.
        if (!authTokenType.equals(AuthConstants.ACCESS_TOKEN_TYPE)) {
            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid authTokenType!");
            return bundle;
        }

        // Try getting a new token using the refresh token.
        boolean hadNetworkError = false;
        AccountManager accountManager = AccountManager.get(context);
        String refreshToken = accountManager.getPassword(account);
        if (refreshToken != null && !refreshToken.isEmpty()) {
            ServerConnector connector = AndroidConnectionProvider.getConnector(context);
            try {
                AuthenticationResponse response = connector.executeRefreshRequest(refreshToken);
                accountManager.setPassword(account, response.getRefreshToken());
                Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthConstants.ACCOUNT_TYPE);
                bundle.putString(AccountManager.KEY_AUTHTOKEN, response.getAccessToken());
                return bundle;
            } catch (IOException e) {
                hadNetworkError = true;
                Log.e(TAG, "Error when retrieving new tokens", e);
            } catch (UnauthenticatedException e) {
                accountManager.setPassword(account, null);
                Log.e(TAG, "The current refresh token is invalid, removing it", e);
            } catch (RequestException e) {
                Log.e(TAG, "Error when retrieving new tokens", e);
            }
        }

        // Refresh didn't work for some reason, so we need to prompt the user for relogin.
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.KEY_USERNAME, account.name);
        intent.putExtra(LoginActivity.KEY_SHOW_CONNECTION_SETTINGS, hadNetworkError);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AuthConstants.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, r);
        Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType.equals(AuthConstants.ACCESS_TOKEN_TYPE) ? authTokenType : null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse r, Account account, String[] strings) throws NetworkErrorException {
        // Whatever the feature is: we don't have it.
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse r, Account account, String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the account for the given username, first creates it if necessary.
     *
     * @param context
     *            The context.
     * @param username
     *            The username to search for.
     * @return The account. Never null.
     */
    public static Account getOrCreateAccount(Context context, String username) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] allAccounts = accountManager.getAccountsByType(AuthConstants.ACCOUNT_TYPE);
        for (Account account : allAccounts) {
            if (account.name.equals(username)) {
                return account;
            }
        }

        Account account = new Account(username, AuthConstants.ACCOUNT_TYPE);
        boolean accountCreated = AccountManager.get(context).addAccountExplicitly(account, "", new Bundle());
        if (accountCreated) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AuthConstants.ACCOUNT_TYPE, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AuthConstants.ACCOUNT_TYPE, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, AuthConstants.ACCOUNT_TYPE, new Bundle(), CalendarSyncService.SYNC_FREQUENCY);
        } else {
            return null;
        }
        return account;
    }

}
