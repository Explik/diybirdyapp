package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.FileContentCommandResult;
import com.explik.diybirdyapp.model.content.TextContentTranscriptionDto;
import com.explik.diybirdyapp.persistence.command.CreateTranscriptionCommand;
import com.explik.diybirdyapp.persistence.command.CreateTranscriptionSystemCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
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
    CommandHandler<CreateTranscriptionSystemCommand> createTranscriptionSystemCommandHandler;

    @Autowired
    CommandHandler<CreateTranscriptionCommand> createTranscriptionCommandHandler;

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

    public void addTranscription(String id, TextContentTranscriptionDto transcription) {
        // Create the transcription system if it doesn't exist
        var createSystemCommand = new CreateTranscriptionSystemCommand();
        createSystemCommand.setId(transcription.getTranscriptionSystem());
        createTranscriptionSystemCommandHandler.handle(createSystemCommand);

        // Create the transcription
        var createTranscriptionCommand = new CreateTranscriptionCommand();
        createTranscriptionCommand.setId(UUID.randomUUID().toString());
        createTranscriptionCommand.setSourceContentId(id);
        createTranscriptionCommand.setTextValue(transcription.getTranscription());
        createTranscriptionCommand.setTranscriptionSystemId(transcription.getTranscriptionSystem());
        createTranscriptionCommandHandler.handle(createTranscriptionCommand);
    }
}
