package com.minStork.Stork.controller;

import com.minStork.Stork.data.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileStorageService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final FileRepository fileRepository;
    private final FileVersionRepository fileVersionRepository;

    public FileStorageService(UserRepository userRepository, FileRepository fileRepository, 
                              PermissionRepository permissionRepository, FileVersionRepository fileVersionRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.permissionRepository = permissionRepository;
        this.fileVersionRepository = fileVersionRepository;
    }

    private static final String STORAGE_DIR = "Stork/storage";

    public FileEntity saveFile(MultipartFile incomingfile, UserEntity user, boolean isPublic) throws IOException {
        if (incomingfile == null || incomingfile.isEmpty()) {
            throw new NullPointerException("No file to save!");
        }
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String originalFilename = Paths.get(incomingfile.getOriginalFilename()).getFileName().toString();
        String nameWithoutExt = originalFilename.contains(".") ? originalFilename.substring(0, originalFilename.lastIndexOf(".")) : originalFilename;
        String ext = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        FileEntity existing = fileRepository.findByFilenameAndOwner(originalFilename, user).orElse(null);

        if (existing != null) {
            int newVersion = fileVersionRepository
                    .findFirstByFileOrderByVersionNumberDesc(existing)
                    .map(v -> v.getVersionNumber() + 1)
                    .orElse(2);
            String versionedFilename = nameWithoutExt + "_v" + newVersion + ext;
            File targetFile = new File(STORAGE_DIR + File.separator + versionedFilename);
            Files.copy(incomingfile.getInputStream(), targetFile.toPath());

            FileVersionEntity version = new FileVersionEntity();
            version.setFile(existing);
            version.setVersionNumber(newVersion);
            version.setStoragePath(targetFile.getAbsolutePath());
            version.setUploadedBy(user);
            fileVersionRepository.save(version);

            existing.setCurrentVersion(newVersion);
            existing.setLastModified(LocalDateTime.now());
            existing.setSize(incomingfile.getSize());
            existing.setIsPublic(isPublic); 
            
            return fileRepository.save(existing);
        } else {
            String versionedFilename = nameWithoutExt + "_v1" + ext;
            File targetFile = new File(STORAGE_DIR + File.separator + versionedFilename);
            Files.copy(incomingfile.getInputStream(), targetFile.toPath());

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(originalFilename); 
            fileEntity.setStoragePath(targetFile.getAbsolutePath());
            fileEntity.setSize(incomingfile.getSize());
            fileEntity.setCurrentVersion(1);
            fileEntity.setDeleted(false);
            fileEntity.setLastModified(LocalDateTime.now());
            fileEntity.setOwner(user);
            fileEntity.setIsPublic(isPublic); 
            
            FileEntity saved = fileRepository.save(fileEntity);

            FileVersionEntity version = new FileVersionEntity();
            version.setFile(saved);
            version.setVersionNumber(1);
            version.setStoragePath(targetFile.getAbsolutePath());
            version.setUploadedBy(user);
            fileVersionRepository.save(version);

            return saved;
        }
    }

    

    @Transactional
    public void deleteFile(FileEntity file) throws Exception {
        // 1. Fetch all version records associated with this asset
        List<FileVersionEntity> versions = fileVersionRepository.findByFileOrderByVersionNumberDesc(file);
        
        // 2. Erase physical versioned files from local system disk
        for (FileVersionEntity version : versions) {
            if (version.getStoragePath() != null) {
                Path versionPath = Paths.get(version.getStoragePath());
                Files.deleteIfExists(versionPath);
            }
        }
        
        // 3. Purge data rows in correct order to respect SQL constraints
        fileVersionRepository.deleteAll(versions);
        permissionRepository.deleteByFile(file);
        fileRepository.delete(file);
    }
    
  public File getDownloadFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }

        // 1. Find the master file entity record
        FileEntity fileEntity = fileRepository.findByFilename(filename).orElse(null);
        if (fileEntity == null) {
            // Fallback: Check if they are trying to access a direct physical filename string instead
            File directFile = new File(STORAGE_DIR + File.separator + filename);
            if (directFile.exists()) {
                return directFile;
            }
            return null;
        }

        // 2. Query the versions table to calculate and locate the latest version entries
        FileVersionEntity latestVersion = fileVersionRepository
                .findFirstByFileOrderByVersionNumberDesc(fileEntity)
                .orElse(null);

        if (latestVersion == null || latestVersion.getStoragePath() == null) {
            return null;
        }

        // 3. Return the evaluated physical disk location
        File fileToDownload = new File(latestVersion.getStoragePath());
        return fileToDownload.exists() ? fileToDownload : null;
    }

    
}
