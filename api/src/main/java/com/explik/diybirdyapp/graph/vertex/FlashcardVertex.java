package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.ExerciseContentFlashcardModel;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.stream.Collectors;

public class FlashcardVertex extends AbstractVertex {
    public FlashcardVertex(GraphTraversalSource traversalSource, Vertex vertex) {
        super(traversalSource, vertex);
    }

    public String getId() {
        return getPropertyAsString("id");
    }

    public void setId(String id) {
        setProperty("id", id);
    }

    public FlashcardDeckVertex getDeck() {
        var deckVertex = traversalSource.V(vertex).in("hasFlashcard").next();
        return new FlashcardDeckVertex(traversalSource, deckVertex);
    }

    public TextContentVertex getLeftContent() {
        var leftContentVertex = traversalSource.V(vertex).out("hasLeftContent").next();
        return new TextContentVertex(traversalSource, leftContentVertex);
    }

    public void setLeftContent(TextContentVertex vertex) {
        addEdgeOneToOne("hasLeftContent", vertex);
    }

    public TextContentVertex getRightContent() {
        var rightContentVertex = traversalSource.V(vertex).out("hasRightContent").next();
        return new TextContentVertex(traversalSource, rightContentVertex);
    }

    public void setRightContent(TextContentVertex vertex) {
        addEdgeOneToOne("hasRightContent", vertex);
    }

    public FlashcardModel toFlashcardModel() {
        var leftContent = getLeftContent();
        var rightContent = getRightContent();

        var model = new FlashcardModel();
        model.setId(getId());
        model.setDeckId(getDeck().getId());
        model.setLeftValue(leftContent.getValue());
        model.setLeftLanguage(leftContent.getLanguage().toFlashcardLanguageModel());
        model.setRightValue(rightContent.getValue());
        model.setRightLanguage(rightContent.getLanguage().toFlashcardLanguageModel());
        return model;
    }

    public ExerciseContentFlashcardModel toExerciseContentFlashcardModel() {
        var model = new ExerciseContentFlashcardModel();
        model.setId(getId());
        model.setBack(getRightContent().toExerciseContentTextModel());
        model.setFront(getLeftContent().toExerciseContentTextModel());
        return model;
    }

    public static FlashcardVertex create(GraphTraversalSource traversalSource) {
        var vertex = traversalSource.addV("flashcard").next();
        return new FlashcardVertex(traversalSource, vertex);
    }

    public static List<FlashcardVertex> findAll(GraphTraversalSource traversalSource) {
        var vertices = traversalSource.V().hasLabel("flashcard").toList();

        return vertices.stream()
                .map(v -> new FlashcardVertex(traversalSource, v))
                .collect(Collectors.toList());
    }

    public static FlashcardVertex findById(GraphTraversalSource traversalSource, String id) {
        var query = traversalSource.V().has("flashcard", "id", id);

        if (!query.hasNext())
            throw new IllegalArgumentException("Flashcard with id " + id + " not found");

        return new FlashcardVertex(traversalSource, query.next());
    }

    public static List<FlashcardVertex> findByDeckId(GraphTraversalSource traversalSource, String deckId) {
        var vertices = traversalSource.V()
                .has("flashcardDeck", "id", deckId).out("hasFlashcard")
                .toList();

        return vertices.stream()
                .map(v -> new FlashcardVertex(traversalSource, v))
                .collect(Collectors.toList());
    }
}
