package de.uni_stuttgart.riot.android.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle the authentication process. This service is used to run the {@link AccountAuthenticator} in its own context.
 */
public class AccountAuthenticatorService extends Service {
    private static final String TAG = "AccountService";

    private AccountAuthenticator mAccountAuthenticator;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
    }

    private AccountAuthenticator getAuthenticator() {
        if (mAccountAuthenticator == null) {
            mAccountAuthenticator = new AccountAuthenticator(this);
        }
        return mAccountAuthenticator;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            return getAuthenticator().getIBinder();
        }
        return null;
    }
}
