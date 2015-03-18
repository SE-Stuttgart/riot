package de.uni_stuttgart.riot.db.thing;

import java.util.HashSet;
import java.util.Set;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingShare;

/**
 * {@link ThingUser} represents the n:m multi-relation between {@link Thing} and {@link User} that essentially manages the permissions for
 * the thing. If a relation entry is present, this means that the user can access the given thing and has the specified permissions.
 * 
 * @author Philipp Keck
 */
public class ThingUser extends Storable {

    private Long thingID;
    private Long userID;
    private boolean canRead;
    private boolean canControl;
    private boolean canExecute;
    private boolean canDelete;
    private boolean canShare;

    /**
     * Instantiates a new ThingUser.
     */
    public ThingUser() {
        this(null, null, null, null);
    }

    /**
     * Instantiates a new ThingUser.
     *
     * @param thing
     *            the thing
     * @param user
     *            the user
     * @param permissions
     *            the permissions
     */
    public ThingUser(Thing thing, User user, Set<ThingPermission> permissions) {
        this(thing.getId(), user.getId(), permissions);
    }

    /**
     * Instantiates a new ThingUser.
     *
     * @param thingID
     *            the thing id
     * @param userID
     *            the user id
     * @param permissions
     *            the permissions
     */
    public ThingUser(Long thingID, Long userID, Set<ThingPermission> permissions) {
        this(thingID, userID, permissions, null);
    }

    /**
     * Instantiates a new role permission.
     *
     * @param thingID
     *            the thing id
     * @param userID
     *            the user id
     * @param permissions
     *            the permissions
     * @param thingUserID
     *            the storable id.
     */
    public ThingUser(Long thingID, Long userID, Set<ThingPermission> permissions, Long thingUserID) {
        super(thingUserID);
        this.thingID = thingID;
        this.userID = userID;
        if (permissions != null) {
            this.canRead = permissions.contains(ThingPermission.READ);
            this.canControl = permissions.contains(ThingPermission.CONTROL);
            this.canExecute = permissions.contains(ThingPermission.EXECUTE);
            this.canDelete = permissions.contains(ThingPermission.DELETE);
            this.canShare = permissions.contains(ThingPermission.SHARE);
        }
    }

    public Long getThingID() {
        return thingID;
    }

    public void setThingID(Long thingID) {
        this.thingID = thingID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanControl() {
        return canControl;
    }

    public void setCanControl(boolean canControl) {
        this.canControl = canControl;
    }

    public boolean isCanExecute() {
        return canExecute;
    }

    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanShare() {
        return canShare;
    }

    public void setCanShare(boolean canShare) {
        this.canShare = canShare;
    }

    /**
     * Constructs a ThingShare from this ThingUser.
     * 
     * @return The ThingShare.
     */
    public ThingShare toShare() {
        Set<ThingPermission> permissions = new HashSet<>();
        if (canRead) {
            permissions.add(ThingPermission.READ);
        }
        if (canControl) {
            permissions.add(ThingPermission.CONTROL);
        }
        if (canExecute) {
            permissions.add(ThingPermission.EXECUTE);
        }
        if (canDelete) {
            permissions.add(ThingPermission.DELETE);
        }
        if (canShare) {
            permissions.add(ThingPermission.SHARE);
        }
        return new ThingShare(userID == null ? 0 : userID, permissions);
    }

    // CHECKSTYLE:OFF
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (canControl ? 1231 : 1237);
        result = prime * result + (canDelete ? 1231 : 1237);
        result = prime * result + (canExecute ? 1231 : 1237);
        result = prime * result + (canRead ? 1231 : 1237);
        result = prime * result + (canShare ? 1231 : 1237);
        result = prime * result + ((thingID == null) ? 0 : thingID.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ThingUser other = (ThingUser) obj;
        if (canControl != other.canControl) {
            return false;
        }
        if (canDelete != other.canDelete) {
            return false;
        }
        if (canExecute != other.canExecute) {
            return false;
        }
        if (canRead != other.canRead) {
            return false;
        }
        if (canShare != other.canShare) {
            return false;
        }
        if (thingID == null) {
            if (other.thingID != null) {
                return false;
            }
        } else if (!thingID.equals(other.thingID)) {
            return false;
        }
        if (userID == null) {
            if (other.userID != null) {
                return false;
            }
        } else if (!userID.equals(other.userID)) {
            return false;
        }
        return true;
    }

}
