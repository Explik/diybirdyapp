package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.FlashcardModel;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardRepositoryImpl implements FlashcardRepository {
    private final GraphTraversalSource traversalSource;

    public FlashcardRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardModel add(FlashcardModel flashcardModel) {
        // Binds flashcard to existing set
        if (flashcardModel.getDeckId() == null)
            throw new IllegalArgumentException("Model is missing deckId");

        // Binds flashcard to existing languages
        if (flashcardModel.getLeftLanguage() == null || flashcardModel.getLeftLanguage().getId() == null)
            throw new IllegalArgumentException("leftLanguage is missing");
        if (flashcardModel.getRightLanguage() == null || flashcardModel.getRightLanguage().getId() == null)
            throw new IllegalArgumentException("rightLanguage is missing");

        // Binds flashcard content
        var flashcardDeckVertex = getFlashcardDeckVertex(traversalSource, flashcardModel.getDeckId());
        var textContentVertex1 = createTextContent(traversalSource, flashcardModel.getLeftLanguage().getId(), flashcardModel.getLeftValue());
        var textContentVertex2 = createTextContent(traversalSource, flashcardModel.getRightLanguage().getId(), flashcardModel.getRightValue());

        var flashcardVertex = FlashcardVertex.create(traversalSource);
        flashcardVertex.setId(UUID.randomUUID().toString());
        flashcardVertex.setLeftContent(textContentVertex1);
        flashcardVertex.setRightContent(textContentVertex2);

        flashcardDeckVertex.addFlashcard(flashcardVertex);

        return flashcardVertex.toFlashcardModel();
    }

    @Override
    public FlashcardModel get(String id) {
        var vertex = FlashcardVertex.findById(traversalSource, id);
        return vertex.toFlashcardModel();
    }

    @Override
    public List<FlashcardModel> getAll(String deckId) {
        List<FlashcardVertex> vertices;

        if (deckId != null) {
            vertices = FlashcardVertex.findByDeckId(traversalSource, deckId);
        }
        else {
            vertices = FlashcardVertex.findAll(traversalSource);
        }

        return vertices
            .stream()
            .map(FlashcardVertex::toFlashcardModel)
            .toList();
    }

    @Override
    public FlashcardModel update(FlashcardModel flashcardModel) {
        if (flashcardModel.getId() == null)
            throw new IllegalArgumentException("Flashcard is missing id");

        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardModel.getId());

        if (flashcardModel.getLeftLanguage() != null) {
            var languageVertex = getLanguageVertex(traversalSource, flashcardModel.getLeftLanguage().getId());
            flashcardVertex.getLeftContent().setLanguage(languageVertex);
        }

        if (flashcardModel.getRightLanguage() != null) {
            var languageVertex = getLanguageVertex(traversalSource, flashcardModel.getRightLanguage().getId());
            flashcardVertex.getRightContent().setLanguage(languageVertex);
        }

        if (flashcardModel.getLeftValue() != null) {
            flashcardVertex.getLeftContent().setValue(flashcardModel.getLeftValue());
        }

        if (flashcardModel.getRightValue() != null) {
            flashcardVertex.getRightContent().setValue(flashcardModel.getRightValue());
        }

        return flashcardVertex.toFlashcardModel();
    }

    private static TextContentVertex createTextContent(GraphTraversalSource traversalSource, String languageId, String value) {
        var languageVertex = getLanguageVertex(traversalSource, languageId);
        var textContentVertex = TextContentVertex.create(traversalSource);
        textContentVertex.setId(UUID.randomUUID().toString());
        textContentVertex.setLanguage(languageVertex);
        textContentVertex.setValue(value != null ? value : "");

        return textContentVertex;
    }

    private static LanguageVertex getLanguageVertex(GraphTraversalSource traversalSource, String languageId) {
        var vertex = traversalSource.V().has(LanguageVertex.LABEL, LanguageVertex.PROPERTY_ID, languageId).next();
        return new LanguageVertex(traversalSource, vertex);
    }

    private static FlashcardDeckVertex getFlashcardDeckVertex(GraphTraversalSource traversalSource, String deckId) {
        var vertex = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, deckId).next();
        return new FlashcardDeckVertex(traversalSource, vertex);
    }
}
