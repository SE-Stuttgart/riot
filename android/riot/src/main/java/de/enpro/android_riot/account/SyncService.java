package de.enpro.android_riot.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private static final Object syncAdapterLock = new Object();
    private static SyncAdapter syncAdapter;

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        //super.onCreate();
        Log.i(TAG, "Service created");
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext());
                Log.i(TAG, "New syncAdapter created");
            }
        }
    }

    @Override
    /**
     * Logging-only destructor.
     */
    public void onDestroy() {
        //super.onDestroy();
        syncAdapter = null;
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

}

