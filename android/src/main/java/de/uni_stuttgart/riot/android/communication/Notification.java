package de.uni_stuttgart.riot.android.communication;

import java.util.Date;

import de.uni_stuttgart.riot.android.NotificationType;

public class Notification {

	private String id;
	private String title;
	private String content;
	private NotificationType type;
	private Date date;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public NotificationType getType() {
		return type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public void setDate(Date date){
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

}
