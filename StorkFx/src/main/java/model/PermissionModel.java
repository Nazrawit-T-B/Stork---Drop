package model;

public class PermissionModel {
    private Long fileId;
    private Long userId;
    private String username;
    private PermissionType permission;

    public PermissionModel(Long fileId, Long userId, String username, PermissionType permission) {
        this.fileId = fileId;
        this.userId = userId;
        this.username = username;
        this.permission = permission;
    }

    public Long getFileId() {
        return fileId;
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

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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
