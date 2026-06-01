package com.minStork.Stork.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByFilename(String filename);
    List<FileEntity> findByOwnerId(Long ownerId);
    Optional<FileEntity> findByFilenameAndOwner(String filename, UserEntity owner);
    List<FileEntity> findByIsPublicTrue();
}
