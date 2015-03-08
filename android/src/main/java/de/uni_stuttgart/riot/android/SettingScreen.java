package de.uni_stuttgart.riot.android;

import android.accounts.Account;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.language.Language;
import de.uni_stuttgart.riot.android.serverconfiguration.ServerConfigurationScreen;

/**
 * Setting screen.
 */
public class SettingScreen extends Activity {

    /** The Constant for the Administrator Role. TODO: using MASTER for testing. Change to ADMIN. */
    private static final String ADMIN_ROLE = "Master";

    /** Flag that tells if current logged in user has Administrator Role. */
    private boolean isAdmin = false;

    /** The current logged in android user. */
    private AndroidUser androidUser;

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
     * Check if current user has ADMIN role.
     */
    private void checkCurrentUserRole() {

        if (androidUser == null) {
            // TODO: how to find current username?
            Account[] accounts = AndroidUser.getAccounts(this);
            if (accounts != null) {
                androidUser = new AndroidUser(this, accounts[0].name);
            }
        }

        // FIXME androidUser.isLoggedIn() -> no server request works after this is called
        if (androidUser != null && /* androidUser.isLoggedIn() && */androidUser.hasRole(ADMIN_ROLE)) {
            this.isAdmin = true;
        } else {
            this.isAdmin = false;
        }
    }

    /**
     * Show admin settings button to go to server settings activity.
     */
    private void showAdminSettings() {
        // if logged in with admin rights, shows server configuration option
        Thread thread = new Thread() {
            @Override
            public void run() {
                checkCurrentUserRole();
            }
        };
        thread.start();

        // waits for thread to finish processing
        try {
            final int timeout = 2000;
            thread.join(timeout);
            if (!isAdmin) {
                return;
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }

        // current user is ADMIN

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        TextView label = new TextView(this);
        label.setText(R.string.serverConfiguration);
        final int paddingStart = 30;
        final int paddingTop = 50;
        final int paddingBottom = 10;
        label.setPaddingRelative(paddingStart, paddingTop, 0, paddingBottom);

        Button btnManageConfiguration = new Button(this);
        btnManageConfiguration.setText(R.string.manageConfiguration);
        btnManageConfiguration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startServerConfigurationActivity();
            }
        });

        LinearLayout ll = (LinearLayout) findViewById(R.id.settingScreen);
        ll.addView(label, lp);
        ll.addView(btnManageConfiguration, lp);
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
