package de.uni_stuttgart.riot.commons.rest.data;

import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class stores a filter attribute and contains the field name, the operator and the value.
 */
public class FilterAttribute {

    /**
     * Enum for supported Operators. "EQ" (equals), "NE" (not equals), "GT" (greater than), "GE" (greater than or equals), "LT" (lower
     * than), "LE" (lower than or equals)
     *
     */
    public enum FilterOperator {
        /**
         * "EQ" (equals).
         */
        EQ,
        /**
         * "NE" (not equals).
         */
        NE,
        /**
         * "LT" (lower than).
         */
        LT,
        /**
         * "LE" (lower than or equals).
         */
        LE,
        /**
         * "GT" (greater than).
         */
        GT,
        /**
         * "GE" (greater than or equals).
         */
        GE,
        /**
         * "CT" (contains).
         */
        CT
    }

    private String fieldName;
    private FilterOperator operator;
    private Object value;

    /**
     * Necessary Constructor for JSON serialization.
     */
    public FilterAttribute() {

    }

    /**
     * .
     * 
     * @param fieldName
     *            the field name
     * @param operator
     *            the SQL conform operator
     * @param value
     *            the value
     */
    public FilterAttribute(String fieldName, FilterOperator operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Parses the key to extract the operator and the field name, then applies the first value from the associated list. Multivalued entries
     * are not supported (only the first one will be used)
     * 
     * @param filter
     *            entry
     */
    public FilterAttribute(Entry<String, List<String>> filter) {
        Pattern p = Pattern.compile("^([\\w]+)_(\\w{2})$");
        Matcher m = p.matcher(filter.getKey());
        // check key and operator
        if (m.matches()) {
            this.fieldName = m.group(1);
            String op = m.group(2).toUpperCase();
            this.operator = FilterOperator.valueOf(op);
        } else {
            throw new IllegalArgumentException("Invalid key. Must be in the form 'field_OP' where OP is a comparison operator.");
        }
        // check value
        if (filter.getValue() != null && filter.getValue().size() > 0) {
            this.value = filter.getValue().get(0); // multivalue filters are not yet supported
        } else {
            throw new IllegalArgumentException("Filter expression has no value.");
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public void setOperator(FilterOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Executes this filter attribute.
     * 
     * @param actualValue
     *            The actual value of the attribute to match against.
     * @return True if this filter matches.
     */
    public boolean evaluate(Object actualValue) {
        if (actualValue == null) {
            if (value == null) {
                return operator == FilterOperator.EQ;
            } else {
                return false;
            }
        } else if (value == null) {
            return false;
        } else if (value.getClass() != actualValue.getClass()) {
            return false;
        } else if (!(actualValue instanceof Comparable<?>)) {
            throw new IllegalArgumentException(actualValue.getClass() + " does not implement Comparable!");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        int compared = ((Comparable) actualValue).compareTo(value);
        switch (operator) {
        case EQ:
            return compared == 0;
        case NE:
            return compared != 0;
        case GE:
            return compared >= 0;
        case GT:
            return compared > 0;
        case LE:
            return compared <= 0;
        case LT:
            return compared < 0;
        default:
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(fieldName);
        builder.append(" ");
        builder.append(operator);
        builder.append(" ");
        builder.append(value);
        return builder.toString();
    }
}
