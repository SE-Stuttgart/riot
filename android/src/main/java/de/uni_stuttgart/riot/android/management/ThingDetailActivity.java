package de.uni_stuttgart.riot.android.management;

import java.util.Collection;
import java.util.EnumSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingInformation.Field;

/**
 * Activity that displays all details of a thing. It also provides to edit this information.
 *
 * @author Benny
 */
public class ThingDetailActivity extends ManagementDetailActivity<Thing> {

    private boolean monitoring;

    private Collection<ThingInformation> children;

    @Override
    protected void onStop() {
        super.onStop();
        stopMonitoring();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMonitoring();
        unbindAllManagementProperties();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startMonitoring();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.monitoring = false;
    }

    @Override
    protected void displayDetailData() {
        // If the refresh button was pressed firstly stop monitoring current change events
        stopMonitoring();
        unbindAllManagementProperties();

        // Update the home icon and title
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    return getDrawableLetter(item.getName(), String.valueOf(item.getId()));
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("An error occurred during loading the data: ", e);
                    IM.INSTANCES.getMH().showQuickMessage("An error occurred during loading the data!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);

                if (drawable != null) {
                    setHomeIcon(drawable);
                }
            }
        }.execute();
        setTitle(super.item.getName());

        // Prepare the items and save them in the right group
        prepareAndGroupItems(super.item.getProperties());

        // Add prepared and grouped items to the main layout
        addGroupedItemsToMainLayout();

        // Add a list of children
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout childrenView = prepareItemName("children");
        int listItemBackground = obtainStyledAttributes(new int[] { android.R.attr.selectableItemBackground }).getResourceId(0, android.R.drawable.list_selector_background);
        for (ThingInformation child : children) {
            View childView = inflater.inflate(R.layout.managment_list_item, childrenView, false);
            ((TextView) childView.findViewById(R.id.list_item_management_subject)).setText(child.getMetainfo().getName());
            ((TextView) childView.findViewById(R.id.list_item_management_description)).setText(getString(R.string.default_thing_description) + " (" + child.getId() + ")");
            ((ImageView) childView.findViewById(R.id.list_item_management_picture)).setImageDrawable(getDrawableLetter(child.getMetainfo().getName(), String.valueOf(child.getId())));
            ((ImageView) childView.findViewById(R.id.list_item_management_online_state)).setImageDrawable(getDrawableByResource(getOnlineStateResourceId(ThingClient.mapLastOnlineToOnlineState(child.getLastConnection()))));
            final Long finalId = child.getId();
            childView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent detailView = new Intent(ThingDetailActivity.this, ThingDetailActivity.class);
                    detailView.putExtra(BUNDLE_OBJECT_ID, finalId);
                    startActivity(detailView);
                }
            });
            childView.setBackgroundResource(listItemBackground);
            childrenView.addView(childView);
        }
        super.mainLayout.addView(childrenView);

        // Bind all properties and start monitoring the change events
        bindAllManagementProperties();
        startMonitoring();
    }

    @Override
    protected void setDefaultData() {
        setHomeLogo(android.R.drawable.ic_menu_gallery);
        setTitle(getString(R.string.default_thing_subject));
    }

    @Override
    protected void loadManagementData() {
        try {
            // Get the thing with the given id
            super.item = ThingManager.getInstance().getThing(this, super.itemId);
            children = new ThingClient(AndroidConnectionProvider.getConnector(this)).getThingInformation(super.itemId, EnumSet.of(Field.DIRECTCHILDREN, Field.METAINFO, Field.LASTCONNECTION)).getChildren();
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }

    /**
     * Start monitoring to receive change events.
     */
    private void startMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (item != null && !monitoring) {
                    ThingManager.getInstance().startMonitoring(item, this);
                    monitoring = true;
                }
            }
        }).start();
    }

    /**
     * Stop monitoring receiving change events.
     */
    private void stopMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (item != null && monitoring) {
                    ThingManager.getInstance().stopMonitoring(item, this);
                    monitoring = false;
                }
            }
        }).start();
    }
}
