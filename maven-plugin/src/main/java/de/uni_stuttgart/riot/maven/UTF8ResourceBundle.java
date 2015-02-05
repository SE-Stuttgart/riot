package de.uni_stuttgart.riot.maven;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Loads .properties files with UTF-8 encoding.
 * 
 * @author Philipp Keck
 */
public class UTF8ResourceBundle extends ResourceBundle {

    private static final Control UTF8_CONTROL = new UTF8Control();

    /**
     * Loads the underlying .properties file with UTF-8 encoding.
     * 
     * @param baseBundle
     *            The name of the .properties file without the extension.
     * @param locale
     *            The locale to be loaded.
     */
    public UTF8ResourceBundle(String baseBundle, Locale locale) {
        setParent(ResourceBundle.getBundle(baseBundle, locale, UTF8_CONTROL));
    }

    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }

    @Override
    protected Object handleGetObject(String key) {
        return parent.getObject(key);
    }

    /**
     * Controls the bundle loading process.
     */
    protected static class UTF8Control extends Control {

        /**
         * {@inheritDoc}
         */
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            // The below code is copied from default Control#newBundle() implementation.
            // Only the PropertyResourceBundle line is changed to read the file as UTF-8.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
