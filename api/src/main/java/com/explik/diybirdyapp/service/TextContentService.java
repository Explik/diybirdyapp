package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.persistence.command.AddAudioForTextCommand;
import com.explik.diybirdyapp.persistence.command.FileContentCommandResult;
import com.explik.diybirdyapp.persistence.command.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.service.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextContentService {
    @Autowired
    BinaryStorageService binaryStorageService;

    @Autowired
    SyncCommandHandler<AddAudioForTextCommand, FileContentCommandResult> commandHandler;

    public void uploadPronunciation(String id, String originalFileName, byte[] fileData) {
        var fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        var newFileName = UUID.randomUUID() + "." + fileExtension;

        binaryStorageService.set(newFileName, fileData);

        try {
            var command = new AddAudioForTextCommand(id, newFileName);
            commandHandler.handle(command);
        }
        catch (Exception e) {
            binaryStorageService.delete(newFileName);
            throw new RuntimeException("Failed to add audio for text content", e);
        }
    }
}
