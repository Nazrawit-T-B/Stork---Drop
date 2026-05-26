package com.minStork.Stork.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.minStork.Stork.data.PermissionRepository;
import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.data.FileEntity;
import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.data.PermissionType;

public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;

    public String getPermission(UserEntity user, FileEntity file) {
        return permissionRepository.findByUserAndFile(user, file).getPermissionType().name();
    }

    public boolean verifyOwnership(UserEntity user, FileEntity file) {
        return permissionRepository.findByUserAndFile(user, file).getPermissionType() == PermissionType.OWNER;
    }

    public void grantPermission(PermissionEntity permission) {
        permissionRepository.save(permission);
    }

    public void revokePermission(PermissionEntity permission) {
        permissionRepository.delete(permissionRepository.findByUserAndFile(permission.getUser(), permission.getFile()));
    }
}
