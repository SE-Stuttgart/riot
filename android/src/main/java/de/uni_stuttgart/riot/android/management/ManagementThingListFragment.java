package de.uni_stuttgart.riot.android.management;

import android.app.Fragment;

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
 * Fragment that displays all things in a list.
 *
 * @author Benny
 */
public class ManagementThingListFragment extends ManagementListFragment {

    @Override
    protected String getTitle() {
        return getResources().getString(R.string.thing_list);
    }

    @Override
    protected List<Object> getData() {
        if (!isDummyThing) {
            try {
                // Get all thing descriptions and return them
                Collection<ThingDescription> thingDescriptions = RIOTApiClient.getInstance().getDeviceBehavior().getDescriptions();
                return new ArrayList<Object>(thingDescriptions);

//            // Get all descriptions of the available things
//            for (ThingDescription thingDescription : RIOTApiClient.getInstance().getDeviceBehavior().getDescriptions()) {
//                // Get the id and the name of the thing
//                long thingId = thingDescription.getThingId();
//                Thing thing = RIOTApiClient.getInstance().getDeviceBehavior().getThingByDiscription(thingDescription);
//                // ToDo noch erweitern!! deviceBehavior.getThingById(thingId);
//                String thingName = thing.getName();
//                // TODO or use this: String thingName = thingDescription.getThingName();
//
//                // thingDescription.getType(); // ToDo check if thing is a device or a thing..
//
//                // Get the current online state of the thing
//                // TODO dynamisch laden? OnlineState onlineState = RIOTApiClient.getInstance().getThingClient().getOnlineState(thingDescription.getThingId());
//
//                // Get all properties of the thing
//                List<PropertyDescription> propertyDescriptions = thingDescription.getProperties();
//                for (PropertyDescription propertyDescription : propertyDescriptions) {
//                    String propertyName = propertyDescription.getName();
//                    Class<?> propertyValueType = propertyDescription.getValueType();
//                    UIHint propertyUIHint = propertyDescription.getUiHint();
//                }
//            }
            } catch (Exception e) {
                // FIXME output message!!
                IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: " + e.getMessage());
            }
        }

        // Load dummy objects if the above method was not successful
        return (List<Object>) (List<?>) getThings();
    }

    @Override
    protected ManagementListFragment getFragment() {
        return this;
    }

    @Override
    protected Fragment getOnItemClickFragment() {
        return new ManagementThingDetailFragment();
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        if (item instanceof DummyThing) {
            isDummyThing = true;
        }
        return (isDummyThing || item instanceof ThingDescription);
//        return (item instanceof ThingDescription);
    }

    @Override
    protected long getId(Object item) {
        if (isDummyThing) {
            return ((DummyThing) item).getId();
        }
        return ((ThingDescription) item).getThingId();
    }

    @Override
    protected String getDefaultSubject() {
        return "This is a Thing";
    }

    @Override
    protected String getSubject(Object item) {
        if (isDummyThing) {
            return ((DummyThing) item).getName();
        }

        try {
            // Get the name of the thing
            return RIOTApiClient.getInstance().getDeviceBehavior().getThingByDiscription((ThingDescription) item).getName();
            // ToDo maybe extend that!! deviceBehavior.getThingById(getId(item));
            // TODO or use this: String thingName = thingDescription.getThingName();
        } catch (Exception e) {
            // FIXME output message!!
            IM.INSTANCES.getMH().writeErrorMessage("Problems by displaying data: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected String getDefaultDescription() {
        return "This is the description of the thing";
    }

    @Override
    protected String getDescription(Object item) {
        if (isDummyThing) {
            return "This is the description: " + ((DummyThing) item).getId();
        }

        // Get the description of the thing
        return "This is the description: " + String.valueOf(getId(item));
    }

    @Override
    protected String getDefaultImageUri() {
        return null; // ToDo loading images asynchronous?
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
    protected OnlineState getOnlineState(Object item) {
        if (isDummyThing) {
            return ((DummyThing) item).getOnlineState();
        }

//        try {
//            return RIOTApiClient.getInstance().getThingClient().getOnlineState(getId(item));
//        } catch (Exception e) {
//            // FIXME output message!!
//            IM.INSTANCES.getMH().writeErrorMessage("Problems by displaying data: " + e.getMessage());
//        }
        return null;
    }
}
