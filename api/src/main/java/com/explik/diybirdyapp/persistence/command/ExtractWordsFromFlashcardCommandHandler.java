package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.VertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.WordVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ExtractWordsFromFlashcardCommandHandler implements CommandHandler<ExtractWordsFromFlashcardCommand> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    WordVertexFactory wordVertexFactory;

    @Override
    public void handle(ExtractWordsFromFlashcardCommand command) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, command.getFlashcardId());
        createWordsForSide((TextContentVertex)flashcardVertex.getLeftContent());
        createWordsForSide((TextContentVertex)flashcardVertex.getRightContent());
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
                wordVertexFactory.create(
                        traversalSource,
                        new WordVertexFactory.Options(null, wordValue, textContent, language));
            }
            else word.addExample(textContent);
        }
    }
}
