package com.minStork.Stork.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileVersionRepository extends JpaRepository<FileVersionEntity, Long> {
    List<FileVersionEntity> findByFileOrderByVersionNumberDesc(FileEntity file);
    Optional<FileVersionEntity> findFirstByFileOrderByVersionNumberDesc(FileEntity file);
}