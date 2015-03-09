package de.uni_stuttgart.riot.android;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.language.Language;
import de.uni_stuttgart.riot.android.serverconfiguration.ServerConfigurationScreen;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Setting screen.
 */
public class SettingScreen extends Activity {

    /** The Constant for the Administrator Role. TODO: using MASTER for testing. Change to ADMIN. */
    private static final String ADMIN_ROLE = "Master";

    private Button btnLanguage;
    private Button btnColorCalendar;

    private Intent intent;
    private ActionBar actionBar;

    private int choice = 0;
    private int selectedLanguage;

    private RIOTDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseAccess.getDatabase();
        intent = getIntent();

        if (db.getLanguageCount() > 0) {
            if (db.getLanguage().equals("en")) {
                selectedLanguage = 0;
            } else {
                selectedLanguage = 1;
            }
        } else {
            db.setLanguage("en");
            selectedLanguage = 0;
        }

        Language.setLanguage(this);

        setContentView(R.layout.setting_screen);

        // Sets the title and icon of the action bar
        actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.settings));
        actionBar.setIcon(R.drawable.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Button opens the language dialog
        btnLanguage = (Button) findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });

        final ColorPicker picker = new ColorPicker(getApplicationContext(), new ColorPicker.OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                Log.v("RIOT", "Color:" + color);
                // ContentProviderClient client =
                // SettingScreen.this.getContentResolver().acquireContentProviderClient(CalendarContract.Calendars.CONTENT_URI);
                // Calendar cal = new Calendar(AndroidUser.getAccount(SettingScreen.this.getApplicationContext()), client, "RIOT");
                // cal.changeColor(color);
            }
        }, 0x000000);
        btnColorCalendar = (Button) findViewById(R.id.btnColor);
        btnColorCalendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show();
            }
        });

        // checks if current user has admin role and if it is true, shows server configuration
        showAdminSettings();
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.language).setSingleChoiceItems(R.array.language_array, selectedLanguage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                choice = which;
            }
        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setSelectedLanguage(choice);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create();
        builder.show();
    }

    private void setSelectedLanguage(int getChoice) {
        if (choice == 0) {
            db.setLanguage("en");
            restartActivity();
        }

        if (choice == 1) {
            db.setLanguage("de");
            restartActivity();
        }
    }

    private void restartActivity() {
        finish();
        startActivity(intent);
    }

    /**
     * Function to go back to the HomeScreen.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows admin settings button to go to server settings activity, but only if logged in user has admin role.
     */
    private void showAdminSettings() {

        new ActivityServerConnection<User>(this) {
            @Override
            protected User executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    Log.v("SettingScreen", "getting current user.");
                    return new UsermanagementClient(serverConnector).getCurrentUser();
                } catch (NotFoundException e) {
                    Log.v("SettingScreen", "Exception getting current user.", e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onSuccess(User result) {
                Log.v("SettingScreen", "Received user: " + result.getUsername());
                boolean hasAdminRole = false;
                for (Role role : result.getRoles()) {
                    if (role.getRoleName().equalsIgnoreCase(ADMIN_ROLE)) {
                        hasAdminRole = true;
                        break;
                    }
                }

                if (hasAdminRole) {
                    // current user is ADMIN
                    ((TextView) findViewById(R.id.textViewServerConfig)).setVisibility(View.VISIBLE);
                    ((View) findViewById(R.id.viewServerConfig)).setVisibility(View.VISIBLE);

                    Button btnConfig = (Button) findViewById(R.id.btnManageConfig);
                    btnConfig.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startServerConfigurationActivity();
                        }
                    });
                    btnConfig.setVisibility(View.VISIBLE);

                } else {
                    ((Button) findViewById(R.id.btnManageConfig)).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.textViewServerConfig)).setVisibility(View.GONE);
                    ((View) findViewById(R.id.viewServerConfig)).setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    /**
     * Start server configuration activity when button in settingScreen is pressed.
     */
    private void startServerConfigurationActivity() {
        Intent serverConfigurationIntent = new Intent(this, ServerConfigurationScreen.class);
        serverConfigurationIntent.putExtra("pressedButton", this.getString(R.string.manageConfiguration));
        this.startActivity(serverConfigurationIntent);
    }
}
