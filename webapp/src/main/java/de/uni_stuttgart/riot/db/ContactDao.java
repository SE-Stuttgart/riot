package de.uni_stuttgart.riot.db;

import java.util.List;

import de.uni_stuttgart.riot.model.Contact;

public interface ContactDao {

  public List<Contact> getContacts();
 
  public Contact getContactById(Integer id);
  
  public void insertContact(Contact contact);
  
  public void updateContact(Contact contact);
  
  public void deleteContact(Contact contact);
}
