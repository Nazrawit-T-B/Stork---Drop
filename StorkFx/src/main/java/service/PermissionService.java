package service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.FileModel;
import model.PermissionModel;
import model.PermissionType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import ui.SessionManager;

public class PermissionService {

    public List<FileModel> getAvailableFiles() throws IOException {
        List<FileModel> files = new ArrayList<>();
        URL url=new URL("http://localhost:8080/api/permissions/owned");
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer "+ SessionManager.getActiveToken());
        try {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        files = mapper.readValue(inputStream, new TypeReference<List<FileModel>>(){});
                }
        } finally {
                connection.disconnect();
        }
        return files;
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