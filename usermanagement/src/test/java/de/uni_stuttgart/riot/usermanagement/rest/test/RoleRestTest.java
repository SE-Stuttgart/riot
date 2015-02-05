package de.uni_stuttgart.riot.usermanagement.rest.test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.security.AuthenticationFilterBinding;
import de.uni_stuttgart.riot.usermanagement.service.rest.RoleService;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class RoleRestTest extends BaseResourceTest<RoleService, Role> {

    public RoleRestTest() {
        AuthenticationFilterBinding.enable = false;
    }

    // @Before
    // public void setUp() {
    // // AuthenticationFilterBinding.enable = false;
    // }

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
        return new FilterAttribute("rolename", FilterOperator.EQ, "Dark");
    }

    @Override
    public Role getNewObject() {
        return new Role("Test");
    }

    @Override
    public Class<Role> getObjectClass() {
        return Role.class;
    }

}
