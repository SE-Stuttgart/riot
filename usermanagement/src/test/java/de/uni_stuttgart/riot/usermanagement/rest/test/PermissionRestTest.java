package de.uni_stuttgart.riot.usermanagement.rest.test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.service.rest.PermissionService;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class PermissionRestTest extends BaseResourceTest<PermissionService, Permission> {

    private int count = 0;
    
    @Override
    public String getSubPath() {
        return "permissions";
    }

    @Override
    public int getTestDataSize() {
        return 7;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("permissionValue", FilterOperator.EQ, "y");
    }

    @Override
    public Permission getNewObject() {
        count = count + 1;
        return new Permission("Blah" + count);
    }

    @Override
    public Class<Permission> getObjectClass() {
        return Permission.class;
    }

    @Override
    public Permission getTestObject() {
        return new Permission("Blah");
    }
}
