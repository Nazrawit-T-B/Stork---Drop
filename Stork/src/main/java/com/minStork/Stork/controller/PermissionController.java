package com.minStork.Stork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.services.PermissionService;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/grant")
    public ResponseEntity<?> grantPermission(@RequestBody PermissionEntity permission) {
        permissionService.grantPermission(permission);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokePermission(@RequestBody PermissionEntity permission) {
        permissionService.revokePermission(permission);
        return ResponseEntity.ok().build();
    }
}