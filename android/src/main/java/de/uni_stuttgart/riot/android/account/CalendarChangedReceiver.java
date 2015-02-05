package de.uni_stuttgart.riot.android.account;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * This class receives calder change events. So if the user changes something with the default calendar app we will be notified.
 */
public class CalendarChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "CalendarChangedReceiver";

    /*
     * (non-Javadoc)
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "calendar changed! " + intent.toUri(Intent.URI_INTENT_SCHEME));

        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Account acc = AndroidUser.getAccount(context);
        if (acc != null) {
            ContentResolver.requestSync(acc, acc.name, settingsBundle);
        }
    }
}
