package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.FileUploadResultDto;
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

@RestController
public class FileController {
    private final String uploadDir = "uploads/";

    @PostMapping("/file/upload")
    public ResponseEntity<FileUploadResultDto> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());

            // TODO make configurable
            var result = new FileUploadResultDto();
            result.setUrl("http://localhost:8080/file/" + filename);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @GetMapping("/file/")
    public ResponseEntity<List<String>> getAllFiles() {
        try {
            File folder = new File(uploadDir);
            if (!folder.exists() || !folder.isDirectory()) return ResponseEntity.ok(new ArrayList<>());
            String[] files = folder.list();
            return ResponseEntity.ok(Arrays.asList(files));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/file/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filepath = Paths.get(uploadDir, filename);
            Resource file = new UrlResource(filepath.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
