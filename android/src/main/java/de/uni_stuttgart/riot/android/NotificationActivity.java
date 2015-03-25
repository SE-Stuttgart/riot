package de.uni_stuttgart.riot.android;

import java.io.IOException;
import java.util.Collection;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.client.NotificationClient;

// CHECKSTYLE:OFF FIXME Fix checkstyle errors.
/**
 * The notification screen that displays all notifications of the current user and allows filtering.
 */
public class NotificationActivity extends Activity {

    private ListView notificationList;

    private String pressedHomeScreenButton;

    private RIOTDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // get the value of the pressed button
        pressedHomeScreenButton = intent.getStringExtra("pressedButton");

        database = DatabaseAccess.getDatabase();
        
        //database.setInvokedNotificationScreen(pressedHomeScreenButton);
        //database.setNotificationScreen(this);
        // TODO Use the getFilteredNotifications()

        // Save the application context in the singleton objects
        IM.INSTANCES.setContext(getApplicationContext());

        // Sets the language
        // Language.setLanguage(this);

        setContentView(R.layout.notification_screen);

        getActionBar().setTitle(pressedHomeScreenButton);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (pressedHomeScreenButton.equals("Car")) {
            getActionBar().setIcon(R.drawable.car);
        } else if (pressedHomeScreenButton.equals("House")) {
            getActionBar().setIcon(R.drawable.house);
        } else if (pressedHomeScreenButton.equals("Coffee")) {
            getActionBar().setIcon(R.drawable.coffee);
        }

        // ClickListener for the Notification List
        notificationList = (ListView) findViewById(R.id.NotificationList);
        notificationList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                System.out.println("click");

            }
        });

        if (isNetworkAvailable()) {
            // get the latest Notifications
            refreshNotificationsFromServer();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Delete all saved notifications
        IM.INSTANCES.getNF().clearPreparedNotifications();
    }

    /**
     * 
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Prepare the refresh button on the right side
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define displaying settings for the refresh button
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // //get the values for the each filter from the database
        // TODO Redo filters
//        menu.findItem(R.id.filter_error).setChecked(database.getFilterSettings(NotificationType.ERROR));
//        menu.findItem(R.id.filter_warning).setChecked(database.getFilterSettings(NotificationType.WARNING));
//        menu.findItem(R.id.filter_appointment).setChecked(database.getFilterSettings(NotificationType.APPOINTMENT));

        menu.findItem(R.id.action_refresh).setVisible(true);
        // TODO What's this? database.filterNotifications();

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Actions for the refresh button (right upper corner). It can later be extended with more options.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO Redo filters
        switch (item.getItemId()) {
        case R.id.filter_error:
            //database.setFilter(new FilterItem(1, item, NotificationType.ERROR, false));
            return true;
        case R.id.filter_appointment:
            //database.setFilter(new FilterItem(2, item, NotificationType.APPOINTMENT, false));
            return true;
        case R.id.filter_warning:
            //database.setFilter(new FilterItem(3, item, NotificationType.WARNING, false));
            return true;
        case R.id.action_refresh:
            refreshNotificationsFromServer();
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void refreshNotificationsFromServer() {
        new ActivityServerConnection<Collection<Notification>>(this) {
            protected Collection<Notification> executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                return new NotificationClient(serverConnector).getOutstandingNotifications();
            }

            protected void onSuccess(Collection<Notification> result) {
                DatabaseAccess.getDatabase().setAllNotifications(result);
                // TODO: Update the adapter and stuff here.
            }
        }.execute();
    }
    
}
