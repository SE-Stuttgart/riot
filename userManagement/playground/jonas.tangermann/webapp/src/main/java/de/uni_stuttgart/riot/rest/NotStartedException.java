package de.uni_stuttgart.riot.rest;

public class NotStartedException extends Exception {
	
	public NotStartedException(){
		super("The Usermanagement ist not started!");
	}

}
