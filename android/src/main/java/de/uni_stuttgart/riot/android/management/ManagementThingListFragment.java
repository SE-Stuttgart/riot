package de.uni_stuttgart.riot.android.management;

import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.messages.IM;
>>>>>>> RIOT-87:Android:Get things from the server
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.thing.ThingDescription;

/**
 * Activity that displays all things in a list.
 *
 * @author Benny
 */
public class ManagementThingListFragment extends ManagementListFragment {

    @Override
    protected String getPageTitle() {
        return getResources().getString(R.string.thing_list);
    }

    @Override
    protected List<Object> getData() {
        try {
            // Get all thing descriptions and return them
            Collection<ThingDescription> thingDescriptions = RIOTApiClient.getInstance().getDeviceBehavior().getDescriptions();
            return new ArrayList<Object>(thingDescriptions);
        } catch (Exception e) {
            // FIXME output message!!
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected ManagementListFragment getFragment() {
        return this;
    }

    @Override
    protected Class getOnItemClickActivity() {
        return ManagementThingDetailFragment.class;
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return (item instanceof ThingDescription);
    }

    @Override
    protected long getId(Object item) {
        return ((ThingDescription) item).getThingId();
    }

    @Override
    protected String getDefaultSubject() {
        return getString(R.string.thing_default);
    }

    @Override
    protected String getSubject(Object item) {
        try {
            // Get the name of the thing
            return RIOTApiClient.getInstance().getDeviceBehavior().getThingByDiscription((ThingDescription) item).getName();
            // ToDo maybe extend that!! deviceBehavior.getThingById(getId(item));
        } catch (Exception e) {
            // FIXME output message!!
            IM.INSTANCES.getMH().writeErrorMessage("Problems by displaying data: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected String getDefaultDescription() {
        return getString(R.string.default_thing_description);
    }

    @Override
    protected String getDescription(Object item) {
        return "This is the description: " + String.valueOf(getId(item));
    }

    @Override
    protected String getDefaultImageUri() {
        return null;
    }

    @Override
    protected String getImageUri(Object item) {
        return null; // ((ThingDescription) item).getImageUri();  // ToDo loading images asynchronous?
    }

    @Override
    protected int getDefaultImageId() {
        return android.R.drawable.ic_menu_gallery;
    }

    @Override
    protected int getImageId(Object item) {
        return 0; // ((ThingDescription) item).getImageId(); // ToDo
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected void getOnlineState(final Object item, final View view) {
        new Thread() {

            @Override
            public void run() {
                try {
                    doUpdateOnlineState(view, RIOTApiClient.getInstance().getThingClient().getOnlineState(((ThingDescription) item).getThingId()));
                } catch (Exception e) {
                    // FIXME output message!!
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by getting online state: " + e.getMessage());
                }
            }
        }.start();
    }
}
