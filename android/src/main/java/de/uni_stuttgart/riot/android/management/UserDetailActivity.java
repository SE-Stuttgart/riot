package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.Collection;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.ActivityServerConnection;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.thingproperty.ThingProperty;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
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
public class UserDetailActivity extends ManagementDetailActivity {

    private Collection<Role> roles;

    @Override
    protected String getDefaultPageTitle() {
        return getString(R.string.user_detail);
    }

    @Override
    protected void displayDetailData(Object data) {
        // Check if there is the needed main linear layout
        if (findViewById(R.id.management_linear_layout) == null) {
            return;
        }

        // Clear all children
        ((LinearLayout) findViewById(R.id.management_linear_layout)).removeAllViews();

        // Add items to the main layout
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.management_linear_layout);
        User user = (User) data;

        mainLayout.addView(prepareItemBy(getString(R.string.user_name), user.getUsername(), UIHint.editText()));
        mainLayout.addView(prepareItemBy(getString(R.string.user_mail), user.getEmail(), UIHint.editText()));
        if (this.roles != null) {
            Collection<Role> userRoles = user.getRoles();
            for (Role role : this.roles) {
                mainLayout.addView(prepareItemBy(role.getRoleName(), userRoles.contains(role), UIHint.toggleButton(role.getRoleName(), role.getRoleName())));
            }
        }
    }

    @Override
    protected void addThingProperty(ThingProperty thingProperty, LinearLayout linearLayoutItem) {

    }

    @Override
    protected void getAndDisplayData() {
        new ActivityServerConnection<Object>(getParent()) {
            @Override
            protected Object executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                try {
                    roles = new UsermanagementClient(serverConnector).getRoles();
                    return new UsermanagementClient(serverConnector).getUser(itemId);
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
                    IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
                }
                return null;
            }

            @Override
            protected void onSuccess(Object result) {
                // ToDo do the same for the thing views or change it on some way (to use the ManagementActivity)
                displayData(result);

                // End processing animation
                startProcessingAnimation(false);
            }
        }.execute();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.management_detail;
    }

    @Override
    protected Drawable getHomeIcon() {
        return null; // ToDo
    }

    @Override
    protected int getHomeLogo() {
        return R.drawable.ic_drawer; // ToDo
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return (item instanceof User);
    }
}
