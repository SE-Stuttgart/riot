package de.uni_stuttgart.riot.android;

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
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.ServerConnection;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.notification.NotificationType;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
/**
 * The main window.
 */
public class NotificationScreen extends Activity {

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
        database.setInvokedNotificationScreen(pressedHomeScreenButton);
        database.setNotificationScreen(this);

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
            new ServerConnection(this, database).execute();
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
        menu.findItem(R.id.filter_error).setChecked(database.getFilterSettings(NotificationType.ERROR));
        menu.findItem(R.id.filter_warning).setChecked(database.getFilterSettings(NotificationType.WARNING));
        menu.findItem(R.id.filter_appointment).setChecked(database.getFilterSettings(NotificationType.APPOINTMENT));

        menu.findItem(R.id.action_refresh).setVisible(true);
        database.filterNotifications();

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Actions for the refresh button (right upper corner). It can later be extended with more options.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.filter_error:
            database.setFilter(new FilterItem(1, item, NotificationType.ERROR, false));
            return true;
        case R.id.filter_appointment:
            database.setFilter(new FilterItem(2, item, NotificationType.APPOINTMENT, false));
            return true;
        case R.id.filter_warning:
            database.setFilter(new FilterItem(3, item, NotificationType.WARNING, false));
            return true;
        case R.id.action_refresh:
            new ServerConnection(this, database).execute();
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
