package service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
                        System.out.println(files.size() + " file(s) received.");
                }
        } finally {
                connection.disconnect();
        }
        return files;
    }

    public List<PermissionModel> getPermissions(Long fileId) throws IOException {
        List<PermissionModel> permissions = new ArrayList<>();
        URL url=new URL("http://localhost:8080/api/permissions/file/" + fileId);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer "+ SessionManager.getActiveToken());
        try {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        ObjectMapper mapper = new ObjectMapper();
                        permissions = mapper.readValue(inputStream, new TypeReference<List<PermissionModel>>(){});
                        System.out.println(permissions.size() + " permissions received.");
                }
        } finally {
                connection.disconnect();
        }
        return permissions;
    }

    public void grantAccess(Long fileId, String username, PermissionType permission) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpClient httpClient = HttpClient.newHttpClient();
        try {
                PermissionModel permissionModel = new PermissionModel(fileId,  username, permission);
                String jsonPayload = objectMapper.writeValueAsString(permissionModel);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/permissions/grant"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + SessionManager.getActiveToken())
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public void updatePermission(Long fileId, String username, PermissionType permission) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpClient httpClient = HttpClient.newHttpClient();
        try {
                PermissionModel permissionModel = new PermissionModel(fileId,  username, permission);
                String jsonPayload = objectMapper.writeValueAsString(permissionModel);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/permissions/update"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + SessionManager.getActiveToken())
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public void revokeAccess(Long fileId, String username) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpClient httpClient = HttpClient.newHttpClient();
        try {
                PermissionModel permissionModel = new PermissionModel(fileId,  username, null);
                String jsonPayload = objectMapper.writeValueAsString(permissionModel);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/permissions/revoke"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + SessionManager.getActiveToken())
                        .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
}