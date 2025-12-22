package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardDecksQuery;
import com.explik.diybirdyapp.persistence.query.GetFlashcardDeckQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
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

    @Autowired
    private QueryHandler<GetFlashcardDeckQuery, FlashcardDeckDto> getFlashcardDeckQueryHandler;

    @Autowired
    private QueryHandler<GetAllFlashcardDecksQuery, List<FlashcardDeckDto>> getAllFlashcardDecksQueryHandler;

    public FlashcardDeckRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardDeckDto add(String userId, FlashcardDeckDto model) {
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
    public FlashcardDeckDto get(String userId, String id) {
        var query = new GetFlashcardDeckQuery();
        query.setUserId(userId);
        query.setDeckId(id);
        return getFlashcardDeckQueryHandler.handle(query);
    }

    @Override
    public List<FlashcardDeckDto> getAll(String userId) {
        var query = new GetAllFlashcardDecksQuery();
        query.setUserId(userId);
        return getAllFlashcardDecksQueryHandler.handle(query);
    }

    @Override
    public FlashcardDeckDto update(String userId, FlashcardDeckDto flashcardDeckModel) {
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

    private static FlashcardDeckDto createModel(FlashcardDeckVertex v) {
        var model = new FlashcardDeckDto();
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
