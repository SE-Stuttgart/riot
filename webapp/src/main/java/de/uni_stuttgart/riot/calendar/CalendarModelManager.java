package de.uni_stuttgart.riot.calendar;

import java.util.Collection;
import java.util.HashMap;

import de.uni_stuttgart.riot.rest.ModelManager;

/**
 * The Class CalendarModelManager.
 */
public class CalendarModelManager implements ModelManager<CalendarEntry> {

    /** Some dummy entries to substitute the persistence layer. */
    private static HashMap<Integer, CalendarEntry> entries = new HashMap<>();

    static {
        int id = 1;
        entries.put(id, new CalendarEntry(id++, "important meeting", "Some body text"));
        entries.put(id, new CalendarEntry(id++, "entry2", "unicode support? äüößſðđŋ"));
        entries.put(id, new CalendarEntry(id++, "", "lorem ipsum dolor sit amet"));
        entries.put(id, new CalendarEntry(id++, "sprint planning", null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#getById(int)
     */
    @Override
    public CalendarEntry getById(int id) {
        return entries.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#get()
     */
    @Override
    public Collection<CalendarEntry> get() {
        return entries.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#create(de.uni_stuttgart.riot.rest.ResourceModel)
     */
    @Override
    public CalendarEntry create(CalendarEntry model) {
        Integer newId = entries.size() + 1;
        model.setId(newId);
        entries.put(newId, model);
        return model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#delete(int)
     */
    @Override
    public void delete(int id) {
        entries.remove(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#update(de.uni_stuttgart.riot.rest.ResourceModel)
     */
    @Override
    public CalendarEntry update(CalendarEntry model) {
        entries.put(model.getId(), model);
        return model;
    }

}
