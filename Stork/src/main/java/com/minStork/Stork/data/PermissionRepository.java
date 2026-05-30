package com.minStork.Stork.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    PermissionEntity findByUserAndFile(UserEntity user, FileEntity file);

    List<PermissionEntity> findByUser(UserEntity user);

    List<PermissionEntity> findByFile(FileEntity file);

    List<PermissionEntity> findByFileId(Long userId);
}