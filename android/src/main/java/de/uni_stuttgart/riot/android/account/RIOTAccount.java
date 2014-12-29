package de.uni_stuttgart.riot.android.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * A RIOT Android account.
 */
public class RIOTAccount {

    /**
     * The authority of the accounts.
     */
    public static final String AUTHORITY = "de.uni_stuttgart.riot.android.account.auth";

    private static final String TAG = "RIOTAccount";

    private static final long SYNC_FREQUENCY = 60 * 60;
    private static final String ACCOUNT_TYPE = "de.uni_stuttgart.riot.android.account.type";

    /**
     * Singleton instance holder.
     */
    private static RIOTAccount riotAccount;

    private Account account;
    private Context ctx;

    /**
     * Private singleton constructor.
     * 
     * @param ctx
     *            The Android context.
     */
    private RIOTAccount(Context ctx) {
        this.ctx = ctx;
        getOrCreateAccount();
    }

    /**
     * Get function for singleton. FIXME Is it really a good idea to use a singleton pattern here?
     * 
     * @param ctx
     *            The Android context.
     * @return The single instance.
     */
    public static RIOTAccount getRIOTAccount(Context ctx) {
        if (riotAccount == null) {
            riotAccount = new RIOTAccount(ctx);
        }
        return riotAccount;
    }

    /**
     * Gets the underlying Android account.
     * 
     * @return The underlying Android account.
     */
    public Account getAccount() {
        getOrCreateAccount();
        return account;
    }

    private void getOrCreateAccount() {
        if (account != null) {
            return;
        }

        // search account
        AccountManager accountManager = AccountManager.get(ctx);
        Account[] allAccounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (allAccounts.length > 0) {
            account = allAccounts[0];
            Log.v(TAG, "Found account: " + account);
            return;
        }

        // if we reach this line there is no account, so create one
        account = new Account(AUTHORITY, ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);

            Log.v(TAG, "New Account added" + account);
            return;
        }
        account = null;
    }

}
