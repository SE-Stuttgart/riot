package de.uni_stuttgart.riot.android;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;

/**
 * Setting screen.
 * 
 * 
 *
 */
public class SettingScreen extends Activity {

    private String pressedHomeScreenButton;
    private Intent intent;
    private ActionBar actionBar;

    private int choice = 0;
    private int selectedLanguage;
    private Button btn_color_change;
    private ImageButton btn_language;

    private RIOTDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new RIOTDatabase(this);
        intent = getIntent();

        if (db.getLanguageCount() > 0) {
            if (db.getLanguage().equals("en")) {
                selectedLanguage = 0;
            } else {
                selectedLanguage = 1;
            }
        } else {
            db.setLanguage("en");
        }

        setLanguage();

        setContentView(R.layout.setting_screen);

        // get the value of the pressed button
        pressedHomeScreenButton = intent.getStringExtra("pressedButton");

        // sets the title and icon of the Actionbar
        actionBar = getActionBar();
        actionBar.setTitle(pressedHomeScreenButton);
        actionBar.setIcon(R.drawable.settings);

        btn_language = (ImageButton) findViewById(R.id.btn_lang);
        btn_language.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });

        btn_color_change = (Button) findViewById(R.id.btn_change_color);
        btn_color_change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: change color and open color picker
            }
        });

    }

    private void setLanguage() {
        Locale locale;
        if (db.getLanguageCount() == 0) {
            locale = new Locale("en");
        } else {
            locale = new Locale(db.getLanguage());
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.language_header).setSingleChoiceItems(R.array.language_array, selectedLanguage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                choice = which;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setSelectedLanguage(choice);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

}
