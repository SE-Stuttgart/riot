package de.uni_stuttgart.riot.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import de.uni_stuttgart.riot.calendar.CalendarEntry;
import de.uni_stuttgart.riot.calendar.CalendarModelManager;
import de.uni_stuttgart.riot.rest.ModelManager;
import de.uni_stuttgart.riot.rest.RiotApplication;

public class CalendarModelManagerTest extends JerseyDBTestBase {

    ModelManager<CalendarEntry> modelManager = new CalendarModelManager();

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }

    /**
     * Test create one entry.
     * 
     * @throws DaoException
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
     */
    @Test
    public void getAllTest() throws DaoException {

        // retrieving all entries: collection shall be empty
        Collection<CalendarEntry> retrievedElements = modelManager.get();
        assertNotNull("returned collection is null", retrievedElements);
        assertEquals("collection size not as expected", 0, retrievedElements.size());

        int size = 5;
        HashMap<Long, CalendarEntry> savedElements = new HashMap<Long, CalendarEntry>();

        for (int i = 0; i < size; i++) {
            CalendarEntry created = modelManager.create(new CalendarEntry(i, "Appointment " + i, "getAllTest"));
            savedElements.put(created.getId(), created);
        }

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
     * Test delete one entry.
     * 
     * @throws DaoException
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
