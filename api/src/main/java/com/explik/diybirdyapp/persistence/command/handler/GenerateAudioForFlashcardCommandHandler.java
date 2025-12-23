package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.GenerateAudioForFlashcardCommand;
import com.explik.diybirdyapp.persistence.query.GenerateVoiceConfigQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.service.TextToSpeechService;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Command handler for generating audio pronunciation for flashcards.
 * Processes both left and right sides of the flashcard if they contain text content.
 */
@Component
public class GenerateAudioForFlashcardCommandHandler implements CommandHandler<GenerateAudioForFlashcardCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandHandler;

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Autowired
    private QueryHandler<GenerateVoiceConfigQuery, TextToSpeechService.Text> generateVoiceConfigQueryHandler;

    @Override
    public void handle(GenerateAudioForFlashcardCommand command) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, command.getFlashcardId());
        if (flashcardVertex == null) {
            throw new RuntimeException("Flashcard not found: " + command.getFlashcardId());
        }

        boolean failOnMissingVoice = command.getFailOnMissingVoice();
        if (flashcardVertex.getLeftContent() instanceof TextContentVertex leftTextContent)
            saveAudioContent(leftTextContent, failOnMissingVoice);
        if (flashcardVertex.getRightContent() instanceof TextContentVertex rightTextContent)
            saveAudioContent(rightTextContent, failOnMissingVoice);
    }


    private void saveAudioContent(TextContentVertex textContentVertex, boolean failOnMissingVoice) {
        var query = new GenerateVoiceConfigQuery();
        query.setTextContentVertexId(textContentVertex.getId());
        var voiceConfig = generateVoiceConfigQueryHandler.handle(query);
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
        var createCommand = new CreatePronunciationVertexCommand();
        createCommand.setId(UUID.randomUUID().toString());
        createCommand.setAudioUrl(filePath);
        createCommand.setSourceVertex(textContentVertex.getId());
        createPronunciationVertexCommandHandler.handle(createCommand);
    }
}
