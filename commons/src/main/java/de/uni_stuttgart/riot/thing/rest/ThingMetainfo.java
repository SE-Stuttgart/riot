package de.uni_stuttgart.riot.thing.rest;

import de.uni_stuttgart.riot.thing.Thing;

/**
 * A container for the meta-info fields in {@link Thing} used for REST communications. Note that not all of these fields may be changed
 * during POST or PUT requests!
 * 
 * @author Philipp Keck
 */
public class ThingMetainfo {

    private Long parentId;
    private Long ownerId;
    private String name;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Writes the info to a thing.
     * 
     * @param thing
     *            The thing to write to.
     */
    public void apply(Thing thing) {
        thing.setName(name);
        thing.setOwnerId(ownerId);
        thing.setParentId(parentId);
    }

    /**
     * Creates a meta-info object from the given Thing.
     * 
     * @param thing
     *            The thing to read from.
     * @return The meta-info of the thing.
     */
    public static ThingMetainfo create(Thing thing) {
        ThingMetainfo result = new ThingMetainfo();
        result.setName(thing.getName());
        result.setOwnerId(thing.getOwnerId());
        result.setParentId(thing.getParentId());
        return result;
    }

}
