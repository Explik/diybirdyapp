package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.model.FileUploadResultDto;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
