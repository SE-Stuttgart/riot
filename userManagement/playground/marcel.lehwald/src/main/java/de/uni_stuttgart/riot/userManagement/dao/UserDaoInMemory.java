package de.uni_stuttgart.riot.userManagement.dao;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.riot.userManagement.resource.User;

public class UserDaoInMemory implements UserDao {

	List<User> users = new ArrayList<User>();
	private int nextUserId = 0;

	public UserDaoInMemory() {
		//create some dummy users
		User user1 = new User();
		user1.setName("foo");
		user1.setEmail("foo@bar.com");
		insertUser(user1);

		User user2 = new User();
		user2.setName("bar");
		user2.setEmail("bar@foo.com");
		insertUser(user2);
	}

	public List<User> getUsers() {
		return users;
	}

	public User getUserById(Integer id) {
		for (User user : users)
		{
			if (user.getId() == id)
			{
				return user;
			}
		}

		throw new RuntimeException("User Not Found: " + id);
	}

	public void insertUser(User user) {
		user.setId(nextUserId++);
		users.add(user);
	}

	public void updateUser(User user) {
		User editUser = getUserById(user.getId());

		editUser.setBirthDate(user.getBirthDate());
		editUser.setCity(user.getCity());
		editUser.setEmail(user.getEmail());
		editUser.setName(user.getName());
		editUser.setState(user.getState());
	}

	public void deleteUser(User user) {
		User delUser = getUserById(user.getId());
		users.remove(delUser);
	}

}
