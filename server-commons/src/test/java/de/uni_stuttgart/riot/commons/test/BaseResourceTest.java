package de.uni_stuttgart.riot.commons.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;

/**
 * Base class for tests of {@link BaseResource} subclasses. The most common CRUD operations are tested generically.
 * 
 * @param <E>
 *            The type of the {@link BaseResource} subclass.
 * @param <T>
 *            The type of resources handled by the BaseResource.
 */
public abstract class BaseResourceTest<E extends BaseResource<T>, T extends Storable> extends JerseyDBTestBase {

    public abstract String getSubPath();

    public abstract int getTestDataSize();

    public abstract FilterAttribute getFilter();

    public abstract T getNewObject();

    public abstract T getTestObject();

    public abstract Class<T> getObjectClass();

    /**
     * Test get one.
     */
    @Test
    public void testGetOne() {
        // expect a 404 for nonexisting resources
        Response a = target(this.getSubPath() + "/" + (this.getTestDataSize() + 1)).request(MediaType.APPLICATION_JSON).get();
        assertThat(a.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
        // expect a 200 for existing ones
        a = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).get();
        assertThat(a.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    /**
     * Test get all.
     */
    @Test
    public void testGetAll() {
        Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        Collection<T> models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(this.getTestDataSize()));
        assertThat(resp.getHeaders().getFirst("offset"), is("0"));
        assertThat(resp.getHeaders().getFirst("limit"), is("20"));
        assertThat(resp.getHeaders().getFirst("total"), is(String.valueOf(this.getTestDataSize())));
    }

    /**
     * Test get with server pagination.
     */
    @Test
    public void testGetPagination() {
        // negative offset: expects a 400 for bad request
        Response resp = target(this.getSubPath()).queryParam("offset", -1).queryParam("limit", 2).request(MediaType.APPLICATION_JSON).header("Access-Token", "token1").get();
        assertThat(resp.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));

        // negative limit: expect a 400 for bad request
        resp = target(this.getSubPath()).queryParam("offset", 1).queryParam("limit", -1).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));

        // normal behavior
        final int pageSize = 2;
        final int offset = 1;
        resp = target(this.getSubPath()).queryParam("offset", offset).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        Collection<T> models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(pageSize));
        assertThat(resp.getHeaders().getFirst("offset"), is(String.valueOf(offset)));
        assertThat(resp.getHeaders().getFirst("limit"), is(String.valueOf(pageSize)));
        assertThat(resp.getHeaders().getFirst("total"), is(String.valueOf(this.getTestDataSize())));

        // using only limit parameter: api/resource?limit=2
        resp = target(this.getSubPath()).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(pageSize));
    }

    /**
     * Tests that response delivers default amount = 20.
     */
    @Test
    public void testGetFilteringAndDefaultPageSize() {
        // Default page size is 20.

        // Filling test data with + 20 elements
        List<Long> insertedIDs = new ArrayList<Long>(20);
        for (int i = 0; i < 20; i++) {
            Entity<T> testEntity = Entity.entity(this.getNewObject(), MediaType.APPLICATION_JSON_TYPE);
            Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).post(testEntity);
            assertThat(resp.getStatus(), is(Response.Status.CREATED.getStatusCode()));
            insertedIDs.add(resp.readEntity(getObjectClass()).getId());
        }

        // get request without "limit" returns the default amount = 20
        Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        Collection<T> models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(20));
        assertThat(resp.getHeaders().getFirst("offset"), is("0"));
        assertThat(resp.getHeaders().getFirst("limit"), is("20"));
        assertThat(resp.getHeaders().getFirst("total"), is(String.valueOf(this.getTestDataSize() + 20)));

        // get with filtering-unspecific query params must work
        resp = target(this.getSubPath()).queryParam("bla", "bli").queryParam("foo", "bar").request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));

        // Testing a simple equals filter. Only 1 element should be found.
        final int pageSize = 10;
        final int offset = 0;

        resp = target(this.getSubPath()).queryParam("offset", offset).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        models = resp.readEntity(new GenericType<List<T>>() {
        });
        assertThat(models, hasSize(pageSize));
        assertThat(resp.getHeaders().getFirst("offset"), is(String.valueOf(offset)));
        assertThat(resp.getHeaders().getFirst("limit"), is(String.valueOf(pageSize)));
        assertThat(resp.getHeaders().getFirst("total"), is(String.valueOf(this.getTestDataSize() + 20)));

        FilterAttribute filter = getFilter();
        String filterStr = String.format("%s_%s", filter.getFieldName(), filter.getOperator());
        resp = target(this.getSubPath()).queryParam(filterStr, filter.getValue()).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertThat(resp.getStatus(), is(Response.Status.OK.getStatusCode()));
        models = resp.readEntity(new GenericType<List<T>>() {
        });
        assertThat(models.size(), lessThanOrEqualTo(pageSize));
        assertThat(resp.getHeaders().getFirst("offset"), is(String.valueOf(offset)));
        assertThat(resp.getHeaders().getFirst("limit"), is(String.valueOf(pageSize)));
        int totalHeader = Integer.parseInt(resp.getHeaders().getFirst("total").toString());
        assertThat(totalHeader, is(1)); // Only one item should match the filter from getFilter().

        // Tidy up
        for (long id : insertedIDs) {
            resp = target(this.getSubPath() + "/" + id).request(MediaType.APPLICATION_JSON).delete();
            assertThat(resp.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        }
    }

    /**
     * Test creating a new object.
     */
    @Test
    public void testCreateAndDelete() {
        Entity<T> testEntity = Entity.entity(this.getNewObject(), MediaType.APPLICATION_JSON_TYPE);
        Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).post(testEntity);
        assertThat(resp.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        // post requests must return the newly created resource with the id set
        T created = resp.readEntity(this.getObjectClass());
        assertThat(created.getId().intValue(), is(this.getTestDataSize() + 1));
        // post must fail with invalid body
        resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).post(null);
        // expect a 400 for bad request
        assertThat(resp.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));

        resp = target(this.getSubPath() + "/" + created.getId()).request(MediaType.APPLICATION_JSON).delete();
        assertThat(resp.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        // resource deleted, so same request should return 404
        resp = target(this.getSubPath() + "/" + created.getId()).request(MediaType.APPLICATION_JSON).delete();
        assertThat(resp.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    /**
     * Test put one. Response of 204 indicates success, 404 when nothing found that could be updated.
     */
    @Test
    public void testUpdateOne() {
        Entity<T> testEntity = Entity.entity(this.getTestObject(), MediaType.APPLICATION_JSON_TYPE);
        T testValue = this.getTestObject();
        testValue.setId(Long.valueOf(this.getTestDataSize()));

        // get the last element (for restoring it later)
        T original = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).get(getObjectClass());

        // updates the last element
        Response resp = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).put(testEntity);
        assertThat(resp.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        Response a = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).get();
        assertThat(a.getStatus(), is(Response.Status.OK.getStatusCode()));
        T updated = a.readEntity(this.getObjectClass());

        assertThat(updated, equalTo(testValue));
        // resource doesnt exist, so put request should return 404
        resp = target(this.getSubPath() + "/" + (this.getTestDataSize() + 1)).request(MediaType.APPLICATION_JSON).put(testEntity);
        assertThat(resp.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));

        // Restore
        testEntity = Entity.entity(original, MediaType.APPLICATION_JSON_TYPE);
        resp = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).put(testEntity);
        assertThat(resp.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

}
