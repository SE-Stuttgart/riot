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

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * Use a mock implementation of the BaseResource to check if the behavior is as expected.
 *
 */
public class BaseResourceTest extends JerseyTest {

    /**
     * Mock implementation of a resource.
     */
    private static class TestModel extends Storable {

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
        public TestModel(long i) {
            super(i);
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
    private static class ModelManagerImpl implements DAO<TestModel> {

        /** The test data. */
        private static HashMap<Long, TestModel> testData = new HashMap<>();

        public ModelManagerImpl() {

        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#getById(long)
         */
        @Override
        public TestModel findBy(long id) {
            return testData.get(id);
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#get()
         */
        @Override
        public Collection<TestModel> findAll() {
            return testData.values();
        }

        @Override
        public Collection<TestModel> findAll(int offset, int limit) throws DatasourceFindException {
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
        public void insert(TestModel model) throws DatasourceInsertException {
            if (model == null) {
                throw new IllegalArgumentException("The resource model must not be null");
            }
            model.setId((long) (testData.size() + 1));
            testData.put(model.getId(), model);
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#delete(long)
         */
        @Override
        public void delete(long id) throws DatasourceDeleteException {
            if (testData.remove(id) == null) {
                throw new DatasourceDeleteException("Object doesn't exist or already deleted");
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.uni_stuttgart.riot.rest.ModelManager#update(de.uni_stuttgart.riot.rest.ResourceModel)
         */
        @Override
        public void update(TestModel model) throws DatasourceUpdateException {
            if (testData.put(model.getId(), model) == null) {
                throw new DatasourceUpdateException("Object doesn't exist");
            }
        }

        @Override
        public void delete(TestModel t) throws DatasourceDeleteException {
            // FIXME inherited from UM. Functionality not implemented on server REST API

        }

        @Override
        public Collection<TestModel> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
            // FIXME inherited from UM. Functionality not implemented on server REST API
            return null;
        }

        @Override
        public TestModel findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException {
            // FIXME inherited from UM. Functionality not implemented on server REST API
            return null;
        }

        @Override
        public Collection<TestModel> findAll(FilteredRequest filter) throws DatasourceFindException {
            // Dummy implementation just for testing REST API
            Collection<TestModel> values = new ArrayList<TestModel>();
            for (FilterAttribute att : filter.getFilterAttributes()) {
                String value = (String) att.getValue();
                for (int i = 1; i < testData.size(); i++) {
                    if (value.equals(testData.get((long) i).getComment())) {
                        values.add(testData.get((long) i));
                    }
                }
                break;
            }

            return values;
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

    @Test
    public void testGetFiltering() {
        TestModel m = new TestModel();
        m.setComment("changed"); // change some field at the object
        Response resp = target("tests/1").request(MediaType.APPLICATION_JSON).put(Entity.entity(m, MediaType.APPLICATION_JSON_TYPE));

        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        filterAtts.add(new FilterAttribute("comment", FilterOperator.EQ, "changed"));
        FilteredRequest reqEntity = new FilteredRequest();
        reqEntity.setOrMode(false);
        reqEntity.setFilterAttributes(filterAtts);

        resp = target("tests/filter").request(MediaType.APPLICATION_JSON).post(Entity.entity(reqEntity, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Collection<TestModel> models = resp.readEntity(new GenericType<List<TestModel>>() {
        });
        assertThat(models, hasSize(1));

        // post must fail with invalid body
        resp = target("tests/filter").request(MediaType.APPLICATION_JSON).post(null);
        // expect a 400 for bad request
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
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
        assertEquals(ModelManagerImpl.testData.size(), created.getId().intValue());
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
