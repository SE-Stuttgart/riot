package de.uni_stuttgart.riot.clientlibrary;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.test.ShiroEnabledTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;

/**
 * Tests the ServerConnector class.
 * 
 * @author Philipp Keck
 */
@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_configuration.sql", "/data/testdata_configuration.sql" })
public class ServerConnectorTest extends ShiroEnabledTest {

    ConnectionInformationProvider provider = mock(ConnectionInformationProvider.class);
    ServerConnector connector;

    @Before
    public void setup() {
        when(provider.getAccessToken()).thenReturn("token1");
        when(provider.getRefreshToken()).thenReturn("token1R");
        when(provider.getInformation()).thenReturn(new ConnectionInformation("http", "localhost", getPort(), "/api/v1/"));
        connector = new ServerConnector(provider);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundOnGET() throws IOException, RequestException, NotFoundException {
        connector.doGET("users/104", User.class);
    }

    @Test
    public void shouldRefreshTokenIfExpiredOrUnknown() throws DatasourceFindException, DatasourceUpdateException, IOException, RequestException, NotFoundException {
        // This test uses R2D2 instead of Yoda to be independent of other tests in this class.
        when(provider.getAccessToken()).thenReturn("token2");
        when(provider.getRefreshToken()).thenReturn("token2R");

        // Make the token expire artificially.
        DAO<Token> tokenDAO = new TokenSqlQueryDAO();
        Token token = tokenDAO.findBy(2);
        assertThat(token.getTokenValue(), is("token2"));
        assertThat(token.getRefreshtokenValue(), is("token2R"));
        token.setExpirationTime(new Timestamp(System.currentTimeMillis() - 5000));
        tokenDAO.update(token);

        // Let the mock provider store and restore the new token just like a normal one would.
        Mockito.doAnswer((invocation) -> {
            when(provider.getAccessToken()).thenReturn(invocation.getArgumentAt(0, String.class));
            return null;
        }).when(provider).setAccessToken(anyString());

        // Test if the token is refreshed and the request goes through without any further notice to the caller.
        User user = connector.doGET("users/self", User.class);
        assertThat(user.getUsername(), is("R2D2"));
        verify(provider).setAccessToken(anyString());

        // Get the new tokens.
        ArgumentCaptor<String> refreshTokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(provider).setRefreshToken(refreshTokenCaptor.capture());
        String refreshToken = refreshTokenCaptor.getValue();

        // Now we try an access token that certainly does not exist.
        Mockito.reset(provider);
        Mockito.doAnswer((invocation) -> {
            when(provider.getAccessToken()).thenReturn(invocation.getArgumentAt(0, String.class));
            return null;
        }).when(provider).setAccessToken(anyString());
        when(provider.getAccessToken()).thenReturn("ThisIsNotAnActualToken");
        when(provider.getRefreshToken()).thenReturn(refreshToken);
        user = connector.doGET("users/self", User.class);
        assertThat(user.getUsername(), is("R2D2"));
        verify(provider).setAccessToken(anyString());
        verify(provider).setRefreshToken(anyString());

    }

    @Test(expected = RequestException.class)
    public void shouldRefreshTokenOnlyOnce() throws IOException, RequestException, NotFoundException {
        // These tokens will fail forever. The connector must notice that and abort.
        when(provider.getAccessToken()).thenReturn("ThisDoesNotExist");
        when(provider.getRefreshToken()).thenReturn("ThisNeither");
        connector.doGET("users/self", User.class);
    }

    @Test
    public void shouldUpdateConnectionInfoOnNetworkFailure() throws IOException, RequestException, NotFoundException {
        ConnectionInformation oldInformation = new ConnectionInformation("http", "this.host.does.not.exist", getPort(), "/api/v1/");
        when(provider.getInformation()).thenReturn(oldInformation);
        connector = new ServerConnector(provider);

        ConnectionInformation newInformation = new ConnectionInformation("http", "localhost", getPort(), "/api/v1/");
        when(provider.getNewInformation(any())).thenReturn(newInformation);
        connector.doGET("users/self", User.class);
        verify(provider).getNewInformation(oldInformation);
    }

    @Test(expected = IOException.class)
    public void shouldUpdateConnectionInfoOnlyOnce() throws IOException, RequestException, NotFoundException {
        // This connection will fail forever.
        ConnectionInformation oldInformation = new ConnectionInformation("http", "this.host.does.not.exist", getPort(), "/api/v1/");
        when(provider.getInformation()).thenReturn(oldInformation);
        connector = new ServerConnector(provider);
        connector.doGET("users/self", User.class);
    }

}
