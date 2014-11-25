package de.uni_stuttgart.riot.userManagement.dao;

import java.util.List;

import de.uni_stuttgart.riot.userManagement.resource.User;

public interface UserDao {
	
	public List<User> getUsers();
	public User getUserById(Integer id);
	public void insertUser(User user);
	public void updateUser(User user);
	public void deleteUser(User user);
	
}
