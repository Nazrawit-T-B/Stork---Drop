package com.minStork.Stork.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minStork.Stork.data.*;
import com.minStork.Stork.services.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private FileVersionRepository fileVersionRepository;

    private static final Logger log = Logger.getLogger(FileController.class.getName());

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file, 
            @RequestParam("isPublic") Boolean isPublic, 
            HttpServletRequest request) {
        try {
            UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
            }

            FileEntity fileEntity = fileStorageService.saveFile(file, user, isPublic);
            permissionService.makeOwner(user, fileEntity);
            
            log.info("File uploaded by " + user.getUsername() + " [Public=" + isPublic + "]");
            return ResponseEntity.ok("Uploaded: " + file.getOriginalFilename());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error during upload", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
    }

    @GetMapping("/files/public-feed")
    public ResponseEntity<List<Map<String, Object>>> getPublicFeed(HttpServletRequest request) {
        UserEntity currentUser = (UserEntity) request.getAttribute("authenticatedUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FileEntity> publicFiles = fileRepository.findByIsPublicTrue();
        List<Map<String, Object>> feed = publicFiles.stream()
            .filter(file -> file.getOwner() != null && !file.getOwner().getId().equals(currentUser.getId()))
            .map(file -> {
                Map<String, Object> info = new HashMap<>();
                info.put("filename", file.getFilename());
                info.put("ownerName", file.getOwner().getUsername());
                info.put("size", file.getSize());
                info.put("lastModified", file.getLastModified().toString());
                return info;
            })
            .toList();

        return ResponseEntity.ok(feed);
    }

 @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String filename, HttpServletRequest request) {
        try {
            String sanitized = Paths.get(filename).getFileName().toString();
            if (!sanitized.equals(filename) || filename.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            FileEntity fileEntity = fileRepository.findByFilename(sanitized).orElse(null);
            
            // Fallback lookup if the parameter passed is an explicit version string
            if (fileEntity == null) {
                String strippedName = sanitized.replaceAll("_v\\d+", "");
                fileEntity = fileRepository.findByFilename(strippedName).orElse(null);
            }

            if (fileEntity == null) return ResponseEntity.notFound().build();

            // FIX: Check if the file is public. If it is NOT public, then enforce ownership checks.
            boolean isPublic = fileEntity.getIsPublic() != null && fileEntity.getIsPublic();
            if (!isPublic) {
                PermissionEntity permission = permissionRepository.findByUserAndFile(user, fileEntity);
                if (permission == null) {
                    log.warning("Access forbidden! User " + user.getUsername() + " lacks explicit read access.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            // Let the storage service calculate versions and resolve the target disk path safely
            File fileToDownload = fileStorageService.getDownloadFile(sanitized);
            if (fileToDownload == null || !fileToDownload.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sanitized + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/files")
    public ResponseEntity<List<Map<String,Object>>> getAllFiles(HttpServletRequest request){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PermissionEntity> permissions = permissionRepository.findByUser(user);
        List<Map<String,Object>> files = permissions.stream().map(p -> {
                    Map<String,Object> fileInfo = new HashMap<>();
                    fileInfo.put("filename", p.getFile().getFilename());
                    fileInfo.put("size", p.getFile().getSize());
                    fileInfo.put("lastModified", p.getFile().getLastModified().toString());
                    fileInfo.put("permission", p.getPermissionType().name());
                    return fileInfo;
                })
                .toList();
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String filename, HttpServletRequest request){
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FileEntity file = fileRepository.findByFilename(filename).orElse(null);
        if(file == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
        if(!permissionService.verifyOwnership(user,file)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to delete");
        }
        try{
            // FIX: Responsibility offloaded completely to the service layer to process cleanly
            fileStorageService.deleteFile(file);
            return ResponseEntity.ok("File deleted successfully");
        }catch(Exception e){
            log.log(Level.SEVERE, "Failed to completely purge file asset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file");
        }
    }

    @GetMapping("/files/versions")
    public ResponseEntity<List<Map<String, Object>>> getVersions(
            @RequestParam("fileName") String filename,
            HttpServletRequest request) {
        UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        FileEntity file = fileRepository.findByFilenameAndOwner(filename, user).orElse(null);
        if (file == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        List<FileVersionEntity> versions = fileVersionRepository.findByFileOrderByVersionNumberDesc(file);

        List<Map<String, Object>> response = versions.stream().map(v -> {
            Map<String, Object> info = new HashMap<>();
            info.put("filename", Paths.get(v.getStoragePath()).getFileName().toString());
            info.put("version", v.getVersionNumber());
            info.put("size", file.getSize());
            return info;
        }).toList();

        return ResponseEntity.ok(response);
    }
}
