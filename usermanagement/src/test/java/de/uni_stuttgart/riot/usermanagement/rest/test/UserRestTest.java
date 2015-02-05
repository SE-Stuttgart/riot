package de.uni_stuttgart.riot.usermanagement.rest.test;

import de.uni_stuttgart.riot.commons.test.TestData;


// FIXME Handling of UMUser json null values in technical fields due to ignore property
@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class UserRestTest {
/*
 *  extends BaseResourceTest<UserService , UMUser>
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
*/
}
