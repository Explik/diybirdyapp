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

        var contentType = getContentType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    private String getContentType(String filename) {
        var fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (fileExtension) {
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "webm" -> "audio/webm";
            case "ogg" -> "audio/ogg";
            case "mp4" -> "video/mp4";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }
}
