package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.service.TextContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class TextContentController {
    // TODO add REST actions for /text-content/{id}
    // TODO add action for /text-content/{id}/generate-pronunciation
    // TODO add action for /text-content/{id}/upload-pronunciation
    // TODO add fetch for /text-content/{id}/pronunciation

    @Autowired
    TextContentService textContentService;

    @PostMapping("/text-content/{id}/upload-pronunciation")
    public void uploadPronunciation(@PathVariable(name = "id") String id, @RequestParam(name = "file") MultipartFile file) {
        try {
            var fileName = file.getOriginalFilename();
            var fileData = file.getBytes();
            textContentService.uploadPronunciation(id, fileName, fileData);
        }
        catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
