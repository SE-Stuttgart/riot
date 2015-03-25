package de.uni_stuttgart.riot.android.things;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import de.uni_stuttgart.riot.android.R;

//CHECKSTYLE:OFF TODO 
public class CoffeeMachine extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.coffee);

        setContentView(new CoffeeMachineCanvas(this));
    }

    /**
     * Go back to the home screen.
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
