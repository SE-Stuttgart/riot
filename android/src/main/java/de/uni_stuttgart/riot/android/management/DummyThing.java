package de.uni_stuttgart.riot.android.management;


import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Dummy class
 * Created by Benny on 22.01.2015
 */
public class DummyThing extends Storable {

    private String thingName;

    public DummyThing(long id, String thingName) {
        super(id);
        this.thingName = thingName;
    }

    public String getThingName() {
        return thingName;
    }


}
