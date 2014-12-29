package de.uni_stuttgart.riot.contacts;

import de.uni_stuttgart.riot.rest.ResourceModel;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarEntry.
 */
public class ContactEntry implements ResourceModel {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    /**
     * Creates a new ContactEntry.
     * 
     * @param id
     *            The ID of the entry.
     * @param firstName
     *            The contact's first name.
     * @param lastName
     *            The contact's last name.
     * @param phoneNumber
     *            The contact's phone number.
     */
    public ContactEntry(long id, String firstName, String lastName, String phoneNumber) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ResourceModel#getId()
     */
    @Override
    public long getId() {
        return this.id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ResourceModel#setId(int)
     */
    @Override
    public void setId(long id) {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
