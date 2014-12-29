package de.uni_stuttgart.riot.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.db.DaoException;

/**
 * Use a mock implementation of the BaseResource to check if the behavior is as expected.
 *
 */
public class BaseResourceTest extends JerseyTest {

    /**
     * Mock implementation of a resource.
     */
    private static class TestModel implements ResourceModel {

        /** The id. */
        private long id;

        /** Some comment */
        private String comment;

        /**
         * Instantiates a new test model.
         */
        public TestModel() {
        }

        /**
         * Instantiates a new test model.
         *
         * @param i
         *            the i
         */
        public TestModel(int i) {
            id = i;
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ResourceModel#getId()
         */
        @Override
        public long getId() {
            return id;
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ResourceModel#setId(long)
         */
        @Override
        public void setId(long id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    /**
     * Mock implementation of a resource manager that doesn't need a DB.
     */
    private static class ModelManagerImpl implements ModelManager<TestModel> {

        /** The test data. */
        private static HashMap<Long, TestModel> testData = new HashMap<>();

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#getById(long)
         */
        @Override
        public TestModel getById(long id) {
            return testData.get(id);
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#get()
         */
        @Override
        public Collection<TestModel> get() {
            return testData.values();
        }

        @Override
        public Collection<TestModel> get(long offset, int limit) throws DaoException {
            // Dummy implementation just for testing REST API
            int testDataSize = testData.size();
            Collection<TestModel> values = new ArrayList<TestModel>();
            long i = offset;
            while (values.size() < limit) {
                TestModel model = testData.get(i);
                if (model != null) {
                    values.add(model);
                }
                if (i >= testDataSize) { // stopping loop if there's no more items to return
                    break;
                }
                i++;
            }

            return values;
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#create(de.uni_stuttgart.riot.rest.ResourceModel)
         */
        @Override
        public TestModel create(TestModel model) {
            if (model == null) {
                throw new IllegalArgumentException("The resource model must not be null");
            }
            model.setId(testData.size() + 1);
            testData.put(model.getId(), model);
            return model;
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#delete(long)
         */
        @Override
        public boolean delete(long id) {
            return testData.remove(id) != null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#update(de.uni_stuttgart.riot.rest.ResourceModel)
         */
        @Override
        public boolean update(TestModel model) {
            return testData.put(model.getId(), model) != null;
        }

    }

    /**
     * Implementation of the base class to be tested.
     */
    @Path("tests")
    private static class BaseResourceImpl extends BaseResource<TestModel> {

        /**
         * Instantiates a new base resource impl.
         */
        public BaseResourceImpl() {
            super(new ModelManagerImpl());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new ResourceConfig(BaseResourceImpl.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#getBaseUri()
     */
    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("api/v1/").build();
    }

    /**
     * Fills map with test data.
     */
    @Before
    public void resetTestData() {
        ModelManagerImpl.testData.clear();
        for (int i = 1; i <= 10; i++) {
            ModelManagerImpl.testData.put((long) i, new TestModel(i));
        }
    }

    /**
     * Test get one.
     */
    @Test
    public void testGetOne() {
        // expect a 404 for nonexisting resources
        Response a = target("tests/11").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), a.getStatus());
        // expect a 200 for existing ones
        a = target("tests/1").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), a.getStatus());
    }

    /**
     * Test get all.
     */
    @Test
    public void testGetAll() {
        Response resp = target("tests").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<TestModel> models = resp.readEntity(new GenericType<List<TestModel>>() {
        });
        assertEquals(ModelManagerImpl.testData.size(), models.size());
    }

    /**
     * Test get with server pagination.
     */
    @Test
    public void testGetPagination() {
        // negative offset: expects a 400 for bad request
        Response resp = target("tests").queryParam("offset", -1).queryParam("limit", 2).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());

        // negative limit: expect a 400 for bad request
        resp = target("tests").queryParam("offset", 1).queryParam("limit", -1).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());

        // normal behavior
        final int pageSize = 2;
        resp = target("tests").queryParam("offset", 2).queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<TestModel> models = resp.readEntity(new GenericType<List<TestModel>>() {
        });
        assertThat(models, hasSize(pageSize));

        // using only limit parameter: api/resource?limit=2
        resp = target("tests").queryParam("limit", pageSize).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        models = resp.readEntity(new GenericType<List<TestModel>>() {
        });
        assertThat(models, hasSize(pageSize));
    }

    /**
     * Test posting new objects.
     */
    @Test
    public void testPost() {
        TestModel m = new TestModel();
        Entity<TestModel> testEntity = Entity.entity(m, MediaType.APPLICATION_JSON_TYPE);
        Response resp = target("tests").request(MediaType.APPLICATION_JSON).post(testEntity);
        assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
        // post requests must return the newly created resource with the id set
        TestModel created = resp.readEntity(TestModel.class);
        assertEquals(ModelManagerImpl.testData.size(), created.getId());
        // post must fail with invalid body
        resp = target("tests").request(MediaType.APPLICATION_JSON).post(null);
        // expect a 400 for bad request
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    }

    /**
     * Test deletion of objects. Response of 204 indicates success, 404 when nothing found that could be deleted.
     */
    @Test
    public void testDelete() {
        Response resp = target("tests/9").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());
        // resource deleted, so same request should return 404
        resp = target("tests/9").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    /**
     * Test put one. Response of 204 indicates success, 404 when nothing found that could be updated.
     */
    @Test
    public void testPutOne() {

        TestModel m = new TestModel();
        // change some field at the object
        m.setComment("changed");

        Entity<TestModel> testEntity = Entity.entity(m, MediaType.APPLICATION_JSON_TYPE);
        Response resp = target("tests/1").request(MediaType.APPLICATION_JSON).put(testEntity);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());

        // retrieved object must have the "comment" field updated
        Response a = target("tests/1").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), a.getStatus());
        TestModel updated = a.readEntity(TestModel.class);
        assertEquals(m.getComment(), updated.getComment());

        // resource doesnt exist, so put request should return 404
        resp = target("tests/11").request(MediaType.APPLICATION_JSON).put(testEntity);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

}
