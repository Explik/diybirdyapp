package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateWordVertexCommand;
import com.explik.diybirdyapp.persistence.command.ExtractWordsFromFlashcardCommand;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * Command handler for extracting words from flashcard text content.
 * Processes both left and right sides of the flashcard, splitting text by spaces
 * and creating word vertices for unique words.
 */
@Component
public class ExtractWordsFromFlashcardCommandHandler implements CommandHandler<ExtractWordsFromFlashcardCommand> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreateWordVertexCommand> createWordVertexCommandHandler;

    @Override
    public void handle(ExtractWordsFromFlashcardCommand command) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, command.getFlashcardId());
        if (flashcardVertex == null) {
            throw new RuntimeException("Flashcard not found: " + command.getFlashcardId());
        }

        if (flashcardVertex.getLeftContent() instanceof TextContentVertex leftTextContent)
            createWordsForSide(leftTextContent);

        if (flashcardVertex.getRightContent() instanceof TextContentVertex rightTextContent)
            createWordsForSide(rightTextContent);
    }

    private void createWordsForSide(TextContentVertex textContent) {
        var language = textContent.getLanguage();

        var wordValues = Stream.of(textContent.getValue().split(" "))
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        // Add new word connections
        for (var wordValue : wordValues) {
            var word = WordVertex.findByValue(traversalSource, wordValue);

            if (word == null) {
                var createCommand = new CreateWordVertexCommand();
                createCommand.setId(null);
                createCommand.setValue(wordValue);
                createCommand.setMainExample(textContent.getId());
                createCommand.setLanguageVertex(language.getId());
                createWordVertexCommandHandler.handle(createCommand);
            }
            else {
                word.addExample(textContent);
            }
        }
    }
}
