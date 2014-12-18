package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.memorydao.MemoryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

public class MemoryDaoTest {

    @Test
    public void insertTest() {
        MemoryDAO<UMUser> dao = new MemoryDAO<UMUser>();
        try {
            UMUser user = new UMUser(new Long(42), "Yoda", "PW", "Salt", 200000);
            dao.insert(user);
            UMUser u = dao.findBy(new Long(42));
            assertEquals(user, u);
            u.setUsername("Yoda2");
            dao.update(u);
            UMUser u2 = dao.findBy(new Long(42));
            LinkedList<SearchParameter> param = new LinkedList<SearchParameter>();
            param.add(new SearchParameter(SearchFields.USERNAME, "Yoda2"));
            Collection<UMUser> u3 = dao.findBy(param, false);
            UMUser u3u = u3.iterator().next();
            assertEquals(u2, u3u);
            assertEquals("Yoda2", u2.getUsername());
            Collection<UMUser> allUser = dao.findAll();
            assertEquals(1, allUser.size());
            dao.delete(u2);
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
