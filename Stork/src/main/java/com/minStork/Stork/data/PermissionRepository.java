package com.minStork.Stork.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    PermissionEntity findByUserAndFile(UserEntity user, FileEntity file);
    List<PermissionEntity> findByUser(UserEntity user);
}