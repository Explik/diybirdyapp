package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.persistence.generalCommand.*;
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

    @Autowired
    SyncCommandHandler<FetchAudioForTextContentCommand, FileContentCommandResult> fetchCommandHandler;

    @Autowired
    SyncCommandHandler<GenerateAudioForTextContentCommand, FileContentCommandResult> generateCommandHandler;

    public FileContentCommandResult getPronunciation(String id) {
        var fetchExistingCommand = new FetchAudioForTextContentCommand(id);
        var existingResult = fetchCommandHandler.handle(fetchExistingCommand);
        if (existingResult != null)
            return existingResult;

        var generateCommand = new GenerateAudioForTextContentCommand(id);
        return generateCommandHandler.handle(generateCommand);
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
