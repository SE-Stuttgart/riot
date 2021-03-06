package de.uni_stuttgart.riot.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * Tests the operations executed at the persistence layer.
 *
 */
@TestData({ "/schema/schema_calendar.sql", "/data/testdata_calendar.sql" })
public class CalendarModelManagerTest extends BaseDatabaseTest {

    private DAO<CalendarEntry> modelManager;

    /**
     * help method to create test data in DB.
     * 
     * @param testDataSize
     * @return
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceInsertException
     * @throws DatasourceFindException
     */
    private HashMap<Long, CalendarEntry> createTestData(final int testDataSize) throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {

        // inserting test data
        HashMap<Long, CalendarEntry> savedElements = new HashMap<Long, CalendarEntry>();
        for (int i = 0; i < testDataSize; i++) {
            CalendarEntry create = new CalendarEntry(i, "Appointment " + i, "getAllTest");
            modelManager.insert(create);
            savedElements.put(create.getId(), modelManager.findBy(create.getId()));
        }
        return savedElements;
    }

    @Before
    public void initSUT() {
        modelManager = new CalendarSqlQueryDAO();
    }

    /**
     * Test create one entry.
     * 
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceInsertException
     * @throws DatasourceFindException
     * 
     */
    @Test
    public void createEntryTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException {

        CalendarEntry model = new CalendarEntry(1, "Appointment 1", "createTest");

        // Creating entry
        modelManager.insert(model);

        // retrieving all entries: exactly one element shall exist
        CalendarEntry retrievedElement = modelManager.findBy(model.getId());

        // the element must correspond to the received created obj
        assertEquals(model, retrievedElement);
    }

    /**
     * Test get one entry by id.
     * 
     * 
     * @throws DatasourceFindException
     * @throws DatasourceInsertException
     * @throws NamingException
     * @throws SQLException
     */
    @Test
    public void getByIdTest() throws DatasourceFindException, DatasourceInsertException, SQLException, NamingException {

        CalendarEntry model = new CalendarEntry(1, "Appointment 1", "getByIdTest");

        // Creating entry
        modelManager.insert(model);

        // retrieving created entry: shall not return null
        CalendarEntry retrieved = modelManager.findBy(model.getId());
        Assert.assertNotNull("returned obj is null", retrieved);

        // objects must have the same attribute values
        assertEquals(model, retrieved);

        // retrieving an element that does not exist: shall return null
        try {
            long notExistingId = 11;
            retrieved = modelManager.findBy(notExistingId);
            fail("Expected an DatasourceFindException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }
    }

    /**
     * Test get all.
     * 
     * 
     * @throws DatasourceFindException
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceInsertException
     */
    @Test
    public void getAllTest() throws DatasourceFindException, SQLException, NamingException, DatasourceInsertException {

        // retrieving all entries: collection shall contains 3 elements
        Collection<CalendarEntry> retrievedElements = modelManager.findAll();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 3, retrievedElements.size());

        final int size = 5;
        createTestData(size);

        // retrieving all entries from DB
        retrievedElements = modelManager.findAll();

        // size of returned collection must correspond to number of created elements + old items
        assertNotNull("returned collection cannot be null", retrievedElements);
        assertEquals("returned collection size not same as created entries", size + 3, retrievedElements.size());

    }

    /**
     * get entries using pagination.
     * 
     * @throws DatasourceFindException
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceInsertException
     */
    @Test
    public void getPaginationTest() throws DatasourceFindException, SQLException, NamingException, DatasourceInsertException {

        final int pageSize = 2; // LIMIT

        // --- retrieving first page
        Collection<CalendarEntry> retrievedElements = modelManager.findAll(0, pageSize); // OFFSET=0, LIMIT = 2
        assertThat(retrievedElements, hasSize(pageSize)); // shall return 2 items
        CalendarEntry elem1 = (CalendarEntry) retrievedElements.toArray()[0];
        CalendarEntry elem2 = (CalendarEntry) retrievedElements.toArray()[1];

        // --- retrieving second page
        retrievedElements = modelManager.findAll(2, pageSize); // OFFSET=2, LIMIT = 2
        assertThat(retrievedElements, hasSize(pageSize - 1)); // shall return 1 item since there is only 3 elements in db
        CalendarEntry elem3 = (CalendarEntry) retrievedElements.toArray()[0];
        Assert.assertNotEquals(elem3, elem1);
        Assert.assertNotEquals(elem3, elem2);

        // --- OFFSET bigger than number of existing elements: returns empty collection
        retrievedElements = modelManager.findAll(4, pageSize); // OFFSET=4, LIMIT = 2
        assertThat(retrievedElements, hasSize(0));

        // using the number of items as offset means skipping all items, zero items should be returned
        retrievedElements = modelManager.findAll(3, pageSize); // OFFSET=3, LIMIT = 2
        assertThat(retrievedElements, hasSize(0));

    }

    /**
     * test getting entries using pagination after some entry is deleted.
     * 
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceFindException
     * @throws DatasourceInsertException
     * @throws DatasourceDeleteException
     */
    @Test
    public void getPaginationAfterDeletionTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException, DatasourceDeleteException {

        final int pageSize = 2; // LIMIT
        // creating test data
        HashMap<Long, CalendarEntry> savedElements = createTestData(2);

        // skip 3 entries, get max 2 remaining
        modelManager.delete(3);
        Collection<CalendarEntry> retrievedElements = modelManager.findAll(3, pageSize); // OFFSET=3, LIMIT = 2
        assertThat("(should return 1 as only 4 items available", retrievedElements, hasSize(1)); // shall still return 2 items
        CalendarEntry elem1 = (CalendarEntry) retrievedElements.toArray()[0];

        assertThat(savedElements.get(elem1.getId()), equalTo(elem1)); // elements still the same as created

        // using an offset of 2 should return 2 items
        retrievedElements = modelManager.findAll(2, pageSize); // OFFSET=2, LIMIT = 2
        assertThat("using an offset of 2 should return 2 items", retrievedElements, hasSize(pageSize));
        elem1 = (CalendarEntry) retrievedElements.toArray()[0];
        CalendarEntry elem2 = (CalendarEntry) retrievedElements.toArray()[1];

        // returned elements shall be the last 2
        assertThat(Arrays.asList(elem1.getId(), elem2.getId()), containsInAnyOrder((long) 4, (long) 5));
        assertThat(savedElements.get(elem1.getId()), equalTo(elem1)); // elements still the same as created
        assertThat(savedElements.get(elem2.getId()), equalTo(elem2));
    }

    /**
     * test getting entries using pagination with invalid parameters.
     * 
     * @throws NamingException
     * @throws SQLException
     */
    @Test
    public void getPaginationFailedTest() throws SQLException, NamingException {

        // negative offset: throws exception (negative offset and limit values are however already handled at the REST layer)
        try {
            modelManager.findAll(-1, 2); // OFFSET=-1, LIMIT = 2
            fail("Expected an DatasourceFindException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // negative limit: throws exception
        try {
            modelManager.findAll(1, -1); // OFFSET=1, LIMIT = -1
            fail("Expected an DatasourceFindException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }
    }

    /**
     * get entries using filtering with AND / OR operators.
     * 
     * @throws DatasourceInsertException
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceFindException
     * 
     * @throws DaoException
     *             when access not possible.
     */
    @Test
    public void getFilterAndOrTest() throws DatasourceInsertException, SQLException, NamingException, DatasourceFindException {

        // creating test data
        CalendarEntry model1 = new CalendarEntry(1, "Important Appointment", "getFilterTest");
        model1.setAllDayEvent(true);
        CalendarEntry model2 = new CalendarEntry(2, "Not Important Appointment", "getFilterTest");
        modelManager.insert(model1);
        modelManager.insert(model2);

        // --- parameter of type String AND boolean, 'equals'
        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        filterAtts.add(new FilterAttribute("title", FilterOperator.EQ, "Important Appointment"));
        filterAtts.add(new FilterAttribute("allDayEvent", FilterOperator.EQ, true));
        FilteredRequest filter = new FilteredRequest();
        filter.setFilterAttributes(filterAtts);
        Collection<CalendarEntry> retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(1)); // shall return 1 item
        assertEquals(model1, (CalendarEntry) retrievedElements.toArray()[0]);

        // --- parameter of type String OR boolean, 'equals'
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("title", FilterOperator.EQ, "Standup Meeting 1"));
        filterAtts.add(new FilterAttribute("allDayEvent", FilterOperator.EQ, true));
        filter.setOrMode(true);
        filter.setFilterAttributes(filterAtts);
        retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(2)); // shall return 2 items
    }

    /**
     * get entries using filtering with EQ, NE, GT, GE, LT, LE operators.
     * 
     * @throws DatasourceInsertException
     * @throws DatasourceFindException
     * @throws NamingException
     * @throws SQLException
     * 
     * @throws DaoException
     *             when access not possible.
     */
    @Test
    public void getFilterTest() throws DatasourceInsertException, DatasourceFindException, SQLException, NamingException {

        Object[] elems = modelManager.findAll().toArray();

        // test data
        CalendarEntry model1 = (CalendarEntry) elems[0];
        CalendarEntry model2 = (CalendarEntry) elems[1];
        Date date1 = ((CalendarEntry) elems[0]).getStartTime();
        Date date2 = ((CalendarEntry) elems[1]).getStartTime();

        // --- parameter of type Date, 'equals'
        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        FilteredRequest filter = new FilteredRequest();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.EQ, date1));
        filter.setFilterAttributes(filterAtts);
        Collection<CalendarEntry> retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(1)); // shall return 1 item
        assertEquals(model1, (CalendarEntry) retrievedElements.toArray()[0]);

        // --- parameter of type Date, 'not equals'
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.NE, date1));
        filter.setFilterAttributes(filterAtts);
        retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(2)); // shall return 2 item
        assertEquals(model2, (CalendarEntry) retrievedElements.toArray()[0]);

        // --- parameter of type Date, 'greater than'
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.GT, date1));
        filter.setFilterAttributes(filterAtts);
        retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(2)); // shall return 2 item
        assertEquals(model2, (CalendarEntry) retrievedElements.toArray()[0]);

        // --- parameter of type Date, 'greater than or equal'
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.GE, date1));
        filter.setFilterAttributes(filterAtts);
        retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(3)); // shall return 3 items

        // --- parameter of type Date, 'lower than'
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.LT, date2));
        filter.setFilterAttributes(filterAtts);
        retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(1)); // shall return 1 item
        assertEquals(model1, (CalendarEntry) retrievedElements.toArray()[0]);

        // --- parameter of type Date, 'lower than or equal'
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.LE, date2));
        filter.setFilterAttributes(filterAtts);
        retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(2)); // shall return 2 items
    }

    /**
     * get entries using pagination.
     * 
     * @throws DatasourceFindException
     * @throws DatasourceInsertException
     * @throws NamingException
     * @throws SQLException
     * 
     * @throws DaoException
     *             when access not possible.
     */
    @Test
    public void getFilterPaginationTest() throws DatasourceFindException, DatasourceInsertException, SQLException, NamingException {

        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        filterAtts.add(new FilterAttribute("title", FilterOperator.EQ, "Important Appointment"));
        FilteredRequest filter = new FilteredRequest();
        filter.setFilterAttributes(filterAtts);
        filter.setOffset(0);
        filter.setLimit(1);

        // --- no test data at database: returned collection shall be empty
        Collection<CalendarEntry> retrievedElements = modelManager.findAll(filter);
        assertThat(retrievedElements, hasSize(0));

        // creating test data
        CalendarEntry model1 = new CalendarEntry(1, "Important Appointment", "getFilterPaginationTest");
        CalendarEntry model2 = new CalendarEntry(2, "Not Important Appointment", "getFilterPaginationTest");
        CalendarEntry model3 = new CalendarEntry(1, "Important Appointment", "getFilterPaginationTest");
        modelManager.insert(model1);
        modelManager.insert(model2);
        modelManager.insert(model3);

        // --- retrieving first page
        retrievedElements = modelManager.findAll(filter); // OFFSET=0, LIMIT = 1
        assertThat(retrievedElements, hasSize(1)); // shall return 1 item
        assertEquals(model1, (CalendarEntry) retrievedElements.toArray()[0]); // elements still the same as created

        // --- retrieving second page
        filter.setOffset(1);
        retrievedElements = modelManager.findAll(filter); // OFFSET=1, LIMIT = 1
        assertThat(retrievedElements, hasSize(1)); // shall return 1 item
        assertEquals(model3, (CalendarEntry) retrievedElements.toArray()[0]); // elements still the same as created

        // --- OFFSET bigger than number of existing elements: returns empty collection
        filter.setOffset(7);
        retrievedElements = modelManager.findAll(filter); // OFFSET=7, LIMIT = 1
        assertThat(retrievedElements, hasSize(0));
    }

    /**
     * test using filtering syntax wrong.
     * 
     * @throws NamingException
     * @throws SQLException
     * 
     */
    @Test
    public void getFilterFailedTest() throws SQLException, NamingException {

        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        filterAtts.add(new FilterAttribute("blabla", FilterOperator.EQ, "Important Appointment"));
        FilteredRequest filter = new FilteredRequest();
        filter.setFilterAttributes(filterAtts);

        // inexistent field
        try {
            modelManager.findAll(filter);
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // invalid operator
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("title", null, "Important Appointment"));
        filter.setFilterAttributes(filterAtts);
        try {
            modelManager.findAll(filter);
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // invalid value
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("title", FilterOperator.EQ, null));
        filter.setFilterAttributes(filterAtts);
        try {
            modelManager.findAll(filter);
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // wrong value for attribute type boolean
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("allDayEvent", FilterOperator.EQ, "bla"));
        filter.setFilterAttributes(filterAtts);
        try {
            modelManager.findAll(filter);
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // wrong value for attribute type Date
        filterAtts.clear();
        filterAtts.add(new FilterAttribute("startTime", FilterOperator.EQ, "bla"));
        filter.setFilterAttributes(filterAtts);
        try {
            modelManager.findAll(filter);
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }
    }

    /**
     * test getting entries using filtering and pagination with invalid parameters.
     * 
     * @throws NamingException
     * @throws SQLException
     * 
     * @throws DaoException
     *             access not possible
     */
    @Test
    public void getFilterPaginationFailedTest() throws SQLException, NamingException {

        List<FilterAttribute> filterAtts = new ArrayList<FilterAttribute>();
        filterAtts.add(new FilterAttribute("title", FilterOperator.EQ, "Important Appointment"));
        FilteredRequest filter = new FilteredRequest();
        filter.setFilterAttributes(filterAtts);
        filter.setOffset(-1);
        filter.setLimit(1);
        // negative offset: throws exception
        try {
            modelManager.findAll(filter); // OFFSET=-1, LIMIT = 1
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // negative limit: throws exception
        try {
            filter.setOffset(1);
            filter.setLimit(-1);
            modelManager.findAll(filter); // OFFSET=1, LIMIT = -1
            fail("Expected an DaoException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

    }

    /**
     * Test delete one entry.
     * 
     * @throws DatasourceFindException
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceInsertException
     * @throws DatasourceDeleteException
     */
    @Test
    public void deleteTest() throws DatasourceFindException, SQLException, NamingException, DatasourceInsertException, DatasourceDeleteException {

        CalendarEntry model = new CalendarEntry(1, "Appointment 1", "deleteTest");

        // Creating entry
        modelManager.insert(model);
        // deleting created entry
        modelManager.delete(model.getId());

        // trying to delete again
        try {
            modelManager.delete(model.getId());
            fail("Expected an DatasourceDeleteException to be thrown");
        } catch (DatasourceDeleteException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        try {
            // retrieving object, shall return null since we delete it
            modelManager.findBy(model.getId());
            fail("Expected an DatasourceFindException to be thrown");
        } catch (DatasourceFindException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }

        // retrieving all entries
        Collection<CalendarEntry> retrievedElements = modelManager.findAll();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 3, retrievedElements.size());
    }

    /**
     * Test update one entry given the id.
     * 
     * 
     * @throws NamingException
     * @throws SQLException
     * @throws DatasourceInsertException
     * @throws DatasourceFindException
     * @throws DatasourceUpdateException
     */
    @Test
    public void updateTest() throws SQLException, NamingException, DatasourceInsertException, DatasourceFindException, DatasourceUpdateException {

        String title1 = "Important Appointment";
        String title2 = "Urgent Appointment";
        CalendarEntry model = new CalendarEntry(1, title1, "updateTest");

        // Creating entry
        modelManager.insert(model);

        // retrieving created entry
        CalendarEntry retrieved = modelManager.findBy(model.getId());
        assertNotNull("returned obj cannot be null", retrieved);
        assertEquals(model, retrieved);

        // changing and updating entry
        retrieved.setTitle(title2);
        modelManager.update(retrieved);

        // retrieving updated entry
        CalendarEntry retrievedAfterUpdate = modelManager.findBy(retrieved.getId());
        assertNotNull("returned obj cannot be null", retrievedAfterUpdate);
        assertEquals("attribute was not updated", retrieved, retrievedAfterUpdate);

        try {
            // updating an element that does not exist: shall return false
            model.setId((long) 11);
            modelManager.update(model);
            fail("Expected an DatasourceUpdateException to be thrown");
        } catch (DatasourceUpdateException exception) {
            assertFalse("An error message should be provided", exception.getMessage().isEmpty());
        }
    }
}
