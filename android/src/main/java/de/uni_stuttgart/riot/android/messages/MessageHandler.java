package de.uni_stuttgart.riot.android.messages;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Benny on 12.12.2014.
 */
public class MessageHandler {


    private Context context;

    /**
     * Constructor
     */
    public MessageHandler() {
        init();
    }

    /**
     * Initializes the attributes
     */
    private void init() {
    }

    /**
     * Saves the application context
     *
     * @param pContext the context of the application
     */
    public void setContext(Context pContext) {
        context = pContext;
    }

    /**
     * Writes a message with the Log.DEBUG priority
     *
     * @param text the message that will be saved
     */
    public void writeDebugMessage(String text) {
        Log.d(getCallingClassAndMethodName(), text);
    }

    /**
     * Writes a message with the Log.ERROR priority
     *
     * @param text the message that will be saved
     */
    public void writeErrorMessage(String text) {
        Log.e(getCallingClassAndMethodName(), text); // Throwable tr); ToDo: use throwable in error log?
        // ToDO: show a toast after writing an error message into log?
    }

    /**
     * Writes a message with the Log.INFO priority
     *
     * @param text the message that will be saved
     */
    public void writeInfoMessage(String text) {
        Log.i(getCallingClassAndMethodName(), text);
    }

    /**
     * Writes a message with the Log.VERBOSE priority
     *
     * @param text the message that will be saved
     */
    public void writeMessage(String text) {
        Log.v(getCallingClassAndMethodName(), text);
    }

    /**
     * Writes a message with the Log.WARN priority
     *
     * @param text the message that will be saved
     */
    public void writeWarnMessage(String text) {
        Log.w(getCallingClassAndMethodName(), text);
    }


    /**
     * Shows a toast with short duration
     *
     * @param text the message that will be shown
     */
    public void showQuickMessage(String text) {
        int duration = Toast.LENGTH_SHORT;
        showToast(text, duration);

    }

    /**
     * Shows a toast with long duration
     *
     * @param text the message that will be shown
     */
    public void showMessage(String text) {
        int duration = Toast.LENGTH_LONG;
        showToast(text, duration);
    }

    /**
     * Shows a toast with the given text and duration
     *
     * @param text     the message that will be shown
     * @param duration the time the message will visible
     */
    private void showToast(String text, int duration) {
        Toast.makeText(this.context, text, duration).show();
        // ToDo: If app is in background show notification?
        // ToDo: Show no notification if app is in foreground??
    }

    /**
     * Finds the name of the calling class and the calling method
     *
     * @return the name of the calling class and the calling method
     */
    private String getCallingClassAndMethodName() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        return "##" + getCallingClassName(stackTraceElement) + "##"
                + getCallingMethodName(stackTraceElement);
    }

    /**
     * Returns the name of the calling class
     *
     * @param stackTraceElement the last element before this method
     * @return the name of the calling class
     */
    private String getCallingClassName(StackTraceElement stackTraceElement) {
        return stackTraceElement.getClassName();
    }

    /**
     * Returns the name of the calling method
     *
     * @param stackTraceElement the last element before this method
     * @return the name of the calling method
     */
    private String getCallingMethodName(StackTraceElement stackTraceElement) {
        return stackTraceElement.getMethodName() + "()";
    }
}
