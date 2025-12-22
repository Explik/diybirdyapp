package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.service.TextToSpeechService;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerateAudioForTextContentCommandHandler implements SyncCommandHandler<GenerateAudioForTextContentCommand, FileContentCommandResult>{
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Override
    public FileContentCommandResult handle(GenerateAudioForTextContentCommand command) {
        var textContentId = command.getTextContentId();
        if (textContentId == null || textContentId.isEmpty())
            throw new RuntimeException("Text content ID is empty");

        var textContentVertex = TextContentVertex.findById(traversalSource, textContentId);
        if (textContentVertex == null)
            throw new RuntimeException("Text content not found: " + command.getTextContentId());

        byte[] audioContent = fetchAudioContent(textContentVertex);
        return new FileContentCommandResult(audioContent, "audio/wav");
    }

    private FlashcardVertex getFlashcardVertex(String flashcardId) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardId);
        if (flashcardVertex == null)
            throw new RuntimeException("Flashcard not found: " + flashcardId);
        return flashcardVertex;
    }

    private TextToSpeechService.Text generateVoiceConfig(TextContentVertex textContentVertex) {
        var languageVertex = textContentVertex.getLanguage();

        var textToSpeechConfigs = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        if (textToSpeechConfigs.isEmpty())
            return null;

        var textToSpeechConfig = textToSpeechConfigs.getFirst();
        return new TextToSpeechService.Text(
                textContentVertex.getValue(),
                textToSpeechConfig.getPropertyValue("languageCode"),
                textToSpeechConfig.getPropertyValue("voiceName"),
                "LINEAR16"
        );
    }

    private byte[] fetchAudioContent(TextContentVertex textContentVertex) {
        var voiceConfig = generateVoiceConfig(textContentVertex);
        if (voiceConfig == null)
            throw new RuntimeException("No text to speech config found for text content: " + textContentVertex.getId());

        try {
            return textToSpeechService.generateAudio(voiceConfig);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate audio for text content: " + textContentVertex.getId(), e);
        }
    }
}
