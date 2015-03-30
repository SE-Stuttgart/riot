package de.uni_stuttgart.riot.android.communication;

import java.io.IOException;

import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import android.app.Activity;

/**
 * A helper class for asynchronous requests to the server that are bound to an activity and do not return a result.
 * 
 * @author Philipp Keck
 */
public abstract class ActivityServerTask extends ActivityServerConnection<Void> {

    /**
     * Creates a new connection.
     * 
     * @param activity
     *            The activity in the context of which the request occurs.
     */
    public ActivityServerTask(Activity activity) {
        super(activity);
    }

    @Override
    protected Void executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
        executeTask(serverConnector);
        return null;
    }

    /**
     * Executes the actual task.
     * 
     * @param serverConnector
     *            The server connector for communication.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    protected abstract void executeTask(ServerConnector serverConnector) throws IOException, RequestException;

    @Override
    protected void onSuccess(Void result) {
        // There will be no result, so we can ignore it.
        // Subclasses may still want to override this method.
    }

}
