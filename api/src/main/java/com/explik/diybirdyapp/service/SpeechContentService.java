package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.persistence.query.GetTextContentByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpeechContentService {
    @Autowired
    private TextToSpeechService textToSpeechService;

    @Autowired
    private QueryHandler<GetTextContentByIdQuery, TextContentVertex> getTextContentByIdQueryHandler;

    @Autowired
    private QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> generateVoiceConfigQueryHandler;

    public byte[] generateSpeechForTextContentId(String textContentId) {
        // Fetch text for the given ID
        var textQuery = new GetTextContentByIdQuery();
        textQuery.setId(textContentId);

        var textContentVertex = getTextContentByIdQueryHandler.handle(textQuery);
        if (textContentVertex == null)
            throw new RuntimeException("Text content not found: " + textContentId);

        return generateSpeech(
                textContentVertex.getLanguage().getId(),
                textContentVertex.getValue());
    }

    public byte[] generateSpeech(String languageId, String text) {
        // Fetch voice config for the text's language
        var query = new GetVoiceByLanguageIdQuery();
        query.setLanguageId(languageId);

        var voiceConfig = generateVoiceConfigQueryHandler.handle(query);
        if (voiceConfig == null)
            throw new RuntimeException("No text to speech config found for text content: " + text);

        // Generate audio using TTS service
        var textToSpeechModel = TextToSpeechModel.create(
                text,
                voiceConfig);
        try {
            return textToSpeechService.generateAudio(textToSpeechModel);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate audio", e);
        }
    }
}
