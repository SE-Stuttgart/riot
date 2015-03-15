package de.uni_stuttgart.riot.db;

/**
 * Utility methods for database access.
 * 
 * @author Philipp Keck
 */
public abstract class DBUtils {

    /**
     * Cannot instantiate.
     */
    private DBUtils() {
    }

    /**
     * Transforms a value to a String representation for storage in the database.
     * 
     * @param value
     *            The value to be transformed.
     * @return The string representation.
     */
    public static String valueToString(Object value) {
        return value == null ? null : value.toString();
    }

    /**
     * Calls the <tt>valueOf</tt> method of the given class <tt>valueType</tt> to convert the given <tt>value</tt>.
     * 
     * @param <T>
     *            The value type.
     * @param value
     *            The value.
     * @param valueType
     *            The value type.
     * @return The converted value.
     */
    public static <T> T stringToValue(String value, Class<T> valueType) {
        if (value == null) {
            return null;
        } else if (valueType == String.class) {
            return valueType.cast(value);
        }
        try {
            @SuppressWarnings("unchecked")
            T result = (T) valueType.getMethod("valueOf", String.class).invoke(null, value);
            return result;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The type " + valueType + " is not supported!");
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception when calling " + valueType + "#valueOf(" + value + ")");
        }
    }

}
