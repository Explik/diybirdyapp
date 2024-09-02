package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.graph.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardLanguageVertex;
import com.explik.diybirdyapp.graph.vertex.TextVertex;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardRepositoryImpl implements FlashcardRepository {
    private final FramedGraph framedGraph;

    public FlashcardRepositoryImpl(@Autowired TinkerGraph graph) {
        var vertexTypes = List.of(
            FlashcardVertex.class,
            FlashcardDeckVertex.class,
            FlashcardLanguageVertex.class,
            TextVertex.class);
        framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
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
        var flashcardDeckVertex = getFlashcardDeckVertex(framedGraph, flashcardModel.getDeckId());
        var textContentVertex1 = createTextContent(framedGraph, flashcardModel.getLeftLanguage().getId());
        var textContentVertex2 = createTextContent(framedGraph, flashcardModel.getRightLanguage().getId());

        var flashcardVertex = framedGraph.addFramedVertex(FlashcardVertex.class, T.label, "flashcard");
        flashcardVertex.setId(UUID.randomUUID().toString());
        flashcardVertex.setLeftContent(textContentVertex1);
        flashcardVertex.setRightContent(textContentVertex2);

        flashcardDeckVertex.addFramedEdgeExplicit("hasFlashcard", flashcardVertex);

        return flashcardVertex.toFlashcardModel();
    }

    @Override
    public FlashcardModel get(String id) {
        var vertex = framedGraph
            .traverse(g -> g.V().has("flashcard", "id", id))
            .nextOrDefaultExplicit(FlashcardVertex.class, null);

        if (vertex == null)
            throw new IllegalArgumentException("Flashcard with id " + id + " not found");

        return vertex.toFlashcardModel();
    }

    @Override
    public List<FlashcardModel> getAll(String deckId) {
        List<? extends  FlashcardVertex> vertices;

        if (deckId != null) {
            vertices = framedGraph
                .traverse(g -> g.V().has("flashcardDeck", "id", deckId).out("hasFlashcard"))
                .toListExplicit(FlashcardVertex.class);
        }
        else {
            vertices = framedGraph
                .traverse(g -> g.V().hasLabel("flashcard"))
                .toListExplicit(FlashcardVertex.class);
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

        var flashcardVertex = framedGraph
            .traverse(g -> g.V().has("flashcard", "id", flashcardModel.getId() + ""))
            .nextOrDefaultExplicit(FlashcardVertex.class, null);

        if (flashcardVertex == null)
            throw new IllegalArgumentException("Flashcard with id " + flashcardModel.getId() + " not found");

        if (flashcardModel.getLeftLanguage() != null) {
            var languageVertex = getLanguageVertex(framedGraph, flashcardModel.getLeftLanguage().getId());
            flashcardVertex.getLeftContent().setLanguage(languageVertex);
        }

        if (flashcardModel.getRightLanguage() != null) {
            var languageVertex = getLanguageVertex(framedGraph, flashcardModel.getRightLanguage().getId());
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

    private static TextVertex createTextContent(FramedGraph framedGraph, String languageId) {
        var languageVertex = getLanguageVertex(framedGraph, languageId);
        var textContentVertex = framedGraph.addFramedVertex(TextVertex.class, T.label, "textContent");

        textContentVertex.setLanguage(languageVertex);

        return textContentVertex;
    }

    private static FlashcardLanguageVertex getLanguageVertex(FramedGraph framedGraph, String languageId) {
        return framedGraph
            .traverse(g -> g.V().has("language", "id", languageId))
            .nextExplicit(FlashcardLanguageVertex.class);
    }

    private static FlashcardDeckVertex getFlashcardDeckVertex(FramedGraph framedGraph, String deckId) {
        return framedGraph
            .traverse(g -> g.V().has("flashcardDeck", "id", deckId))
            .nextExplicit(FlashcardDeckVertex.class);
    }
}
