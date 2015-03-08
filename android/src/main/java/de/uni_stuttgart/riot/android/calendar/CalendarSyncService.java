package de.uni_stuttgart.riot.android.calendar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * The service responsible for syncing the calendar data with the server.
 */
public class CalendarSyncService extends Service {

    /**
     * The synchronization frequency of the calendar in seconds.
     */
    public static final int SYNC_FREQUENCY = 1800;

    private static final String TAG = "CalendarSyncService";

    private static CalendarSyncAdapter syncAdapter;

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");
        synchronized (CalendarSyncService.class) {
            if (syncAdapter == null) {
                syncAdapter = new CalendarSyncAdapter(getApplicationContext());
                Log.i(TAG, "New syncAdapter created");
            }
        }
    }

    /**
     * Logging-only destructor.
     */
    @Override
    public void onDestroy() {
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
