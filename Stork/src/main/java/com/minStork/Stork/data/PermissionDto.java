package com.minStork.Stork.data;

public class PermissionDto {
    private Long userId;
    private String username;
    private PermissionType permission;

    public PermissionDto() {

    }

    public PermissionDto(Long userId, String username, PermissionType permission) {
        this.userId = userId;
        this.username = username;
        this.permission = permission;
    }
    
    public Long getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public PermissionType getPermission() {
        return permission;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPermission(PermissionType permission) {
        this.permission = permission;
    }
}
