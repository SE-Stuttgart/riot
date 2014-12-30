package de.uni_stuttgart.riot.android;

import android.view.MenuItem;

public class Filter {

	private MenuItem item;
	private NotificationType type;
	private boolean isChecked;
	
	public Filter(MenuItem item, NotificationType type, boolean isChecked) {
		this.item = item;
		this.type = type;
		this.isChecked = isChecked;
	}
	public MenuItem getItem() {
		return item;
	}
	public void setItem(MenuItem item) {
		this.item = item;
	}
	public NotificationType getType() {
		return type;
	}
	public void setType(NotificationType type) {
		this.type = type;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
