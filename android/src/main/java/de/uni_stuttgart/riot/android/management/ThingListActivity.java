package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * Activity that displays all things in a list.
 *
 * @author Benny
 */
public class ThingListActivity extends ManagementListActivity<Thing, ThingDetailActivity> {

    @Override
    protected Class<ThingDetailActivity> getOnItemClickActivity() {
        return ThingDetailActivity.class;
    }

    @Override
    protected long getId(Thing item) {
        return item.getId();
    }

    @Override
    protected String getDefaultSubject() {
        return getString(R.string.default_thing_subject);
    }

    @Override
    protected String getSubject(Thing item) {
        return item.getName();
    }

    @Override
    protected String getDefaultDescription() {
        return getString(R.string.default_thing_description);
    }

    @Override
    protected String getDescription(Thing item) {
        return getString(R.string.default_thing_description) + " (" + String.valueOf(getId(item)) + ")";
    }

    @Override
    protected Drawable getDefaultImage() {
        return getResources().getDrawable(android.R.drawable.ic_menu_gallery);
    }

    @Override
    protected Drawable getImage(Thing item) {
        // TODO evtl. keine zahl als key???
        return getDrawableLetter(item.getName(), String.valueOf(item.getId()));
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(Thing item) {
        try {
            // FIXME thingClient.getOnlineState(thingId); --> NullPointerException if online state is "offline"!!
            return new ThingClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getOnlineState(item.getId());
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeWarnMessage("Problems by getting online state!");
//            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting online state: ", e);
//            IM.INSTANCES.getMH().showQuickMessage("Problems by getting online state!");
        }
        return null;
    }

    @Override
    protected void setDefaultData() {
        setTitle(getString(R.string.thing_list));
        setHomeLogo(android.R.drawable.ic_menu_sort_by_size);
    }

    @Override
    protected void loadData() {
        try {
            // Get all things and save them
            super.itemList = new ArrayList<Thing>(ThingManager.getInstance().getAllThings(this));
            // FIXME - I want to get an ArrayList instead of a Collection (for the list view)
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }
}
