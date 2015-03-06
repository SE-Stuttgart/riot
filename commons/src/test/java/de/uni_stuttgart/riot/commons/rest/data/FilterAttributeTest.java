package de.uni_stuttgart.riot.commons.rest.data;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * The Class FilterAttributeTest.
 */
public class FilterAttributeTest {

    /** The valid queries. */
    private static Map<String, List<String>> validQueries = new HashMap<String, List<String>>();

    /** The invalid queries. */
    private static Map<String, List<String>> invalidQueries = new HashMap<String, List<String>>();

    /** The default value. */
    private static List<String> defaultValue = new ArrayList<String>();

    static {
        defaultValue.add("value");

        validQueries.put("field_EQ", defaultValue);
        validQueries.put("field_NE", defaultValue);
        validQueries.put("field_lt", defaultValue);
        validQueries.put("field_le", defaultValue);
        validQueries.put("field_GT", defaultValue);
        validQueries.put("fie_ld_GT", defaultValue); // db fields might have underscores

        invalidQueries.put("field_GT", null);
        invalidQueries.put("field_E", defaultValue);
        invalidQueries.put("field_nn", defaultValue);
        invalidQueries.put("fie'*ld_lt", defaultValue);
        invalidQueries.put("fi#eld_le", defaultValue);
    }

    /**
     * Test valid strings.
     */
    @Test
    public void testValidStrings() {
        for (Entry<String, List<String>> entry : validQueries.entrySet()) {
            FilterAttribute attr = new FilterAttribute(entry);
        }
        // all good when this point is reached.
        assertTrue(true);
    }

    /**
     * Test invalid strings.
     */
    @Test
    public void testInvalidStrings() {
        for (Entry<String, List<String>> entry : invalidQueries.entrySet()) {
            try {
                FilterAttribute attr = new FilterAttribute(entry);
                fail(String.format("An exception should have been raised for %s", entry.getKey()));
            } catch (IllegalArgumentException ex) {
                // ecpected
            }
            
        }
    }

}
