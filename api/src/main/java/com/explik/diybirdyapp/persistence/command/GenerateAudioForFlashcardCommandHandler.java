package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.service.TextToSpeechService;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextToSpeechConfigVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.AudioContentVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.PronunciationVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateAudioForFlashcardCommandHandler implements CommandHandler<GenerateAudioForFlashcardCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private TextToSpeechService textToSpeechService;

    public void handle(GenerateAudioForFlashcardCommand command) {
        FlashcardVertex flashcardVertex = FlashcardVertex.findById(traversalSource, command.getFlashcardId());
        if (flashcardVertex == null)
            throw new RuntimeException("Flashcard not found: " + command.getFlashcardId());

        var failOnMissingVoice = command.getFailOnMissingVoice();
        addAudioContent(flashcardVertex.getLeftContent(), failOnMissingVoice);
        addAudioContent(flashcardVertex.getRightContent(), failOnMissingVoice);
    }

    private void addAudioContent(TextContentVertex textContentVertex, boolean failOnMissingVoice) {
        var languageId = textContentVertex.getLanguage().getId();
        var textToSpeechConfigs = TextToSpeechConfigVertex.findByLanguageId(traversalSource, languageId);
        if (textToSpeechConfigs.isEmpty()) {
            if (failOnMissingVoice)
                throw new RuntimeException("No text to speech config found for language: " + languageId);
            else return;
        }

        var textToSpeechConfig = textToSpeechConfigs.getFirst();
        var textObject = new TextToSpeechService.Text(
                textContentVertex.getValue(),
                textToSpeechConfig.getLanguageCode(),
                textToSpeechConfig.getVoiceName(),
                "LINEAR16"
        );

        var filePath = "uploads/" + textContentVertex.getId() + ".wav";
        try {
            textToSpeechService.generateAudioFile(textObject, filePath);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to generate audio for text content: " + textContentVertex.getId(), e);
        }

        // Save audio path to graph
        var audioVertex = audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options(UUID.randomUUID().toString(), filePath, textContentVertex.getLanguage()));

        pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options(UUID.randomUUID().toString(), textContentVertex, audioVertex));
    }
}
