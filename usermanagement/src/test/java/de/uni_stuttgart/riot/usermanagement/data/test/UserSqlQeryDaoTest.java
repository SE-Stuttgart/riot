package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class UserSqlQeryDaoTest extends DaoTestBase{

	@Test
	public void insertAndFindTest() {
		UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
		try {
			User testuser = new User(new Long(42), "TestUser", "TestUserPW", "TestUserSalt");
			dao.insert(testuser);
			User findUser = dao.findBy(testuser.getId());
			assertEquals(testuser, findUser);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
		try {
			User findUser = dao.findBy(new Long(1));
			findUser.setUsername("TestUser2");
			dao.update(findUser);
			User findAfterUpdate = dao.findBy(new Long(1));
			assertEquals(findAfterUpdate, findUser);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest(){
		UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
		try {
			User User = dao.findBy(new Long(1));
			dao.delete(User);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceDeleteException e) {
			fail(e.getMessage());
		}
		try {
			dao.findBy(new Long(1));
		} catch (DatasourceFindException e) {
			return;
		}
		fail("Should not be reached because id 1 does not longer exist");
	}
	
	@Test
	public void findAllTest(){
	    UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
        try {
            Collection<User> user = dao.findAll();
            assertEquals(3, user.size());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
	}
	
	   @Test
	    public void findUniqueTest(){
	        UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
	        try {
	            User user = dao.findByUniqueField(new SearchParameter(SearchFields.USERNAME, "Yoda"));
	            assertEquals(user.getPassword(), "YodaPW");
	        } catch (DatasourceFindException e) {
	            fail(e.getMessage());
	        }
	    }
}
