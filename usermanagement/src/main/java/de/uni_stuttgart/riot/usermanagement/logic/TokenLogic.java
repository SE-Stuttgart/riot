package de.uni_stuttgart.riot.usermanagement.logic;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.logic.exception.token.GetTokenException;

public class TokenLogic {

	private DAO<Token> dao;

	/**
	 * Constructor.
	 */
	public TokenLogic() {
		try {
			dao = new TokenSqlQueryDAO(DatasourceUtil.getDataSource());
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public Token getToken(String token) throws GetTokenException {

		try {
			System.out.println(token);
			// search token by token value
			return dao.findByUniqueField(new SearchParameter(SearchFields.TOKENVALUE, token));
		} catch (Exception e) {
			throw new GetTokenException(e);
		}
	}

}
