package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.service.TextToSpeechService;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.PronunciationVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateAudioForFlashcardCommandHandler implements AsyncCommandHandler<GenerateAudioForFlashcardCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private TextToSpeechService textToSpeechService;

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
        var createAudioCommand = new CreateAudioContentVertexCommand();
        createAudioCommand.setId(UUID.randomUUID().toString());
        createAudioCommand.setUrl(filePath);
        createAudioCommand.setLanguageVertex(textContentVertex.getLanguage());
        createAudioContentVertexCommandHandler.handle(createAudioCommand);

        var audioVertex = AudioContentVertex.getById(traversalSource, createAudioCommand.getId());

        pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options(UUID.randomUUID().toString(), textContentVertex, audioVertex));
    }
}
