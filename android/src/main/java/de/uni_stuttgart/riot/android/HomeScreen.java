package de.uni_stuttgart.riot.android;

import de.enpro.android.riot.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

// CHECKSTYLE:OFF FIXME Please fix the Checkstyle errors in this file.
public class HomeScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Database stuff
		// this.deleteDatabase("Database");

		setContentView(R.layout.home_screen);

		final ImageButton carButton = (ImageButton) findViewById(R.id.homeScreen_carButton);
		carButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					carButton.setAlpha(0.5f);
				}
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent carScreen = new Intent(HomeScreen.this,
							MainActivity.class);
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
					Intent houseScreen = new Intent(HomeScreen.this,
							MainActivity.class);
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
					Intent coffeeScreen = new Intent(HomeScreen.this,
							MainActivity.class);
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

	}

	/*
	private static boolean isCoordsOnButton(int fingerX, int fingerY,
			ImageButton button) {
		return (fingerX >= button.getLeft() && fingerX <= button.getRight()
				&& fingerY >= button.getTop() && fingerY <= button.getBottom());
	}
	*/
}
