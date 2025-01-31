package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.FileUploadResultDto;
import com.explik.diybirdyapp.persistence.service.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// TODO implement file name restrictions
@RestController
public class FileController {
    @Autowired
    BinaryStorageService storageService;

    @PostMapping("upload")
    public ResponseEntity<FileUploadResultDto> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            var fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            var newFileName = UUID.randomUUID() + "." + fileExtension;

            storageService.set(newFileName, file.getBytes());

            var result = new FileUploadResultDto();
            result.setUrl(newFileName);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @GetMapping("{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        var fileContent = storageService.get(filename);
        var resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
