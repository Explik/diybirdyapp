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
public class GenerateAudioForFlashcardCommandHandler implements SyncCommandHandler<GenerateAudioForFlashcardCommand, FileContentCommandResult>, AsyncCommandHandler<GenerateAudioForFlashcardCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Override
    public FileContentCommandResult handle(GenerateAudioForFlashcardCommand command) {
        var flashcardVertex = getFlashcardVertex(command.getFlashcardId());

        var flashcardSideVertex = flashcardVertex.getSide(command.getFlashcardSide());
        if (flashcardSideVertex == null)
            throw new RuntimeException("Flashcard side not found: " + command.getFlashcardSide());
        if (!(flashcardSideVertex instanceof TextContentVertex textContentVertex))
            throw new RuntimeException("Flashcard side is not text content: " + command.getFlashcardSide());

        byte[] audioContent = fetchAudioContent(textContentVertex);
        return new FileContentCommandResult(audioContent, "audio/wav");
    }

    @Override
    public void handleAsync(GenerateAudioForFlashcardCommand command) {
        var flashcardVertex = getFlashcardVertex(command.getFlashcardId());

        boolean failOnMissingVoice = command.getFailOnMissingVoice();
        if (flashcardVertex.getLeftContent() instanceof TextContentVertex leftTextContent)
            saveAudioContent(leftTextContent, failOnMissingVoice);
        if (flashcardVertex.getRightContent() instanceof TextContentVertex rightTextContent)
            saveAudioContent(rightTextContent, failOnMissingVoice);
    }

    private FlashcardVertex getFlashcardVertex(String flashcardId) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardId);
        if (flashcardVertex == null)
            throw new RuntimeException("Flashcard not found: " + flashcardId);
        return flashcardVertex;
    }

    private TextToSpeechService.Text generateVoiceConfig(TextContentVertex textContentVertex) {
        var languageId = textContentVertex.getLanguage().getId();
        var textToSpeechConfigs = TextToSpeechConfigVertex.findByLanguageId(traversalSource, languageId);
        if (textToSpeechConfigs.isEmpty())
            return null;

        var textToSpeechConfig = textToSpeechConfigs.getFirst();
        return new TextToSpeechService.Text(
                textContentVertex.getValue(),
                textToSpeechConfig.getLanguageCode(),
                textToSpeechConfig.getVoiceName(),
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

    private void saveAudioContent(TextContentVertex textContentVertex, boolean failOnMissingVoice) {
        var voiceConfig = generateVoiceConfig(textContentVertex);
        if (voiceConfig == null) {
            if (failOnMissingVoice)
                throw new RuntimeException("No text to speech config found for text content: " + textContentVertex.getId());
            else return;
        }

        var filePath = textContentVertex.getId() + ".wav";
        try {
            textToSpeechService.generateAudioFile(voiceConfig, filePath);
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
