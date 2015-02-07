package de.uni_stuttgart.riot.android.messages;

import android.content.Context;

/**
 * Created by Benny on 10.01.2015.
 * InstanceManager Enum that saves all singleton instances of the application.
 */
public enum IM {
    /**
     * Enum includes the singleton objects.
     */
    INSTANCES(new NotificationFactory(), new MessageHandler());

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
     * It is important that the instances gets the application context.
     *
     * @param context the context of the application
     */
    public void setContext(Context context) {
        notificationFactory.setContext(context);
        messageHandler.setContext(context);
    }
}
