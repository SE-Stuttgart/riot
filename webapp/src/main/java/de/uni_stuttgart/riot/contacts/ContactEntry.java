package de.uni_stuttgart.riot.contacts;

import java.util.UUID;

import de.uni_stuttgart.riot.rest.ResourceModel;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarEntry.
 */
public class ContactEntry implements ResourceModel {

    private String id; // unique ID
    private String firstName; 
    private String lastName;
    private String email;
    private String phoneNumber;
    
    public ContactEntry() {
        super();
    }
        
    public ContactEntry(String firstName, String lastName, String phoneNumber) {
        super();
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.rest.ResourceModel#getId()
     */
    @Override
    public int getId() {
        return this.id.hashCode();
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.rest.ResourceModel#setId(int)
     */
    @Override
    public void setId(int id) {
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
