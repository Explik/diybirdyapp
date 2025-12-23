package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.GenerateAudioForTextContentCommand;
import com.explik.diybirdyapp.persistence.service.TextToSpeechService;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
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
        var textContentVertex = TextContentVertex.findById(traversalSource, command.getTextContentId());
        if (textContentVertex == null) {
            throw new RuntimeException("Text content not found: " + command.getTextContentId());
        }

        var voiceConfig = generateVoiceConfig(textContentVertex);
        if (voiceConfig == null) {
            throw new RuntimeException("No text to speech config found for text content: " + textContentVertex.getId());
        }

        try {
            return textToSpeechService.generateAudio(voiceConfig);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate audio for text content: " + textContentVertex.getId(), e);
        }
    }

    private TextToSpeechService.Text generateVoiceConfig(TextContentVertex textContentVertex) {
        var languageVertex = textContentVertex.getLanguage();

        var textToSpeechConfigs = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        if (textToSpeechConfigs.isEmpty()) {
            return null;
        }

        var textToSpeechConfig = textToSpeechConfigs.getFirst();
        return new TextToSpeechService.Text(
                textContentVertex.getValue(),
                textToSpeechConfig.getPropertyValue("languageCode"),
                textToSpeechConfig.getPropertyValue("voiceName"),
                "LINEAR16"
        );
    }
}
