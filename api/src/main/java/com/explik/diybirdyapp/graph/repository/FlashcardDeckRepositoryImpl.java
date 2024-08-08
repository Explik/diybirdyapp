package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;
import com.explik.diybirdyapp.graph.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.graph.vertex.FlashcardVertex;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckRepositoryImpl implements FlashcardDeckRepository {
    private final FramedGraph framedGraph;

    public FlashcardDeckRepositoryImpl(@Autowired TinkerGraph graph) {
        var vertexTypes = List.of(
            FlashcardVertex.class,
            FlashcardDeckVertex.class);
        framedGraph = new DelegatingFramedGraph<>(graph, vertexTypes);
    }

    @Override
    public FlashcardDeckModel get(String id) {
        var vertex = framedGraph
            .traverse(g -> g.V().has("flashcardDeck", "id", id))
            .nextOrDefaultExplicit(FlashcardDeckVertex.class, null);

        if (vertex == null)
            throw new IllegalArgumentException("Flashcard with id " + id + " not found");

        return create(vertex);
    }

    @Override
    public List<FlashcardDeckModel> getAll() {
        var vertices = framedGraph
            .traverse(g -> g.V().hasLabel("flashcardDeck"))
            .toListExplicit(FlashcardDeckVertex.class);

        return vertices
            .stream()
            .map(FlashcardDeckRepositoryImpl::create)
            .toList();
    }

    @Override
    public FlashcardDeckModel update(FlashcardDeckModel flashcardDeckModel) {
        if (flashcardDeckModel.getId() == null)
            throw new IllegalArgumentException("FlashcardDeck is missing id");

        var vertex = framedGraph
            .traverse(g -> g.V().has("flashcardDeck", "id", flashcardDeckModel.getId()))
            .nextOrDefaultExplicit(FlashcardDeckVertex.class, null);

        if (vertex == null)
            throw new IllegalArgumentException("FlashcardDeck with id " + flashcardDeckModel.getId() + " not found");

        if (flashcardDeckModel.getName() != null) {
            vertex.setName(flashcardDeckModel.getName());
        }

        return create(vertex);
    }

    private static FlashcardDeckModel create(FlashcardDeckVertex v) {
        return new FlashcardDeckModel(v.getId(), v.getName());
    }
}
