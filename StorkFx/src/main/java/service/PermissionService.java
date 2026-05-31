package service;

import java.util.List;

import model.PermissionModel;
import model.PermissionType;

public class PermissionService {

    public List<PermissionModel> getPermissions(Long fileId) {

        // TODO:
        // Send request to server

        return List.of(
                new PermissionModel(1L, 1L, "bob", PermissionType.OWNER)
        );
    }

    public void grantAccess(
            Long fileId,
            String username,
            String permission
    ) {

        // TODO:
        // Send GrantPermissionRequest
    }

    public void updatePermission(
            Long fileId,
            String username,
            String permission
    ) {

        // TODO:
        // Send UpdatePermissionRequest
    }

    public void revokeAccess(
            Long fileId,
            String username
    ) {

        // TODO:
        // Send RevokePermissionRequest
    }
}