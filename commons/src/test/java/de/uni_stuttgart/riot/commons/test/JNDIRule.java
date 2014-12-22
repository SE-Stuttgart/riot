package de.uni_stuttgart.riot.commons.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.junit.rules.ExternalResource;

/**
 * Source: http://stackoverflow.com/questions/17083142/junit-testing-jndi-initialcontext-outside-the-application-server
 * 
 * Note that this implementation is not thread-safe anymore and might fail during integration-tests!
 */
public class JNDIRule extends ExternalResource {

    private static Context context;

    @Override
    protected void before() throws Throwable {
        super.before();
        context = mock(Context.class);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockInitialContextFactory.class.getName());
    }

    @Override
    protected void after() {
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        context = null;
        super.after();
    }

    /**
     * Registers a new JNDI bean, which will then be available through {@link InitialContext#lookup(String)}.
     * 
     * @param name
     *            The nane of the bean.
     * @param bean
     *            The bean.
     * @throws NamingException
     *             Will not occur.
     */
    public void registerJNDI(String name, Object bean) throws NamingException {
        when(context.lookup(name)).thenReturn(bean);
    }

    /**
     * Makes the mock context accessible through the {@link InitialContext} interface.
     */
    public static class MockInitialContextFactory implements InitialContextFactory {

        @Override
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            return context;
        }

    }

}
