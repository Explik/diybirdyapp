package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.FlashcardDeckModel;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardDeckRepositoryImpl implements FlashcardDeckRepository {
    private final GraphTraversalSource traversalSource;

    public FlashcardDeckRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardDeckModel add(FlashcardDeckModel model) {
        var flashcardDeckVertex = FlashcardDeckVertex.create(traversalSource);
        var flashcardDeckId = model.getId() != null ? model.getId() : UUID.randomUUID().toString();
        var flashcardDeckName = model.getName() != null ? model.getName() : "";
        flashcardDeckVertex.setId(flashcardDeckId);
        flashcardDeckVertex.setName(flashcardDeckName);

        return create(flashcardDeckVertex);
    }

    @Override
    public FlashcardDeckModel get(String id) {
        var query = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, id);

        if (!query.hasNext())
            throw new IllegalArgumentException("Flashcard with id " + id + " not found");

        return create(new FlashcardDeckVertex(traversalSource, query.next()));
    }

    @Override
    public List<FlashcardDeckModel> getAll() {
        var vertices = traversalSource.V().hasLabel(FlashcardDeckVertex.LABEL).toList();

        return vertices
            .stream()
            .map(v -> new FlashcardDeckVertex(traversalSource, v))
            .map(FlashcardDeckRepositoryImpl::create)
            .toList();
    }

    @Override
    public FlashcardDeckModel update(FlashcardDeckModel flashcardDeckModel) {
        if (flashcardDeckModel.getId() == null)
            throw new IllegalArgumentException("FlashcardDeck is missing id");

        var query = traversalSource.V().has("flashcardDeck", "id", flashcardDeckModel.getId());
        if (!query.hasNext())
            throw new IllegalArgumentException("FlashcardDeck with id " + flashcardDeckModel.getId() + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, query.next());
        if (flashcardDeckModel.getName() != null) {
            vertex.setName(flashcardDeckModel.getName());
        }

        return create(vertex);
    }

    private static FlashcardDeckModel create(FlashcardDeckVertex v) {
        return new FlashcardDeckModel(v.getId(), v.getName());
    }
}
