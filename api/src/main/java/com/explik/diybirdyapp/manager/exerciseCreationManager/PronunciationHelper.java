package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.TextToSpeechService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper class for fetching or generating pronunciation audio for text content.
 */
@Component
public class PronunciationHelper {
    @Autowired
    private TextToSpeechService textToSpeechService;

    @Autowired
    private QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> generateVoiceConfigQueryHandler;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandHandler;

    /**
     * Attempts to fetch existing pronunciation or generate new pronunciation audio.
     * Returns null if pronunciation cannot be generated.
     */
    public AudioContentVertex tryFetchOrGeneratePronunciation(
            GraphTraversalSource traversalSource, 
            TextContentVertex textContentVertex) {
        
        var existingPronunciation = PronunciationVertex.findByTextContentId(
                traversalSource, 
                textContentVertex.getId());
        
        if (existingPronunciation != null) {
            return existingPronunciation.getAudioContent();
        }

        // Generate pronunciation file
        var query = new GetVoiceByLanguageIdQuery();
        query.setLanguageId(textContentVertex.getLanguage().getId());

        var voiceConfig = generateVoiceConfigQueryHandler.handle(query);
        if (voiceConfig == null) {
            return null;
        }

        var textToSpeechModel = TextToSpeechModel.create(
                textContentVertex.getValue(),
                voiceConfig);
        var filePath = textContentVertex.getId() + ".wav";
        
        try {
            textToSpeechService.generateAudioFile(textToSpeechModel, filePath);
        } catch (Exception e) {
            System.err.println("Failed to generate audio for text content: " + textContentVertex.getId());
            System.err.println(e.toString());
            return null;
        }

        // Save pronunciation to graph
        var createCommand = new CreatePronunciationVertexCommand();
        createCommand.setId(UUID.randomUUID().toString());
        createCommand.setAudioUrl(filePath);
        createCommand.setSourceVertex(textContentVertex.getId());
        createPronunciationVertexCommandHandler.handle(createCommand);

        return null;
    }
}
