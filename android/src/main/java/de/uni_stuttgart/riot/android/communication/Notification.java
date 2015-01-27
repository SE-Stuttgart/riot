package de.uni_stuttgart.riot.android.communication;

import java.util.Date;

import de.uni_stuttgart.riot.android.NotificationType;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class Notification {

	private int id;
	private String title;
	private String content;
	private NotificationType type;
	private String date;
	private String thing;
	
	public Notification(){
		
	}
	
	public Notification(int id, String title, String content, NotificationType type, String date, String thing){
		this.id = id;
		this.title = title;
		this.content = content;
		this.type = type;
		this.date = date;
		this.thing = thing;
	}

	public String getThingName() {
		return thing;
	}

	public void getThingName(String thing) {
		this.thing = thing;
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
	
	public void setDate(String date){
		this.date = date;
	}

	public String getDate() {
		return date;
	}

}
