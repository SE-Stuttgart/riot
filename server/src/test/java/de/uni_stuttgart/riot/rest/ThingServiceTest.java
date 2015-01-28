package de.uni_stuttgart.riot.rest;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;

@TestData({ "/schema/schema_things.sql", "/data/testdata_things.sql" })
public class ThingServiceTest extends BaseResourceTest<ThingService, RemoteThing> {

    @Override
    public String getSubPath() {
        return "thing";
    }

    @Override
    public int getTestDataSize() {
        return 3;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("name", FilterOperator.EQ, "Haus");
    }

    @Override
    public RemoteThing getNewObject() {
        return new RemoteThing("name", 1);
    }

    @Override
    public Class<RemoteThing> getObjectClass() {
        return RemoteThing.class;
    }

}
