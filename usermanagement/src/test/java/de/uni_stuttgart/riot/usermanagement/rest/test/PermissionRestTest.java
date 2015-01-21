package de.uni_stuttgart.riot.usermanagement.rest.test;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.usermanagement.service.rest.PermissionService;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class PermissionRestTest extends BaseResourceTest<PermissionService, Permission>{

    @Override
    public String getSubPath() {
        return "permissions";
    }

    @Override
    public int getTestDataSize() {
        return 4;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("permissionValue", FilterOperator.EQ, "y");
    }

    @Override
    public Permission getNewObject() {
        return new Permission("Blah");
    }

    @Override
    public Class<Permission> getObjectClass() {
        return Permission.class;
    }
}
