package com.minStork.Stork.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.services.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
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