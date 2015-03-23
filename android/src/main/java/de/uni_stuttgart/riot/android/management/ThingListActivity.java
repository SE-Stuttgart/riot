package de.uni_stuttgart.riot.android.management;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.thing.Thing;

/**
 * Activity that displays all things in a list.
 *
 * @author Benny
 */
public class ThingListActivity extends ManagementListActivity {

    @Override
    protected String getPageTitle() {
        return getResources().getString(R.string.thing_list);
    }

    @Override
    protected void getAndDisplayListData() {
        try {
            // Get all things and return them
            final List<Object> data = new ArrayList<Object>(ThingManager.getInstance().getAllThings(this));
            // ToDo I want to get an ArrayList instead of a Collection

            // Display data
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Display the data
                    displayData(data);

                    // End processing animation
                    startProcessingAnimation(false);
                }
            });
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }

    @Override
    protected ManagementListActivity getActivity() {
        return this;
    }

    @Override
    protected Class getOnItemClickActivity() {
        return ThingDetailActivity.class;
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return (item instanceof Thing);
    }

    @Override
    protected long getId(Object item) {
        return ((Thing) item).getId();
    }

    @Override
    protected String getDefaultSubject() {
        return getString(R.string.thing_default);
    }

    @Override
    protected String getSubject(Object item) {
        // Get the name of the thing
        return ((Thing) item).getName();
    }

    @Override
    protected String getDefaultDescription() {
        return getString(R.string.default_thing_description);
    }

    @Override
    protected String getDescription(Object item) {
        return "Description of the thing with the id: " + String.valueOf(getId(item));
    }

    @Override
    protected String getDefaultImageUri() {
        return null;
    }

    @Override
    protected String getImageUri(Object item) {
        return null; // ((Thing) item).getImageUri(); // ToDo
    }

    @Override
    protected int getDefaultImageId() {
        return android.R.drawable.ic_menu_gallery;
    }

    @Override
    protected int getImageId(Object item) {
        return 0; // ((Thing) item).getImageId(); // ToDo
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(final Object item) {
        if (item != null) {
            try {
                return null;
                // TODO thingClient.getOnlineState(thingId); --> NullPointerException if online state is "offline"!!
                // return new ThingClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getOnlineState(((Thing) item).getId());
            } catch (Exception e) {
                IM.INSTANCES.getMH().writeErrorMessage("Problems by getting online state: ", e);
                IM.INSTANCES.getMH().showQuickMessage("Problems by getting online state!");
            }
        }
        return null;
    }

    @Override
    protected String getDetailPageTitle(Object item) {
        // Get the name of the item as title
        if (isInstanceOf(item)) {
            return ((Thing) item).getName();
        }
        return null;
    }
}
