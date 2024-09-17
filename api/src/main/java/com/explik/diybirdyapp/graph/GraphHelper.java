package com.explik.diybirdyapp.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class GraphHelper {
    public static final String DefaultValue = "PLACEHOLDER";

    public static Vertex addLanguage(GraphTraversalSource traversalSource, String id) {
        return traversalSource.addV("language")
                .property("id", id)
                .property("abbreviation", DefaultValue)
                .property("name", DefaultValue)
                .next();
    }

    public static Vertex addTextContentWithLanguage(GraphTraversalSource traversalSource, String id, String languageId) {
        var languageVertex = traversalSource.V("language", "id", languageId).next();
       return addTextContentWithLanguage(traversalSource, id, languageVertex);
    }

    public static Vertex addTextContentWithLanguage(GraphTraversalSource traversalSource, String id, Vertex languageVertex) {
        var vertex = traversalSource.addV("textContent")
                .property("id", id)
                .property("value", DefaultValue).next();

        traversalSource.V(vertex)
                .addE("hasLanguage").to(languageVertex)
                .next();

        return vertex;
    }

    public static Vertex addFlashcardWithTextContent(GraphTraversalSource traversalSource, String id, String contentId1, String contentId2) {
        var contentVertex1 = traversalSource.V("textContent", "id", contentId1).next();
        var contentVertex2 = traversalSource.V("textContent", "id", contentId2).next();

        return addFlashcardWithTextContent(traversalSource, id, contentVertex1, contentVertex2);
    }

    public static Vertex addFlashcardWithTextContent(GraphTraversalSource traversalSource, String id, Vertex contentVertex1, Vertex contentVertex2) {
        var vertex = traversalSource.addV("flashcard")
                .property("id", id)
                .next();

        traversalSource.V(vertex).as("flashcard")
                .addE("hasLeftContent").from("flashcard").to(contentVertex1)
                .addE("hasRightContent").from("flashcard").to(contentVertex2)
                .next();

        return vertex;
    }

    public static Vertex addFlashcardDeckWithFlashcards(GraphTraversalSource traversalSource, String id, Vertex... flashcardVertices) {
        var vertex = traversalSource.addV("flashcardDeck")
                .property("id", id)
                .property("name", DefaultValue)
                .next();

        for(var flashcardVertex : flashcardVertices)
            vertex.addEdge("hasFlashcard", flashcardVertex);

        return vertex;
    }
}
