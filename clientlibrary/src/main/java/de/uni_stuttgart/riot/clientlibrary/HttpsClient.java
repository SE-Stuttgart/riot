package de.uni_stuttgart.riot.clientlibrary;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

/**
 * Uses the keystore.bks file to secure server connections with a pinned HTTPS certificate.
 * 
 * @author Philipp Keck
 */
public class HttpsClient extends DefaultHttpClient {

    private static final String KEYSTORE_TYPE = "BKS";
    private static final String KEYSTORE_FILE = "/keystore.bks";
    private static final String KEYSTORE_PASS = "belgrad";
    private static final String KEYSTORE_PROVIDER = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    private final int port;

    static {
        try {
            KeyStore.getInstance(KEYSTORE_TYPE);
        } catch (KeyStoreException e) {
            // The JVM does not provide a BKS keystore, so we include our own one.
            try {
                Class<?> keystoreProvider = Class.forName(KEYSTORE_PROVIDER);
                Security.addProvider((Provider) keystoreProvider.newInstance());
            } catch (ClassNotFoundException c) {
                throw new RuntimeException("You need to attach the bouncycastle JAR containing " + KEYSTORE_PROVIDER + " to the class path if you run this on non-Android platforms!");
            } catch (InstantiationException i) {
                throw new RuntimeException("Could not instantiate the bouncycastle provider " + KEYSTORE_PROVIDER + "!");
            } catch (IllegalAccessException e1) {
                throw new RuntimeException("Could not instantiate the bouncycastle provider " + KEYSTORE_PROVIDER + "!");
            }
        }
    }

    /**
     * Creates a new HttpsClient.
     * 
     * @param port
     *            The HTTPS port to be used.
     */
    public HttpsClient(int port) {
        this.port = port;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", newSslSocketFactory(), port));
        return new ThreadSafeClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance(KEYSTORE_TYPE);

            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = HttpsClient.class.getResourceAsStream(KEYSTORE_FILE);
            if (in == null) {
                throw new RuntimeException("Could not open the keystore file " + KEYSTORE_FILE);
            }

            try {
                // Initialize the keystore with the provided trusted
                // certificates
                // Also provide the password of the keystore
                trusted.load(in, KEYSTORE_PASS.toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is
            // responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);

            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}
