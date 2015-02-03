package de.uni_stuttgart.riot.android.communication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import de.uni_stuttgart.riot.android.NotificationScreen;
import de.uni_stuttgart.riot.android.NotificationType;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class ServerConnection extends AsyncTask<Void, Void, List<Notification>> {

    private NotificationScreen notificationScreen;
    private RIOTDatabase database;
    private Notification testNotification;
    private List<Notification> notificationList = new ArrayList<Notification>();

    public ServerConnection(NotificationScreen notificationScreen, RIOTDatabase database) {
        this.notificationScreen = notificationScreen;
        this.database = database;
    }

    @Override
    protected List<Notification> doInBackground(Void... params) {
        try {
            final String url = "http://rest-service.guides.spring.io/greeting";
            RestTemplate restTemplate = new RestTemplate();

            if (isNetworkAvailable()) {
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                testNotification = restTemplate.getForObject(url, Notification.class);
            } else {
                Toast.makeText(notificationScreen, "No internet connection", Toast.LENGTH_SHORT).show();
                // notificationScreen.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }

            if (testNotification == null) {
                Toast.makeText(notificationScreen, "Server Connection Error", 5).show();
            } else {

                Notification n1 = new Notification(1, "FIRE!!!", "Call 112", NotificationType.ERROR, new SimpleDateFormat("K:mm a, E d.MMM, yyyy").format(new Date()), "house");
                notificationList.add(n1);
                Notification n2 = new Notification(2, "Refill water", "", NotificationType.ERROR, new SimpleDateFormat("K:mm a, E d.MMM, yyyy").format(new Date()), "coffeeMachine");
                notificationList.add(n2);
                Notification n3 = new Notification(3, "Refill beans", "", NotificationType.ERROR, new SimpleDateFormat("K:mm a, E d.MMM, yyyy").format(new Date()), "coffeeMachine");
                notificationList.add(n3);
                Notification n4 = new Notification(4, "Buy some food", "Bananas, Beer, Butter", NotificationType.WARNING, new SimpleDateFormat("K:mm a, E d.MMM,yyyy").format(new Date()), "house");
                notificationList.add(n4);
                Notification n5 = new Notification(5, "Refuel car", "", NotificationType.WARNING, new SimpleDateFormat("K:mm a, E d.MMM, yyyy").format(new Date()), "car");
                notificationList.add(n5);

                return notificationList;
            }

        } catch (HttpClientErrorException e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) notificationScreen.getSystemService(notificationScreen.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPostExecute(List<Notification> notificationList) {
        database.updateNotificationEntries(notificationList);
        database.filterNotifications();
    }

}
