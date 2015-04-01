package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.clientlibrary.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Activity that displays all details of an user.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class UserDetailActivity extends ManagementDetailActivity<User> {

    @Override
    protected void displayDetailData() {
        // Update the home icon and title
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    if (item.getId() == 1) { // TODO this is for testing....
                        return getDrawableByUri("http://crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png?itok=xhm7jaxS");
                    }
                    return getDrawableLetter(item.getUsername(), item.getEmail());
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
        };
// ToDo DELTE!
//        new AsyncHelper<Drawable>() {
//
//            @Override
//            protected Drawable loadData() {
//                if (data.getId() == 1) {
//                    return getDrawableByUri("http://crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png?itok=xhm7jaxS");
//                }
//                return getDrawableLetter(data.getUsername(), data.getEmail());
//            }
//
//            @Override
//            protected void processData(Drawable data) {
//                if (data != null) {
//                    setHomeIcon(data);
//                }
//            }
//        };
        setTitle(super.item.getUsername());

        // Add items to the group collection
        addPreparedItemToGroup(0, prepareItemBy(getString(R.string.user_name), super.item.getUsername(), UIHint.editText()));
        addPreparedItemToGroup(0, prepareItemBy(getString(R.string.user_mail), super.item.getEmail(), UIHint.editText()));
        for (Role role : super.item.getRoles()) {
            addPreparedItemToGroup(1, prepareItemBy(null, role.getRoleName(), UIHint.editText()));
        }
        saveGroupName(1, getString(R.string.user_role));

        // Add prepared and grouped items to the main layout
        addGroupedItemsToMainLayout();
    }

    @Override
    protected void setDefaultData() {
        setHomeLogo(android.R.drawable.ic_menu_gallery);
        setTitle(getString(R.string.default_user_subject));
    }

    @Override
    protected void loadManagementData() {
        try {
            // Get the thing with the given id
            super.item = new UsermanagementClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getUser(super.itemId);
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }
}
