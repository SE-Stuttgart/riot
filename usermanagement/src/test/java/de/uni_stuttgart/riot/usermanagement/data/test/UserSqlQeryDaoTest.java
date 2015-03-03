package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class UserSqlQeryDaoTest extends DaoTestBase {

    private UserSqlQueryDAO dao = new UserSqlQueryDAO();

    @Test
    public void insertAndFindTest() throws DatasourceInsertException, DatasourceFindException, SQLException {
        UMUser testuser = new UMUser(new Long(42), "TestUser", "email@test.org", "TestUserPW", "TestUserSalt", 42);
        dao.insert(testuser);
        UMUser findUser = dao.findBy(testuser.getId());
        assertEquals(testuser, findUser);
    }

    @Test
    public void findUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException, SQLException {
        UMUser findUser = dao.findBy(new Long(1));
        findUser.setUsername("TestUser2");
        dao.update(findUser);
        UMUser findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findUser);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException, SQLException {
        UMUser user = dao.findBy(new Long(1));
        dao.delete(user);
        dao.findBy(new Long(1));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException {
        Collection<UMUser> user = dao.findAll();
        assertEquals(3, user.size());
    }

    @Test
    public void findUniqueTest() throws DatasourceFindException, SQLException {
        UMUser user = dao.findByUniqueField(new SearchParameter(SearchFields.USERNAME, "Yoda"));
        assertEquals(user.getHashedPassword(), "yPYMjqXzWOPKaAKNJXfEw7Gu3EnckZmoWUuEhOqz/7IqGd4Ub+3/X3uANlO0mkIOqIMhxhUi/ieU1KZt2BK+eg==");
    }
}
