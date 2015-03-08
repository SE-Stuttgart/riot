package de.uni_stuttgart.riot.android.location;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.language.Language;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class LocationScreen extends Activity implements LocationListener, OnClickListener {

    private static final String TAG = "LocationScreen";

    public RIOTDatabase db;
    private ActionBar actionBar;
    private LocationManager locationManager;
    private String provider;
    private Criteria criteria;
    private List<MyLocation> myLocation;

    private Button btn_show_location;
    private Button btn_hide_location;
    private TextView tv_show_location, tv_current_position, tv_distance_between, tv_location_range;
    private double setRange;

    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Language.setLanguage(this);
        setContentView(R.layout.location_screen);

        // Sets the title and icon of the action bar
        actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.location));
        actionBar.setIcon(R.drawable.location);
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = DatabaseAccess.getDatabase();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(provider, 200, 1, this);

        btn_show_location = (Button) findViewById(R.id.location_show);
        btn_hide_location = (Button) findViewById(R.id.location_hide);
        btn_hide_location.setVisibility(View.INVISIBLE);
        tv_show_location = (TextView) findViewById(R.id.location_tv_show);
        tv_current_position = (TextView) findViewById(R.id.location_actual_pos);
        tv_distance_between = (TextView) findViewById(R.id.location_distance_between);
        tv_location_range = (TextView) findViewById(R.id.location_range);

        MyLocation stadtmitte = new MyLocation("Stadtmitte", "Keplerstrasse 7, 70174 Stuttgart", 48.78127, 9.17489);
        MyLocation vaihingen = new MyLocation("Vaihingen", "Pfaffenwaldring, 70569 Stuttgart", 48.742861, 9.098906);

        db.updateLocation(stadtmitte);
        db.updateLocation(vaihingen);
        myLocation = db.getLocation();

        btn_show_location.setOnClickListener(this);
        btn_hide_location.setOnClickListener(this);

        setRange = 1600.0;
        tv_location_range.setText(getString(R.string.location_store_distance) + ": " + setRange + "m.");

        alertDialog = new AlertDialog.Builder(this);
    }

    float distance = 0;
    StringBuffer bufferDistance = new StringBuffer();
    StringBuffer bufferPlace = new StringBuffer();

    @Override
    public void onLocationChanged(Location location) {
        bufferDistance.setLength(0);
        bufferPlace.setLength(0);
        final float[] results = new float[1];

        double lat = 0.0;
        double lng = 0.0;

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();

            tv_current_position.setText("Current position " + "\n" + getString(R.string.location_latitude) + ":\t" + lat + "\n" + getString(R.string.location_longitude) + ":\t" + lng);

            for (int i = 0; i < myLocation.size(); i++) {

                Location.distanceBetween(location.getLatitude(), location.getLongitude(), myLocation.get(i).getLatitude(), myLocation.get(i).getLongitude(), results);
                distance = results[0];

                Log.e(TAG, "distance " + distance + " range " + setRange);
                if (distance < setRange) {
                    bufferDistance.append(getString(R.string.location_distance) + " " + myLocation.get(i).getPlace() + " " + distance + "m\n");
                    bufferPlace.append("Termin: xxyyzz\t" + "Ort: " + myLocation.get(i).getPlace() + ".\n");

                }
                tv_distance_between.setText(bufferDistance.toString());

            }

            alertDialog.setTitle("Notification");
            alertDialog.setMessage(bufferPlace.toString());
            alertDialog.setPositiveButton("Show", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            Log.e(TAG, bufferPlace.length() + "");
            if (bufferPlace.length() > 0) {
                alertDialog.show();
            }
        }

    }

    private StringBuffer getStoredLocation() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < myLocation.size(); i++) {

            buffer.append(myLocation.get(i).getPlace());
            buffer.append("\n");
            buffer.append(myLocation.get(i).getAddress());
            buffer.append("\n");
            buffer.append(getString(R.string.location_latitude) + ": " + myLocation.get(i).getLatitude() + "\n" + getString(R.string.location_longitude) + ": " + myLocation.get(i).getLongitude());
            buffer.append("\n");
            buffer.append("\n");
        }
        return buffer;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.location_show) {
            tv_show_location.setText(getStoredLocation().toString());
            tv_show_location.setVisibility(View.VISIBLE);
            btn_hide_location.setVisibility(View.VISIBLE);
        }

        if (view.getId() == R.id.location_hide) {
            tv_show_location.setVisibility(View.INVISIBLE);
            btn_hide_location.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

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
