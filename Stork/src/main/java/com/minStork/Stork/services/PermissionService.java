package com.minStork.Stork.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minStork.Stork.data.FileEntity;
import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.data.PermissionRepository;
import com.minStork.Stork.data.PermissionType;
import com.minStork.Stork.data.UserEntity;

@Service
public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;

    public String getPermission(UserEntity user, FileEntity file) {
        return permissionRepository.findByUserAndFile(user, file).getPermissionType().name();
    }

    public List<PermissionEntity> getFilePermissions(Long fileId) {
        return permissionRepository.findByFileId(fileId);
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

    public void makeOwner(UserEntity user, FileEntity file) {
        PermissionEntity permission = new PermissionEntity();
        permission.setPermissionType(PermissionType.OWNER);
        permission.setUser(user);
        permission.setFile(file);
        permissionRepository.save(permission);
    }
}
