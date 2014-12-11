package de.enpro.android_riot.account;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CalendarChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "CalendarChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "calendar changed! " + intent.toUri(Intent.URI_INTENT_SCHEME));

        //TODO add code to synchronize to server
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(RIOTAccount.getRIOTAccount(context).getAccount(), RIOTAccount.AUTHORITY, settingsBundle);
    }
}