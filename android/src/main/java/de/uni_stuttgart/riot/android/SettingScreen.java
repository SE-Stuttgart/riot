package de.uni_stuttgart.riot.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.language.Language;

/**
 * Setting screen.
 * 
 * 
 *
 */
public class SettingScreen extends Activity {

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

        btnColorCalendar = (Button) findViewById(R.id.btnColor);
        btnColorCalendar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: change color and open color picker
            }
        });

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

}
