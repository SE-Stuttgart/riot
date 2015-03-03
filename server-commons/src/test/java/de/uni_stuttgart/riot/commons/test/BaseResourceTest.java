package de.uni_stuttgart.riot.commons.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;

/**
 * Use a mock implementation of the BaseResource to check if the behavior is as expected.
 * 
 * @param <T>
 *
 */
public abstract class BaseResourceTest<E extends BaseResource<T>, T extends Storable> extends JerseyDBTestBase {

    public abstract String getSubPath();

    public abstract int getTestDataSize();

    public abstract FilterAttribute getFilter();

    /**
     * Test get one.
     */
    @Test
    public void testGetOne() {
        // expect a 404 for nonexisting resources
        Response a = target(this.getSubPath() + "/" + (this.getTestDataSize() + 1)).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), a.getStatus());
        // expect a 200 for existing ones
        a = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), a.getStatus());
    }

    /**
     * Test get all.
     */
    @Test
    public void testGetAll() {
        Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<T> models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertEquals(this.getTestDataSize(), models.size());
        assertEquals("wrong offset", String.valueOf(0), resp.getHeaders().getFirst("offset"));
        assertEquals("wrong limit", String.valueOf(20), resp.getHeaders().getFirst("limit")); // Default page size is 20.
        String expectedTotal = String.valueOf(this.getTestDataSize());
        assertEquals("wrong total", expectedTotal, resp.getHeaders().getFirst("total"));
    }

    /**
     * Test get with server pagination.
     */
    @Test
    public void testGetPagination() {        
        // negative offset: expects a 400 for bad request
        Response resp = target(this.getSubPath()).queryParam("offset", -1).queryParam("limit", 2).request(MediaType.APPLICATION_JSON).header("Access-Token", "token1").get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());

        // negative limit: expect a 400 for bad request
        resp = target(this.getSubPath()).queryParam("offset", 1).queryParam("limit", -1).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());

        // normal behavior
        final int pageSize = 2;
        final int offset = 1;
        resp = target(this.getSubPath()).queryParam("offset", offset).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<T> models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(pageSize));
        assertEquals("wrong offset", String.valueOf(offset), resp.getHeaders().getFirst("offset"));
        assertEquals("wrong limit", String.valueOf(pageSize), resp.getHeaders().getFirst("limit"));
        String expectedTotal = String.valueOf(this.getTestDataSize());
        assertEquals("wrong total", expectedTotal, resp.getHeaders().getFirst("total"));

        // using only limit parameter: api/resource?limit=2
        resp = target(this.getSubPath()).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(pageSize));
    }

    /**
     * Tests that response delivers default amount = 20.
     */
    @Test
    public void testGetDefaultPageSize() {
        // Default page size is 20.

        // Filling test data with + 20 elements
        for (int i = 0; i < 20; i++) {
            Entity<T> testEntity = Entity.entity(this.getNewObject(), MediaType.APPLICATION_JSON_TYPE);
            Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).post(testEntity);
            assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
        }

        // get request without "limit" returns the default amount = 20
        Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<T> models = resp.readEntity(new GenericType<Collection<T>>() {
        });
        assertThat(models, hasSize(20));
        assertEquals("wrong offset", String.valueOf(0), resp.getHeaders().getFirst("offset"));
        assertEquals("wrong limit", String.valueOf(20), resp.getHeaders().getFirst("limit"));
        String expectedTotal = String.valueOf(this.getTestDataSize() + 20);
        assertEquals("wrong total", expectedTotal, resp.getHeaders().getFirst("total"));
    }

    /**
     * Tests a simple filter.
     */
    @Test
    public void testGetFiltering() {
        // post must fail with invalid body, expect a 400 for bad request
        Response resp = target(this.getSubPath() + "/filter").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());

        // testing a simple equals filter. Only 1 element should be found.
        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        FilterAttribute filterAtt = this.getFilter();
        filterAtts.add(filterAtt);
        final int pageSize = 1;
        final int offset = 0;
        FilteredRequest reqEntity = new FilteredRequest();
        reqEntity.setOrMode(false);
        reqEntity.setFilterAttributes(filterAtts);
        reqEntity.setOffset(offset);
        reqEntity.setLimit(pageSize);

        resp = target(this.getSubPath() + "/filter").request(MediaType.APPLICATION_JSON).post(Entity.entity(reqEntity, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<T> models = resp.readEntity(new GenericType<List<T>>() {
        });
        assertThat(models, hasSize(pageSize));
        assertEquals("wrong offset", String.valueOf(offset), resp.getHeaders().getFirst("offset"));
        assertEquals("wrong limit", String.valueOf(pageSize), resp.getHeaders().getFirst("limit"));
        String expectedTotal = "1";
        assertEquals("wrong total", expectedTotal, resp.getHeaders().getFirst("total"));

        // if filter from concrete test class is EQ (equals), tests also NE (not equals) based on it
        // 2 elements should be found, but only 1 is returned because of pageSize = 1
        if (filterAtt.getOperator().equals(FilterOperator.EQ)) {
            filterAtts.clear();
            filterAtts.add(new FilterAttribute(filterAtt.getFieldName(), FilterOperator.NE, filterAtt.getValue()));
            reqEntity.setFilterAttributes(filterAtts);

            resp = target(this.getSubPath() + "/filter").request(MediaType.APPLICATION_JSON).post(Entity.entity(reqEntity, MediaType.APPLICATION_JSON_TYPE));
            assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
            models = resp.readEntity(new GenericType<List<T>>() {
            });
            assertThat(models, hasSize(pageSize));
            assertEquals("wrong offset", String.valueOf(offset), resp.getHeaders().getFirst("offset"));
            assertEquals("wrong limit", String.valueOf(pageSize), resp.getHeaders().getFirst("limit"));
            assertEquals("wrong total", String.valueOf(getTestDataSize() - 1), resp.getHeaders().getFirst("total"));
        }
    }

    public abstract T getNewObject();
    public abstract T getTestObject();

    public abstract Class<T> getObjectClass();

    /**
     * Test creating a new object.
     */
    @Test
    public void testCreate() {
        Entity<T> testEntity = Entity.entity(this.getNewObject(), MediaType.APPLICATION_JSON_TYPE);
        Response resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).post(testEntity);
        assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
        // post requests must return the newly created resource with the id set
        T created = resp.readEntity(this.getObjectClass());
        assertEquals(this.getTestDataSize() + 1, created.getId().intValue());
        // post must fail with invalid body
        resp = target(this.getSubPath()).request(MediaType.APPLICATION_JSON).post(null);
        // expect a 400 for bad request
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    }

    /**
     * Test deletion of objects. Response of 204 indicates success, 404 when nothing found that could be deleted.
     */
    @Test
    public void testDelete() {
        Response resp = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());
        // resource deleted, so same request should return 404
        resp = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    /**
     * Test put one. Response of 204 indicates success, 404 when nothing found that could be updated.
     */
    @Test
    public void testUpdateOne() {
        Entity<T> testEntity = Entity.entity(this.getTestObject(), MediaType.APPLICATION_JSON_TYPE);
        T testValue = this.getTestObject();
        testValue.setId(Long.valueOf(this.getTestDataSize()));

        // updates the last element
        Response resp = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).put(testEntity);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());
        Response a = target(this.getSubPath() + "/" + this.getTestDataSize()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), a.getStatus());
        T updated = a.readEntity(this.getObjectClass());

        assertTrue(testValue.equals(updated));
        // resource doesnt exist, so put request should return 404
        resp = target(this.getSubPath() + "/" + (this.getTestDataSize() + 1)).request(MediaType.APPLICATION_JSON).put(testEntity);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }
}
