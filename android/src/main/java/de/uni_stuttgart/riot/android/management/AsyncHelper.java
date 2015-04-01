package de.uni_stuttgart.riot.android.management;

import android.app.Activity;

import de.uni_stuttgart.riot.android.messages.IM;

/**
 * This class helps to load data in an extra thread and process them in the ui thread.
 *
 * @param <T> type of the handled object
 * @author Benny
 */
public abstract class AsyncHelper<T> extends Activity {
    /**
     * Constructor.
     */
    public AsyncHelper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final T data = loadData();
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processData(data);
                            }
                        });
                    } catch (Exception e) {
                        errorOnProcessing(e);
                    }
                } catch (Exception e) {
                    errorOnLoading(e);
                }
            }
        }).start();
    }

    /**
     * Show an output message if an error occurred during loading the data.
     *
     * @param e the exception that was thrown
     */
    protected void errorOnLoading(Exception e) {
        errorOnLoadingOrProcessing(e, true);
    }

    /**
     * Show an output message if an error occurred during processing the data.
     *
     * @param e the exception that was thrown
     */
    protected void errorOnProcessing(Exception e) {
        errorOnLoadingOrProcessing(e, false);
    }

    /**
     * Show an output message if an error occurred during loading or processing the data.
     *
     * @param e                 the exception that was thrown
     * @param occurredOnLoading true if the error occurred during loading the data
     */
    protected void errorOnLoadingOrProcessing(Exception e, final boolean occurredOnLoading) {
        IM.INSTANCES.getMH().writeErrorMessage("An error occurred during " + (occurredOnLoading ? "loading" : "processing") + " the data: ", e);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IM.INSTANCES.getMH().showQuickMessage("An error occurred during " + (occurredOnLoading ? "loading" : "processing") + " the data!");
                doAfterErrorOccurred();
            }
        });
    }

    /**
     * Method that provides an action that will be processed after an error occurred.
     */
    protected void doAfterErrorOccurred() {
    }

    /**
     * Load the data in an extra thread.
     *
     * @return the loaded data
     */
    protected abstract T loadData();

    /**
     * Process the received data in an ui thread.
     *
     * @param data the loaded data
     */
    protected abstract void processData(T data);
}
