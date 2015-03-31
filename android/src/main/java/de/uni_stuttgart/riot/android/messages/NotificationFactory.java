package de.uni_stuttgart.riot.android.messages;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Vector;

import de.uni_stuttgart.riot.android.R;


/**
 * This class provides building notifications and show them in the android system.
 *
 * @author Benny
 */
public class NotificationFactory {

    // private static String NOTIFICATION_GROUP = "RIOT-Group";
    private static final int POSITIVE = 2;
    private static final int NUMBER_OF_RESERVED_NOTIFICATION_ID = 5;

    private Context context;
    private Activity activity;
    private int notificationId;
    private HashMap<Integer, Vector<Notification.Builder>> notificationList;
    private Notification.Builder[] stackedNotificationList;

    /**
     * Constructor.
     */
    public NotificationFactory() {
        init();
    }

    /**
     * Initializes the attributes.
     */
    private void init() {
        notificationId = NUMBER_OF_RESERVED_NOTIFICATION_ID; // the first five ids are for the stacked notifications
        notificationList = new HashMap<Integer, Vector<Notification.Builder>>();
        notificationList.put(Notification.PRIORITY_MAX + POSITIVE, new Vector<Notification.Builder>());
        notificationList.put(Notification.PRIORITY_HIGH + POSITIVE, new Vector<Notification.Builder>());
        notificationList.put(Notification.PRIORITY_DEFAULT + POSITIVE, new Vector<Notification.Builder>());
        notificationList.put(Notification.PRIORITY_LOW + POSITIVE, new Vector<Notification.Builder>());
        notificationList.put(Notification.PRIORITY_MIN + POSITIVE, new Vector<Notification.Builder>());
        stackedNotificationList = new Notification.Builder[NUMBER_OF_RESERVED_NOTIFICATION_ID];
    }

    /**
     * Clears all prepared (saved) notifications.
     */
    public void clearPreparedNotifications() {
        init();
    }

    /**
     * Saves the application context.
     *
     * @param context the context of the application
     */
    private void setContext(Context context) {
        this.context = context;
    }

    /**
     * Saves the main activity.
     *
     * @param activity the main activity of the application
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        setContext(this.activity.getApplicationContext());
    }

    /**
     * Prepares a critical notification with the highest priority.
     *
     * @param title the title of the notification
     */
    public void prepareCriticalNotification(String title) {
        prepareNotification(title, Notification.PRIORITY_MAX);
    }

    /**
     * Prepares a critical notification with the highest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     */
    public void prepareCriticalNotification(String title, String body) {
        prepareNotification(title, body, Notification.PRIORITY_MAX);
    }

    /**
     * Prepares a critical notification with the highest priority.
     *
     * @param title  the title of the notification
     * @param body   the body of the notification
     * @param iconId the id of the icon in the drawables
     */
    public void prepareCriticalNotification(String title, String body, int iconId) {
        prepareNotification(title, body, iconId, Notification.PRIORITY_MAX);
    }

    /**
     * Prepares a critical notification with the highest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     * @param icon  the icon that will be shown on the left
     */
    public void prepareCriticalNotification(String title, String body, Bitmap icon) {
        prepareNotification(title, body, icon, Notification.PRIORITY_MAX);
    }


    /**
     * Prepares a important notification with the second highest priority.
     *
     * @param title the title of the notification
     */
    public void prepareImportantNotification(String title) {
        prepareNotification(title, Notification.PRIORITY_HIGH);
    }

    /**
     * Prepares a important notification with the second highest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     */
    public void prepareImportantNotification(String title, String body) {
        prepareNotification(title, body, Notification.PRIORITY_HIGH);
    }

    /**
     * Prepares a important notification with the second highest priority.
     *
     * @param title  the title of the notification
     * @param body   the body of the notification
     * @param iconId the id of the icon in the drawables
     */
    public void prepareImportantNotification(String title, String body, int iconId) {
        prepareNotification(title, body, iconId, Notification.PRIORITY_HIGH);
    }

    /**
     * Prepares a important notification with the second highest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     * @param icon  the icon that will be shown on the left
     */
    public void prepareImportantNotification(String title, String body, Bitmap icon) {
        prepareNotification(title, body, icon, Notification.PRIORITY_HIGH);
    }


    /**
     * Prepares a default notification with the second highest priority.
     *
     * @param title the title of the notification
     */
    public void prepareDefaultNotification(String title) {
        prepareNotification(title, Notification.PRIORITY_DEFAULT);
    }

    /**
     * Prepares a default notification with the second highest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     */
    public void prepareDefaultNotification(String title, String body) {
        prepareNotification(title, body, Notification.PRIORITY_DEFAULT);
    }

    /**
     * Prepares a default notification with the second highest priority.
     *
     * @param title  the title of the notification
     * @param body   the body of the notification
     * @param iconId the id of the icon in the drawables
     */
    public void prepareDefaultNotification(String title, String body, int iconId) {
        prepareNotification(title, body, iconId, Notification.PRIORITY_DEFAULT);
    }

    /**
     * Prepares a default notification with the second highest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     * @param icon  the icon that will be shown on the left
     */
    public void prepareDefaultNotification(String title, String body, Bitmap icon) {
        prepareNotification(title, body, icon, Notification.PRIORITY_DEFAULT);
    }


    /**
     * Prepares a unimportant notification with the second lowest priority.
     *
     * @param title the title of the notification
     */
    public void prepareUnimportantNotification(String title) {
        prepareNotification(title, Notification.PRIORITY_LOW);
    }

    /**
     * Prepares a unimportant notification with the second lowest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     */
    public void prepareUnimportantNotification(String title, String body) {
        prepareNotification(title, body, Notification.PRIORITY_LOW);
    }

    /**
     * Prepares a unimportant notification with the second lowest priority.
     *
     * @param title  the title of the notification
     * @param body   the body of the notification
     * @param iconId the id of the icon in the drawables
     */
    public void prepareUnimportantNotification(String title, String body, int iconId) {
        prepareNotification(title, body, iconId, Notification.PRIORITY_LOW);
    }

    /**
     * Prepares a unimportant notification with the second lowest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     * @param icon  the icon that will be shown on the left
     */
    public void prepareUnimportantNotification(String title, String body, Bitmap icon) {
        prepareNotification(title, body, icon, Notification.PRIORITY_LOW);
    }


    /**
     * Prepares a uncritical notification with the lowest priority.
     *
     * @param title the title of the notification
     */
    public void prepareUncriticalNotification(String title) {
        prepareNotification(title, Notification.PRIORITY_MIN);
    }

    /**
     * Prepares a uncritical notification with the lowest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     */
    public void prepareUncriticalNotification(String title, String body) {
        prepareNotification(title, body, Notification.PRIORITY_MIN);
    }

    /**
     * Prepares a uncritical notification with the lowest priority.
     *
     * @param title  the title of the notification
     * @param body   the body of the notification
     * @param iconId the id of the icon in the drawables
     */
    public void prepareUncriticalNotification(String title, String body, int iconId) {
        prepareNotification(title, body, iconId, Notification.PRIORITY_MIN);
    }

    /**
     * Prepares a uncritical notification with the lowest priority.
     *
     * @param title the title of the notification
     * @param body  the body of the notification
     * @param icon  the icon that will be shown on the left
     */
    public void prepareUncriticalNotification(String title, String body, Bitmap icon) {
        prepareNotification(title, body, icon, Notification.PRIORITY_MIN);
    }


    /**
     * Prepares a notification with the given priority.
     *
     * @param title    the title of the notification
     * @param priority the priority of the notification
     */
    private void prepareNotification(String title, int priority) {
        prepareNotification(title, null, null, priority);
    }

    /**
     * Prepares a notification with the given priority.
     *
     * @param title    the title of the notification
     * @param body     the body of the notification
     * @param priority the priority of the notification
     */
    private void prepareNotification(String title, String body, int priority) {
        prepareNotification(title, body, null, priority);
    }

    /**
     * Prepares a notification with the given priority.
     *
     * @param title    the title of the notification
     * @param body     the body of the notification
     * @param iconId   the id of the icon in the drawables
     * @param priority the priority of the notification
     */
    private void prepareNotification(String title, String body, int iconId, int priority) {
        if (this.context == null) {
            // ToDo information!!!
            return;
        }
        prepareNotification(title, body, BitmapFactory.decodeResource(
                context.getResources(), iconId), priority);
    }

    /**
     * Prepares a notification with the given priority.
     *
     * @param title    the title of the notification
     * @param body     the body of the notification
     * @param icon     the icon that will be shown on the left
     * @param priority the priority of the notification
     */
    private void prepareNotification(String title, String body, Bitmap icon, int priority) {
        if (this.context == null) {
            // ToDo information!!!
            return;
        }
        Notification.Builder notification = new Notification.Builder(this.context);

        // ToDo: Design Big and Small icon
        // ToDo: Maybe use the level of small icons (for error, warning, .. icon)
        notification.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setPriority(priority)
                .setWhen(System.currentTimeMillis())
                        // ToDo: .setGroup() // API 20 - SDK 4.4W
                .setAutoCancel(true);
        if (body != null) {
            notification.setContentText(body);
        }
        if (icon != null) {
            notification.setLargeIcon(icon);
        }
        // ToDo: Use the right units

        // ToDo: Action (nicht vergessen: Preserving Navigation when Starting an Activity)
        // Benutzer soll beim zurückklicken zum homescreen oder so kommen (wie bei der normalen benutzung der app auch)
        // http://developer.android.com/guide/topics/ui/notifiers/notifications.html#NotificationResponse

        // ToDo: Category?
        // ToDo: Sound and vibration?


        // Save new notification in a list
        notificationList.get(priority + POSITIVE).add(notification);


        // If the list is bigger than one than prepare a stacked notification
        Integer listSize = notificationList.get(priority + POSITIVE).size();
        if (listSize > 1) {
            TypedArray priorityTxt = context.getResources().obtainTypedArray(R.array.priority);
            String stackedTitle = listSize.toString() + context.getString(R.string.notification_messages_with)
                    + priorityTxt.getString(priority + POSITIVE) + context.getString(R.string.notification_priority);
            String stackedSummary = "...";

            // Prepare the expanded stacked notification
            Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
            inboxStyle.setBigContentTitle(stackedTitle)
                    .setSummaryText(stackedSummary);
            // Add notifications of the same priority as lines in the stacked notification
            if (!notificationList.get(priority + POSITIVE).isEmpty()) {
                for (Notification.Builder tmpNot : notificationList.get(priority + POSITIVE)) {
                    inboxStyle.addLine(tmpNot.toString()); // ToDo: Anpassen
                }
            }

            // ##########################
            // Eingeklappt:
            // B    ContentTitel
            // I
            // L    ContentText
            // D                        ICON
            // ##############################
            // Ausgeklappt:
            // B    BigContentTitel
            // I
            // L    bigText
            // D
            //      BigContentSummary   ICON
            // ##############################

            // Prepare the contracted stacked notification
            Notification.Builder stackedNotification = new Notification.Builder(this.context);
            stackedNotification.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(stackedTitle)
                    .setContentText(stackedSummary)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(priority)
                    .setNumber(listSize)
                    .setAutoCancel(true);

            // Add expanded style to the stacked notification
            stackedNotification.setStyle(inboxStyle);

            // Save stacked notification in list
            stackedNotificationList[priority + POSITIVE] = stackedNotification;
            //showStackedNotification();
            //return;
        }

        // Else return the upper prepared notification
        //showNotification(notification);
    }


    /**
     * Displays a prepared notification.
     */
    public void showStackedNotification() {
        if (this.context == null) {
            // ToDo information!!!
            return;
        }

        // Cancel all notifications
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

        for (int i = 0; i < NUMBER_OF_RESERVED_NOTIFICATION_ID; i++) {
            if (stackedNotificationList[i] == null) {
                // Display all notifications if there does not exists a stacked notification for this priority
                if (!notificationList.get(i).isEmpty()) {
                    for (Notification.Builder tmpNot : notificationList.get(i)) {
                        showNotification(tmpNot);
                    }
                }
            } else {
                // Display all stacked notifications
                showNotification(stackedNotificationList[i]);
            }
        }
        // ToDo: Ueber "ContentnIntent" aus Liste loeschen.
        // ToDo: Von User entfernte Notifications aus Listen loeschen.
    }

    /**
     * Displays a prepared notification.
     *
     * @param notification the notification that will be shown
     */
    private void showNotification(Notification.Builder notification) {
        if (this.context == null) {
            // ToDo information!!!
            return;
        }

        notificationId++;
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(notificationId, notification.build());
        // ToDo: Save the notificationManager as private variable?
    }
}
