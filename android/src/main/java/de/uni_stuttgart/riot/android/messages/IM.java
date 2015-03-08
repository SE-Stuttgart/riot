package de.uni_stuttgart.riot.android.messages;

import android.content.Context;

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

    private Context context;
    private NotificationFactory notificationFactory = null;
    private MessageHandler messageHandler = null;

    // TODO for testing reasons
    private boolean isDummyThing = true;

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
     * .
     *
     * @param isDummyThing .
     */
    public void setDummyThing(boolean isDummyThing) {
        this.isDummyThing = isDummyThing;
    }

    /**
     * .
     *
     * @return .
     */
    public boolean getDummyThing() {
        return this.isDummyThing;
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
     * It is important that the instances gets the application context.
     *
     * @param context the context of the application
     */
    public void setContext(Context context) {
        this.context = context;
        notificationFactory.setContext(context);
        messageHandler.setContext(context);
    }

    /**
     * Return the application context.
     *
     * @return the context of the application
     */
    public Context getContext() {
        return this.context;
    }
}
