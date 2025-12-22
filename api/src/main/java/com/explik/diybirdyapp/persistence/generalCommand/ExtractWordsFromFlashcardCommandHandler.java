package com.explik.diybirdyapp.persistence.generalCommand;

import com.explik.diybirdyapp.persistence.command.CreateWordVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ExtractWordsFromFlashcardCommandHandler implements AsyncCommandHandler<ExtractWordsFromFlashcardCommand> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    CommandHandler<CreateWordVertexCommand> createWordVertexCommandHandler;

    @Override
    public void handleAsync(ExtractWordsFromFlashcardCommand command) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, command.getFlashcardId());

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
            else word.addExample(textContent);
        }
    }
}
