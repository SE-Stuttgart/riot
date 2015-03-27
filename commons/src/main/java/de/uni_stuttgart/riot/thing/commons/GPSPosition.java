package de.uni_stuttgart.riot.thing.commons;

/**
 * Represents the GPS Position as longitude and latitude.
 *
 */
public class GPSPosition {

    private final Double longitude;
    private final Double latitude;

    /**
     * Constructor.
     * 
     * @param longitude
     *            .
     * @param latitude
     *            .
     */
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
