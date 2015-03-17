package de.uni_stuttgart.riot.thing.commons;

public class GPSPosition {
    
    private final Double longitude;
    private final Double latitude;
 
    public GPSPosition(Double longitude, Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

}
