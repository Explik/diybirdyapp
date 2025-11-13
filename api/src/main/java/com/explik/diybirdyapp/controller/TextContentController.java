package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.service.TextContentService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

@RestController
public class TextContentController {
    // TODO add REST actions for /text-content/{id}
    // TODO add action for /text-content/{id}/generate-pronunciation
    // TODO add action for /text-content/{id}/upload-pronunciation
    // TODO add fetch for /text-content/{id}/pronunciation

    @Autowired
    TextContentService textContentService;

    @GetMapping("/text-content/{id}/pronunciation")
    public ResponseEntity<byte[]> getPronunciation(@PathVariable(name = "id") String id) {
        var result = textContentService.getPronunciation(id);

        if (result == null)
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null);

        return ResponseEntity
                .status(HttpStatus.SC_PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .header("Accept-Ranges", "bytes")
                .body(Arrays.copyOfRange(result.getContent(), 0, result.getContent().length));
    }

    @PostMapping("/text-content/{id}/upload-pronunciation")
    public void uploadPronunciation(@PathVariable(name = "id") String id, @RequestParam(name = "file") MultipartFile file) {
        try {
            var fileName = file.getOriginalFilename();
            var fileData = file.getBytes();

            assert fileName != null;

            textContentService.uploadPronunciation(id, fileName, fileData);
        }
        catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
