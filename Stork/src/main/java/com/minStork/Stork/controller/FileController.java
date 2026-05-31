package com.minStork.Stork.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minStork.Stork.data.*;
import com.minStork.Stork.services.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping ("/api")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private PermissionService permissionService;

    private static final Logger log=Logger.getLogger(FileController.class.getName());
    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        try{
            UserEntity user=(UserEntity) request.getAttribute("authenticatedUser");
            if(user== null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
            }

            FileEntity fileEntity=fileStorageService.saveFile(file,user);
            permissionService.makeOwner(user,fileEntity);
            log.info("File uploaded by "+ user.getUsername()+":"+ file.getOriginalFilename());
            return ResponseEntity.ok("Uploaded: "+file.getOriginalFilename());
            //add exsisting file logic for replace
        }catch(IOException e){
            log.log(Level.SEVERE,"Error during upload",e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");

    }
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String filename ,HttpServletRequest request) {
        try{

            String sanitized = Paths.get(filename).getFileName().toString();
            if (!sanitized.equals(filename) || filename.contains("..")) {
                return ResponseEntity.badRequest().build();
            }


            UserEntity user = (UserEntity) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            FileEntity fileEntity = fileRepository.findByFilename(sanitized).orElse(null);
            if (fileEntity == null) {
                return ResponseEntity.notFound().build();
            }

            PermissionEntity permission = permissionRepository.findByUserAndFile(user, fileEntity);
            if (permission == null ) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            var fileToDownload= fileStorageService.getDownloadFile(filename);
            System.out.println("Requested file: "+filename);
            System.out.println("Resolved path: "+fileToDownload.getAbsolutePath());
            System.out.println("Exists? "+ fileToDownload.exists());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ filename+"\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            //throw new RuntimeException(e);
        }

    }
    @GetMapping("/files")
    public ResponseEntity<List<Map<String,Object>>> getAllFiles(HttpServletRequest request){
        UserEntity user=(UserEntity) request.getAttribute("authenticatedUser");
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PermissionEntity> permissions=permissionRepository.findByUser(user);
        List<Map<String,Object>> files=permissions.stream().map(p->{
                    Map<String,Object> fileInfo =new HashMap<>();
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
        UserEntity user=(UserEntity) request.getAttribute("authenticatedUser");
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
       FileEntity file=fileRepository.findByFilename(filename).orElse(null);
        if(file == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
        if(!permissionService.verifyOwnership(user,file)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to delete");

        }
        try{
            fileStorageService.deleteFile(file);
            return ResponseEntity.ok("File deleted successfully");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file");
        }
    }


}