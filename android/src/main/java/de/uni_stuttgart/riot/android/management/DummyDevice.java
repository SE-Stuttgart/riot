package de.uni_stuttgart.riot.android.management;


import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Dummy class.
 *
 * @author Benny
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

    /**
     * Returns the image resource id to display the online state.
     *
     * @return .
     */
    public OnlineState getOnlineState() {
        final int number = 3;
        return OnlineState.getEnumById(this.getId() % number);
    }
}
