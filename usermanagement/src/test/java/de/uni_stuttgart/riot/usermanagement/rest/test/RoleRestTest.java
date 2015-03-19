package de.uni_stuttgart.riot.usermanagement.rest.test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.service.rest.RoleService;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class RoleRestTest extends BaseResourceTest<RoleService, Role> {

    private int count = 0;
    
    @Override
    public String getSubPath() {
        return "roles";
    }

    @Override
    public int getTestDataSize() {
        return 4;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("rolename", FilterOperator.EQ, "admin");
    }

    @Override
    public Role getNewObject() {
        count = count + 1;
        return new Role("Test" + count);
    }

    @Override
    public Class<Role> getObjectClass() {
        return Role.class;
    }

    @Override
    public Role getTestObject() {
        return new Role("Test");
    }

}
