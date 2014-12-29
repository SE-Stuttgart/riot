package de.uni_stuttgart.riot.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import de.uni_stuttgart.riot.calendar.CalendarEntry;
import de.uni_stuttgart.riot.calendar.CalendarModelManager;
import de.uni_stuttgart.riot.rest.ModelManager;
import de.uni_stuttgart.riot.rest.RiotApplication;

/**
 * Tests the operations executed at the persistence layer.
 *
 */
public class CalendarModelManagerTest extends JerseyDBTestBase {

    ModelManager<CalendarEntry> modelManager = new CalendarModelManager();

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }


    /**
     * help method to create test data in DB.
     * 
     * @param testDataSize
     * @return
     * @throws DaoException
     *             creation of testData not possible
     */
    private HashMap<Long, CalendarEntry> createTestData(final int testDataSize) throws DaoException {
        // inserting test data
        HashMap<Long, CalendarEntry> savedElements = new HashMap<Long, CalendarEntry>();
        for (int i = 0; i < testDataSize; i++) {
            CalendarEntry created = modelManager.create(new CalendarEntry(i, "Appointment " + i, "getAllTest"));
            savedElements.put(created.getId(), modelManager.getById(created.getId()));
        }
        return savedElements;
    }

    /**
     * Test create one entry.
     * 
     * @throws DaoException
     *             when creation not possible
     */
    @Test
    public void createEntryTest() throws DaoException {
        CalendarEntry model = new CalendarEntry(1, "Appointment 1", "createTest");

        // Creating entry
        CalendarEntry created = modelManager.create(model);

        assertNotNull("returned created obj cannot be null", created);
        assertNotNull("id from created obj cannot be null", created.getId());
        assertEquals(created, model); // create() returns the same reference

        // retrieving all entries: exactly one element shall exist
        Collection<CalendarEntry> retrievedElements = modelManager.get();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 1, retrievedElements.size());

        // the only element in collection must correspond to the received created obj
        assertEquals(created, retrievedElements.toArray()[0]);
    }

    /**
     * Test get one entry by id.
     * 
     * @throws DaoException
     *             when access not possible.
     */
    @Test
    public void getByIdTest() throws DaoException {
        CalendarEntry model = new CalendarEntry(1, "Appointment 1", "getByIdTest");

        // Creating entry
        CalendarEntry created = modelManager.create(model);
        Assert.assertNotNull("returned created obj cannot be null", created);

        // retrieving created entry: shall not return null
        CalendarEntry retrieved = modelManager.getById(created.getId());
        Assert.assertNotNull("returned obj is null", retrieved);

        // objects must have the same attribute values
        assertEquals(created, retrieved);

        // retrieving an element that does not exist: shall return null
        retrieved = modelManager.getById(11);
        Assert.assertNull("returned obj is not null", retrieved);
    }

    /**
     * Test get all.
     * 
     * @throws DaoException
     *             when access not possible.
     */
    @Test
    public void getAllTest() throws DaoException {

        // retrieving all entries: collection shall be empty
        Collection<CalendarEntry> retrievedElements = modelManager.get();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 0, retrievedElements.size());

        final int size = 5;
        HashMap<Long, CalendarEntry> savedElements = createTestData(size);

        // retrieving all entries from DB
        retrievedElements = modelManager.get();

        // size of returned collection must correspond to number of created elements
        assertNotNull("returned collection cannot be null", retrievedElements);
        assertEquals("returned collection size not same as created entries", size, retrievedElements.size());

        // all elements at returned collections must correspond to all previously created elements
        for (CalendarEntry retrievedElem : retrievedElements) {
            CalendarEntry expectedElem = savedElements.get(retrievedElem.getId());
            assertNotNull("returned element was not created", expectedElem);
            assertEquals(expectedElem, retrievedElem);
        }
    }

    /**
     * get entries using pagination.
     * 
     * @throws DaoException
     *             when access not possible.
     */
    @Test
    public void getPaginationTest() throws DaoException {
        final int pageSize = 2; // LIMIT
        final int testDataSize = 6; // size of test data

        // --- no test data at database: returned collection shall be empty
        Collection<CalendarEntry> retrievedElements = modelManager.get(0, pageSize);
        assertThat(retrievedElements, hasSize(0));

        // creating test data
        HashMap<Long, CalendarEntry> savedElements = createTestData(testDataSize);

        // --- retrieving first page
        retrievedElements = modelManager.get(0, pageSize); // OFFSET=0, LIMIT = 2
        assertThat(retrievedElements, hasSize(pageSize)); // shall return 2 items
        CalendarEntry elem1 = (CalendarEntry) retrievedElements.toArray()[0];
        CalendarEntry elem2 = (CalendarEntry) retrievedElements.toArray()[1];

        // returned elements shall be (id=1, id=2)
        assertThat(Arrays.asList(elem1.getId(), elem2.getId()), containsInAnyOrder((long) 1, (long) 2));
        assertThat(savedElements.get(elem1.getId()), equalTo(elem1)); // elements still the same as created
        assertThat(savedElements.get(elem2.getId()), equalTo(elem2));

        // --- OFFSET bigger than number of existing elements: returns empty collection
        retrievedElements = modelManager.get(testDataSize + 1, pageSize); // OFFSET=7, LIMIT = 2
        assertThat(retrievedElements, hasSize(0));

        // using the number of items as offset means skipping all items, zero items should be returned
        retrievedElements = modelManager.get(testDataSize, pageSize); // OFFSET=6, LIMIT = 2
        assertThat(retrievedElements, hasSize(0));

    }

    /**
     * test getting entries using pagination after some entry is deleted.
     * 
     * @throws DaoException
     *             access not possible
     */
    @Test
    public void getPaginationAfterDeletionTest() throws DaoException {
        final int pageSize = 2; // LIMIT
        // creating test data
        HashMap<Long, CalendarEntry> savedElements = createTestData(5);

        // skip 3 entries, get max 2 remaining
        modelManager.delete(3);
        Collection<CalendarEntry> retrievedElements = modelManager.get(3, pageSize); // OFFSET=3, LIMIT = 2
        assertThat("(should return 1 as only 4 items available", retrievedElements, hasSize(1)); // shall still return 2 items
        CalendarEntry elem1 = (CalendarEntry) retrievedElements.toArray()[0];

        assertThat(savedElements.get(elem1.getId()), equalTo(elem1)); // elements still the same as created

        // using an offset of 2 should return 2 items
        retrievedElements = modelManager.get(2, pageSize); // OFFSET=2, LIMIT = 2
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
     * @throws DaoException
     *             access not possible
     */
    @Test
    public void getPaginationFailedTest() throws DaoException {
        // negative offset: throws exception (negative offset and limit values are however already handled at the REST layer)
        try {
            modelManager.get(-1, 2); // OFFSET=-1, LIMIT = 2
            fail("Expected an DaoException to be thrown");
        } catch (DaoException exception) {
            assertThat(exception.getMessage(), equalTo(CalendarModelManager.INVALID_PAR_EXC));
        }

        // negative limit: throws exception
        try {
            modelManager.get(1, -1); // OFFSET=1, LIMIT = -1
            fail("Expected an DaoException to be thrown");
        } catch (DaoException exception) {
            assertThat(exception.getMessage(), equalTo(CalendarModelManager.INVALID_PAR_EXC));
        }
    }

    /**
     * Test delete one entry.
     * 
     * @throws DaoException
     *             when deletion not possible
     */
    @Test
    public void deleteTest() throws DaoException {
        CalendarEntry model = new CalendarEntry(1, "Appointment 1", "deleteTest");
        CalendarEntry created = null;

        // Creating entry
        created = modelManager.create(model);
        Assert.assertNotNull("returned created obj cannot be null", created);

        // deleting created entry
        boolean deleted = modelManager.delete(created.getId());
        assertTrue("row was not found to be deleted", deleted);

        // trying to delete again created entry, shall return false
        deleted = modelManager.delete(created.getId());
        assertFalse("it should found no row to be deleted", deleted);

        // retrieving object, shall return null since we delete it
        CalendarEntry retrieved = modelManager.getById(created.getId());
        assertNull("returned obj not null", retrieved);

        // retrieving all entries: no element shall exist
        Collection<CalendarEntry> retrievedElements = modelManager.get();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 0, retrievedElements.size());
    }

    /**
     * Test update one entry given the id.
     * 
     * @throws DaoException
     *             when update not possible
     */
    @Test
    public void updateTest() throws DaoException {
        String title1 = "Important Appointment";
        String title2 = "Urgent Appointment";
        CalendarEntry model = new CalendarEntry(1, title1, "updateTest");

        // Creating entry
        CalendarEntry created = modelManager.create(model);
        assertNotNull("returned created obj cannot be null", created);

        // retrieving created entry
        CalendarEntry retrieved = modelManager.getById(created.getId());
        assertNotNull("returned obj cannot be null", retrieved);
        assertEquals(created, retrieved);

        // changing and updating entry
        retrieved.setTitle(title2);
        boolean updated = modelManager.update(retrieved);
        assertTrue("row was not found to be updated", updated);

        // retrieving updated entry
        CalendarEntry retrievedAfterUpdate = modelManager.getById(retrieved.getId());
        assertNotNull("returned obj cannot be null", retrievedAfterUpdate);
        assertEquals("attribute was not updated", retrieved, retrievedAfterUpdate);

        // retrieving all entries: exactly one element shall exist
        Collection<CalendarEntry> retrievedElements = modelManager.get();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 1, retrievedElements.size());
        assertEquals(retrieved, retrievedElements.toArray()[0]);

        // updating an element that does not exist: shall return false
        created.setId(11);
        updated = modelManager.update(created);
        assertFalse("row was found to be updated", updated);
    }
}
