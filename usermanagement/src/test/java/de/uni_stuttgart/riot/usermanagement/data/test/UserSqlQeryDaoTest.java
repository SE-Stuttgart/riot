package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class UserSqlQeryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceInsertException, DatasourceFindException {
        UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
        UMUser testuser = new UMUser(new Long(42), "TestUser", "TestUserPW", "TestUserSalt", 42);
        dao.insert(testuser);
        UMUser findUser = dao.findBy(testuser.getId());
        assertEquals(testuser, findUser);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException {
        UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
        UMUser findUser = dao.findBy(new Long(1));
        findUser.setUsername("TestUser2");
        dao.update(findUser);
        UMUser findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findUser);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException {
        UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
        UMUser User = dao.findBy(new Long(1));
        dao.delete(User);
        dao.findBy(new Long(1));
    }

    @Test
    public void findAllTest() throws DatasourceFindException {
        UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
        Collection<UMUser> user = dao.findAll();
        assertEquals(3, user.size());
    }

    @Test
    public void findUniqueTest() throws DatasourceFindException {
        UserSqlQueryDao dao = new UserSqlQueryDao(getDataSource());
        UMUser user = dao.findByUniqueField(new SearchParameter(SearchFields.USERNAME, "Yoda"));
        assertEquals(user.getHashedPassword(), "yPYMjqXzWOPKaAKNJXfEw7Gu3EnckZmoWUuEhOqz/7IqGd4Ub+3/X3uANlO0mkIOqIMhxhUi/ieU1KZt2BK+eg==");
    }
}
