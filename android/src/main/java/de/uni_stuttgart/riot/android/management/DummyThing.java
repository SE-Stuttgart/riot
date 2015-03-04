package de.uni_stuttgart.riot.android.management;


import java.util.ArrayList;

import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * Dummy class.
 *
 * @author Benny
 */
public class DummyThing extends Thing {

    private ArrayList<DummyProperty> properties;

    /**
     * Constructor.
     *
     * @param id        .
     * @param thingName .
     */
    public DummyThing(long id, String thingName) {
        super(thingName, new ThingBehavior() {
            @Override
            protected <A extends ActionInstance> void userFiredAction(A a) {

            }
        });
        setId(id);
        properties = new ArrayList<DummyProperty>();
    }

    /**
     * .
     *
     * @param properties .
     */
    public void setProperties(ArrayList<DummyProperty> properties) {
        this.properties = properties;
    }

    /**
     * .
     *
     * @return .
     */
    public ArrayList<DummyProperty> getDummyProperties() {
        return this.properties;
    }

    /**
     * .
     *
     * @param property .
     * @param value    .
     */
    public void updateProperty(DummyProperty property, Object value) {
        int index = this.properties.indexOf(property);
        if (index != -1) {
            property.setValue(value);
            this.properties.set(index, property);
        } else {
            // ToDo problem -- property not found!
        }
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
