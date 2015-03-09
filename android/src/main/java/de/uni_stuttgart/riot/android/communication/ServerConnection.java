package de.uni_stuttgart.riot.android.communication;

import java.io.IOException;

import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.UnauthenticatedException;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Helper class for executing asynchronous requests to the server on Android.
 * 
 * @author Philipp Keck
 * @param <T>
 *            The type of the result, may be {@link Void}.
 */
public abstract class ServerConnection<T> extends AsyncTask<Void, Void, T> {

    private IOException ioException;
    private UnauthenticatedException unauthenticatedException;

    @Override
    protected T doInBackground(Void... params) {
        ioException = null;
        unauthenticatedException = null;
        try {
            ServerConnector serverConnector = AndroidConnectionProvider.getConnector(getContext());
            return executeRequest(serverConnector);
        } catch (IOException e) {
            ioException = e;
            return null;
        } catch (UnauthenticatedException e) {
            unauthenticatedException = e;
            return null;
        } catch (AbortTaskException e) {
            abortTask();
            return null;
        } catch (RequestException e) {
            // This really shouldn't happen.
            throw new RuntimeException(e);
        }

    }

    @Override
    protected final void onPostExecute(T result) {
        if (ioException != null) {
            handleNetworkError(ioException);
            ioException = null;
        } else if (unauthenticatedException != null) {
            handleAuthenticationError(unauthenticatedException);
            unauthenticatedException = null;
        } else {
            onSuccess(result);
        }
    }

    /**
     * Aborts the task and tries to close the caller (e.g. an activity) of the task.
     */
    protected void abortTask() {
        cancel(false);
    }

    /**
     * Gets the context.
     * 
     * @return The context for the request.
     */
    protected abstract Context getContext();

    /**
     * Handles a network error that occured, probably by refreshing the connection information in the connector or by simply aborting the
     * operation.
     * 
     * @param e
     *            The error.
     */
    protected abstract void handleNetworkError(IOException e);

    /**
     * Handles an authentication error that occurred, probably by re-logging in the connector or by simply aborting the operation.
     * 
     * @param e
     *            The error.
     */
    protected abstract void handleAuthenticationError(UnauthenticatedException e);

    /**
     * Executes the actual request.
     * 
     * @param serverConnector
     *            The server connector for communication.
     * @return The request result.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    protected abstract T executeRequest(ServerConnector serverConnector) throws IOException, RequestException;

    /**
     * This will be called on the UI thread to work with the result.
     * 
     * @param result
     *            The result of the request.
     */
    protected abstract void onSuccess(T result);

}
