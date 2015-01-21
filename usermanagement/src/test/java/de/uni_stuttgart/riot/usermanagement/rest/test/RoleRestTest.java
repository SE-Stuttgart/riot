package de.uni_stuttgart.riot.usermanagement.rest.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.service.rest.PermissionService;
import de.uni_stuttgart.riot.usermanagement.service.rest.RoleService;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class RoleRestTest extends BaseResourceTest<RoleService , Role>{

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
