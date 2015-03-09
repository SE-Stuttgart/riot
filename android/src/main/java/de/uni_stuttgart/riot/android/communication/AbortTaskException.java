package de.uni_stuttgart.riot.android.communication;

import android.os.AsyncTask;

/**
 * This exception indicates that the running {@link AsyncTask}, particularly the running {@link ServerConnection} should be cancelled since
 * there is no way it is going to complete successfully.
 * 
 * @author Philipp Keck
 */
public class AbortTaskException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for AbortTaskException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public AbortTaskException(String message) {
        super(message);
    }

    /**
     * Constructor for AbortTaskException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public AbortTaskException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for AbortTaskException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public AbortTaskException(Throwable cause) {
        super(cause);
    }

}
