package de.uni_stuttgart.riot.clientlibrary;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;

/**
 * Helper methods for creating HTTPS/SSL connection. Uses the keystore.bks file to secure server connections with a pinned HTTPS
 * certificate.
 * 
 * @author Philipp Keck
 */
public abstract class SSLHelper {

    private static final String KEYSTORE_TYPE = "BKS";
    private static final String KEYSTORE_FILE = "/keystore.bks";
    private static final String KEYSTORE_PASS = "belgrad";
    private static final String KEYSTORE_PROVIDER = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    /**
     * Cannot instantiate.
     */
    private SSLHelper() {
    }

    /**
     * Static constructor, ensures that the right bouncycastle provider is loaded, both on Android and other JVMs.
     */
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
     * Creates an SSL socket factory that allows all servers, if their certificate matches one of the trusted ones.
     * 
     * @return The socket factory.
     */
    public static SSLConnectionSocketFactory createSLLSocketFactory() {
        return new SSLConnectionSocketFactory(createSSLContext(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    /**
     * Creates an SSL context with the respective trusted keys.
     * 
     * @return The SSL context.
     */
    public static SSLContext createSSLContext() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(loadTrustedKeys());
            return builder.build();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Loads the trusted keys from a file.
     * 
     * @return All server certificates that should be trusted.
     */
    public static KeyStore loadTrustedKeys() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance(KEYSTORE_TYPE);

            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = SSLHelper.class.getResourceAsStream(KEYSTORE_FILE);
            if (in == null) {
                throw new RuntimeException("Could not open the keystore file " + KEYSTORE_FILE);
            }

            try {
                // Initialize the keystore with the provided trusted
                // certificates
                // Also provide the password of the keystore
                trusted.load(in, KEYSTORE_PASS.toCharArray());
                return trusted;
            } finally {
                in.close();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
