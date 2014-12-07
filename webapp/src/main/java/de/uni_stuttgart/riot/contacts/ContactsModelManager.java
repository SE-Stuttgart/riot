package de.uni_stuttgart.riot.contacts;

import java.util.Collection;
import java.util.HashMap;

import de.uni_stuttgart.riot.rest.ModelManager;

/**
 * The Class ContactsModelManager.
 */
public class ContactsModelManager implements ModelManager<ContactEntry> {

    /** Some dummy entries to substitute the persistence layer. */
    private static HashMap<Integer, ContactEntry> entries = new HashMap<>();

    static {
    	ContactEntry c = new ContactEntry("Max", "Mustermann", "0123456");
    	entries.put(c.getId(), c);
    	c = new ContactEntry("Markus", "Mustermann", "0456789");
    	entries.put(c.getId(), c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#getById(int)
     */
    @Override
    public ContactEntry getById(int id) {
        return entries.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#get()
     */
    @Override
    public Collection<ContactEntry> get() {
        return entries.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#create(de.uni_stuttgart.riot.rest.ResourceModel)
     */
    @Override
    public ContactEntry create(ContactEntry model) {
        entries.put(model.getId(), model);
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
    public ContactEntry update(ContactEntry model) {
        entries.put(model.getId(), model);
        return model;
    }

}
