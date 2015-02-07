package de.uni_stuttgart.riot.android.management;


import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Dummy class.
 * Created by Benny on 22.01.2015.
 */
public class DummyDevice extends Storable {

    private String deviceName;

    /**
     * Constructor.
     *
     * @param id         .
     * @param deviceName .
     */
    public DummyDevice(long id, String deviceName) {
        super(id);
        this.deviceName = deviceName;
    }

    /**
     * .
     *
     * @return .
     */
    public String getDeviceName() {
        return deviceName;
    }
}
