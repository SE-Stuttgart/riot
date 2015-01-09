package de.uni_stuttgart.riot.android.communication;

import java.util.Date;

import de.uni_stuttgart.riot.android.NotificationType;
import de.uni_stuttgart.riot.android.messages.NotificationFactory;

public class Notification {

	private int id;
	private String title;
	private String content;
	private NotificationType type;
	private Date date;
	
	public Notification(){
		
	}
	
	public Notification(int id, String title, String content, NotificationType type, Date date){
		this.id = id;
		this.title = title;
		this.content = content;
		this.type = type;
		this.date = date;
    }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
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
