package com.minStork.Stork.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByFilename(String filename);
    Optional<FileEntity> findByFilenameAndOwner(String filename, UserEntity owner);
}