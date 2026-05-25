package com.minStork.Stork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    private static final Logger log=Logger.getLogger(FileController.class.getName());
    @PostMapping("/file")
    public boolean uploadFile(@RequestParam("file") MultipartFile file){
        try{
            fileStorageService.saveFile(file);
            return true;
        }catch(IOException e){
            log.log(Level.SEVERE,"Error during upload",e);
        }
        return false;
    }
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String filename) {
        try{
            var fileToDownload= fileStorageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ filename+"\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
            //throw new RuntimeException(e);
        }

    }


}
