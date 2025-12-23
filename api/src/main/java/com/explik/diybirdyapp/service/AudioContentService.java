package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.persistence.command.AddAudioToTextContentCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetTextContentByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service for managing audio content associated with text content.
 * Handles coordination and business logic for adding audio pronunciations to text.
 */
@Component
public class AudioContentService {
    @Autowired
    private QueryHandler<GetTextContentByIdQuery, TextContentVertex> getTextContentByIdQueryHandler;

    @Autowired
    private CommandHandler<AddAudioToTextContentCommand> addAudioToTextContentCommandHandler;

    /**
     * Adds audio pronunciation to a text content vertex.
     * Validates inputs and orchestrates the command to persist the pronunciation.
     *
     * @param textId The ID of the text content to add audio to
     * @param audioUrl The URL/path of the audio file
     * @throws RuntimeException if textId is null/empty, audioUrl is null/empty, or text content not found
     */
    public void addAudioToTextContent(String textId, String audioUrl) {
        // Validate input
        if (textId == null || textId.isEmpty()) {
            throw new RuntimeException("Text ID is required");
        }
        if (audioUrl == null || audioUrl.isEmpty()) {
            throw new RuntimeException("Audio URL is required");
        }

        // Query to check if text content exists
        var query = new GetTextContentByIdQuery();
        query.setId(textId);
        var textContent = getTextContentByIdQueryHandler.handle(query);
        
        if (textContent == null) {
            throw new RuntimeException("Text content not found: " + textId);
        }

        // Execute command to add audio
        var command = new AddAudioToTextContentCommand();
        command.setId(UUID.randomUUID().toString());
        command.setTextContentId(textId);
        command.setAudioUrl(audioUrl);
        
        addAudioToTextContentCommandHandler.handle(command);
    }
}
