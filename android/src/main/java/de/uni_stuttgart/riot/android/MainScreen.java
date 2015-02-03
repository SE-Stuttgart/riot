package de.uni_stuttgart.riot.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.communication.ServerConnection;
import de.uni_stuttgart.riot.android.database.FilterDataObjects;
import de.uni_stuttgart.riot.android.language.Language;

/**
 * Main screen template.
 *
 */
public class MainScreen extends Activity {

    private String getValueFromIntent;

    private FilterDataObjects db;

    private ListView notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets the intent from HomeScreen
        Intent intent = getIntent();

        // Get the value of the pressed button from the HomeScreen
        getValueFromIntent = intent.getStringExtra("pressedButton");

        // Initialize database
        db = new FilterDataObjects(this, getValueFromIntent);

        // Initialize the API client. Initialization is not allowed in the main thread.
        final MainScreen instance = this;
        new Thread() {
            public void run() {
                // TODO: add device name
                RIOTApiClient.getInstance().init(instance, "AndriodApp");
            }
        }.start();

        // Sets the language
        Language.setLanguage(this);

        // Sets the layout
        setContentView(R.layout.activity_main);

        // ActionBar events
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(getValueFromIntent);

        // ClickListener for the notificationList
        notificationList = (ListView) findViewById(R.id.NotificationList);
        notificationList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });

        // Get the latest notifications
        new ServerConnection(this, db).execute();
    }

    public String getPressedHomeScreenButton() {
        return getValueFromIntent;
    }

    /**
     * Prepare the refresh button on the right side.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define displaying settings for the refresh button.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // //get the values for the each filter from the database
        menu.findItem(R.id.filter_error).setChecked(db.getFilterStatus(NotificationType.ERROR));
        menu.findItem(R.id.filter_warning).setChecked(db.getFilterStatus(NotificationType.WARNING));
        menu.findItem(R.id.filter_appointment).setChecked(db.getFilterStatus(NotificationType.APPOINTMENT));

        // Refresh button, filter buttons and the notification list is only
        // shown in the home screen
        if (!getActionBar().getTitle().equals("Home")) {
            menu.findItem(R.id.action_refresh).setVisible(false);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
            notificationList.setAdapter(null);
        } else {
            menu.findItem(R.id.action_refresh).setVisible(true);
            db.getDatabase().filterNotifications();
        }

        menu.findItem(R.id.action_refresh).setVisible(true);
        db.getDatabase().filterNotifications();

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Actions for the refresh button (right upper corner). It can later be extended with more options.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.filter_error:
            db.setFilter(new Filter(1, item, NotificationType.ERROR, false));
            return true;
        case R.id.filter_appointment:
            db.setFilter(new Filter(2, item, NotificationType.APPOINTMENT, false));
            return true;
        case R.id.filter_warning:
            db.setFilter(new Filter(3, item, NotificationType.WARNING, false));
            return true;
        case R.id.action_refresh:
            new ServerConnection(this, db).execute();
            NotificationAdapter chapterListAdapter = new NotificationAdapter(getApplicationContext(), db.getDatabase().filterNotifications());
            ListView notification = (ListView) findViewById(R.id.NotificationList);
            notification.setAdapter(chapterListAdapter);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
