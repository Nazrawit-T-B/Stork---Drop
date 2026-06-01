package com.minStork.Stork.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minStork.Stork.data.FileEntity;
import com.minStork.Stork.data.FilePermissionInfoDto;
import com.minStork.Stork.data.FileRepository;
import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.services.PermissionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    private final FileRepository fileRepository;

    public PermissionController(PermissionService permissionService, FileRepository fileRepository) {
        this.permissionService = permissionService;
        this.fileRepository = fileRepository;
    }

    @GetMapping("/owned")
    public ResponseEntity<List<FilePermissionInfoDto>> getOwnedFiles(HttpServletRequest request) {
        UserEntity user=(UserEntity) request.getAttribute("authenticatedUser");
        if(user== null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = user.getUsername();
        List<FileEntity> files = fileRepository.findByOwnerId(user.getId());
        List<FilePermissionInfoDto> result = new ArrayList<>();
        for (FileEntity file : files) {
            result.add(new FilePermissionInfoDto(file.getId(), file.getFilename(), username));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/file/{id}")
    public List<PermissionEntity> getFilePermissions(@PathVariable Long id) {
        return permissionService.getFilePermissions(id);
    }

    @PostMapping("/grant")
    public ResponseEntity<?> grantPermission(@RequestBody PermissionEntity permission) {
        permissionService.grantPermission(permission);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePermission(@RequestBody PermissionEntity permission) {

        return ResponseEntity.ok().build();
    } 

    @DeleteMapping("/revoke")
    public ResponseEntity<?> revokePermission(@RequestBody PermissionEntity permission) {
        permissionService.revokePermission(permission);
        return ResponseEntity.ok().build();
    }
}