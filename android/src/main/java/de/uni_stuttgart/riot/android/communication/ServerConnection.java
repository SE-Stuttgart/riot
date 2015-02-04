package de.uni_stuttgart.riot.android.communication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.MainActivity;
import de.uni_stuttgart.riot.android.NotificationAdapter;
import de.uni_stuttgart.riot.android.NotificationType;
import de.uni_stuttgart.riot.android.database.FilterDataObjects;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class ServerConnection extends AsyncTask<Void, Void, List<Notification>> {

	private MainActivity mainActivity;
	private FilterDataObjects filterObjects;

	public ServerConnection(MainActivity mainActivity, FilterDataObjects filterObjects) {
		this.mainActivity = mainActivity;
		this.filterObjects = filterObjects;
	}

	@Override
	protected List<Notification> doInBackground(Void... params) {
		try {
			final String url = "http://rest-service.guides.spring.io/greeting";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJackson2HttpMessageConverter());
			Notification testNotification = restTemplate.getForObject(url,
					Notification.class);

			List<Notification> notificationList = new ArrayList<Notification>();

			if (testNotification == null) {
				Toast.makeText(mainActivity.getApplicationContext(),
						"Server Connection Error", 5).show();
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

	@Override
	protected void onPostExecute(List<Notification> notificationList) {	
//TODO problem with the DB? FIXME seems that a column does not exist
// 		filterObjects.getDatabase().updateNotificationEntries(notificationList);
//		filterObjects.getDatabase().filterNotifications();
	}

}
