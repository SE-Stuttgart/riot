package datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import datasource.common.DaoTestBase;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.data.storable.User;

public class UserSqlQeryDaoTest extends DaoTestBase{

	@Test
	public void insertAndFindTest() {
		UserSqlQueryDao dao = new UserSqlQueryDao(this.ds);
		try {
			User testuser = new User(new Long(42), "TestUser", "TestUserPW", "TestUserSalt");
			dao.insert(testuser);
			User findUser = dao.findBy(new Long(42));
			System.out.println(findUser);
			assertEquals(testuser, findUser);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		UserSqlQueryDao dao = new UserSqlQueryDao(this.ds);
		try {
			User findUser = dao.findBy(new Long(1));
			System.out.println(findUser);
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
		UserSqlQueryDao dao = new UserSqlQueryDao(this.ds);
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
	    UserSqlQueryDao dao = new UserSqlQueryDao(this.ds);
        try {
            Collection<User> user = dao.findAll();
            assertEquals(3, user.size());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
	}
}
