package com.explik.diybirdyapp.graph;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GraphHelper {
    public static final String DefaultValue = "PLACEHOLDER";

    public static Vertex addLanguage(TinkerGraph graph, String id) {
        Vertex vertex = graph.addVertex("language");
        vertex.property("id", id);
        vertex.property("abbreviation", DefaultValue);
        vertex.property("name", DefaultValue);

        return vertex;
    }

    public static Vertex addTextContentWithLanguage(TinkerGraph graph, String id, String languageId) {
       var languageVertex = graph.traversal().V("language", "id", languageId).next();
       return addTextContentWithLanguage(graph, id, languageVertex);
    }

    public static Vertex addTextContentWithLanguage(TinkerGraph graph, String id, Vertex languageVertex) {
        var vertex = graph.addVertex("textContent");
        vertex.property("id", id);
        vertex.property("value", DefaultValue);
        vertex.addEdge("hasLanguage", languageVertex);

        return vertex;
    }

    public static Vertex addFlashcardWithTextContent(TinkerGraph graph, String id, String contentId1, String contentId2) {
        var contentVertex1 = graph.traversal().V("textContent", "id", contentId1).next();
        var contentVertex2 = graph.traversal().V("textContent", "id", contentId2).next();

        return addFlashcardWithTextContent(graph, id, contentVertex1, contentVertex2);
    }

    public static Vertex addFlashcardWithTextContent(TinkerGraph graph, String id, Vertex contentVertex1, Vertex contentVertex2) {
        var vertex = graph.addVertex("flashcard");
        vertex.property("id", id);
        vertex.addEdge("hasLeftContent", contentVertex1);
        vertex.addEdge("hasRightContent", contentVertex2);

        return vertex;
    }

    public static Vertex addFlashcardDeckWithFlashcards(TinkerGraph graph, String id, Vertex... flashcardVertices) {
        var vertex = graph.addVertex("flashcardDeck");
        vertex.property("id", id);

        for(var flashcardVertex : flashcardVertices) {
            vertex.addEdge("hasFlashcard", flashcardVertex);
        }
        return vertex;
    }
}
