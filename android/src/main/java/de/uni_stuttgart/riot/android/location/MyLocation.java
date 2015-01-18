package de.uni_stuttgart.riot.android.location;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class MyLocation {

	private String place;
	private String address;
	private double latitude;
	private double longitude;

	public MyLocation(String place, String address, double latitude,
			double longitude) {
		this.place = place;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
