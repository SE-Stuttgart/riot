package de.uni_stuttgart.riot.android.messages;

import android.app.Activity;

/**
 * InstanceManager Enum that saves all singleton instances of the application.
 *
 * @author Benny
 */
public enum IM {
    /**
     * Enum includes the singleton objects.
     */
    INSTANCES(new NotificationFactory(), new MessageHandler());

    private Activity activity;
    private NotificationFactory notificationFactory = null;
    private MessageHandler messageHandler = null;

    /**
     * Constructor.
     *
     * @param notificationFactory the instance of the NotificationFactory
     * @param messageHandler      the instance of the MessageHandler
     */
    IM(NotificationFactory notificationFactory, MessageHandler messageHandler) {
        this.notificationFactory = notificationFactory;
        this.messageHandler = messageHandler;
    }

    /**
     * Get the saved NotificationFactory object.
     *
     * @return the instance of the NotificationFactory class
     */
    public NotificationFactory getNF() {
        return notificationFactory;
    }

    /**
     * Get the saved MessageHandler object.
     *
     * @return the instance of the MessageHandler class
     */
    public MessageHandler getMH() {
        return messageHandler;
    }

    /**
     * It is important that the instances gets the application activity.
     *
     * @param activity the main activity of the application
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        notificationFactory.setActivity(activity);
        messageHandler.setActivity(activity);
    }

    /**
     * Return the main activity of the application.
     *
     * @return the activity of the application
     */
    public Activity getActivity() {
        return this.activity;
    }
}
