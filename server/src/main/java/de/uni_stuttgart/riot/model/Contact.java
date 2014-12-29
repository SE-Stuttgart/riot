package de.uni_stuttgart.riot.model;

import java.util.UUID;

/**
 * A contact.
 * 
 * @author Ana
 *
 */
public class Contact {

    private String id; // unique ID
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    /**
     * Creates a new contact.
     * 
     * @param firstName
     *            The first name.
     * @param lastName
     *            The last name.
     * @param phoneNumber
     *            The phone number.
     */
    public Contact(String firstName, String lastName, String phoneNumber) {
        super();
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
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
