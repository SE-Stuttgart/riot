package de.uni_stuttgart.riot.i18n;

/**
 * Contains messages for notifications. This loads notification_xx.properties.
 * 
 * @author Philipp Keck
 */
public class NotificationMessages extends UTF8ResourceBundle {

    /**
     * Static instance for use by applications.
     */
    public static final NotificationMessages INSTANCE = new NotificationMessages();

    /**
     * Creates a new resource bundle instance.
     */
    public NotificationMessages() {
        super("languages.notification");
    }

}
