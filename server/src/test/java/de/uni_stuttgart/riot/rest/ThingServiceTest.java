package de.uni_stuttgart.riot.rest;

import org.apache.shiro.SecurityUtils;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.usermanagement.security.AccessToken;

@TestData({ "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", 
    "/schema/schema_things.sql", "/data/testdata_things.sql" })
//public class ThingServiceTest extends ShiroEnabledBaseResourceTest<ThingService, RemoteThing> {
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

    protected void doLogin() {
//        UserManagementFacade facade = UserManagementFacade.getInstance();
//        try {
//            Token login = facade.login("Yoda", "YodaPW");
            
//            Subject subject = new Subject.Builder(getSecurityManager()).buildSubject();
//            this.setSubject(subject);
//            subject.login(new AccessToken(login.getTokenValue()));
            SecurityUtils.getSubject().login(new AccessToken("token1"));
            
            //CHECKSTYLE: OFF
            System.out.println("############## Logged in with Yoda");
            System.out.println("############## thing:1:* => " + SecurityUtils.getSubject().isPermitted("thing:1:*"));
//        } catch (LoginException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public RemoteThing getTestObject() {
        return new RemoteThing("name", 1);
    }

}
