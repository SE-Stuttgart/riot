package de.uni_stuttgart.riot.android.management;

import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

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
    protected void displayDetailData(LinearLayout mainLayout) {
        // Clear all children
        mainLayout.removeAllViews();

        // Add items to the main layout
        mainLayout.addView(prepareItemBy(getString(R.string.user_name), super.item.getUsername(), UIHint.editText()));
        mainLayout.addView(prepareItemBy(getString(R.string.user_mail), super.item.getEmail(), UIHint.editText()));
        List<View> userRoles = new ArrayList<View>();
        for (Role role : super.item.getRoles()) {
            userRoles.add(prepareItemBy(null, role.getRoleName(), UIHint.editText()));
            userRoles.add(prepareItemBy(null, "Role 2", UIHint.editText()));
            userRoles.add(prepareItemBy(null, "Role 3", UIHint.editText()));
            userRoles.add(prepareItemBy(null, "Role 4", UIHint.editText()));
        }
        mainLayout.addView(prepareGroup(getString(R.string.user_role), userRoles));

        // TODO TEST TODO TEST
        userRoles.clear();
        for (Role role : super.item.getRoles()) {
            userRoles.add(prepareItemBy(null, true, UIHint.toggleButton(role.getRoleName() + " NOT", role.getRoleName())));
            userRoles.add(prepareItemBy(null, true, UIHint.toggleButton("Role 2" + " NOT", "Role 2")));
            userRoles.add(prepareItemBy(null, true, UIHint.toggleButton("Role 3" + " NOT", "Role 3")));
            userRoles.add(prepareItemBy(null, true, UIHint.toggleButton("Role 4" + " NOT", "Role 4")));
        }
        mainLayout.addView(prepareGroup(getString(R.string.user_role) + "_2", userRoles));

        // Update the home icon and title
        setHomeIcon(getDrawableLetter(super.item.getUsername(), super.item.getEmail()));
        setTitle(super.item.getUsername());
    }

    @Override
    protected void setDefaultData() {
        setHomeLogo(android.R.drawable.ic_menu_gallery);
        setTitle(getString(R.string.default_user_subject));
    }

    @Override
    protected void loadData() {
        try {
            // Get the thing with the given id
            super.item = new UsermanagementClient(AndroidConnectionProvider.getConnector(getApplicationContext())).getUser(super.itemId);
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }
}
