package de.uni_stuttgart.riot.commons.rest.data;

import java.util.List;

/**
 * This class specifies a Request using filtering and pagination.
 * <p>
 * It contains a collection of {@link FilterAttribute}. Each filter attribute has a field name, a operator and a value.
 * <p>
 * 
 * Pagination can be set by setting <code>offset</code> and <code>limit</code>.
 */
public class FilteredRequest {

    private boolean orMode;
    private List<FilterAttribute> filterAttributes;
    private int offset = 0;
    private int limit = 0;

    public boolean isOrMode() {
        return orMode;
    }

    public void setOrMode(boolean orMode) {
        this.orMode = orMode;
    }

    public List<FilterAttribute> getFilterAttributes() {
        return filterAttributes;
    }

    public void setFilterAttributes(List<FilterAttribute> filterAttributes) {
        this.filterAttributes = filterAttributes;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
