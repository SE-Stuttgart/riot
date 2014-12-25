package de.uni_stuttgart.riot.android;

import java.util.List;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAdapter extends BaseAdapter {

	private MainActivity mainActivity;

	List<Notification> notificationList;

	public NotificationAdapter(MainActivity mainActivity,
			List<Notification> list) {
		this.mainActivity = mainActivity;
		notificationList = list;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			LayoutInflater inflater = (LayoutInflater) mainActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.notification_item, arg2, false);
		}

		TextView title = (TextView) arg1.findViewById(R.id.notificationTitle);
		TextView content = (TextView) arg1
				.findViewById(R.id.notificationContent);
		TextView date = (TextView) arg1.findViewById(R.id.notificationDate);
		ImageView typIcon = (ImageView) arg1.findViewById(R.id.typeIcon);

		Notification noti = notificationList.get(arg0);

		title.setText(noti.getTitle());
		content.setText(noti.getContent());
		date.setText(noti.getDate().toString());

		if (noti.getType().equals(NotificationType.ERROR)) {
			typIcon.setImageResource(R.drawable.error_icon);
		} else if (noti.getType().equals(NotificationType.NOTIFCATION)) {
			typIcon.setImageResource(R.drawable.notification_icon);
		} else if (noti.getType().equals(NotificationType.APPOINTMENT)) {
			typIcon.setImageResource(R.drawable.appointment_icon);
		}

		return arg1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return notificationList.size();
	}

	@Override
	public Notification getItem(int arg0) {
		// TODO Auto-generated method stub
		return notificationList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

}
