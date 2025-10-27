package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardDeckModel;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.UserVertex;
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
    public FlashcardDeckModel add(String userId, FlashcardDeckModel model) {
        // Fetch owner vertex
        var userVertex = UserVertex.findWithEmail(traversalSource, userId);
        if (userVertex == null)
            throw new IllegalArgumentException("User with id " + userId + " not found");

        // Create flashcard deck vertex
        var flashcardDeckVertex = FlashcardDeckVertex.create(traversalSource);
        var flashcardDeckId = model.getId() != null ? model.getId() : UUID.randomUUID().toString();
        var flashcardDeckName = model.getName() != null ? model.getName() : "";
        flashcardDeckVertex.setId(flashcardDeckId);
        flashcardDeckVertex.setName(flashcardDeckName);
        flashcardDeckVertex.setOwner(userVertex);

        return createModel(flashcardDeckVertex);
    }

    @Override
    public FlashcardDeckModel get(String userId, String id) {
        var query = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, id);

        if (!query.hasNext())
            throw new IllegalArgumentException("Flashcard with id " + id + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, query.next());
        if (!isDeckOwner(userId, vertex))
            throw new IllegalArgumentException("FlashcardDeck with id " + id + " not found");

        return createModel(vertex);
    }

    @Override
    public List<FlashcardDeckModel> getAll(String userId) {
        var vertices = traversalSource.V().hasLabel(FlashcardDeckVertex.LABEL).toList();

        return vertices
            .stream()
            .map(v -> new FlashcardDeckVertex(traversalSource, v))
                .filter(deck -> isDeckOwner(userId, deck))
                .map(FlashcardDeckRepositoryImpl::createModel)
            .toList();
    }

    @Override
    public FlashcardDeckModel update(String userId, FlashcardDeckModel flashcardDeckModel) {
        if (flashcardDeckModel.getId() == null)
            throw new IllegalArgumentException("FlashcardDeck is missing id");

        var query = traversalSource.V().has("flashcardDeck", "id", flashcardDeckModel.getId());
        if (!query.hasNext())
            throw new IllegalArgumentException("FlashcardDeck with id " + flashcardDeckModel.getId() + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, query.next());
        if (!isDeckOwner(userId, vertex))
            throw new IllegalArgumentException("FlashcardDeck with id " + flashcardDeckModel.getId() + " not found");

        if (flashcardDeckModel.getName() != null) {
            vertex.setName(flashcardDeckModel.getName());
        }
        if (flashcardDeckModel.getDescription() != null) {
            vertex.setDescription(flashcardDeckModel.getDescription());
        }

        return createModel(vertex);
    }

    @Override
    public void delete(String userId, String id) {
        var query = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, id);
        if (!query.hasNext())
            throw new IllegalArgumentException("FlashcardDeck with id " + id + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, query.next());
        if (!isDeckOwner(userId, vertex))
            throw new IllegalArgumentException("FlashcardDeck with id " + id + " not found");

        query.next().remove();
    }

    private static FlashcardDeckModel createModel(FlashcardDeckVertex v) {
        var model = new FlashcardDeckModel();
        model.setId(v.getId());
        model.setName(v.getName());
        model.setDescription(v.getDescription());
        return model;
    }

    private static boolean isDeckOwner(String userId, FlashcardDeckVertex v) {
        if (v.getOwner() == null)
            return true; // public deck

        return v.getOwner().getEmail().equals(userId);
    }
}
