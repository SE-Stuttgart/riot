package de.uni_stuttgart.riot.commons.rest.data;

/**
 * 
 * Interface for all classes that should be stored at some point.
 * 
 * @author Jonas Tangermann
 *
 */
public abstract class Storable {

    private Long id;

    /**
     * Constructor.
     * 
     * @param id
     *            .
     */
    public Storable(Long id) {
        this.id = id;
    }

    /**
     * Constructor.
     */
    public Storable() {
    }

    /**
     * Returns the unique id of this object.
     * 
     * @return unique id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for the unique id.
     * 
     * @param id
     *            .
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Storable other = (Storable) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
