package de.uni_stuttgart.riot.android;

import android.view.MenuItem;

public class Filter {

	private int id;
	private MenuItem item;
	private NotificationType type;
	private boolean isChecked;
	
	public Filter(int id, MenuItem item, NotificationType type, boolean isChecked) {
		this.id = id;
		this.item = item;
		this.type = type;
		this.isChecked = isChecked;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
