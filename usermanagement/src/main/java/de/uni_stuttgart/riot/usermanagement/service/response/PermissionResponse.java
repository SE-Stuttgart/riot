package de.uni_stuttgart.riot.usermanagement.service.response;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;

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
