package de.uni_stuttgart.riot.server.commons.db;


/**
 * Search parameter, used in {@link DAO#findBy(java.util.Collection, boolean)}.
 * @author Jonas Tangermann
 *
 */
public class SearchParameter {

    private final SearchFields searchField;
    private final Object value;

    /**
     * Constructor.
     * @param searchField search attribute
     * @param value value
     */
    public SearchParameter(SearchFields searchField, Object value) {
        this.value = value;
        this.searchField = searchField;
    }

    /**
     * Getter for searchField.
     * @return the valueName
     */
    public String getValueName() {
        return searchField.toString();
    }

    /**
     * Getter for value.
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((searchField == null) ? 0 : searchField.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SearchParameter))
            return false;
        SearchParameter other = (SearchParameter) obj;
        if (searchField != other.searchField)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
