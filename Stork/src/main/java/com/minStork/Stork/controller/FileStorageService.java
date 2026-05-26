package com.minStork.Stork.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;


@Service
public class FileStorageService {
    private static final String STORAGE_DIR = "Stork/storage";
    public void saveFile (MultipartFile incomingfile) throws IOException {
        if (incomingfile == null|| incomingfile.isEmpty()) {
                throw new NullPointerException("No file to save!");
        }
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        var targetFile = new File(STORAGE_DIR + File.separator + incomingfile.getOriginalFilename());

        if (!Objects.equals(targetFile.getParent(), STORAGE_DIR)) {
                throw new SecurityException("Unsupported filename");
        }
        Files.copy(incomingfile.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    public File getDownloadFile(String filename) throws Exception {
        if(filename==null){
            throw new NullPointerException("Filename is null");
        }
        var fileToDownload=new File(STORAGE_DIR+File.separator+filename);

        if (!Objects.equals(fileToDownload.getParent(), STORAGE_DIR)) {
                throw new SecurityException("Unsupported filename");
        }
        if(!fileToDownload.exists()){
            throw new FileNotFoundException("File does not exist");
        }
        return fileToDownload;

    }

}
