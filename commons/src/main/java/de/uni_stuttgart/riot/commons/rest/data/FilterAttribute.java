package de.uni_stuttgart.riot.commons.rest.data;

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
        GE
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
}
