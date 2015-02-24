package de.uni_stuttgart.riot.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.language.Language;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;

/**
 * This is the new MainAcitity which starts the other activitys.
 */
// CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class HomeScreen extends Activity {

    /**
     * onCreate method
     * 
     * @param savedInstanceState
     *            the last states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Database stuff
        // this.deleteDatabase("Database");

        Language.setLanguage(this);

        setContentView(R.layout.home_screen);

        // Initialize the API client. Initialization is not allowed in the main
        // thread.
        final HomeScreen inst = this;
        new Thread() {
            @Override
            public void run() {
                RIOTApiClient.getInstance().init(inst, "androidApp");
                try {
                    System.out.println(RIOTApiClient.getInstance().getUserManagementClient().getRoles().size());
                } catch (RequestException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }.start();

        final ImageButton carButton = (ImageButton) findViewById(R.id.homeScreen_carButton);
        carButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    carButton.setAlpha(0.5f);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent carScreen = new Intent(HomeScreen.this, NotificationScreen.class);
                    carScreen.putExtra("pressedButton", "car");
                    HomeScreen.this.startActivity(carScreen);
                    carButton.setAlpha(1.0f);
                }
                return false;
            }

        });

        final ImageButton houseButton = (ImageButton) findViewById(R.id.homeScreen_houseButton);
        houseButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    houseButton.setAlpha(0.5f);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent houseScreen = new Intent(HomeScreen.this, NotificationScreen.class);
                    houseScreen.putExtra("pressedButton", "house");
                    HomeScreen.this.startActivity(houseScreen);
                    houseButton.setAlpha(1.0f);
                }

                return false;
            }
        });

        final ImageButton coffeMachineButton = (ImageButton) findViewById(R.id.homeScreen_coffeeMachineButton);
        coffeMachineButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    coffeMachineButton.setAlpha(0.5f);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent coffeeScreen = new Intent(HomeScreen.this, NotificationScreen.class);
                    coffeeScreen.putExtra("pressedButton", "coffeeMachine");
                    HomeScreen.this.startActivity(coffeeScreen);
                    coffeMachineButton.setAlpha(1.0f);
                }

                return false;
            }
        });

        final ImageButton calendarButton = (ImageButton) findViewById(R.id.homeScreen_calendarButton);
        calendarButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    calendarButton.setAlpha(0.5f);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    calendarButton.setAlpha(0.5f);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("content://com.android.calendar/time"));
                    startActivity(i);
                    calendarButton.setAlpha(1.0f);
                }

                return false;
            }
        });

        final ImageButton settingButton = (ImageButton) findViewById(R.id.homeScreen_settings);
        settingButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    settingButton.setAlpha(0.5f);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent i = new Intent(HomeScreen.this, SettingScreen.class);
                    i.putExtra("pressedButton", getString(R.string.settings));
                    HomeScreen.this.startActivity(i);
                    settingButton.setAlpha(1.0f);
                }

                return false;
            }
        });

    }

    /*
     * private static boolean isCoordsOnButton(int fingerX, int fingerY, ImageButton button) { return (fingerX >= button.getLeft() &&
     * fingerX <= button.getRight() && fingerY >= button.getTop() && fingerY <= button.getBottom()); }
     */
}
