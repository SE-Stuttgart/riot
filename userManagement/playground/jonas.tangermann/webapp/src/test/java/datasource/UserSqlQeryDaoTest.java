package datasource;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
