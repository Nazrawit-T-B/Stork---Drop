package com.minStork.Stork.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.data.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minStork.Stork.data.PermissionRepository;

@RestController
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private PermissionRepository permissionRepository;

    private static final Logger log=Logger.getLogger(FileController.class.getName());
    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file/*Authentication auth*/){
        try{
            /*
            if(auth ==null || !auth.isAuthenticated()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
            }*/
            fileStorageService.saveFile(file);
            return ResponseEntity.ok("Uploaded: "+file.getOriginalFilename());
            //add exsisting file logic for replace
        }catch(IOException e){
            log.log(Level.SEVERE,"Error during upload",e);
        }
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");

    }
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String filename, Authentication auth) {
        try{
            /*
            String userRole = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", "").toLowerCase())
                    .findFirst()
                    .orElse("");

            PermissionEntity permission=permissionRepository.findByUserAndFile(filename).orElse(null);
            if (permission == null || !permission.getAllowedRolesAsList().contains(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }*/

            var fileToDownload= fileStorageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ filename+"\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
            //throw new RuntimeException(e);
        }

    }


}
