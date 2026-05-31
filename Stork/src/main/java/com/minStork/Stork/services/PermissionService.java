package com.minStork.Stork.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minStork.Stork.data.FileEntity;
import com.minStork.Stork.data.PermissionEntity;
import com.minStork.Stork.data.PermissionRepository;
import com.minStork.Stork.data.PermissionType;
import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.data.UserRepository;

@Service
public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

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

    public void grantPermission(String username, FileEntity file, String permissionType) {
        PermissionEntity permission = new PermissionEntity();
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        permission.setUser(user);
        permission.setFile(file);
        permission.setPermissionType(PermissionType.valueOf(permissionType));
        permissionRepository.save(permission);
    }

    public void revokePermission(PermissionEntity permission) {
        permissionRepository.delete(permissionRepository.findByUserAndFile(permission.getUser(), permission.getFile()));
    }

    public void makeOwner(UserEntity user, FileEntity file) {
        PermissionEntity existing = permissionRepository.findByUserAndFile(user, file);
        if (existing != null) return;
        PermissionEntity permission = new PermissionEntity();
        permission.setPermissionType(PermissionType.OWNER);
        permission.setUser(user);
        permission.setFile(file);
        permissionRepository.save(permission);
    }
}
