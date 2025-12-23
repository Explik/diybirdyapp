package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.FileContentCommandResult;
import com.explik.diybirdyapp.persistence.service.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextContentService {
    @Autowired
    BinaryStorageService binaryStorageService;

    @Autowired
    AudioContentService audioContentService;

    public FileContentCommandResult getPronunciation(String id) {
        var audio = audioContentService.getAudioForTextContent(id);
        return new FileContentCommandResult(audio.getData(), audio.getContentType());
    }

    public void uploadPronunciation(String id, String originalFileName, byte[] fileData) {
        var fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        var newFileName = UUID.randomUUID() + "." + fileExtension;

        binaryStorageService.set(newFileName, fileData);

        try {
            audioContentService.addAudioToTextContent(id, newFileName);
        }
        catch (Exception e) {
            binaryStorageService.delete(newFileName);
            throw new RuntimeException("Failed to add audio for text content", e);
        }
    }
}
