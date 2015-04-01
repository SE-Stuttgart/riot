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
        return getDrawableByResource(android.R.drawable.ic_menu_gallery);
    }

    @Override
    protected Drawable getImage(Thing item) {
        return getDrawableLetter(item.getName(), String.valueOf(item.getId()));
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(Thing item) {
        try {
            return new ThingClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getOnlineState(item.getId());
        } catch (Exception e) {
            /* TODO -> throws NullPointerException
            java.lang.NullPointerException
            at de.uni_stuttgart.riot.thing.client.ThingClient.getOnlineState(ThingClient.java:386)
            at de.uni_stuttgart.riot.android.management.ThingListActivity.getOnlineState(ThingListActivity.java:70)
            at de.uni_stuttgart.riot.android.management.ThingListActivity.getOnlineState(ThingListActivity.java:20)
            at de.uni_stuttgart.riot.android.management.ManagementListActivity$3.doInBackground(ManagementListActivity.java:188)
            at de.uni_stuttgart.riot.android.management.ManagementListActivity$3.doInBackground(ManagementListActivity.java:183)
            at android.os.AsyncTask$2.call(AsyncTask.java:288)
            at java.util.concurrent.FutureTask.run(FutureTask.java:237)
            at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:231)
            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1112)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:587)
            at java.lang.Thread.run(Thread.java:841)
             */
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting online state! ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting online state!");
        }
        return null;
    }

    @Override
    protected void setDefaultData() {
        setTitle(getString(R.string.thing_list));
        setHomeLogo(android.R.drawable.ic_menu_sort_by_size);
    }

    @Override
    protected void loadManagementData() {
        try {
            /* TODO throws class cast exception (if the id of the things is higher than 9)
            java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Double
            at java.lang.Class.cast(Class.java:1278)
            at de.uni_stuttgart.riot.thing.ThingState.silentSetThingProperty(ThingState.java:145)
            at de.uni_stuttgart.riot.thing.ThingState.apply(ThingState.java:129)
            at de.uni_stuttgart.riot.thing.rest.ThingInformation.apply(ThingInformation.java:156)
            at de.uni_stuttgart.riot.thing.ThingFactory.create(ThingFactory.java:158)
            at de.uni_stuttgart.riot.thing.client.ThingClient.getThings(ThingClient.java:135)
            at de.uni_stuttgart.riot.android.things.AndroidDeviceBehavior.getAllThings(AndroidDeviceBehavior.java:111)
            at de.uni_stuttgart.riot.android.things.ThingManager.getAllThings(ThingManager.java:239)
            at de.uni_stuttgart.riot.android.management.ThingListActivity.loadManagementData(ThingListActivity.java:102)
            at de.uni_stuttgart.riot.android.management.ManagementActivity$2.doInBackground(ManagementActivity.java:106)
            at de.uni_stuttgart.riot.android.management.ManagementActivity$2.doInBackground(ManagementActivity.java:101)
            at android.os.AsyncTask$2.call(AsyncTask.java:288)
            at java.util.concurrent.FutureTask.run(FutureTask.java:237)
            at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:231)
            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1112)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:587)
            at java.lang.Thread.run(Thread.java:841)
             */
            // Get all things and save them
            this.items = new ArrayList<Thing>(ThingManager.getInstance().getAllThings(this));
            // FIXME - I want to get an ArrayList instead of a Collection (for the list view)
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }
}
