package de.uni_stuttgart.riot.android.messages;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * This class provides sending messages to the user by a toast or a log message.
 *
 * @author Benny
 */
public class MessageHandler {

    private Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Constructor.
     */
    public MessageHandler() {
        init();
    }

    /**
     * Initializes the attributes.
     */
    private void init() {
    }

    /**
     * Saves the application context.
     *
     * @param applicationContext
     *            the context of the application
     */
    public void setContext(Context applicationContext) {
        this.context = applicationContext;
    }

    /**
     * Writes a message with the Log.DEBUG priority.
     *
     * @param text
     *            the message that will be saved
     */
    public void writeDebugMessage(String text) {
        Log.d(getCallingClassAndMethodName(), text);
    }

    /**
     * Writes a message with the Log.ERROR priority.
     *
     * @param text
     *            the message that will be saved
     * @param exception
     *            includes the error message and cause
     */
    public void writeErrorMessage(String text, Exception exception) {
        writeErrorMessage(text + exception.getCause() + ": " + exception.getMessage());
    }

    /**
     * Writes a message with the Log.ERROR priority.
     *
     * @param text
     *            the message that will be saved
     */
    public void writeErrorMessage(String text) {
        Log.e(getCallingClassAndMethodName(), text); // Throwable tr); ToDo: use throwable in error log?
        // ToDO: show a toast after writing an error message into log?
    }

    /**
     * Writes a message with the Log.INFO priority.
     *
     * @param text
     *            the message that will be saved
     */
    public void writeInfoMessage(String text) {
        Log.i(getCallingClassAndMethodName(), text);
    }

    /**
     * Writes a message with the Log.VERBOSE priority.
     *
     * @param text
     *            the message that will be saved
     */
    public void writeMessage(String text) {
        Log.v(getCallingClassAndMethodName(), text);
    }

    /**
     * Writes a message with the Log.WARN priority.
     *
     * @param text
     *            the message that will be saved
     */
    public void writeWarnMessage(String text) {
        Log.w(getCallingClassAndMethodName(), text);
    }

    /**
     * Shows a toast with short duration.
     *
     * @param text
     *            the message that will be shown
     */
    public void showQuickMessage(String text) {
        int duration = Toast.LENGTH_SHORT;
        showToast(text, duration);

    }

    /**
     * Shows a toast with long duration.
     *
     * @param text
     *            the message that will be shown
     */
    public void showMessage(String text) {
        int duration = Toast.LENGTH_LONG;
        showToast(text, duration);
    }

    /**
     * Shows a toast with the given text and duration.
     *
     * @param text
     *            the message that will be shown
     * @param duration
     *            the time the message will visible
     */
    private void showToast(final String text, final int duration) {
        if (this.context == null) {
            // ToDo information!!!
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
                // ToDo: If app is in background show notification?
                // ToDo: Show no notification if app is in foreground??
            }
        });
    }

    /**
     * Finds the name of the calling class and the calling method.
     *
     * @return the name of the calling class and the calling method
     */
    private String getCallingClassAndMethodName() {
        final int stackTraceNumber = 4;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[stackTraceNumber];
        return "##" + getCallingClassName(stackTraceElement) + "##" + getCallingMethodName(stackTraceElement);
    }

    /**
     * Returns the name of the calling class.
     *
     * @param stackTraceElement
     *            the last element before this method
     * @return the name of the calling class
     */
    private String getCallingClassName(StackTraceElement stackTraceElement) {
        return stackTraceElement.getClassName();
    }

    /**
     * Returns the name of the calling method.
     *
     * @param stackTraceElement
     *            the last element before this method
     * @return the name of the calling method
     */
    private String getCallingMethodName(StackTraceElement stackTraceElement) {
        return stackTraceElement.getMethodName() + "()";
    }
}
