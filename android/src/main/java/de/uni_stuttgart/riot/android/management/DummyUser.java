package de.uni_stuttgart.riot.android.management;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Created by Benny on 22.01.2015.
 */
public class DummyUser extends User {

    /**
     * Returns the image resource id to display the online state
     * @return
     */
    public OnlineState getOnlineState() {
        return OnlineState.getEnumById(this.getId() % 5);
    }

    public String getImageUri() {
        return "https://lh4.googleusercontent.com/-mRLn8mYbJw0/AAAAAAAAAAI/AAAAAAAAAAA/tVxGLHv78hg/photo.jpg";
    }
}
