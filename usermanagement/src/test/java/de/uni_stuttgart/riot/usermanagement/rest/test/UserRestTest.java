package de.uni_stuttgart.riot.usermanagement.rest.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.service.rest.PermissionService;
import de.uni_stuttgart.riot.usermanagement.service.rest.UserService;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class UserRestTest extends BaseResourceTest<UserService , UMUser>{

    @Override
    public String getSubPath() {
        return "users";
    }

    @Override
    public int getTestDataSize() {
        return 3;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("username", FilterOperator.EQ, "Yoda");
    }

    @Override
    public UMUser getNewObject() {
        return new UMUser("", "", "", 0);
    }

    @Override
    public Class<UMUser> getObjectClass() {
        return UMUser.class;
    }

}
