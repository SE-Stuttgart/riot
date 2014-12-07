package de.enpro.android_riot.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;

/**
 * Created by dirkmb on 12/7/14.
 */
public class RIOTAccount {
    // static stuff
    private final static String TAG= "RIOTAccount";

    public static final long SYNC_FREQUENCY = 60 * 60;
    public static final String ACCOUNT_TYPE = "de.enpro.android_riot.account.type";
    public static final String AUTHORITY = "de.enpro.android_riot.account.auth";

    private static RIOTAccount riot_acc;

    // get function for singleton
    public static RIOTAccount getRIOTAccount(Context ctx)
    {
        if(riot_acc == null)
            riot_acc = new RIOTAccount(ctx);
        return riot_acc;
    }

    // constructor for singleton
    private RIOTAccount(Context ctx)
    {
        this.ctx = ctx;
        getOrCreateAccount();
    }

    Account account;
    Context ctx;

    private void getOrCreateAccount()
    {
        if(account != null)
        {
            return;
        }

        // search account
        AccountManager accountManager = AccountManager.get(ctx);
        Account allAccounts[] = accountManager.getAccountsByType(ACCOUNT_TYPE);
        for(int i = 0 ; i < allAccounts.length ; i++) {
            account = allAccounts[i];
            Log.v(TAG, "Found account: " + account);
            return;
        }

        // if we reach this line there is no account, so create one
        account = new Account(AUTHORITY, ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(account, null, null))
        {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);

            Log.v(TAG, "New Account added"+account);
            return;
        }
        account = null;
    }

    void AddAccount() {
        //ContentProviderClient client = getContentResolver().acquireContentProviderClient(CalendarContract.AUTHORITY);
        Account acc = new Account(AUTHORITY, ACCOUNT_TYPE);
        //Calendar cal = new Calendar(acc, client, "RIOT");
        //cal.AddEvent("MyEvent");
    }


}
