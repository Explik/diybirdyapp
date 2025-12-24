package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.persistence.command.GenerateAudioForTextContentCommand;
import com.explik.diybirdyapp.persistence.query.GetTextContentByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.TextToSpeechService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Command handler for generating audio pronunciation using text-to-speech.
 * Fetches TTS configuration for the text's language and generates audio.
 * Note: This returns byte[] instead of void, which is an exception to the typical pattern.
 */
@Component
public class GenerateAudioForTextContentCommandHandler implements CommandHandler<GenerateAudioForTextContentCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Autowired
    private QueryHandler<GetTextContentByIdQuery, TextContentVertex> getTextContentByIdQueryHandler;

    @Autowired
    private QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> generateVoiceConfigQueryHandler;

    @Override
    public void handle(GenerateAudioForTextContentCommand command) {
        throw new UnsupportedOperationException("Use handleAndReturnAudio method instead");
    }

    /**
     * Generates audio for the text content and returns the audio bytes.
     * This is a special case where we need to return data from a command handler.
     * 
     * @param command The command containing text content ID
     * @return The generated audio bytes
     */
    public byte[] handleAndReturnAudio(GenerateAudioForTextContentCommand command) {
        // Fetch text for the given ID
        var textQuery = new GetTextContentByIdQuery();
        textQuery.setId(command.getTextContentId());

        var textContentVertex = getTextContentByIdQueryHandler.handle(textQuery);
        if (textContentVertex == null)
            throw new RuntimeException("Text content not found: " + command.getTextContentId());

        // Fetch voice config for the text's language
        var query = new GetVoiceByLanguageIdQuery();
        query.setLanguageId(textContentVertex.getLanguage().getId());

        var voiceConfig = generateVoiceConfigQueryHandler.handle(query);
        if (voiceConfig == null)
            throw new RuntimeException("No text to speech config found for text content: " + command.getTextContentId());

        // Generate audio using TTS service
        var textToSpeechModel = TextToSpeechModel.create(
                textContentVertex.getValue(),
                voiceConfig);
        try {
            return textToSpeechService.generateAudio(textToSpeechModel);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate audio for text content: " + command.getTextContentId(), e);
        }
    }
}
