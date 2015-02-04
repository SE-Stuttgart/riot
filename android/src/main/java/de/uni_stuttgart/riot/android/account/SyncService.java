package de.uni_stuttgart.riot.android.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * The service responsible for syncing with the server.
 */
public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private static SyncAdapter syncAdapter;

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        // super.onCreate();
        Log.i(TAG, "Service created");
        synchronized (SyncService.class) {
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
        // super.onDestroy();
        syncAdapter = null;
        Log.i(TAG, "Service destroyed");
    }

    /**
     * Return an object that allows the system to invoke the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        /*
         * Get the object that allows external processes to call onPerformSync(). The object is created in the base class code when the
         * SyncAdapter constructors call super()
         */
        return syncAdapter.getSyncAdapterBinder();
    }

}
