package datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Collection;

import org.junit.Test;

import datasource.common.DaoTestBase;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Token;

public class TokenSqlQueryDaoTest extends DaoTestBase{

	@Test
	public void insertAndFindTest() {
		TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.ds);
		try {
			Token testToken = new Token(new Long(42),
					new Long(1),
					"TestToken", 
					new Timestamp(System.currentTimeMillis()),
					new Timestamp(System.currentTimeMillis()+10000));
			dao.insert(testToken);
			Token findToken = dao.findBy(new Long(42));
			System.out.println(testToken);
			System.out.println(findToken);
			assertEquals(testToken, findToken);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.ds);
		try {
			Token findToken = dao.findBy(new Long(1));
			findToken.setTokenValue("testvalue");
			dao.update(findToken);
			Token findAfterUpdate = dao.findBy(new Long(1));
			assertEquals(findAfterUpdate, findToken);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest(){
		TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.ds);
		try {
			Token Token = dao.findBy(new Long(1));
			dao.delete(Token);
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
	public void errorUpdateTest(){
		TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.ds);
		try {
			dao.update(new Token(new Long(32), new Long(34), "", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis())));
		} catch (DatasourceUpdateException e) {
			return;
		}
		fail("Should not ne reached because there is no item with id 32");
	}
	
	   @Test
	    public void findAllTest(){
	       TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.ds);
	        try {
	            Collection<Token> Token = dao.findAll();
	            assertEquals(3, Token.size());
	        } catch (DatasourceFindException e) {
	            fail(e.getMessage());
	        }
	    }
}
