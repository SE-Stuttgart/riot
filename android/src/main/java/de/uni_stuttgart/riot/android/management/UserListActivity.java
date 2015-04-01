package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Activity that displays all users in a list.
 *
 * @author Benny
 */
public class UserListActivity extends ManagementListActivity<User, UserDetailActivity> {

    @Override
    protected Class<UserDetailActivity> getOnItemClickActivity() {
        return UserDetailActivity.class;
    }

    @Override
    protected long getId(User item) {
        return item.getId();
    }

    @Override
    protected String getDefaultSubject() {
        return getString(R.string.default_user_subject);
    }

    @Override
    protected String getSubject(User item) {
        return item.getUsername();
    }

    @Override
    protected String getDefaultDescription() {
        return getString(R.string.default_user_description);
    }

    @Override
    protected String getDescription(User item) {
        return item.getEmail();
    }

    @Override
    protected Drawable getDefaultImage() {
        return getDrawableByResource(android.R.drawable.ic_menu_gallery);
    }

    @Override
    protected Drawable getImage(User item) {
        if (item.getId() == 1) { // TODO this is for testing....
            return getDrawableByUri("http://crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png?itok=xhm7jaxS");
        }
        return getDrawableLetter(item.getUsername(), item.getEmail());
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(final User item) {
        try {
            return new UsermanagementClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getOnlineState(item.getId());
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting online state: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting online state!");
        }
        return null;
    }

    @Override
    protected void setDefaultData() {
        setTitle(getString(R.string.user_list));
        setHomeLogo(android.R.drawable.ic_menu_sort_by_size);
    }

    @Override
    protected List<User> loadManagementData() {
        try {
            // Get all users and save them
            return new ArrayList<User>(new UsermanagementClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getUsers());
            // FIXME - I want to get an ArrayList instead of a Collection (for the list view)
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
        return null;
    }
}
