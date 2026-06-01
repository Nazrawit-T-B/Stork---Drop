package com.minStork.Stork.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minStork.Stork.data.FileEntity;
import com.minStork.Stork.data.FileRepository;
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

    @Autowired
    private FileRepository fileRepository;

    public String getPermission(UserEntity user, FileEntity file) {
        return permissionRepository.findByUserAndFile(user, file).getPermissionType().name();
    }

    public List<PermissionEntity> getFilePermissions(Long fileId) {
        return permissionRepository.findByFileId(fileId);
    }

    public boolean verifyOwnership(UserEntity user, FileEntity file) {
        return permissionRepository.findByUserAndFile(user, file).getPermissionType() == PermissionType.OWNER;
    }

    public void grantPermission(Long fileId, String username, PermissionType permissionType) {
        PermissionEntity permission = new PermissionEntity();
        permission.setFile(fileRepository.findById(fileId).orElse(null));
        permission.setUser(userRepository.findByUsername(username).orElse(null));
        permission.setPermissionType(permissionType);
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

    public void updatePermission(Long fileId, String username, PermissionType permissionType) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        FileEntity file = fileRepository.findById(fileId).orElse(null);
        PermissionEntity permission = permissionRepository.findByUserAndFile(user, file);
        permission.setPermissionType(permissionType);
        permissionRepository.save(permission);
    }

    public void revokePermission(Long fileId, String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        FileEntity file = fileRepository.findById(fileId).orElse(null);
        permissionRepository.delete(permissionRepository.findByUserAndFile(user, file));
    }

    public void makeOwner(UserEntity user, FileEntity file) {
        PermissionEntity permission = new PermissionEntity();
        permission.setPermissionType(PermissionType.OWNER);
        permission.setUser(user);
        permission.setFile(file);
        permissionRepository.save(permission);
    }
}
