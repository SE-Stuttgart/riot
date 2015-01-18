package de.uni_stuttgart.riot.android;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public enum NotificationType {
	VOID,ERROR, WARNING, APPOINTMENT;
	
	public NotificationType stringToType(String type){
		if(type.equals(ERROR)){
			return ERROR;
		}else if(type.equals(WARNING)){
			return WARNING;
		}else if(type.equals(APPOINTMENT)){
			return APPOINTMENT;
		}
		
		return VOID;
	}
}
