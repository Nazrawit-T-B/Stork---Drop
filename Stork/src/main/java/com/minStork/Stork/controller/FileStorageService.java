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
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class FileStorageService  {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final FileRepository fileRepository;
    private final FileVersionRepository fileVersionRepository;
    public FileStorageService(UserRepository userRepository, FileRepository fileRepository, PermissionRepository permissionRepository, FileVersionRepository fileVersionRepository){
        this.userRepository=userRepository;
        this.fileRepository=fileRepository;
        this.permissionRepository=permissionRepository;
        this.fileVersionRepository = fileVersionRepository;
    }

    private static final String STORAGE_DIR = "Stork/storage";
public FileEntity saveFile (MultipartFile incomingfile, UserEntity user, boolean isPublic) throws IOException {
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
    public File getDownloadFile(String filename) throws Exception {
        if(filename==null){
            throw new NullPointerException("Filename is null");
        }
        var fileToDownload=new File(STORAGE_DIR+File.separator+filename);
        if(!fileToDownload.exists()){
            throw new FileNotFoundException("File does not exist");
        }
        return fileToDownload;

    }
    @Transactional
    public void deleteFile(FileEntity file) throws Exception{
        Path path=Paths.get(STORAGE_DIR).resolve(file.getFilename());
        Files.deleteIfExists(path);
        permissionRepository.deleteByFile(file);
       fileRepository.delete(file);
    }


}
