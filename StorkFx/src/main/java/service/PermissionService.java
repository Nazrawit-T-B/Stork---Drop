package service;

import java.util.List;

import model.FileModel;
import model.PermissionModel;
import model.PermissionType;

public class PermissionService {

    public List<FileModel> getAvailableFiles() {

        // TODO
        // Request files from server

        return List.of(
                new FileModel(1L, "report.docx", "Alice"),
                new FileModel(2L, "budget.xlsx", "Bob"),
                new FileModel(3L, "notes.txt", "Charlie")
        );
    }

    public List<PermissionModel> getPermissions(
            Long fileId
    ) {

        // TODO
        // Request permissions from server

        return List.of(
                new PermissionModel(1L,1L,
                        "Alice",
                        PermissionType.OWNER
                ),
                new PermissionModel(1L,1L,
                        "Bob",
                        PermissionType.READ
                ),
                new PermissionModel(1L,1L,
                        "Charlie",
                        PermissionType.WRITE
                )
        );
    }

    public void grantAccess(
            Long fileId,
            String username,
            PermissionType permission
    ) {

        // TODO
        // Send GrantPermissionRequest
    }

    public void updatePermission(
            Long fileId,
            String username,
            PermissionType permission
    ) {

        // TODO
        // Send UpdatePermissionRequest
    }

    public void revokeAccess(
            Long fileId,
            String username
    ) {

        // TODO
        // Send RevokePermissionRequest
    }
}