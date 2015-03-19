package de.uni_stuttgart.riot.thing.rest;

import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;

/**
 * A container for various kinds of information about a {@link Thing}. The idea is that not all fields will always be filled. The fields
 * depend on the values of the <tt>return</tt> query parameter. {@link #id} and {@link #type} will always be present.
 * 
 * @author Philipp
 */
@JsonInclude(Include.NON_NULL)
public class ThingInformation {

    /**
     * Possible values for the <tt>return</tt> query parameter, representing subfields of the ThingInformation to be returned.
     */
    public enum Field {

        /**
         * Corresponds to {@link ThingInformation#metainfo}.
         */
        METAINFO,

        /**
         * Corresponds to {@link ThingInformation#state}.
         */
        STATE,

        /**
         * Corresponds to {@link ThingInformation#description}.
         */
        DESCRIPTION,

        /**
         * Corresponds to {@link ThingInformation#children} and includes only direct children.
         */
        DIRECTCHILDREN,

        /**
         * Corresponds to {@link ThingInformation#children} and includes the entire tree of descendants.
         */
        ALLCHILDREN,

        /**
         * Corresponds to {@link ThingInformation#shares}.
         */
        SHARES,

        /**
         * Corresponds to {@link ThingInformation#lastConnection}.
         */
        LASTCONNECTION;

    }

    private String type;
    private Long id;
    private ThingMetainfo metainfo;
    private ThingState state;
    private ThingDescription description;
    private Collection<ThingInformation> children;
    private Collection<ThingShare> shares;
    private Date lastConnection;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ThingMetainfo getMetainfo() {
        return metainfo;
    }

    public void setMetainfo(ThingMetainfo metainfo) {
        this.metainfo = metainfo;
    }

    public ThingState getState() {
        return state;
    }

    public void setState(ThingState state) {
        this.state = state;
    }

    public ThingDescription getDescription() {
        return description;
    }

    public void setDescription(ThingDescription description) {
        this.description = description;
    }

    public Collection<ThingInformation> getChildren() {
        return children;
    }

    public void setChildren(Collection<ThingInformation> children) {
        this.children = children;
    }

    public Collection<ThingShare> getShares() {
        return shares;
    }

    public void setShares(Collection<ThingShare> shares) {
        this.shares = shares;
    }

    public Date getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Date lastConnection) {
        this.lastConnection = lastConnection;
    }

    /**
     * Applies all information contained in this object to the given thing.
     * 
     * @param thing
     *            The thing to write to.
     */
    public void apply(Thing thing) {
        if (thing == null) {
            throw new IllegalArgumentException("thing must not be null!");
        } else if (!thing.getClass().getName().equals(type)) {
            throw new IllegalArgumentException("thing type mismatch!");
        } else if (thing.getId() == null || !thing.getId().equals(id)) {
            throw new IllegalArgumentException("thing ID mismatch!");
        }

        if (metainfo != null) {
            metainfo.apply(thing);
        }
        if (state != null) {
            state.apply(thing);
        }
    }

}
