package de.uni_stuttgart.riot.server.commons.rest;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection containing pagination information.
 *
 * @param <E>
 *            the element type
 */
public class PaginatedCollection<E> extends ArrayList<E> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    /** Total number of available objects in the db. */
    int total;

    /** The offset. */
    int offset;

    /** The limit. */
    int limit;

    /**
     * Constructor.
     */
    public PaginatedCollection() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param collection
     *            .
     */
    public PaginatedCollection(Collection<E> collection) {
        super(collection);
    }

    /**
     * Constructor.
     * 
     * @param collection
     *            .
     */
    public PaginatedCollection(Collection<E> collection, int offset, int limit) {
        super(collection);
        total = collection.size();
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * Gets the total.
     *
     * @return the total
     */
    public final int getTotal() {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total
     *            the total to set
     */
    public final void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the offset.
     *
     * @return the offset.
     */
    public final int getOffset() {
        return offset;
    }

    /**
     * Returns the offset.
     * 
     * @param offset
     *            the offset to set.
     */
    public final void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public final int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit
     *            the limit to set
     */
    public final void setLimit(int limit) {
        this.limit = limit;
    }

}
