package datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.memorydao.MemoryDAO;
import de.uni_stuttgart.riot.data.sqlquerydao.SearchFields;
import de.uni_stuttgart.riot.data.sqlquerydao.SearchParameter;
import de.uni_stuttgart.riot.data.storable.User;


public class MemoryDaoTest {
	
	@Test
	public void insertTest(){
		MemoryDAO<User> dao = new MemoryDAO<User>();
		try {
			User user = new User(new Long(42), "Yoda", "PW", "Salt");
			dao.insert(user);
			User u = dao.findBy(new Long(42));
			assertEquals(user, u);
			u.setUsername("Yoda2");
			dao.update(u);
			User u2 = dao.findBy(new Long(42));
			LinkedList<SearchParameter> param = new LinkedList<SearchParameter>();
			param.add(new SearchParameter(SearchFields.USERNAME, "Yoda2"));
			Collection<User> u3 = dao.findBy(param, false);
			User u3u = u3.iterator().next();
			assertEquals(u2, u3u);
			assertEquals("Yoda2", u2.getUsername());
			dao.delete(u2);
			Collection<User> allUser = dao.findAll();
			assertEquals(1, allUser.size());
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		} catch (DatasourceDeleteException e) {
			fail(e.getMessage());
		}
		try {
			dao.findBy(new Long(42));
		} catch (DatasourceFindException e) {
			return;
		}
		fail("Should not be reached because object with id 42 is deleted");
	}

}
