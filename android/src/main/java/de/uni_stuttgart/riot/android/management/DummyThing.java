package de.uni_stuttgart.riot.android.management;


import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Dummy class.
 *
 * @author Benny
 */
public class DummyThing extends Storable {

    private String thingName;

    /**
     * Constructor.
     *
     * @param id        .
     * @param thingName .
     */
    public DummyThing(long id, String thingName) {
        super(id);
        this.thingName = thingName;
    }

    /**
     * .
     *
     * @return .
     */
    public String getThingName() {
        return thingName;
    }
}
