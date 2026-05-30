package com.minStork.Stork.controller;

import com.minStork.Stork.data.FileEntity;
import com.minStork.Stork.data.FileRepository;
import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.data.UserRepository;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderThreadLocalAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Objects;


@Service
public class FileStorageService  {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    public FileStorageService(UserRepository userRepository,FileRepository fileRepository){
        this.userRepository=userRepository;
        this.fileRepository=fileRepository;
    }

    private static final String STORAGE_DIR = "Stork/storage";
    public FileEntity saveFile (MultipartFile incomingfile,UserEntity user) throws IOException {
        if (incomingfile == null|| incomingfile.isEmpty()) {
            throw new NullPointerException("No file to save!");
        }
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename=Paths.get(incomingfile.getOriginalFilename()).getFileName().toString();
        var targetFile = new File(STORAGE_DIR + File.separator + incomingfile.getOriginalFilename());

        Files.copy(incomingfile.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        //add file metadata into the database
        //metadata's to be saved

        FileEntity fileEntity=new FileEntity();
        fileEntity.setFilename(incomingfile.getOriginalFilename());
        fileEntity.setStoragePath(targetFile.getAbsolutePath());
        fileEntity.setSize(incomingfile.getSize());
        fileEntity.setCurrentVersion(1);
        fileEntity.setDeleted(false);
        fileEntity.setLastModified(LocalDateTime.now());
        fileEntity.setOwner(user);

        return fileRepository.save(fileEntity);
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


}
