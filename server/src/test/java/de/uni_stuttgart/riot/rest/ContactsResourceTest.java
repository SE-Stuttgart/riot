package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.contacts.ContactsResource;

/**
 * Test class for the contacts which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
@TestData({ "/schema/schema_contacts.sql", "/data/testdata_contacts.sql" })
public class ContactsResourceTest extends BaseResourceTest<ContactsResource, Contact> {

    /**
     * Tests if a GET request to the resource work.
     */
    @Test
    public void testGetRequest() {
        final String response = target("contacts").request().get(String.class);
        assertNotNull(response);
    }

    /**
     * Tests if a GET request (with id) to the resource work.
     */
    @Test
    public void testGetRequestWithId() {
        final Contact response = target("contacts/1").request().get(Contact.class);
        assertEquals("John", response.getFirstName());
    }

    @Override
    public String getSubPath() {
        return "contacts";
    }

    @Override
    public int getTestDataSize() {
        return 3;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("firstName", FilterOperator.EQ, "John");
    }

    @Override
    public Contact getNewObject() {
        // TODO Auto-generated method stub
        return new Contact(1, "first name", "last name", "0000");
    }

    @Override
    public Class<Contact> getObjectClass() {
        return Contact.class;
    }
}
