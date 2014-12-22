package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.util.Collection;

/**
 * 
 * Interface for all classes that should be stored at some point.
 * @author Jonas Tangermann
 *
 */
public abstract class Storable {
	
	public Storable(Long id) {
		this.id = id;
	}

	public Storable() {
	}
	
    private Long id;
	
	/**
     * Returns the unique id of this object.
     * 
     * @return unique id
     */
    public Long getId() {
		return this.id;
	}
    
    /**
     * Setter for the unique id
     */
    public void setId(Long id) {
    	this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Storable other = (Storable) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
