package de.uni_stuttgart.riot.android.communication;

import java.io.IOException;
import java.lang.ref.WeakReference;

import de.uni_stuttgart.riot.android.account.LoginActivity;
import de.uni_stuttgart.riot.clientlibrary.UnauthenticatedException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * A helper class for asynchronous requests to the server that are bound to an activity and return a result.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The type of the result, may be {@link Void}.
 */
public abstract class ActivityServerConnection<T> extends ServerConnection<T> {

    private final WeakReference<Activity> activityReference;

    /**
     * Creates a new connection.
     * 
     * @param activity
     *            The activity in the context of which the request occurs.
     */
    public ActivityServerConnection(Activity activity) {
        this.activityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected Context getContext() {
        return activityReference.get();
    }

    @Override
    protected T doInBackground(Void... params) {
        Activity activity = activityReference.get();
        if (activity == null) {
            return super.doInBackground(params);
        } else {
            AndroidConnectionProvider.REQUESTING_ACTIVITY.set(activity);
            try {
                return super.doInBackground(params);
            } finally {
                AndroidConnectionProvider.REQUESTING_ACTIVITY.remove();
            }
        }
    }

    @Override
    protected void handleAuthenticationError(UnauthenticatedException e) {
        showLoginActivity(false);
    }

    @Override
    protected void handleNetworkError(IOException e) {
        showLoginActivity(true);
    }

    @Override
    protected void abortTask() {
        super.abortTask();
        Activity activity = activityReference.get();
        if (activity != null) {
            activity.finish();
        }
    }

    private void showLoginActivity(boolean showConnectionSettings) {
        Activity activity = activityReference.get();
        if (activity == null) {
            return;
        }

        String defaultName = AndroidConnectionProvider.getInstance().getCurrentAccountName();

        Intent intent = new Intent(activity, LoginActivity.class);
        if (defaultName != null) {
            intent.putExtra(LoginActivity.KEY_USERNAME, defaultName);
        }
        intent.putExtra(LoginActivity.KEY_SHOW_CONNECTION_SETTINGS, showConnectionSettings);
        Intent redirectIntent = new Intent(activity.getIntent());
        redirectIntent.setComponent(activity.getComponentName());
        intent.putExtra(LoginActivity.KEY_REDIRECT_INTENT, redirectIntent);
        activity.startActivity(intent);
        activity.finish();
    }

}
