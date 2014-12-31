package de.uni_stuttgart.riot.usermanagement.service.rest.response;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;

/**
 * Wrapper around a {@link Permission}.<br>
 * FIXME These wrapper classes should not be necessary. Instead, use JAXB annotations to tell, which attributes are to be sent to the
 * client. CHECKSTYLE:OFF
 */
public class PermissionResponse {

    private Permission permission;

    public PermissionResponse() {

    }

    public PermissionResponse(Permission permission) {
        this.permission = permission;
    }

    public Long getId() {
        return permission.getId();
    }

    public void setId(Long id) {
        permission.setId(id);
    }

    public String getPermissionValue() {
        return permission.getPermissionValue();
    }

    public void setPermissionValue(String permissionValue) {
        permission.setPermissionValue(permissionValue);
    }

}
